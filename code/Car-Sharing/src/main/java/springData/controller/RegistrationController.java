package springData.controller;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import springData.DTO.UserDTO;
import springData.domain.User;
import springData.repository.RoleRepository;
import springData.repository.UserRepository;
import springData.services.EmailServiceImpl;

@Controller
@RequestMapping("/register")
public class RegistrationController {

   Logger logger = LoggerFactory.getLogger(RegistrationController.class);

   BCryptPasswordEncoder pe = new  BCryptPasswordEncoder();

   @Autowired UserRepository userRepo;
   @Autowired RoleRepository roleRepo;
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

   @PostMapping(value = "/create")
   public String createUser(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult result, Model model) {

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
         newUser.setPassword(pe.encode(userDTO.getPassword()));

         newUser.setRole(roleRepo.findByRoleName("USER"));

         //Save User
         userRepo.save(newUser);

         //Send Email
         emailService.sendRegistrationEmail(userDTO);

         logger.info("\n User registered: " + userDTO.getUsername()
         +"\n Registration email sent.");

         return "redirect:/";
      }
   }

}
//RegistrationController
