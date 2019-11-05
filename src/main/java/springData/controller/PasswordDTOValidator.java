package springData.controller;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import springData.DTO.PasswordDTO;

public class PasswordDTOValidator implements Validator {

   public boolean supports(Class<?> clazz) {
      return PasswordDTO.class.equals(clazz);
   }

   @Override
   public void validate(Object target, Errors errors) {
      PasswordDTO dto = (PasswordDTO) target;

      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "", "Please enter a password");
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "", "Please enter a password");

      //Ensure Password is entered
      if ((dto.getPassword() != null) && (dto.getPassword().length() > 0) && (dto.getPassword().length() < 4)) {
         errors.rejectValue("password", "", "Password must have more than 4 characters.");
      }
      //Ensure Password is entered
      if ((dto.getConfirmPassword() != null) && (dto.getConfirmPassword().length() > 0)
              && (dto.getConfirmPassword().length() < 4)) {
         errors.rejectValue("password", "", "Password must have more than 4 characters.");
      }
      //Ensure Password is accurate
      if ((dto.getPassword() != null) && (dto.getConfirmPassword() != null)) {
         if ((!dto.getPassword().equals(dto.getConfirmPassword()))) {
            errors.rejectValue("password", "", "Password must match.");
            errors.rejectValue("confirmPassword", "", "Password must match.");
         }
      }
   }

}
//UserDTOValidator
