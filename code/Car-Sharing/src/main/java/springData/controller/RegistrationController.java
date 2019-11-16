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

import springData.DTO.PasswordDTO;
import springData.DTO.UserDTO;
import springData.domain.User;
import springData.repository.RoleRepository;
import springData.repository.UserRepository;

@Controller
@RequestMapping("/register")
public class RegistrationController {

   BCryptPasswordEncoder pe = new  BCryptPasswordEncoder();

   @Autowired UserRepository userRepo;
   @Autowired RoleRepository roleRepo;

   @InitBinder("userDTO")
   protected void initUserDTOBinder(WebDataBinder binder) {
      binder.addValidators(new UserDTOValidator(userRepo));
   }

   @InitBinder("passwordDTO")
   protected void initPasswordDTOBinder(WebDataBinder binder) {
      binder.addValidators(new PasswordDTOValidator());
   }

   @GetMapping()
   public String register(Model model, Principal principal) {
      //List of Roles
      UserDTO userDTO = new UserDTO();

      model.addAttribute("userDTO", userDTO);
      return "register";
   }

   @PostMapping(value = "/create")
   public String createUser(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult result, Model model) {

      if (result.hasErrors()) {
         return "register";
      } else {
         //Create new User using UserDTO details
         User newUser = new User();
         newUser.setFirstName(userDTO.getFirstName());
         newUser.setLastName(userDTO.getLastName());
         newUser.setUsername(userDTO.getUsername());
         newUser.setPassword(pe.encode(userDTO.getPassword()));
         newUser.setRole(roleRepo.findByRoleName("USER"));

         //Save User
         userRepo.save(newUser);

         return "/login";
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

      //Drop User from database
      userRepo.delete(user);

      return "redirect:/admin/view-all-users";
   }

}
//AdminController
