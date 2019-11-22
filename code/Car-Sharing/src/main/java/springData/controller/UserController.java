package springData.controller;

import java.security.Principal;
import java.util.List;

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
   protected void initUserDTOBinder(WebDataBinder binder) {
      binder.addValidators(new RequestDTOValidator(requestRepo));
   }

   @PostMapping(value = "/requestCar")
   public String createUser(@Valid @ModelAttribute("requestDTO") RequestDTO requestDTO, BindingResult result,
           Model model, Principal principal) {

      if (result.hasErrors()) {
         //Add Car Request form
         RequestDTO newRequestDTO = new RequestDTO();
         model.addAttribute("requestDTO", newRequestDTO);
         return "/user/dashboard";

      } else {
         //Create new User using UserDTO details
         Request newRequest = new Request();

         newRequest.setStartTime(requestDTO.getStartTime());
         newRequest.setEndTime(requestDTO.getEndTime());
         newRequest.setRequestDate(requestDTO.getRequestDate());
         newRequest.setLatitude(requestDTO.getLatitude());
         newRequest.setLongitude(requestDTO.getLongitude());
         newRequest.setUser(userRepo.findByUsername(principal.getName()));

         //Find available Car
         newRequest.setCar(findCar(newRequest));

         //Save Request
         requestRepo.save(newRequest);

        // model.addAttribute("startTime", newRequest.getStartTime());
        // model.addAttribute("endTime", newRequest.getEndTime());
        // model.addAttribute("accessCode", newRequest.getCar().getCarAvailability().getAccessCode());
        // model.addAttribute("carColor", newRequest.getCar().getCarColor());
        // model.addAttribute("carName", newRequest.getCar().getCarName());
        // model.addAttribute("registrationNumber", newRequest.getCar().getRegistrationNumber());
         model.addAttribute("requestID", newRequest.getRequestID());

         return "redirect:/user/requestCar";
      }
   }

   @GetMapping("/requestCar")
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
      Request newRequest = requestRepo.findById(requestID);

      //Add details to model
      model.addAttribute("startTime", newRequest.getStartTime());
      model.addAttribute("endTime", newRequest.getEndTime());
      model.addAttribute("accessCode", newRequest.getCar().getCarAvailability().getAccessCode());
      model.addAttribute("carColor", newRequest.getCar().getCarColor());
      model.addAttribute("carName", newRequest.getCar().getCarName());
      model.addAttribute("registrationNumber", newRequest.getCar().getRegistrationNumber());

      return "/user/view-request";
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
