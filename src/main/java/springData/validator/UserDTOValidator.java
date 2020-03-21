package springData.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import springData.DTO.UserDTO;
import springData.repository.UserRepository;

public class UserDTOValidator implements Validator {

   public boolean supports(Class<?> clazz) {
      return UserDTO.class.equals(clazz);
   }

   private UserRepository userRepo;

   public UserDTOValidator(UserRepository userRepo) {
      this.userRepo = userRepo;
   }

   @Override
   public void validate(Object target, Errors errors) {
      UserDTO dto = (UserDTO) target;

      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "", "Enter a First Name");
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "", "Enter a Last Name");
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "", "Username is required");

      //Ensure First Name is within minimum length
      if ((dto.getFirstName() != null) && (dto.getFirstName().length() > 0) && (dto.getFirstName().length() < 2)) {
         errors.rejectValue("firstName", "", "First name has less than 2 characters.");
      }
      //Ensure First Name is within maximum length
      if ((dto.getFirstName() != null) && (dto.getFirstName().length() > 0) && (dto.getFirstName().length() > 255)) {
         errors.rejectValue("firstName", "", "First name is too long. Maximum length 255.");
      }
      //Ensure Last Name is within minimum length
      if ((dto.getLastName() != null) && (dto.getLastName().length() > 0) && (dto.getLastName().length() < 2)) {
         errors.rejectValue("lastName", "", "Last name has less than 2 characters.");
      }
      //Ensure Last Name is within maximum length
      if ((dto.getLastName() != null) && (dto.getLastName().length() > 0) && (dto.getLastName().length() > 255)) {
         errors.rejectValue("lastName", "", "Last name is too long. Maximum length 255.");
      }
      //Ensure Username is within minimum length
      if ((dto.getUsername() != null) && (dto.getUsername().length() > 0) && (dto.getUsername().length() < 6)) {
         errors.rejectValue("username", "", "Username has less than 4 characters.");
      }
      //Ensure Password is entered
      if ((dto.getPassword() != null) && (dto.getPassword().length() > 0) && (dto.getPassword().length() < 4)) {
         errors.rejectValue("password", "", "Password must have more than 4 characters.");
         System.out.println("Password error");
      }
      //Ensure Matching Password is entered
      if ((dto.getMatchingPassword() != null) && (dto.getMatchingPassword().length() > 0)
              && (dto.getMatchingPassword().length() < 4)) {
         errors.rejectValue("matchingPassword", "", "Password must have more than 4 characters.");
         System.out.println("Password error");
      }
      //Ensure Passwords match
      if ((dto.getPassword() != null) && (dto.getMatchingPassword() != null) && !(dto.getPassword().equals(dto.getMatchingPassword()))) {
         errors.rejectValue("password", "", "Password must match.");
         errors.rejectValue("matchingPassword", "", "Password must match.");
      }
   }

}
//UserDTOValidator
