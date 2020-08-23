package springData.controller;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
import springData.services.StripeService;
import springData.utils.AccessCodeGenerator;
import springData.constants.Constants;
import springData.validator.RequestDTOValidator;

@Controller
@RequestMapping("/user")
public class UserController {

   private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

   @Autowired private UserRepository userRepo;
   @Autowired private RequestRepository requestRepo;
   @Autowired private LocationRepository locationRepo;
   @Autowired private CarRepository carRepo;
   @Autowired private CarAvailabilityRepository carAvailabilityRepo;
   @Autowired private CarAllocation carAllocator;

   @Autowired private StripeService stripeService;
   @Autowired private InvoiceService invoiceService;

   @InitBinder("requestDTO")
   protected void initRequestDTOBinder(WebDataBinder binder) {
      binder.addValidators(new RequestDTOValidator());
   }

   @GetMapping ("/dashboard")
   public String dashboard(Model model, Principal principal, RedirectAttributes redirectAttributes) {

      // Get Logged in User
      User user = userRepo.findByUsername(principal.getName());

      // User account is not verified
      if (user.isEnabled() == false) {
         //Notification message
         String messageCode = Constants.NOTIFICATION_WARNING;
         String message = "Account is not verified";

         redirectAttributes.addFlashAttribute("messageCode", messageCode);
         redirectAttributes.addFlashAttribute("message", message);

         return "redirect:/account/profile";
      }
      // Active Request exists
      if (user.isActive() == true) {
         Request request = requestRepo.findByTopIdDesc(user);

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

      // Get logged in user
      User user = userRepo.findByUsername(principal.getName());

      // User account is not verified
      if (user.isEnabled() == false) {
         // Notification message
         String message = "Account is not verified";

         redirectAttributes.addFlashAttribute("message", message);

         return "redirect:/user/dashboard";
      }
      // User Has No Payment Card
      try {
         if (stripeService.hasPaymentSource(user) == false) {
            // Notification message
            String message = "No payment method";

            redirectAttributes.addFlashAttribute("message", message);

            return "redirect:/user/dashboard";
         }
      } catch (StripeException e) {
         LOGGER.info(e.toString());
      }
      // Validation Error
      if (result.hasErrors()) {
         redirectAttributes.addFlashAttribute("message", result.getFieldError().getDefaultMessage().toString());

         LOGGER.info("RequestDTO Errors: " + result);

         return "redirect:/user/dashboard";
      } else {
         // Create new Request using RequestDTO input
         Request request = new Request();

         // Retrieve Request details from DTO
         request.setRequestDate(requestDTO.getRequestDate());
         request.setStartTime(requestDTO.getStartTime());
         request.setEndTime(requestDTO.getEndTime());

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

      if (carsWithinRadius.isEmpty()) {
         model.addAttribute("carsList", 0);
      }
      model.addAttribute("cars", carsWithinRadius);

      return "/user/car-selection";
   }

   @GetMapping("/next-available-car/{requestID}")
   public String nextAvailableCar(@PathVariable int requestID, Model model, Principal principal,
           RedirectAttributes redirectAttributes) {
      // Find request
      Request request = requestRepo.findById(requestID);

      User user = userRepo.findByUsername(principal.getName());

      // Active Request exists
      if (request == null || ((request != null) && (user.isActive() == true))) {
         return "redirect:/user/dashboard";
      }
      // Retrieve cars
      TreeMap<Double, Car> carsWithinRadius = carAllocator.findFirstCar(request);

      if (carsWithinRadius.isEmpty()) {
         //Notification message
         String messageCode = Constants.NOTIFICATION_ERROR;
         String message = "Unable to fulfill request, please try again later";

         redirectAttributes.addFlashAttribute("messageCode", messageCode);
         redirectAttributes.addFlashAttribute("message", message);

         return "redirect:/user/dashboard";
      }
      model.addAttribute("cars", carsWithinRadius);

      return "/user/car-selection/";
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
         request.setPickupLocation(car.getLocation());
         request.setAccessCode(availability.getAccessCode());
         request.setCar(car);
         request.setStatus(Constants.REQUEST_STATUS_UNSTARTED);
         request.setUser(user);
         requestRepo.save(request);

         // Update User
         userRepo.save(user);

         LOGGER.info("Request created for: " + user.getUsername());

         return "redirect:/user/active-request/" + request.getRequestID();
      } else {
         // Unable to find car
         // Notification Message
         String message = "Unable to fulfil request";

         model.addAttribute("message", message);

         return "redirect:/user/dashboard";
      }
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
      String messageCode = "";
      String message = "";

      // Too early to unlock car
      if (request.getStartTime().minusMinutes(1).isAfter(currentTime)) {
         // Add error notification message
         messageCode = Constants.NOTIFICATION_WARNING;
         message = "Too early to unlock";

         redirectAttributes.addFlashAttribute("messageCode", messageCode);
         redirectAttributes.addFlashAttribute("message", message);

         LOGGER.info("Too early to unlock " + car.getRegistrationNumber());

         return "redirect:/user/active-request/" + requestID;
      }
      else {
         // Unlock & update Car
         car.setUnlocked(true);
         carRepo.save(car);

         request.setStatus(Constants.REQUEST_STATUS_IN_PROGRESS);
         requestRepo.save(request);

         // Add success notification message
         messageCode = Constants.NOTIFICATION_SUCCESS;
         message = "Car unlocked, drive safe";

         redirectAttributes.addFlashAttribute("messageCode", messageCode);
         redirectAttributes.addFlashAttribute("message", message);

         LOGGER.info("User unlocked " + car.getRegistrationNumber());

         return "redirect:/user/active-request/" + requestID;
      }
   }

   @GetMapping("/active-request/{requestID}")
   public String activeRequest(@PathVariable int requestID, Model model) {

      // Find Request by ID
      Request request = requestRepo.findById(requestID);

      // Request has ended
      if (request == null || request.getUser().isActive() == false) {
         LOGGER.info("Invalid request. Request Not found with ID: " + requestID);

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
      // Request Duration
      LocalTime duration = LocalTime.MIN.plus(Duration.ofMinutes
              (ChronoUnit.MINUTES.between(request.getStartTime(), request.getEndTime())));

      model.addAttribute("baseCharge", Constants.PRICE_BASE_CHARGE / 100);
      model.addAttribute("duration", duration);
      model.addAttribute("request", request);
      model.addAttribute("username", request.getUser().getFirstName() + " " + request.getUser().getLastName());

      return "/user/view-request";
   }

   @GetMapping("/end-request/{requestID}")
   public String endRequest(@PathVariable int requestID, Model model, RedirectAttributes redirectAttributes) {
      // Find Request by ID
      Request request = requestRepo.findById(requestID);

      // Request has ended
      if (request == null || !request.getStatus().equals(Constants.REQUEST_STATUS_IN_PROGRESS)) {
         LOGGER.info("Invalid request. Request Not found with ID: " + requestID);

         return "redirect:/user/dashboard";
      }
      // End request 5 minutes after start
      else if (request.getEndTime().isBefore(request.getStartTime().plusMinutes(5))) {
         LOGGER.info("Too early to end request");

         // Notification Message
         String messageCode = Constants.NOTIFICATION_WARNING;
         String message = "Request must end at least 5 minutes after starting";

         redirectAttributes.addFlashAttribute("messageCode", messageCode);
         redirectAttributes.addFlashAttribute("message", message);

         return "redirect:/user/active-request/" + requestID;
      }
      else {
         // Update User
         User user = request.getUser();
         user.setActive(false);
         userRepo.save(user);

         // Update Availability time slot
         CarAvailability availability = carAvailabilityRepo.findByRequest(request);

         // Update Request
         request.setStatus(Constants.REQUEST_STATUS_COMPLETE);
         request.setEndTime(LocalDateTime.now());
         requestRepo.save(request);

         // Generate Invoice for cancelled request
         try {
            invoiceService.generateInvoice(request);
         } catch (StripeException e) {
            LOGGER.info(e.toString());
         }

         // Update Car
         Car car = request.getCar();
         car.setIsActive(false);
         car.setUnlocked(false);
         car.removeCarAvailability(availability);
         carRepo.save(car);

         // Remove time slot
         carAvailabilityRepo.delete(availability);

         // Notification Message
         String messageCode = Constants.NOTIFICATION_SUCCESS;
         String message = "Request Completed";

         redirectAttributes.addFlashAttribute("messageCode", messageCode);
         redirectAttributes.addFlashAttribute("message", message);

         LOGGER.info("Request completed: " + requestID);
      }
      return "redirect:/user/dashboard";
   }

   @GetMapping("/cancel-request/{requestID}")
   public String cancelRequest(@PathVariable int requestID, Model model, RedirectAttributes redirectAttributes) {
      // Find Request by ID
      Request request = requestRepo.findById(requestID);

      // Request has ended
      if (request == null || !request.getStatus().equals(Constants.REQUEST_STATUS_UNSTARTED)) {
         LOGGER.info("Invalid request. Request Not found with ID: " + requestID);

         return "redirect:/user/dashboard";
      } else {
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

         // Generate Invoice for cancelled request
         try {
            invoiceService.generateInvoice(request);
         } catch (StripeException e) {
            LOGGER.info(e.toString());
         }

         // Update Car
         Car car = request.getCar();
         car.setIsActive(false);
         car.setUnlocked(false);
         car.removeCarAvailability(availability);
         carRepo.save(car);

         // Remove time slot
         //requestRepo.save(request);
         carAvailabilityRepo.delete(availability);

         // Notification Message
         String messageCode = Constants.NOTIFICATION_SUCCESS;
         String message = "Request Cancelled";

         redirectAttributes.addFlashAttribute("messageCode", messageCode);
         redirectAttributes.addFlashAttribute("message", message);

         LOGGER.info("Request cancelled: " + requestID);

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

}
// UserController
