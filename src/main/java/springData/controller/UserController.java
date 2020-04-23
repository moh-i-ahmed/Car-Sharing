package springData.controller;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.stripe.exception.StripeException;

import springData.DTO.RequestDTO;
import springData.algorithm.CarAllocation;
import springData.domain.Car;
import springData.domain.CarAvailability;
import springData.domain.Location;
import springData.domain.Request;
import springData.domain.User;
import springData.repository.CarAvailabilityRepository;
import springData.repository.CarRepository;
import springData.repository.LocationRepository;
import springData.repository.RequestRepository;
import springData.repository.UserRepository;
import springData.services.InvoiceService;
import springData.utils.AccessCodeGenerator;
import springData.constants.Constants;
import springData.validator.RequestDTOValidator;

@Controller
@RequestMapping("/user")
public class UserController {

   private static final Logger logger = LoggerFactory.getLogger(UserController.class);

   @Autowired private UserRepository userRepo;
   @Autowired private RequestRepository requestRepo;
   @Autowired private LocationRepository locationRepo;
   @Autowired private CarRepository carRepo;
   @Autowired private CarAvailabilityRepository carAvailabilityRepo;
   @Autowired private CarAllocation carAllocator;
   @Autowired private InvoiceService invoiceService;

   @InitBinder("requestDTO")
   protected void initRequestDTOBinder(WebDataBinder binder) {
      binder.addValidators(new RequestDTOValidator(requestRepo));
   }

   @GetMapping ("/dashboard")
   public String dashboard(Model model, Principal principal) {

      // Get Logged in User
      User user = userRepo.findByUsername(principal.getName());
      //User user = userRepo.getUserDetails(principal.getName());

      // Active Request exists
      if (user.isActive() == true) {
         Request request = requestRepo.findByTopIdDesc(user);//findLatestByDate();

         logger.info("Request is currently In Progress");

         return "redirect:/user/active-request/" + request.getRequestID();
      }
      else {
         // Add Request form
         RequestDTO requestDTO = new RequestDTO();

         model.addAttribute("requestDTO", requestDTO);
         model.addAttribute("username", user.getFirstName() + " " + user.getLastName());

         return "/user/dashboard";
      }
   }

   @PostMapping("/requestCar")
   public String requestCar(@Valid @ModelAttribute("requestDTO") RequestDTO requestDTO, BindingResult result,
         Model model, Principal principal, RedirectAttributes redirectAttributes) {

      // Get Logged in User
      User user = userRepo.findByUsername(principal.getName());

      if (result.hasErrors()) {
         // Add Car Request form
         model.addAttribute("requestDTO", requestDTO);
         model.addAttribute("username", user.getFirstName() + " " + user.getLastName());

         logger.info("RequestDTO Errors: " + result);

         return "redirect:/dashboard";
      }
      else {
         // Create new Request using RequestDTO input
         Request request = new Request();

         HashMap<String, LocalDateTime> dateTimes = toDateTime(LocalTime.parse(requestDTO.getStartTime()), 
               LocalTime.parse(requestDTO.getEndTime()));

         // Invalid startTime
         if (dateTimes.get("startTime").isAfter(LocalDateTime.now())) {
            result.reject("startTime", "Choose a future time");
         }
         // Retrieve Request details from DTO
         request.setRequestDate(requestDTO.getRequestDate());
         request.setStartTime(dateTimes.get("startTime"));
         request.setEndTime(dateTimes.get("endTime"));

         //request.setStartTime(LocalTime.parse(requestDTO.getStartTime()));
         //request.setEndTime(LocalTime.parse(requestDTO.getEndTime()));
         request.setDistance(requestDTO.getDistance());

         Location pickupLocation = new Location(requestDTO.getPickupLatitude(), requestDTO.getPickupLongitude());
         request.setPickupLocation(pickupLocation);
         locationRepo.save(pickupLocation);

         Location dropoffLocation = new Location(requestDTO.getDropoffLatitude(), requestDTO.getDropoffLongitude());
         request.setDropoffLocation(dropoffLocation);
         locationRepo.save(dropoffLocation);

         //request.setStatus(Constants.STATUS_IN_PROGRESS);
         //request.setUser(user);
         requestRepo.save(request);

         // Redirect to car selection
         redirectAttributes.addFlashAttribute("requestID", request.getRequestID());

         return "redirect:/user/car-selection/" + request.getRequestID();
      }
   }

