package springData.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import springData.domain.User;
import springData.repository.UserRepository;

import java.security.Principal;

@Controller
public class AuthenticationController {
   @Autowired UserRepository userRepo;

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
            view = "redirect:/user/dashboard";
            break;
      }
      return view;
   }

   @RequestMapping (value = "/dashboard", method = RequestMethod.GET)
   public String dashboard(Model model, Principal principal) {
      //Get Logged in User
      User user = userRepo.findByUsername(principal.getName());

      model.addAttribute("name", user.getFirstName());

      //Check for Admin access
      if (user.getRole().getRole().equals("ADMIN")) {
         return "/admin/dashboard";
      }
      return "/user/dashboard";
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
      return "access-denied";
   }

}
//AuthenticationController
