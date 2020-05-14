package springData.controller;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import springData.DTO.UserDTO;
import springData.constants.Constants;
import springData.domain.Request;
import springData.domain.Role;
import springData.domain.StripeCustomer;
import springData.domain.User;
import springData.repository.RoleRepository;
import springData.repository.StripeCustomerRepository;
import springData.repository.CarRepository;
import springData.repository.RequestRepository;
import springData.repository.UserRepository;
import springData.services.EmailServiceImpl;
import springData.utils.PasswordGenerator;
import springData.validator.PasswordDTOValidator;
import springData.validator.UserDTOValidator;

@Controller
@RequestMapping("/admin")
public class AdminController {

   private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

   BCryptPasswordEncoder pe = new  BCryptPasswordEncoder();

   @Autowired private UserRepository userRepo;
   @Autowired private RoleRepository roleRepo;
   @Autowired private CarRepository carRepo;
   @Autowired private RequestRepository requestRepo;
   @Autowired private StripeCustomerRepository stripeCustomerRepo;

   @Autowired private EmailServiceImpl emailService;

   @InitBinder("userDTO")
   protected void initUserDTOBinder(WebDataBinder binder) {
      binder.addValidators(new UserDTOValidator());
   }

   @InitBinder("passwordDTO")
   protected void initPasswordDTOBinder(WebDataBinder binder) {
      binder.addValidators(new PasswordDTOValidator());
   }

   @GetMapping ("/dashboard")
   public String dashboard(Model model, Principal principal) throws JsonProcessingException {
      // Parse query result to JSON
      ObjectMapper mapper = new ObjectMapper();
      String carLocations = mapper.writeValueAsString(carRepo.findAllCarLocations());

      model.addAttribute("username", principal.getName());
      model.addAttribute("userCount", userRepo.count());

      model.addAttribute("verifiedUsers", userRepo.findAllVerified());
      model.addAttribute("activeRequests", requestRepo.countByStatus(Constants.REQUEST_STATUS_IN_PROGRESS));
      model.addAttribute("completedRequests", requestRepo.countByStatus(Constants.REQUEST_STATUS_COMPLETE));
      model.addAttribute("cancelledRequests", requestRepo.countByStatus(Constants.REQUEST_STATUS_CANCELLED));
      model.addAttribute("unfulfilledRequests", requestRepo.countByStatus(null));

      model.addAttribute("locations", carLocations);
      model.addAttribute("totalCars", carRepo.count());
      model.addAttribute("activeCars", carRepo.findAllInUse());
      model.addAttribute("requests", requestRepo.count());

      return "/admin/dashboard";
   }

   /* ----------------------------------------------------------
    *          USER MANAGEMENT CONTROLLERS
    * ----------------------------------------------------------*/

   @GetMapping("/createUser")
   public String createUser(Model model, Principal principal) {
      // List of Roles
      List<Role> roles = (List<Role>) roleRepo.findAll();
      UserDTO userDTO = new UserDTO();

      model.addAttribute("roles", roles);
      model.addAttribute("userDTO", userDTO);
      model.addAttribute("username", principal.getName());

      return "admin/createUser";
   }

   @PostMapping("/createUser")
   public String createUser(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult result, Model model,
         HttpServletRequest request, Principal principal) {

      // Email is already in use
      User userExists = userRepo.findByUsername(userDTO.getUsername());

      if (userExists != null) {
         // List of Roles
         List<Role> roles = (List<Role>) roleRepo.findAll();
         model.addAttribute("roles", roles);

         result.rejectValue("username", "", "Email is already in use.");

         return "admin/createUser";
      }

      if (result.hasErrors()) {
         // List of Roles
         List<Role> roles = (List<Role>) roleRepo.findAll();
         model.addAttribute("roles", roles);
         model.addAttribute("username", principal.getName());

         return "admin/createUser";
      } else {
         // Generate password
         String generatedPassword = PasswordGenerator.generateRandomPassword(8);

         // Create new User using UserDTO details
         User newUser = new User();
         newUser.setFirstName(userDTO.getFirstName());
         newUser.setLastName(userDTO.getLastName());
         newUser.setUsername(userDTO.getUsername());
         newUser.setPassword(pe.encode(generatedPassword));
         newUser.setRole(roleRepo.findByRoleName(userDTO.getRoleName()));

         // Save User
         userRepo.save(newUser);

         try {
            // Send Email
            emailService.sendRegistrationEmail(newUser, request);
         } catch (Exception e) {
            LOGGER.info("Error: " + e);
         }

         LOGGER.info("\n Admin Log: Account added for " + userDTO.getUsername() +
               "\n Account creation email sent.");

         return "redirect:/admin/view-all-users";
      }
   }

   @GetMapping("/edit-user/{userID}")
   public String editUser(@PathVariable int userID, Model model, Principal principal) {
      // Find User by ID
      User user = userRepo.findById(userID);

      // List of Roles
      List<Role> roles = (List<Role>) roleRepo.findAll();

      // Add User details to DTO
      UserDTO userDTO = new UserDTO();
      userDTO.setFirstName(user.getFirstName());
      userDTO.setLastName(user.getLastName());
      userDTO.setUsername(user.getUsername());
      userDTO.setDriverLicense(user.getDriverLicense());
      userDTO.setPhoneNumber(user.getPhoneNumber());
      userDTO.setRoleName(user.getRole().getRole());

      model.addAttribute("userID", userID);
      model.addAttribute("userDTO", userDTO);
      model.addAttribute("roles", roles);
      model.addAttribute("username", principal.getName());

      return "admin/edit-user";
   }