   @GetMapping("/car-selection/{requestID}")
   public String carSelection(@PathVariable int requestID, Model model, Principal principal) {
      // Find request
      Request request = requestRepo.findById(requestID);

      User user = userRepo.findByUsername(principal.getName());

      // Active Request exists
      if (request == null || ((request != null) && (user.isActive() == true))) {
         return "redirect:/user/dashboard";
      }

      // Retrieve cars
      TreeMap<Double, Car> carsWithinRadius = carAllocator.carAllocator(request);

      model.addAttribute("cars", carsWithinRadius);

      return "/user/car-selection";
   }

   @GetMapping("/select-car/{registrationNumber}/{requestID}")
   public String selectCar(@PathVariable String registrationNumber, @PathVariable int requestID, Model model,
         Principal principal) {

      // Find request
      Request request = requestRepo.findById(requestID);

      // Get Logged in User
      User user = userRepo.findByUsername(principal.getName());

      // Active Request exists
      if (request == null || ((request != null) && (user.isActive() == true))) {
         return "redirect:/user/dashboard";
      }

      // Find selected car
      Car car = carRepo.findByRegistrationNumber(registrationNumber);

      if (!(car == null)) {
         // Create time slot 
         CarAvailability availability = new CarAvailability(request.getStartTime(), request.getEndTime(),
               AccessCodeGenerator.generateAccessCode(), car);

         carAvailabilityRepo.save(availability);

         // Add request history to car
         car.setIsActive(true);
         car.addCarAvailability(availability);
         carRepo.save(car);

         // Add Request to User's history
         user.addRequest(request);
         user.setActive(true);

         // Save Request
         request.setAccessCode(availability.getAccessCode());
         request.setCar(car);
         request.setStatus(Constants.REQUEST_STATUS_IN_PROGRESS);
         request.setUser(user);
         requestRepo.save(request);

         // Update User
         userRepo.save(user);

         logger.info("Request created for: " + user.getUsername());

         return "redirect:/user/active-request/" + request.getRequestID();
      }
      // Unable to find car
      else {
         // Notification Message
         String requestError = "Error: Unable to fulfil request";
         model.addAttribute("requestError", requestError);

         return "redirect:/user/dashboard";
      }
   }

   /**
    * Checks if time A is within range of time B
    * 
    * @param range
    * @return - boolean
    */
   private boolean timeWithinRange(Request request, int range) {

      // lower and upper limits
      LocalDateTime lower = request.getStartTime().minusMinutes(range);
      LocalDateTime upper = request.getEndTime().plusMinutes(range);
      LocalDateTime startTime = request.getStartTime();

      // also test if A is exactly 90 minutes before or after B
      if ((startTime.isAfter(lower) || startTime.equals(lower))
            && (startTime.isBefore(upper) || startTime.equals(upper))) {
         return true;
      }
      return false;
   }

   @GetMapping("/unlock-car/{requestID}/{accessCode}")
   public String unlockCar(@PathVariable int requestID, @PathVariable String accessCode, Model model,
         Principal principal, RedirectAttributes redirectAttributes) {

      // Find Request by ID
      Request request = requestRepo.findById(requestID);

      Car car = request.getCar();

      if (request == null || !request.getAccessCode().equalsIgnoreCase(accessCode)) {
         return "redirect:/dashboard";
      }
      LocalDateTime currentTime = LocalDateTime.now();

      // Notification message
      String message = "";

      // Too early to unlock car
      if (request.getStartTime().minusMinutes(1).isAfter(currentTime)) {
         // Add error notification message
         message = "Too early to unlock car";
         redirectAttributes.addFlashAttribute("message", message);

         logger.info("Too early to unlock " + car.getRegistrationNumber());

         return "redirect:/user/active-request/" + requestID;
      }
      else {
         // Unlock Car & update
         car.setUnlocked(true);
         carRepo.save(car);

         // Add success notification message
         message = "Car Unlocked";
         redirectAttributes.addFlashAttribute("message", message);

         logger.info("User unlocked " + car.getRegistrationNumber());

         return "redirect:/user/active-request/" + requestID;
      }
   }

   @GetMapping("/active-request/{requestID}")
   public String activeRequest(@PathVariable int requestID, Model model) throws NullPointerException {

      // Find Request by ID
      Request request = requestRepo.findById(requestID);

      // Request has ended
      if (request == null || request.getUser().isActive() == false) {
         logger.info("Invalid request. Request Not found with ID: " + requestID);

         return "redirect:/user/dashboard";
      }
      // Add details to model
      model.addAttribute("request", request);
      model.addAttribute("username", request.getUser().getFirstName() + " " + request.getUser().getLastName());

      return "/user/active-request";
   }

