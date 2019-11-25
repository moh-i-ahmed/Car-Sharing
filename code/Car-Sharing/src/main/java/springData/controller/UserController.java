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
import springData.repository.RoleRepository;
import springData.repository.UserRepository;
import springData.utils.AccessCodeGenerator;

@Controller
@RequestMapping("/user")
public class UserController {

   BCryptPasswordEncoder pe = new  BCryptPasswordEncoder();

   AccessCodeGenerator codeGenerator = new AccessCodeGenerator();

   @Autowired UserRepository userRepo;
   @Autowired RoleRepository roleRepo;
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

      
      if (result.hasErrors()) {
         System.out.println("Lukas likes wings 55");
         //Add Car Request form
         //RequestDTO newRequestDTO = new RequestDTO();
         model.addAttribute("requestDTO", requestDTO);
         
         System.err.println(result);
         return "/user/dashboard";
      } else {
         System.out.println("Lukas likes wings");
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

         //Find available Car
         newRequest.setCar(findCar(newRequest));
         System.out.println("it is a dish");
         //Save Request
         requestRepo.save(newRequest);

         model.addAttribute("requestID", newRequest.getRequestID());

         return "redirect:/user/requestCars";
      }
   }
   
   public LocalTime timeConverter(String time) {
      String convert = LocalTime.parse(time, DateTimeFormatter.ofPattern("hh:mm a", Locale.US)).format(DateTimeFormatter.ofPattern("hh:mm"));
      return LocalTime.parse(convert);
   }

   @GetMapping("/requestCars")
   public String viewUser(@RequestParam(value = "requestID", required = true) int requestID, Model model) {
      //Find Request by ID
      Request newRequest = requestRepo.findById(requestID);

      //Add details to model
      model.addAttribute("startTime", newRequest.getStartTime());
      model.addAttribute("endTime", newRequest.getEndTime());
      model.addAttribute("accessCode", newRequest.getCar().getCarAvailability().getAccessCode());
      model.addAttribute("carColor", newRequest.getCar().getCarColor());
      model.addAttribute("carName", newRequest.getCar().getCarName());
      model.addAttribute("registrationNumber", newRequest.getCar().getRegistrationNumber());

      return "/user/requestCar";
   }

   @RequestMapping("/history")
   public String carHistory(Model model, Principal principal) {
      //Get Logged in User
      User user = userRepo.findByUsername(principal.getName());

      //List of Users
      List<Request> requests = (List<Request>) requestRepo.findAllByUser(user);

      model.addAttribute("requests", requests);

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

      return "/user/view-request";
   }
   
   @GetMapping("/profile")
   public String account(Model model, Principal principal) {
      //Find User
      User user = userRepo.findByUsername(principal.getName());

      //Display User info using UserDTO
      UserDTO userDTO = new UserDTO();

      userDTO.setFirstName(user.getFirstName());
      userDTO.setLastName(user.getLastName());
      userDTO.setUsername(user.getUsername());
      userDTO.setPhoneNumber(user.getPhoneNumber());
      userDTO.setDriverLicense(user.getDriverLicense());

      model.addAttribute("userId", user.getUserID());
      model.addAttribute("userDTO", userDTO);

      return "user/profile";
   }

   //Function that finds 1st available Car
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

}
//UserController
