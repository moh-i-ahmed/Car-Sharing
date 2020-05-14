package springData.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
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

      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newPassword", "", "Enter a new password");
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "", "Enter a matching password");

      // Check password strength using Zxcvbn4j library
      Zxcvbn zxcvbn = new Zxcvbn();
      Strength passwordStrength = zxcvbn.measure(dto.getNewPassword());

      // Ensure currentPassword isn't being used again
      if (dto.getCurrentPassword() != null) {
         if ((dto.getCurrentPassword().equals(dto.getNewPassword()))
                 || (dto.getCurrentPassword().equals(dto.getConfirmPassword()))) {
            errors.rejectValue("confirmPassword", "", "Cannot use current password.");
         }
      }
      // Ensure newPassword is entered
      if ((dto.getNewPassword() != null) && (dto.getNewPassword().length() > 0)
              && (dto.getNewPassword().length() < 6)) {
         errors.rejectValue("newPassword", "", "Password must have at least 6 characters.");
      }
      // Ensure confirmPassword is entered
      if ((dto.getConfirmPassword() != null) && (dto.getConfirmPassword().length() > 0)
              && (dto.getConfirmPassword().length() < 4)) {
         errors.rejectValue("newPassword", "", "Password must have at least 6 characters.");
      }
      // Ensure Passwords match
      if ((dto.getNewPassword() != null) && (dto.getConfirmPassword() != null)) {
         if ((!dto.getNewPassword().equals(dto.getConfirmPassword()))) {
            errors.rejectValue("newPassword", "", "Password must match.");
            errors.rejectValue("confirmPassword", "", "Password must match.");
         }
      }
      // Ensure Password is strong
      if (passwordStrength.getScore() < 2) {
         errors.rejectValue("newPassword", "", "Password is too weak.");
      }
   }

}
// PasswordDTOValidator
