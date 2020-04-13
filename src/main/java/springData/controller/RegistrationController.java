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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import springData.DTO.UserDTO;
import springData.constants.Constants;
import springData.domain.User;
import springData.domain.VerificationToken;
import springData.repository.RoleRepository;
import springData.repository.UserRepository;
import springData.repository.VerificationTokenRepository;
import springData.services.EmailServiceImpl;
import springData.utils.PasswordGenerator;
import springData.validator.PasswordDTOValidator;
import springData.validator.UserDTOValidator;

@Controller
@RequestMapping("/register")
public class RegistrationController {

   private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

   BCryptPasswordEncoder pe = new  BCryptPasswordEncoder();

   @Autowired private UserRepository userRepo;
   @Autowired private RoleRepository roleRepo;
   @Autowired private VerificationTokenRepository verificationTokenRepo;

   @Autowired private EmailServiceImpl emailService;

   @InitBinder("userDTO")
   protected void initUserDTOBinder(WebDataBinder binder) {
      binder.addValidators(new UserDTOValidator(userRepo));
   }

   @InitBinder("passwordDTO")
   protected void initPasswordDTOBinder(WebDataBinder binder) {
      binder.addValidators(new PasswordDTOValidator());
   }

   @GetMapping()
   public String register(Model model) {
      //List of Roles
      UserDTO userDTO = new UserDTO();

      model.addAttribute("userDTO", userDTO);
      return "register";
   }

   @PostMapping("/create")
   public String createUser(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult result,
         HttpServletRequest request, Model model) {

      //Check if username is already in use
      User userExists = userRepo.findByUsername(userDTO.getUsername());

      if (userExists != null) {
         result.rejectValue("username", "", "Email is already in use.");

         return "register";
      }
      if (result.hasErrors()) {
         logger.error("Invalid information in registration form");
         System.err.println(result);

         return "register";
      } else {
         //Create new User using UserDTO details
         User newUser = new User();
         newUser.setFirstName(userDTO.getFirstName());
         newUser.setLastName(userDTO.getLastName());
         newUser.setUsername(userDTO.getUsername());
         newUser.setDriverLicense(userDTO.getDriverLicense());
         newUser.setPassword(pe.encode(userDTO.getPassword()));
         newUser.setEnabled(false);

         newUser.setRole(roleRepo.findByRoleName("USER"));

         //Save User
         userRepo.save(newUser);
         try {
            VerificationToken token = new VerificationToken(newUser);
            verificationTokenRepo.save(token);

            String appUrl = request.getScheme() + Constants.SERVER_URL + "register/confirm/" + token.getToken();
            System.err.println("App url: " + appUrl);
            //Send Email
            emailService.sendRegistrationEmail(userDTO, appUrl);
         } catch (Exception e) {
            logger.info("Error: " + e);
         }
         logger.info("\n User registered: " + userDTO.getUsername()
         +"\n Registration email sent.");

         return "redirect:/";
      }
   }

   @GetMapping("/confirm/{token}")
   public String confirm(@PathVariable String token, Model model) {
      // Retrieve token
      VerificationToken verificationToken = verificationTokenRepo.findByToken(token);

      // Token invalid
      if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
         // Notification message
         String message = "Error: Link has expired";
         model.addAttribute("message", message);

         return "forgot-password";
      }
      else {
         // Enable User Account
         User user = verificationToken.getUser();
         user.setEnabled(true);

         // Save changes
         userRepo.save(user);

         return "redirect:/";
      }
   }

   @GetMapping("/forgot-password")
   public String forgotPassword(Model model) {
      UserDTO userDTO = new UserDTO();
      model.addAttribute("userDTO", userDTO);

      return "forgot-password";
   }

   //TODO change this to POST
   //Update the view
   @GetMapping("/forgot-password/submit")
   public String resetPassword(@ModelAttribute("userDTO") UserDTO userDTO) {

      // Find User using UserDTO details
      User user = userRepo.findByUsername(userDTO.getUsername());

      if (user != null) {

         // Generate password
         String generatedPassword = PasswordGenerator.generateRandomPassword(8);
         System.err.println(generatedPassword);

         userDTO.setFirstName(user.getFirstName());
         userDTO.setPassword(generatedPassword);

         // Send Email
         emailService.sendResetEmail(userDTO);

         logger.info("\n Password reset for: " + userDTO.getUsername()
         + "\n Reset email sent.");

         // Save User
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
//RegistrationController
