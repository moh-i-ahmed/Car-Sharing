package springData.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import springData.DTO.RequestDTO;
import springData.domain.Request;
import springData.domain.User;
import springData.repository.CarRepository;
import springData.repository.RequestRepository;
import springData.repository.UserRepository;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class AuthenticationController {

   @Autowired UserRepository userRepo;
   @Autowired CarRepository carRepo;
   @Autowired RequestRepository requestRepo;

   @RequestMapping(path = "/", method = RequestMethod.GET)
   public String landing() {
      return "login";
   }

   @RequestMapping(value = "/success-login", method = RequestMethod.GET)
   public String successLogin(Principal principal) {
      //Get Logged in User
      User user = userRepo.findByUsername(principal.getName());

      String view;

      //Get View for Role
      switch (user.getRole().getRole()) {
      case "ADMIN":
         view = "redirect:/dashboard";
         break;
      default:
         view = "redirect:/dashboard";
         break;
      }
      return view;
   }

   //   add cookies
   @RequestMapping (value = "/dashboard", method = RequestMethod.GET)
   public String dashboard(Model model, Principal principal) {
      //Get Logged in User
      User user = userRepo.findByUsername(principal.getName());

      model.addAttribute("firstName", user.getFirstName());
      model.addAttribute("lastName", user.getLastName());

      //Check for Admin access
      if (user.getRole().getRole().equals("ADMIN")) {
         model.addAttribute("userCount", userRepo.findAll().size());
         model.addAttribute("totalCars", carRepo.findAll().size());
         model.addAttribute("activeCars", carRepo.findAllAvailable().size());
         model.addAttribute("requests", requestRepo.findAll().size());

         return "/admin/dashboard";
      }
      //User view
      else {
         //Check if car request is currently active
         //if(user.isActive() == true) {
         Request request = requestRepo.findLatestByDate();

         if(!(request == null) && request.getRequestDate().isEqual(LocalDate.now()) &&
               request.getEndTime().isAfter(LocalTime.now())) {
            
            System.err.println("Car already active");
            model.addAttribute("requestID", request.getRequestID());

            return "redirect:/user/request";
         }
         else {
            //Add Car Request form
            RequestDTO requestDTO = new RequestDTO();
            model.addAttribute("requestDTO", requestDTO);
            return "/user/dashboard";
         }
      }
   }

   @RequestMapping(value = "/index")
   public String index() {
      return "index";
   }

   @RequestMapping(value = "/help")
   public String help() {
      return "help";
   }

   @GetMapping("/access-denied")
   public String accessDenied() {
      return "404";
   }

   @GetMapping("/404")
   public String error404() {
      return "404";
   }

   @GetMapping("/error")
   public String error() {
      return "404";
   }

}
//AuthenticationController
