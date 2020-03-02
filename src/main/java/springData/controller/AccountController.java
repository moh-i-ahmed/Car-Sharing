package springData.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

import springData.DTO.PasswordDTO;
import springData.DTO.UserDTO;
import springData.domain.User;
import springData.repository.AddressRepository;
import springData.repository.UserRepository;
import springData.services.EmailServiceImpl;
import springData.validator.PasswordDTOValidator;
import springData.validator.UserDTOValidator;

@Controller
@RequestMapping("/account")
public class AccountController {

   private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

   BCryptPasswordEncoder pe = new BCryptPasswordEncoder();

   @Autowired UserRepository userRepo;
   @Autowired AddressRepository addressRepo;
   @Autowired private EmailServiceImpl emailService;

   @InitBinder("userDTO")
   protected void initUserDTOBinder(WebDataBinder binder) {
      binder.addValidators(new UserDTOValidator(userRepo));
   }

   @InitBinder("passwordDTO")
   protected void initPasswordDTOBinder(WebDataBinder binder) {
      binder.addValidators(new PasswordDTOValidator());
   }

   @GetMapping("/profile")
   public String profile(Model model, Principal principal) {
      // Retrieve logged in User
      User user = userRepo.findByUsername(principal.getName());

      // Check for User
      if (user.getRole().getRole().equals("USER")) {
         // Display User info using UserDTO
         UserDTO userDTO = new UserDTO();
         PasswordDTO passwordDTO = new PasswordDTO();

         userDTO.setFirstName(user.getFirstName());
         userDTO.setLastName(user.getLastName());
         userDTO.setUsername(user.getUsername());
         userDTO.setPhoneNumber(user.getPhoneNumber());
         userDTO.setDriverLicense(user.getDriverLicense());

         model.addAttribute("userId", user.getUserID());
         model.addAttribute("username", principal.getName());
         model.addAttribute("address", user.getAddresses().get(0));

         model.addAttribute("user", user);
         model.addAttribute("userDTO", userDTO);
         model.addAttribute("passwordDTO", passwordDTO);

         //return "user/profile";
      }
      return "user/profile";
   }

   @PostMapping("/change-password/{userId}")
   public String changePassword(@Valid @ModelAttribute("passwordDTO") PasswordDTO passwordDTO, BindingResult result,
         @PathVariable int userId, Model model, RedirectAttributes redirectAttributes) {

      // Retrieve User
      User user = userRepo.findById(userId);

      // Validate Password Input
      if (result.hasErrors() || !(pe.matches(passwordDTO.getCurrentPassword(), user.getPassword()))) {
         // Reject wrong current password
         if (!(pe.matches(passwordDTO.getCurrentPassword(), user.getPassword() ))) {
            result.reject("currentPassword", "Incorrect current password");
         }
         // Display User info using UserDTO
         UserDTO userDTO = new UserDTO();
         userDTO.setFirstName(user.getFirstName());
         userDTO.setLastName(user.getLastName());
         userDTO.setUsername(user.getUsername());
         userDTO.setPhoneNumber(user.getPhoneNumber());
         userDTO.setDriverLicense(user.getDriverLicense());

         model.addAttribute("user", user);
         model.addAttribute("userId", user.getUserID());
         model.addAttribute("username", user.getUsername());
         model.addAttribute("address", user.getAddresses().get(0));
         
         model.addAttribute("userDTO", userDTO);
         model.addAttribute("passwordError", "password error");

         logger.info(result.toString());

         return "user/profile";
      } else {
         // Update Password
         user.setPassword(pe.encode(passwordDTO.getNewPassword()));

         logger.info("\n Password changed by: " + user.getUsername());

         // Save User
         userRepo.save(user);

         // Notification Message
         String success = "Success: Password Changed";
         redirectAttributes.addFlashAttribute("success", success);

         return "redirect:/account/profile";
      }
   }

   @PostMapping("/edit-profile/{userId}")
   public String editProfile(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult result,
         @PathVariable int userId, Model model, RedirectAttributes redirectAttributes) {

      // Retrieve User
      User user = userRepo.findById(userId);

      // Check if another account uses the username
      User userExists = userRepo.findByUsername(userDTO.getUsername());

      // Username is already in use
      if (userExists != null) {
         //result.rejectValue("username", "", "Email is already in use.");
      }

      // Validate Password Input
      if (result.hasErrors()) {
         //Display User info using UserDTO

         model.addAttribute("user", user);
         model.addAttribute("userId", user.getUserID());
         model.addAttribute("username", user.getUsername());
         
         model.addAttribute("userDTO", userDTO);
         model.addAttribute("passwordDTO", new PasswordDTO());
         
         model.addAttribute("address", user.getAddresses().get(0));
         model.addAttribute("profileError", "profile error");

         logger.info(result.toString());

         return "user/profile";
      } else {
         // Send email to new username
         if (!(user.getUsername().equalsIgnoreCase(userDTO.getUsername()))) {
            emailService.sendUsernameChangeEmail(userDTO);
         }
         updateSecurityContext(userDTO);

         // Update User details
         user.setFirstName(userDTO.getFirstName());
         user.setLastName(userDTO.getLastName());
         user.setUsername(userDTO.getUsername());
         user.setPhoneNumber(userDTO.getPhoneNumber());
         user.setDriverLicense(userDTO.getDriverLicense());

         // Save User
         userRepo.save(user);

         logger.info("\n Profile updated: " + user.getUsername());

         // Notification Message
         String success = "Success: Profile Updated";
         redirectAttributes.addFlashAttribute("success", success);

         return "redirect:/account/profile";
      }
   }

   @GetMapping("/delete-account")
   public String deleteAccount(Principal principal) {
      // Find User by @PathVariable
      //User user = userRepo.findById(userID);
      User user = userRepo.findByUsername(principal.getName());

      if (!(addressRepo.findAllByUser(user) == null)) {
         // Delete the user's addresses
         addressRepo.deleteAll(addressRepo.findAllByUser(user));
      }
      logger.info("\n User deleted account: " + user.getUsername());

      // Drop User from database
      userRepo.delete(user);

      return "redirect:/logout";
   }

   // Re-authenticate user after changing username
   private void updateSecurityContext(UserDTO userDTO) {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();

      List<GrantedAuthority> updatedAuthorities = new ArrayList<>(auth.getAuthorities());
      updatedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER_ROLE")); //add your role here [e.g., new SimpleGrantedAuthority("ROLE_NEW_ROLE")]

      Authentication newAuth = new UsernamePasswordAuthenticationToken(userDTO.getUsername(),
            auth.getCredentials(), updatedAuthorities);

      SecurityContextHolder.getContext().setAuthentication(newAuth);
   }
}
//AccountController
