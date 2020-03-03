package springData.controller;

import java.security.Principal;
import java.time.LocalTime;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

import springData.DTO.RequestDTO;
import springData.domain.Car;
import springData.domain.CarAvailability;
import springData.domain.Request;
import springData.domain.User;
import springData.exception.RequestNotFoundException;
import springData.repository.AddressRepository;
import springData.repository.CarAvailabilityRepository;
import springData.repository.CarRepository;
import springData.repository.RequestRepository;
import springData.repository.UserRepository;
import springData.utils.AccessCodeGenerator;
import springData.constants.Constants;
import springData.validator.RequestDTOValidator;

@Controller
@RequestMapping("/user")
public class UserController {

   private final Logger logger = LoggerFactory.getLogger(UserController.class);

   BCryptPasswordEncoder pe = new  BCryptPasswordEncoder();

   AccessCodeGenerator codeGenerator = new AccessCodeGenerator();

   @Autowired UserRepository userRepo;
   @Autowired AddressRepository addressRepo;
   @Autowired CarRepository carRepo;
   @Autowired CarAvailabilityRepository carAvailabilityRepo;
   @Autowired RequestRepository requestRepo;

   @InitBinder("requestDTO")
   protected void initRequestDTOBinder(WebDataBinder binder) {
      binder.addValidators(new RequestDTOValidator(requestRepo));
   }

   @GetMapping ("/dashboard")
   public String dashboard(Model model, Principal principal) {

      //Get Logged in User
      User user = userRepo.findByUsername(principal.getName());

      //Active Request exists
      if (user.isActive() == true) {
         Request request = requestRepo.findByTopIdDesc();//findLatestByDate();
         System.err.println(request);
         logger.info("Request is currently In Progress");

         return "redirect:/user/active-request/" + request.getRequestID();
      }
      else {
         //Add Request form
         RequestDTO requestDTO = new RequestDTO();
         
         model.addAttribute("requestDTO", requestDTO);
         model.addAttribute("username", user.getFirstName() + " " + user.getLastName());

         return "/user/dashboard";
      }
   }

   @PostMapping("/requestCar")
   public String requestCar(@Valid @ModelAttribute("requestDTO") RequestDTO requestDTO, BindingResult result,
           Model model, Principal principal, RedirectAttributes redirectAttributes) {

      //Get Logged in User
      User user = userRepo.findByUsername(principal.getName());
      
      if (result.hasErrors()) {
         //Add Car Request form
         model.addAttribute("requestDTO", requestDTO);
         
         logger.info("RequestDTO Errors: " + result);
         return "redirect:/dashboard";
      }
      else {
         //Create new User using UserDTO details
         Request request = new Request();

         //Retrieve Request details from DTO
         request.setRequestDate(requestDTO.getRequestDate());
         request.setStartTime(LocalTime.parse(requestDTO.getStartTime()));
         request.setEndTime(LocalTime.parse(requestDTO.getEndTime()));
         request.setLatitude(requestDTO.getLatitude());
         request.setLongitude(requestDTO.getLongitude());
         request.setStatus(Constants.STATUS_IN_PROGRESS);
         request.setUser(user);

         //Find available Car
         Car car = findCar(request);
         if (!(car.equals(null))) {
            request.setCar(car);
            
            //Add Request to User's history
            user.addRequest(request);
            user.setActive(true);
   
            //Save Request
            requestRepo.save(request);
   
            //Update User
            userRepo.save(user);
            
            logger.info("Request created: ");
   
            return "redirect:/user/active-request/" + request.getRequestID();
         }
         //Unable to find car
         else {
            // Notification Message
            String requestError = "Error: Unable to fulfil request";
            model.addAttribute("requestError", requestError);

            return "redirect:/user/dashboard";
         }
      }
   }

   @GetMapping("/active-request/{requestID}")
   public String activeRequest(@PathVariable int requestID, Model model)
           throws NullPointerException{
      
      //Find Request by ID
      Request request = requestRepo.findById(requestID);
      
      if (request == null) {
         logger.info("Invalid request. Request Not found with ID: " + requestID);

         throw new RequestNotFoundException("Invalid request. Request Not found with ID: " + requestID);
      }
      //Add details to model
      model.addAttribute("request", request);
      model.addAttribute("username", request.getUser().getFirstName() + " " + request.getUser().getLastName());

      return "/user/active-Request";
   }

   @GetMapping("/view-request/{requestID}")
   public String viewRequestDetails(@PathVariable int requestID, Model model) {
      //Find Request by ID
      Request request = requestRepo.findById(requestID);

      model.addAttribute("request", request);
      model.addAttribute("username", request.getUser().getFirstName() + " " + request.getUser().getLastName());

      return "/user/view-request";
   }

   @GetMapping("/cancel-request/{requestID}")
   public String cancelRequest(@PathVariable int requestID, Model model, RedirectAttributes redirectAttributes) {
      //Find Request by ID
      Request request = requestRepo.findById(requestID);
      
      //Find User
      User user = request.getUser();
      user.setActive(false);

      //Update Availability time
      CarAvailability availability = carAvailabilityRepo.findById(request.getCar().getCarAvailability().getAvailabilityID());
      availability.setAvailabilityTime(LocalTime.now());

      //Update Request details
      request.setStatus(Constants.STATUS_CANCELLED);
      request.setEndTime(LocalTime.now());
      request.getCar().setIsActive(false);
      request.getCar().setCarAvailability(null);

      //Save Changes
      requestRepo.save(request);
      userRepo.save(user);
      carAvailabilityRepo.save(availability);

      // Notification Message
      String message = "Update: Request Cancelled";
      redirectAttributes.addFlashAttribute("message", message);

      logger.info("Request cancelled: " + requestID);

      return "redirect:/user/dashboard";
   }

   @GetMapping("/history")
   public String history(Model model, Principal principal) {
      //Get Logged in User
      User user = userRepo.findByUsername(principal.getName());

      //List of Users
      List<Request> requests = (List<Request>) requestRepo.findAllByUser(user);

      model.addAttribute("requests", requests);
      model.addAttribute("username", user.getFirstName() + " " + user.getLastName());

      return "/user/history";
   }

   //Function that finds first available Car
   private Car findCar(Request request) {
      try {
         Car car = new Car();
         List<Car> availableCars = carRepo.findAllAvailable();
         
         car = availableCars.get(0);
         car.setIsActive(true);
         carRepo.save(car);

         CarAvailability availability = new CarAvailability(request.getEndTime(),
                 AccessCodeGenerator.generateAccessCode());

         carAvailabilityRepo.save(availability);

         car.setCarAvailability(availability);

         carRepo.save(car);

         return car;
      } catch (Exception e) {
         return null;
      }
      
   }

}
//UserController