   @GetMapping("/view-request/{requestID}")
   public String viewRequestDetails(@PathVariable int requestID, Model model) {
      // Find Request by ID
      Request request = requestRepo.findById(requestID);

      if (request == null) {
         return "redirect:/user/history";
      }
      System.err.println("StartTime is " + request.getStartTime());
      System.err.println("EndTime is " + request.getEndTime());

      // Request Duration
      LocalTime duration = LocalTime.MIN.plus(Duration.ofMinutes
            (ChronoUnit.MINUTES.between(request.getStartTime(),request.getEndTime())));

      model.addAttribute("baseCharge", Constants.PRICE_BASE_CHARGE/100);
      model.addAttribute("duration", duration);
      model.addAttribute("request", request);
      model.addAttribute("username", request.getUser().getFirstName() + " " + request.getUser().getLastName());

      return "/user/view-request";
   }

   @GetMapping("/cancel-request/{requestID}")
   public String cancelRequest(@PathVariable int requestID, Model model, RedirectAttributes redirectAttributes) {
      // Find Request by ID
      Request request = requestRepo.findById(requestID);

      // Request has ended
      if (request == null || !request.getStatus().equals(Constants.REQUEST_STATUS_IN_PROGRESS)) {
         logger.info("Invalid request. Request Not found with ID: " + requestID);

         return "redirect:/user/dashboard";
      }
      else {
         // Update User
         User user = request.getUser();
         user.setActive(false);
         userRepo.save(user);

         // Update Availability time slot
         CarAvailability availability = carAvailabilityRepo.findByRequest(request);

         // Update Request
         request.setStatus(Constants.REQUEST_STATUS_CANCELLED);
         //request.setEndTime(LocalDateTime.now());
         requestRepo.save(request);

         logger.info("pre Invoice generated");
         // Generate Invoice for cancelled request
         try {
            invoiceService.generateInvoice(request);
            logger.info("Invoice generated");
         } catch (StripeException e) {
            logger.info(e.toString());
         }

         // Update Car
         Car car = request.getCar();
         car.setIsActive(false);
         car.removeCarAvailability(availability);
         carRepo.save(car);

         // Remove time slot
         //requestRepo.save(request);
         carAvailabilityRepo.delete(availability);

         // Notification Message
         String message = "Update: Request Cancelled";
         redirectAttributes.addFlashAttribute("message", message);

         logger.info("Request cancelled: " + requestID);

         return "redirect:/user/dashboard";
      }
   }

   @GetMapping("/history")
   public String history(Model model, Principal principal) {
      // Get Logged in User
      User user = userRepo.findByUsername(principal.getName());

      // List of Users
      List<Request> requests = (List<Request>) requestRepo.findAllByUser(user);

      model.addAttribute("requests", requests);
      model.addAttribute("username", user.getFirstName() + " " + user.getLastName());

      return "/user/history";
   }

   /**
    * Function to convert LocalDateTime input to millisecond value
    * 
    * @param time - LocalDateTime to be converted
    * @return - millisecond value of LocalDateTime
    */
   private long toMilliSeconds(LocalDateTime time) {
      ZonedDateTime zdt = ZonedDateTime.of(time, ZoneId.systemDefault());
      return zdt.toInstant().toEpochMilli();
   }

   /**
    * Function to convert two LocalTime objects to LocalDateTime
    * 
    * @param startTime - request start time (LocalTime)
    * @param endTime - LocalTime end time (LocalTime)
    * @return - HashMap containing two LocalDateTime objects
    */
   private HashMap<String, LocalDateTime> toDateTime(LocalTime startTime, LocalTime endTime) {
      // HashMap to return
      HashMap<String, LocalDateTime> dateTimes = new HashMap<String, LocalDateTime>();

      // End time in next day (11:59 to 01:00)
      if ((endTime.isBefore(startTime))) {
         // Create LocalDateTime & add to HashMap
         dateTimes.put("startTime", LocalDateTime.of(LocalDate.now(), startTime));
         dateTimes.put("endTime", LocalDateTime.of(LocalDate.now().plusDays(1), endTime));
      }
      // TODO fix this
      else {
         // Create LocalDateTime & add to HashMap
         dateTimes.put("startTime", LocalDateTime.of(LocalDate.now(), startTime));
         dateTimes.put("endTime", LocalDateTime.of(LocalDate.now(), endTime));
      }
      System.err.println("Times are " + dateTimes.toString());
      return dateTimes;
   }

}
//UserController
