package springData.controller;

import java.security.Principal;

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
import springData.repository.UserRepository;

@Controller
@RequestMapping("/account")
public class AccountController {

   BCryptPasswordEncoder pe = new  BCryptPasswordEncoder();

   @Autowired UserRepository userRepo;

   @InitBinder("passwordDTO")
   protected void initPasswordDTOBinder(WebDataBinder binder) {
      binder.addValidators(new PasswordDTOValidator());
   }

   @GetMapping
   public String account(Model model, Principal principal) {
      //Find User
      User user = userRepo.findByUsername(principal.getName());

      //Display User info using UserDTO
      UserDTO userDTO = new UserDTO();

      userDTO.setFirstName(user.getFirstName());
      userDTO.setLastName(user.getLastName());
      userDTO.setUsername(user.getUsername());
      userDTO.setRoleName(user.getRole().getRole());
      userDTO.setOrganizationName(user.getOrganization().getName());

      model.addAttribute("userId", user.getUserId());
      model.addAttribute("userDTO", userDTO);

      return "account/account-details";
   }

   @GetMapping("/change-password/{userId}")
   public String changePassword(@PathVariable int userId, Model model) {
      PasswordDTO passwordDTO = new PasswordDTO();

      model.addAttribute("userId", userId);
      model.addAttribute("passwordDTO", passwordDTO);

      return "account/change-password";
   }

   @PostMapping(value = "/change-password/submit/{userId}")
   public String changePassword(@Valid @ModelAttribute("passwordDTO") PasswordDTO passwordDTO, BindingResult result,
           @PathVariable int userId) {

      if (result.hasErrors()) {
         return "account/change-password";
      } else {
         //Create new User using UserDTO details
         User user = userRepo.findById(userId);
         user.setPassword(pe.encode(passwordDTO.getPassword()));

         //Save User
         userRepo.save(user);

         return "redirect:/account";
      }
   }

}
//AccountController
