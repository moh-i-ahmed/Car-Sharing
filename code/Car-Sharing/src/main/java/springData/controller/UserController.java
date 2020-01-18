package springData.controller;

import java.security.Principal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.RequestParam;

import springData.DTO.RequestDTO;
import springData.DTO.UserDTO;
import springData.domain.Car;
import springData.domain.CarAvailability;
import springData.domain.Request;
import springData.domain.User;
import springData.repository.AddressRepository;
import springData.repository.CarAvailabilityRepository;
import springData.repository.CarRepository;
import springData.repository.RequestRepository;
import springData.repository.UserRepository;
import springData.utils.AccessCodeGenerator;

@Controller
@RequestMapping("/user")
public class UserController {

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

   @PostMapping(value = "/requestCar")
   public String createUser(@Valid @ModelAttribute("requestDTO") RequestDTO requestDTO, BindingResult result,
           Model model, Principal principal) {

      //Get Logged in User
      User user = userRepo.findByUsername(principal.getName());
      
      if (result.hasErrors()) {
         //Add Car Request form
         //RequestDTO newRequestDTO = new RequestDTO();
         model.addAttribute("requestDTO", requestDTO);
         
         System.err.println(result);
         return "redirect:/dashboard";
      }
      else {
         //Create new User using UserDTO details
         Request newRequest = new Request();

/*         String time = "05:32 AM";
         //time = time.split(" "); 
         String result = time.substring(0, time.length() - 3);
         
        System.out.println(LocalTime.parse(result));*/
         //String result = LocalTime.parse("03:30 PM" , DateTimeFormatter.ofPattern("hh:mm a" , Locale.US)).format(DateTimeFormatter.ofPattern("HH:mm"));
         
         newRequest.setStartTime(requestDTO.getStartTime());
         newRequest.setEndTime(requestDTO.getEndTime());
         newRequest.setRequestDate(requestDTO.getRequestDate());
         newRequest.setLatitude(requestDTO.getLatitude());
         newRequest.setLongitude(requestDTO.getLongitude());
         newRequest.setUser(userRepo.findByUsername(principal.getName()));
         user.setActive(true);

         //Find available Car
         newRequest.setCar(findCar(newRequest));
         System.out.println("it is a dish");
         //Save Request
         requestRepo.save(newRequest);
         userRepo.save(user);
         
         model.addAttribute("requestID", newRequest.getRequestID());
         
         return "redirect:/user/request";
      }
   }

   @GetMapping("/request")
   public String viewUser(@RequestParam(value = "requestID", required = true) int requestID, Model model) {
      //Find Request by ID
      Request request = requestRepo.findById(requestID);

      //Add details to model
      model.addAttribute("request", request);
      model.addAttribute("username", request.getUser().getUsername());

      return "/user/requestCar";
   }

   @RequestMapping("/history")
   public String carHistory(Model model, Principal principal) {
      //Get Logged in User
      User user = userRepo.findByUsername(principal.getName());

      //List of Users
      List<Request> requests = (List<Request>) requestRepo.findAllByUser(user);

      model.addAttribute("requests", requests);
      model.addAttribute("username", principal.getName());

      return "/user/history";
   }

   @GetMapping("/view-request/{requestID}")
   public String viewRequest(@PathVariable int requestID, Model model) {
      //Find Request by ID
      Request request = requestRepo.findById(requestID);

      //Add details to model
      model.addAttribute("startTime", request.getStartTime());
      model.addAttribute("endTime", request.getEndTime());
      model.addAttribute("accessCode", request.getCar().getCarAvailability().getAccessCode());
      model.addAttribute("carColor", request.getCar().getCarColor());
      model.addAttribute("carName", request.getCar().getCarName());
      model.addAttribute("registrationNumber", request.getCar().getRegistrationNumber());
      model.addAttribute("username", request.getUser().getUsername());

      return "/user/view-request";
   }

   //Function that finds first available Car
   private Car findCar(Request request) {
      Car car = new Car();
      List<Car> availableCars = carRepo.findAllAvailable();
      
      car = availableCars.get(0);
      car.setIsActive(true);
      carRepo.save(car);

      CarAvailability availability = new CarAvailability(request.getEndTime(),
              AccessCodeGenerator.generateAccessCode(6), car);

      carAvailabilityRepo.save(availability);

      car.setCarAvailability(availability);

      carRepo.save(car);

      return car;
   }
   
   public LocalTime timeConverter(String time) {
      String convert = LocalTime.parse(time, DateTimeFormatter.ofPattern("hh:mm a", Locale.US)).format(DateTimeFormatter.ofPattern("hh:mm"));
      return LocalTime.parse(convert);
   }

}
//UserController
