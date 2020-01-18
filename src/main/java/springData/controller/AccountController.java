package springData.controller;

import java.security.Principal;

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

import springData.DTO.PasswordDTO;
import springData.DTO.UserDTO;
import springData.domain.User;
import springData.repository.UserRepository;
import springData.services.EmailServiceImpl;
import springData.utils.PasswordGenerator;

@Controller
@RequestMapping("/account")
public class AccountController {

   Logger logger = LoggerFactory.getLogger(AccountController.class);

   BCryptPasswordEncoder pe = new  BCryptPasswordEncoder();

   @Autowired UserRepository userRepo;
   @Autowired private EmailServiceImpl emailService;

   @InitBinder("passwordDTO")
   protected void initPasswordDTOBinder(WebDataBinder binder) {
      binder.addValidators(new PasswordDTOValidator());
   }

   @GetMapping("/profile")
   public String profile(Model model, Principal principal) {
      //Retrieve logged in User
      User user = userRepo.findByUsername(principal.getName());

      //Check for User
      if (user.getRole().getRole().equals("USER")) {
         //Display User info using UserDTO
         UserDTO userDTO = new UserDTO();
         PasswordDTO passwordDTO = new PasswordDTO();

         userDTO.setFirstName(user.getFirstName());
         userDTO.setLastName(user.getLastName());
         userDTO.setUsername(user.getUsername());
         userDTO.setPhoneNumber(user.getPhoneNumber());
         userDTO.setDriverLicense(user.getDriverLicense());

         model.addAttribute("userId", user.getUserID());
         model.addAttribute("username", principal.getName());

         model.addAttribute("userDTO", userDTO);
         model.addAttribute("passwordDTO", passwordDTO);

         return "user/profile";
      }
      return "user/profile";
   }

   @PostMapping(value = "/change-password/submit/{userId}")
   public String changePassword(@Valid @ModelAttribute("passwordDTO") PasswordDTO passwordDTO, BindingResult result,
         @PathVariable int userId, Model model) {

      //Retrieve User
      User user = userRepo.findById(userId);

      if (result.hasErrors() || (user.getPassword() != passwordDTO.getCurrentPassword())) {
         //Reject wrong current password
         if (user.getPassword() != passwordDTO.getCurrentPassword()) {
            result.reject("currentPassword", "Incorrect current password");

            logger.info("\n Current password entered != stored password");
         }
         //Display User info using UserDTO
         UserDTO userDTO = new UserDTO();
         userDTO.setFirstName(user.getFirstName());
         userDTO.setLastName(user.getLastName());
         userDTO.setUsername(user.getUsername());
         userDTO.setPhoneNumber(user.getPhoneNumber());
         userDTO.setDriverLicense(user.getDriverLicense());

         model.addAttribute("userId", user.getUserID());
         model.addAttribute("username", user.getUsername());
         model.addAttribute("userDTO", userDTO);

         logger.info(result.toString());

         return "user/profile";
      } else {
         //Update Password
         //User user = userRepo.findById(userId);
         user.setPassword(pe.encode(passwordDTO.getNewPassword()));

         logger.info("\n Password changed by: " + user.getUsername());

         //Save User
         userRepo.save(user);

         return "redirect:/account/profile";
      }
   }

   @RequestMapping(value = "/forgot-password")
   public String forgotPassword(Model model) {
      UserDTO userDTO = new UserDTO();
      model.addAttribute("userDTO", userDTO);

      return "forgot-password";
   }

   @GetMapping(value = "/forgot-password/submit")
   public String resetPassword(@ModelAttribute("userDTO") UserDTO userDTO, Model model) {

      //Find User using UserDTO details
      User user = userRepo.findByUsername(userDTO.getUsername());

      if (user != null) {

         //Generate password
         String generatedPassword = PasswordGenerator.generateRandomPassword(8);
         System.err.println(generatedPassword);

         userDTO.setFirstName(user.getFirstName());
         userDTO.setPassword(generatedPassword);

         //Send Email
         emailService.sendResetEmail(userDTO);

         logger.info("\n Password reset for: " + userDTO.getUsername() +
               "\n Reset email sent.");

         //Save User
         user.setPassword(pe.encode(generatedPassword));   
         userRepo.save(user);

         return "redirect:/";
      }
      else {
         System.err.println("User not found");
         return "forgot-password";
      }
   }

}
//AccountController
