package springData.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
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
import springData.constants.Constants;
import springData.domain.User;
import springData.domain.VerificationToken;
import springData.repository.UserRepository;
import springData.repository.VerificationTokenRepository;
import springData.services.EmailServiceImpl;
import springData.services.StripeService;
import springData.validator.PasswordDTOValidator;
import springData.validator.UserDTOValidator;

@Controller
@RequestMapping("/account")
public class AccountController {

   private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

   BCryptPasswordEncoder pe = new BCryptPasswordEncoder();

   @Autowired private UserRepository userRepo;
   @Autowired private VerificationTokenRepository verificationTokenRepo;

   @Autowired private EmailServiceImpl emailService;
   @Autowired private StripeService stripeService;

   @InitBinder("userDTO")
   protected void initUserDTOBinder(WebDataBinder binder) {
      binder.addValidators(new UserDTOValidator());
   }

   @InitBinder("passwordDTO")
   protected void initPasswordDTOBinder(WebDataBinder binder) {
      binder.addValidators(new PasswordDTOValidator());
   }

   @GetMapping("/profile")
   public String profile(Model model, Principal principal) {
      //Retrieve logged in User
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
         model.addAttribute("address", user.getAddress());

         model.addAttribute("user", user);
         model.addAttribute("username", user.getFirstName() + " " + user.getLastName());
         model.addAttribute("userDTO", userDTO);
         model.addAttribute("passwordDTO", passwordDTO);
      }
      return "user/profile";
   }

   @GetMapping("/send-token/{userId}")
   public String sendToken(@PathVariable int userId, Model model, HttpServletRequest request,
         RedirectAttributes redirectAttributes) {

      //Retrieve user
      User user = userRepo.findById(userId);

      // Notification notificationBody
      String notificationHeader = "";
      String notificationBody = "";

      // Generate token & send email
      try {
         // Send Email
         emailService.sendRegistrationEmail(user, request);

         // Notification notificationBody
         notificationHeader = Constants.NOTIFICATION_SUCCESS;
         notificationBody = "Verification email sent";

         LOGGER.info("\n Verification email sent to " + user.getUsername());

      } catch (Exception e) {
         LOGGER.info("Exception: " + e.toString());
      }
      redirectAttributes.addFlashAttribute("notificationHeader", notificationHeader);
      redirectAttributes.addFlashAttribute("notificationBody", notificationBody);

      return "redirect:/account/profile";
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
         //userDTO.setDriverLicense(user.getDriverLicense());

         model.addAttribute("user", user);
         model.addAttribute("userId", user.getUserID());
         model.addAttribute("address", user.getAddress());

         model.addAttribute("userDTO", userDTO);
         model.addAttribute("passwordError", "password error");

         LOGGER.info(result.toString());

         return "user/profile";
      } else {
         // Update Password
         user.setPassword(pe.encode(passwordDTO.getNewPassword()));

         // Save User
         userRepo.save(user);

         // Notification notificationBody
         String notificationHeader = Constants.NOTIFICATION_SUCCESS;
         String notificationBody = "Password Changed";

         redirectAttributes.addFlashAttribute("notificationHeader", notificationHeader);
         redirectAttributes.addFlashAttribute("notificationBody", notificationBody);

         LOGGER.info("\n Password changed by: " + user.getUsername());

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

      // Email in use by another user
      if (!user.getUsername().equalsIgnoreCase(userExists.getUsername()) && userExists != null) {
         result.rejectValue("username", "", "Email is not available.");
      }
      // Validate Password Input
      if (result.hasErrors()) {
         model.addAttribute("user", user);
         model.addAttribute("userId", user.getUserID());

         model.addAttribute("userDTO", userDTO);
         model.addAttribute("passwordDTO", new PasswordDTO());

         model.addAttribute("address", user.getAddress());
         model.addAttribute("profileError", "profile error");
         model.addAttribute("username", user.getFirstName() + " " + user.getLastName());

         LOGGER.info(result.toString());

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

         LOGGER.info("\n Profile updated: " + user.getUsername());

         // Notification message
         String notificationHeader = Constants.NOTIFICATION_SUCCESS;
         String notificationBody = "Profile Updated";

         redirectAttributes.addFlashAttribute("notificationHeader", notificationHeader);
         redirectAttributes.addFlashAttribute("notificationBody", notificationBody);

         return "redirect:/account/profile";
      }
   }

   @GetMapping("/delete-account")
   public String deleteAccount(RedirectAttributes redirectAttributes, Principal principal) {
      // Find User by @PathVariable
      User user = userRepo.findByUsername(principal.getName());

      // Notification message
      String notificationHeader = "";
      String notificationBody = "";

      if (user.isActive() == true) {
         notificationHeader = Constants.NOTIFICATION_ERROR;
         notificationBody = "Can't delete account during request";

         redirectAttributes.addFlashAttribute("notificationHeader", notificationHeader);
         redirectAttributes.addFlashAttribute("notificationBody", notificationBody);

         return "redirect:/account/profile";
      } else {
         stripeService.deleteStripeCustomer(user);

         // Delete any valid token
         VerificationToken verificationToken = verificationTokenRepo.findByUser(user);
         verificationTokenRepo.delete(verificationToken);

         // Delete User from database
         userRepo.delete(user);

         LOGGER.info("\n User deleted account: " + user.getUsername());

         return "redirect:/logout";
      }
   }

   // Re-authenticate user after changing username
   private void updateSecurityContext(UserDTO userDTO) {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();

      List<GrantedAuthority> updatedAuthorities = new ArrayList<>(auth.getAuthorities());
      updatedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER_ROLE"));

      Authentication newAuth = new UsernamePasswordAuthenticationToken(userDTO.getUsername(),
            auth.getCredentials(), updatedAuthorities);

      SecurityContextHolder.getContext().setAuthentication(newAuth);
   }
}
// AccountController
