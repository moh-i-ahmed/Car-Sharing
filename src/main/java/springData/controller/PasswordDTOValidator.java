package springData.controller;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import springData.DTO.PasswordDTO;

public class PasswordDTOValidator implements Validator {

   public boolean supports(Class<?> clazz) {
      return PasswordDTO.class.equals(clazz);
   }

   public PasswordDTOValidator() {
   }

   @Override
   public void validate(Object target, Errors errors) {
      PasswordDTO dto = (PasswordDTO) target;

      //Ensure currentPassword isn't being used again
      if ((dto.getCurrentPassword().equals(dto.getNewPassword())) || (dto.getCurrentPassword().equals(dto.getConfirmPassword()))) {
         errors.rejectValue("confirmPassword", "", "Cannot use current password.");
      }
      //Ensure newPassword is entered
      if ((dto.getNewPassword() != null) && (dto.getNewPassword().length() > 0) && (dto.getNewPassword().length() < 4)) {
         errors.rejectValue("newPassword", "", "Password must have more than 4 characters.");
      }
      //Ensure Password is entered
      if ((dto.getConfirmPassword() != null) && (dto.getConfirmPassword().length() > 0)
            && (dto.getConfirmPassword().length() < 4)) {
         errors.rejectValue("newPassword", "", "Password must have more than 4 characters.");
      }
      //Ensure Passwords match
      if ((dto.getNewPassword() != null) && (dto.getConfirmPassword() != null)) {
         if ((!dto.getNewPassword().equals(dto.getConfirmPassword()))) {
            errors.rejectValue("newPassword", "", "Password must match.");
            errors.rejectValue("confirmPassword", "", "Password must match.");
         }
      }
   }

}
//PasswordDTOValidator
