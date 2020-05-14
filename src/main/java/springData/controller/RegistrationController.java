package springData.controller;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import springData.DTO.PasswordDTO;
import springData.DTO.UserDTO;
import springData.constants.Constants;
import springData.domain.User;
import springData.domain.VerificationToken;
import springData.repository.RoleRepository;
import springData.repository.UserRepository;
import springData.repository.VerificationTokenRepository;
import springData.services.EmailServiceImpl;
import springData.validator.PasswordDTOValidator;
import springData.validator.UserDTOValidator;

@Controller
@RequestMapping("/register")
public class RegistrationController {

   private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationController.class);

   BCryptPasswordEncoder pe = new  BCryptPasswordEncoder();

   @Autowired private UserRepository userRepo;
   @Autowired private RoleRepository roleRepo;

   @Autowired private EmailServiceImpl emailService;
   @Autowired private VerificationTokenRepository verificationTokenRepo;

   @InitBinder("userDTO")
   protected void initUserDTOBinder(WebDataBinder binder) {
      binder.addValidators(new UserDTOValidator());
   }

   @InitBinder("passwordDTO")
   protected void initPasswordDTOBinder(WebDataBinder binder) {
      binder.addValidators(new PasswordDTOValidator());
   }

   @GetMapping()
   public String register(Model model) {
      UserDTO userDTO = new UserDTO();

      model.addAttribute("userDTO", userDTO);
      return "register";
   }

   @PostMapping("/create")
   public String createUser(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult result,
         HttpServletRequest request, Model model) {

      // Email is already in use
      User userExists = userRepo.findByUsername(userDTO.getUsername());

      if (userExists != null) {
         result.rejectValue("username", "", "Email is already in use.");

         return "register";
      }
      if (result.hasErrors()) {
         LOGGER.error("Invalid information in registration form. " + result.toString());

         return "register";
      } else {
         // Create new User using UserDTO details
         User newUser = new User();
         newUser.setFirstName(userDTO.getFirstName());
         newUser.setLastName(userDTO.getLastName());
         newUser.setUsername(userDTO.getUsername());
         //newUser.setDriverLicense(userDTO.getDriverLicense());
         newUser.setPhoneNumber(userDTO.getPhoneNumber());
         newUser.setPassword(pe.encode(userDTO.getPassword()));
         newUser.setRole(roleRepo.findByRoleName("USER"));
         newUser.setEnabled(false);

         // Save User
         userRepo.save(newUser);

         LOGGER.info("\n User registered: " + userDTO.getUsername());

         try {
            // Send Registration Email
            emailService.sendRegistrationEmail(newUser, request);

            LOGGER.info("\n Registration email sent to " + userDTO.getUsername());

         } catch (Exception e) {
            LOGGER.info("Error: " + e);
         }

         return "redirect:/";
      }
   }

   @GetMapping("/confirm/{token}")
   public String confirm(@PathVariable String token, Model model) {
      // Retrieve verification token
      VerificationToken verificationToken = verificationTokenRepo.findByToken(token);

      // Token invalid
      if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now()) || verificationToken == null) {
         // Notification message
         String notificationHeader = Constants.NOTIFICATION_ERROR;
         String notificationBody = "Link has expired. Please login and request another";

         model.addAttribute("notificationHeader", notificationHeader);
         model.addAttribute("notificationBody", notificationBody);

         return "forgot-password";
      } else {
         // Enable User Account
         User user = verificationToken.getUser();
         user.setEnabled(true);
         userRepo.save(user);

         // Delete used token
         verificationTokenRepo.delete(verificationToken);

         return "redirect:/";
      }
   }

   @GetMapping("/forgot-password")
   public String forgotPassword(Model model) {
      UserDTO userDTO = new UserDTO();
      model.addAttribute("userDTO", userDTO);

      return "forgot-password";
   }

   //Update the view
   @PostMapping("/forgot-password")
   public String resetPassword(@ModelAttribute("userDTO") UserDTO userDTO, HttpServletRequest request) {
      // Find User using UserDTO details
      User user = userRepo.findByUsername(userDTO.getUsername());

      if (user != null) {
         try {
            // Send Reset Email
            emailService.sendResetEmail(user, request);

            LOGGER.info("\n Reset email sent to " + userDTO.getUsername());
         } catch (Exception e) {
            LOGGER.info("Error: " + e.toString());
         }
         return "redirect:/";
      }
      else {
         LOGGER.info("User not found");

         return "forgot-password";
      }
   }

   @GetMapping("/reset-password/{token}")
   public String resetPassword(@PathVariable String token, Model model, RedirectAttributes redirectAttributes) {
      // Retrieve token
      VerificationToken verificationToken = verificationTokenRepo.findByToken(token);

      // Token invalid
      if (verificationToken == null || verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
         // Notification message
         String notificationHeader = Constants.NOTIFICATION_ERROR;
         String notificationBody = "Link has expired";

         redirectAttributes.addFlashAttribute("notificationHeader", notificationHeader);
         redirectAttributes.addFlashAttribute("notificationBody", notificationBody);

         return "redirect:/forgot-password";
      }
      else {
         PasswordDTO passwordDTO = new PasswordDTO();

         // Retrieve User
         model.addAttribute("passwordDTO", passwordDTO);
         return "/reset-password";
      }
   }

   @PostMapping("/reset-password/{token}")
   public String resetPassword(@Valid @ModelAttribute("passwordDTO") PasswordDTO passwordDTO, BindingResult result,
         @PathVariable String token, Model model, RedirectAttributes redirectAttributes) {

      // Notification message
      String notificationHeader = Constants.NOTIFICATION_ERROR;
      String notificationBody = "Link has expired";

      // Retrieve token
      VerificationToken verificationToken = verificationTokenRepo.findByToken(token);

      // Retrieve User
      User user = verificationToken.getUser();

      if (result.hasErrors() || (pe.matches(passwordDTO.getNewPassword(), user.getPassword()))) {
         // Reject wrong current password
         if ((pe.matches(passwordDTO.getNewPassword(), user.getPassword()))) {
            result.reject("newPassword", "This matches your current password");
         }
         model.addAttribute("passwordDTO", passwordDTO);

         LOGGER.info(result.toString());

         return "reset-password";
      } else if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
         notificationHeader = Constants.NOTIFICATION_ERROR;
         notificationBody = "Link has expired";

         redirectAttributes.addFlashAttribute("notificationHeader", notificationHeader);
         redirectAttributes.addFlashAttribute("notificationBody", notificationBody);

         return "redirect:/forgot-password";
      } else {
         // Update Password
         user.setPassword(pe.encode(passwordDTO.getNewPassword()));
         userRepo.save(user);

         // Delete used token
         verificationTokenRepo.delete(verificationToken);

         notificationHeader = Constants.NOTIFICATION_SUCCESS;
         notificationBody = "Password Reset";

         redirectAttributes.addFlashAttribute("notificationHeader", notificationHeader);
         redirectAttributes.addFlashAttribute("notificationBody", notificationBody);

         LOGGER.info("\n Password changed by: " + user.getUsername());

         return "redirect:/";
      }
   }

}
// RegistrationController
