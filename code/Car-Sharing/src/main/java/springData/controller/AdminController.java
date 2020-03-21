package springData.controller;

import java.security.Principal;
import java.util.List;

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

import springData.DTO.UserDTO;
import springData.domain.Request;
import springData.domain.Role;
import springData.domain.User;
import springData.repository.AddressRepository;
import springData.repository.RoleRepository;
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

   private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

   BCryptPasswordEncoder pe = new  BCryptPasswordEncoder();

   @Autowired UserRepository userRepo;
   @Autowired RoleRepository roleRepo;
   @Autowired CarRepository carRepo;
   @Autowired RequestRepository requestRepo;
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

   @GetMapping ("/dashboard")
   public String dashboard1(Model model, Principal principal) {

      model.addAttribute("username", principal.getName());
      model.addAttribute("userCount", userRepo.findAll().size());
      model.addAttribute("totalCars", carRepo.findAll().size());
      model.addAttribute("activeCars", carRepo.findAllInUse().size());
      model.addAttribute("requests", requestRepo.findAll().size());

      return "/admin/dashboard"; 
   }

   /* ----------------------------------------------------------
    *          USER MANAGEMENT CONTROLLERS
    * ----------------------------------------------------------*/

   @GetMapping("/createUser")
   public String createUser(Model model, Principal principal) {
      //List of Roles
      List<Role> roles = (List<Role>) roleRepo.findAll();
      UserDTO userDTO = new UserDTO();

      model.addAttribute("roles", roles);
      model.addAttribute("userDTO", userDTO);
      model.addAttribute("username", principal.getName());

      return "admin/createUser";
   }

   @PostMapping("/createUser")
   public String createUser(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult result, Model model,
         Principal principal) {

      User userExists = userRepo.findByUsername(userDTO.getUsername());

      //Username is already in use
      if (userExists != null) {
         //List of Roles
         List<Role> roles = (List<Role>) roleRepo.findAll();
         model.addAttribute("roles", roles);
         
         result.rejectValue("username", "", "Email is already in use.");
         
         return "admin/createUser";
      }

      if (result.hasErrors()) {
         //List of Roles
         List<Role> roles = (List<Role>) roleRepo.findAll();
         model.addAttribute("roles", roles);
         model.addAttribute("username", principal.getName());

         System.err.println(result);

         return "admin/createUser";
      } else {
         //Generate password
         String generatedPassword = PasswordGenerator.generateRandomPassword(8);

         //Create new User using UserDTO details
         User newUser = new User();
         newUser.setFirstName(userDTO.getFirstName());
         newUser.setLastName(userDTO.getLastName());
         newUser.setUsername(userDTO.getUsername());
         newUser.setPassword(pe.encode(generatedPassword));
         newUser.setRole(roleRepo.findByRoleName(userDTO.getRoleName()));

         //Save User
         userRepo.save(newUser);

         //Send Email
         emailService.sendRegistrationEmail(userDTO);

         logger.info("\n Admin Log: Account added for " + userDTO.getUsername() +
               "\n Account creation email sent.");

         return "redirect:/admin/view-all-users";
      }
   }

   @GetMapping("/edit-user/{userID}")
   public String editUser(@PathVariable int userID, Model model, Principal principal) {
      //Find User by ID
      User user = userRepo.findById(userID);
      //List of Roles
      List<Role> roles = (List<Role>) roleRepo.findAll();

      //Add User details to DTO
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
      //Find User by ID
      User user = userRepo.findById(userID);

      //Add User details to DTO
      UserDTO userDTO = new UserDTO();
      userDTO.setFirstName(user.getFirstName());
      userDTO.setLastName(user.getLastName());
      userDTO.setUsername(user.getUsername());
      userDTO.setRoleName(user.getRole().getRole());
      userDTO.setDriverLicense(user.getDriverLicense());
      userDTO.setPhoneNumber(user.getPhoneNumber());

      model.addAttribute("userID", userID);
      model.addAttribute("userDTO", userDTO);
      model.addAttribute("username", principal.getName());

      return "admin/view-user";
   }

   @PostMapping("/update-user/{userID}")
   public String updateUser(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult result,
         @PathVariable int userID, Model model, RedirectAttributes redirectAttributes) {

      //Find User using UserDTO details
      User user = userRepo.findById(userID);

      User userExists = userRepo.findByUsername(userDTO.getUsername());

      //Username is already in use
      if (!(userDTO.getUsername().equalsIgnoreCase(user.getUsername())) && (userExists != null)) {
         //List of Roles
         List<Role> roles = (List<Role>) roleRepo.findAll();
         model.addAttribute("roles", roles);
         
         result.rejectValue("username", "", "Email is already in use.");

         return "admin/edit-user";
      }

      if (result.hasErrors()) {
         System.err.println(result);
         return "admin/edit-user";
      } 
      else {
         //Update User using UserDTO details
         user.setFirstName(userDTO.getFirstName());
         user.setLastName(userDTO.getLastName());
         user.setUsername(userDTO.getUsername());
         user.setRole(roleRepo.findByRoleName(userDTO.getRoleName()));

         //Save User
         userRepo.save(user);

         // Notification Message
         String message = "Success: Car Updated";
         redirectAttributes.addFlashAttribute("message", message);

         logger.info("\n Admin Log: Account updated for " + userDTO.getUsername() +
               "\n Email sent.");

         return "redirect:/admin/view-all-users";
      }
   }

   @GetMapping("/reset-password/{userID}")
   public String resetPassword(@PathVariable int userID, Model model) {
      //Generate password
      String generatedPassword = PasswordGenerator.generateRandomPassword(8);

      //Find User by ID
      User user = userRepo.findById(userID);

      UserDTO userDTO = new UserDTO();
      userDTO.setFirstName(user.getFirstName());
      userDTO.setPassword(generatedPassword);
      userDTO.setUsername(user.getUsername());

      //Send Email
      // try {
      emailService.sendResetEmail(userDTO);
      // }finally {

      //Save User
      user.setPassword(pe.encode(generatedPassword));   
      userRepo.save(user);

      logger.info("\n Admin Log: Password reset for: " + user.getUsername() +
            "\n Reset email sent.");

      return "redirect:/admin/view-all-users";
   }

   @GetMapping("/view-all-users")
   public String viewAllUsers(Model model, Principal principal) {
      //List of Users
      List<User> users = (List<User>) userRepo.findAll();

      model.addAttribute("users", users);
      model.addAttribute("username", principal.getName());

      return "admin/view-all-users";
   }

   @GetMapping("/delete-user/{userID}")
   public String deleteUser(@PathVariable int userID) {
      //Find User by @PathVariable
      User user = userRepo.findById(userID);

      if (!(addressRepo.findAllByUser(user) == null)) {
         //Delete the user's addresses
         addressRepo.deleteAll(addressRepo.findAllByUser(user));
      }
      //Drop User from database
      userRepo.delete(user);

      logger.info("\n Admin Log: " + user.getUsername() + " deleted by Admin.");

      return "redirect:/admin/view-all-users";
   }

   /* ----------------------------------------------------------
    *          REQUEST MANAGEMENT CONTROLLERS
    * ----------------------------------------------------------*/

   @GetMapping("/view-request/{requestID}")
   public String viewRequestDetails(@PathVariable int requestID, Model model, Principal principal) {
      //Find Request by ID
      Request request = requestRepo.findById(requestID);

      model.addAttribute("request", request);
      model.addAttribute("username", principal.getName());

      return "/admin/view-request";
   }

   @GetMapping("/view-all-requests")
   public String viewAllRequests(Model model, Principal principal) {
      //List of Requests
      List<Request> requests = (List<Request>) requestRepo.findAll();

      model.addAttribute("requests", requests);
      model.addAttribute("username", principal.getName());

      return "admin/view-all-requests";
   }

}
//AdminController
