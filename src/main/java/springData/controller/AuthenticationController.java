package springData.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import springData.domain.User;
import springData.repository.CarRepository;
import springData.repository.RequestRepository;
import springData.repository.UserRepository;

import java.security.Principal;

@Controller
public class AuthenticationController {

   @Autowired UserRepository userRepo;
   @Autowired CarRepository carRepo;
   @Autowired RequestRepository requestRepo;

   @GetMapping("/")
   public String landing() {
      return "login";
   }

   //TODO   add cookies
   @GetMapping("/success-login")
   public String successLogin() {
      return "redirect:/dashboard";
   }

   @GetMapping ("/dashboard")
   public String dashboard(Model model, Principal principal) {
      //Get Logged in User
      User user = userRepo.findByUsername(principal.getName());

      //Check for Admin access
      if (user.getRole().getRole().equals("ADMIN")) {
         return "redirect:/admin/dashboard";
      }
      //User view
      else {
         return "redirect:/user/dashboard";
      }
   }

   @GetMapping("/help")
   public String help() {
      return "help";
   }

   @ResponseStatus(HttpStatus.FORBIDDEN)
   @GetMapping("/access-denied")
   public String accessDenied(Model model, Principal principal) {
      model.addAttribute("username", principal.getName());
      
      return "access-denied";
   }

   @ResponseStatus(HttpStatus.NOT_FOUND)
   @GetMapping("/404")
   public String error404() {
      return "404";
   }

   @ResponseStatus(HttpStatus.NOT_FOUND)
   @GetMapping(value = "/error")
   public String error(Model model, Principal principal) {
      model.addAttribute("username", principal.getName());

      return "404";
   }

}
//AuthenticationController