   @GetMapping("/view-user/{userID}")
   public String viewUser(@PathVariable int userID, Model model, Principal principal) {
      // Find User by ID
      User user = userRepo.findById(userID);

      // Add User details to DTO
      UserDTO userDTO = new UserDTO();
      userDTO.setFirstName(user.getFirstName());
      userDTO.setLastName(user.getLastName());
      userDTO.setUsername(user.getUsername());
      userDTO.setRoleName(user.getRole().getRole());
      userDTO.setDriverLicense(user.getDriverLicense());
      userDTO.setPhoneNumber(user.getPhoneNumber());

      model.addAttribute("userID", userID);
      model.addAttribute("stripeCustomerID", user.getStripeCustomer().getTokenID());
      model.addAttribute("userDTO", userDTO);
      model.addAttribute("username", principal.getName());

      return "admin/view-user";
   }

   @PostMapping("/update-user/{userID}")
   public String updateUser(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult result,
         @PathVariable int userID, Model model, RedirectAttributes redirectAttributes) {

      // Find User using UserDTO details
      User user = userRepo.findById(userID);

      // Email is already in use
      User userExists = userRepo.findByUsername(userDTO.getUsername());

      if (!(userDTO.getUsername().equalsIgnoreCase(user.getUsername())) && (userExists != null)) {
         result.rejectValue("username", "", "Email already in use.");

         // List of Roles
         List<Role> roles = (List<Role>) roleRepo.findAll();
         model.addAttribute("roles", roles);

         return "admin/edit-user";
      }

      if (result.hasErrors()) {
         // List of Roles
         List<Role> roles = (List<Role>) roleRepo.findAll();
         model.addAttribute("roles", roles);

         return "admin/edit-user";
      } else {
         // Update User using UserDTO details
         user.setFirstName(userDTO.getFirstName());
         user.setLastName(userDTO.getLastName());
         user.setUsername(userDTO.getUsername());
         user.setRole(roleRepo.findByRoleName(userDTO.getRoleName()));
         userRepo.save(user);

         // Notification Message
         String messageCode = Constants.NOTIFICATION_SUCCESS;
         String message = "Car Updated";

         redirectAttributes.addFlashAttribute("messageCode", messageCode);
         redirectAttributes.addFlashAttribute("message", message);

         LOGGER.info("\n Admin Log: Account updated for " + userDTO.getUsername() +
               "\n Email sent.");

         return "redirect:/admin/view-all-users";
      }
   }

   @GetMapping("/reset-password/{userID}")
   public String resetPassword(@PathVariable int userID, Model model, HttpServletRequest request) {
      // Find User by ID
      User user = userRepo.findById(userID);

      UserDTO userDTO = new UserDTO();
      userDTO.setFirstName(user.getFirstName());
      userDTO.setUsername(user.getUsername());

      try {
         // Send Email
         emailService.sendResetEmail(user, request);

         LOGGER.info("\n Reset email sent to " + userDTO.getUsername());
      } catch (Exception e) {
         LOGGER.info("Error: " + e.toString());
      }

      //Save User
      userRepo.save(user);

      LOGGER.info("\n Admin Log: Password reset for: " + user.getUsername()
      + "\n Reset email sent.");

      return "redirect:/admin/view-all-users";
   }

   @GetMapping("/view-all-users")
   public String viewAllUsers(Model model, Principal principal) {
      // List of Users
      List<User> users = (List<User>) userRepo.findAll();

      model.addAttribute("users", users);
      model.addAttribute("username", principal.getName());

      return "admin/view-all-users";
   }

   @GetMapping("/delete-user/{userID}")
   public String deleteUser(@PathVariable int userID, RedirectAttributes redirectAttributes) {
      // Find User by @PathVariable
      User user = userRepo.findById(userID);

      // Delete User from database
      userRepo.delete(user);

      LOGGER.info("\n Admin Log: " + user.getUsername() + " deleted by Admin.");

      return "redirect:/admin/view-all-users";
   }

   /* ----------------------------------------------------------
    *          REQUEST MANAGEMENT CONTROLLERS
    * ----------------------------------------------------------*/

   @GetMapping("/view-request/{requestID}")
   public String viewRequestDetails(@PathVariable int requestID, Model model, Principal principal) {
      // Find Request by ID
      Request request = requestRepo.findById(requestID);

      // Request Duration
      LocalTime duration = LocalTime.MIN.plus(Duration.ofMinutes
            (ChronoUnit.MINUTES.between(request.getStartTime(), request.getEndTime())));

      model.addAttribute("baseCharge", Constants.PRICE_BASE_CHARGE / 100);
      model.addAttribute("duration", duration);
      model.addAttribute("request", request);
      model.addAttribute("username", principal.getName());

      return "/admin/view-request";
   }

   @GetMapping("/view-all-requests")
   public String viewAllRequests(Model model, Principal principal) {
      // List of Requests
      List<Request> requests = (List<Request>) requestRepo.findAll();

      model.addAttribute("requests", requests);
      model.addAttribute("username", principal.getName());

      return "admin/view-all-requests";
   }

   /* ----------------------------------------------------------
    *          STRIPE DASHBOARD CONTROLLER
    * ----------------------------------------------------------*/

   @GetMapping("/stripe-dashboard")
   public String stripedashboard(Model model, Principal principal) {
      // List of Requests
      List<Request> requests = (List<Request>) requestRepo.findAll();
      List<StripeCustomer> stripeCustomers = (List<StripeCustomer>) stripeCustomerRepo.findAll();

      model.addAttribute("requests", requests);
      model.addAttribute("stripeCustomers", stripeCustomers);
      model.addAttribute("username", principal.getName());

      return "admin/stripe-dashboard";
   }

}
// AdminController
