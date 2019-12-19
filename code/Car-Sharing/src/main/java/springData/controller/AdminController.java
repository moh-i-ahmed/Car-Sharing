package springData.controller;

import java.security.Principal;
import java.util.List;

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

import springData.DTO.PasswordDTO;
import springData.DTO.UserDTO;
import springData.domain.Role;
import springData.domain.User;
import springData.repository.AddressRepository;
import springData.repository.RoleRepository;
import springData.repository.UserRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

   BCryptPasswordEncoder pe = new  BCryptPasswordEncoder();

   @Autowired UserRepository userRepo;
   @Autowired RoleRepository roleRepo;
   @Autowired AddressRepository addressRepo;

   @InitBinder("userDTO")
   protected void initUserDTOBinder(WebDataBinder binder) {
      binder.addValidators(new UserDTOValidator(userRepo));
   }

   @InitBinder("passwordDTO")
   protected void initPasswordDTOBinder(WebDataBinder binder) {
      binder.addValidators(new PasswordDTOValidator());
   }

   @RequestMapping("/createUser")
   public String createUser(Model model, Principal principal) {
      //List of Roles
      List<Role> roles = (List<Role>) roleRepo.findAll();
      UserDTO userDTO = new UserDTO();

      model.addAttribute("roles", roles);
      model.addAttribute("userDTO", userDTO);

      return "admin/createUser";
   }

   @PostMapping(value = "/createUser/create")
   public String createUser(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult result, Model model) {

      if (result.hasErrors()) {
         //List of Roles
         List<Role> roles = (List<Role>) roleRepo.findAll();
         model.addAttribute("roles", roles);

         return "admin/createUser";
      } else {
         //Create new User using UserDTO details
         User newUser = new User();
         newUser.setFirstName(userDTO.getFirstName());
         newUser.setLastName(userDTO.getLastName());
         newUser.setUsername(userDTO.getUsername());
         newUser.setPassword(pe.encode(userDTO.getPassword()));
         newUser.setRole(roleRepo.findByRoleName(userDTO.getRoleName()));

         //Save User
         userRepo.save(newUser);

         return "redirect:/dashboard";
      }
   }

   @GetMapping("/edit-user/{userID}")
   public String editUser(@PathVariable int userID, Model model) {
      //Find User by ID
      User user = userRepo.findById(userID);

      //Add User details to DTO
      UserDTO userDTO = new UserDTO();
      userDTO.setFirstName(user.getFirstName());
      userDTO.setLastName(user.getLastName());
      userDTO.setUsername(user.getUsername());
      userDTO.setPassword(user.getPassword());
      userDTO.setRoleName(user.getRole().getRole());

      //List of Roles
      List<Role> roles = (List<Role>) roleRepo.findAll();

      model.addAttribute("roles", roles);
      model.addAttribute("userID", userID);
      model.addAttribute("userDTO", userDTO);

      return "admin/edit-user";
   }

   @GetMapping("/view-user/{userID}")
   public String viewUser(@PathVariable int userID, Model model) {
      //Find User by ID
      User user = userRepo.findById(userID);

      //Add User details to DTO
      UserDTO userDTO = new UserDTO();
      userDTO.setFirstName(user.getFirstName());
      userDTO.setLastName(user.getLastName());
      userDTO.setUsername(user.getUsername());
      userDTO.setPassword(user.getPassword());
      userDTO.setRoleName(user.getRole().getRole());

      //List of Roles
      List<Role> roles = (List<Role>) roleRepo.findAll();

      model.addAttribute("roles", roles);
      model.addAttribute("userID", userID);
      model.addAttribute("userDTO", userDTO);

      return "admin/view-user";
   }

   @PostMapping(value = "/update-user/{userID}")
   public String updateUser(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult result,
           @PathVariable int userID, Model model, RedirectAttributes redirectAttributes) {

      User user = userRepo.findById(userID);
      
      if (result.hasErrors()) {
         //List of Roles
         //List<Role> roles = (List<Role>) roleRepo.findAll();
         System.err.println(result);
         //userDTO.setRoleName(user.getRole().getRole());
         //model.addAttribute("roles", roles);

         return "admin/edit-user";
      } else {
         //Create new User using UserDTO details
         //User user = userRepo.findById(userID);
         user.setFirstName(userDTO.getFirstName());
         user.setLastName(userDTO.getLastName());
         user.setUsername(userDTO.getUsername());
         //user.setPassword(pe.encode(userDTO.getPassword()));
         //user.setRole(roleRepo.findByRoleName(userDTO.getRoleName()));

         //Save User
         userRepo.save(user);
         
         redirectAttributes.addFlashAttribute("message", "User profile updated");
         redirectAttributes.addFlashAttribute("alertClass", "alert-success");

         return "redirect:/admin/view-all-users";
      }
   }

   @RequestMapping("/reset-password/{userID}")
   public String resetPassword(@PathVariable int userID, Model model) {
      PasswordDTO passwordDTO = new PasswordDTO();

      model.addAttribute("userID", userID);
      model.addAttribute("passwordDTO", passwordDTO);

      return "admin/reset-password";
   }

   @PostMapping(value = "/reset-password/submit/{userID}")
   public String resetPassword(@Valid @ModelAttribute("passwordDTO") PasswordDTO passwordDTO, BindingResult result,
           @PathVariable int userID, Model model) {

      if (result.hasErrors()) {
         return "admin/reset-password";
      } else {
         //Create new User using UserDTO details
         User user = userRepo.findById(userID);
         user.setPassword(pe.encode(passwordDTO.getPassword()));

         //Save User
         userRepo.save(user);

         return "redirect:/admin/view-all-users";
      }
   }

   @RequestMapping("/view-all-users")
   public String viewAllUsers(Model model) {
      //List of Users
      List<User> users = (List<User>) userRepo.findAll();

      model.addAttribute("users", users);

      return "admin/view-all-users";
   }

   @GetMapping("/delete-user/{userID}")
   public String deleteUser(@PathVariable int userID) {
      //Find User by @PathVariable
      User user = userRepo.findById(userID);

      //Delete the user's addresses
      addressRepo.deleteAll(addressRepo.findAllByUser(user));

      //Drop User from database
      userRepo.delete(user);

      return "redirect:/admin/view-all-users";
   }

}
//AdminController