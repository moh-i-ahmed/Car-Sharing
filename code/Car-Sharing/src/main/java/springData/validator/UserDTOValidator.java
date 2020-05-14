package springData.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;

import springData.DTO.UserDTO;
import springData.constants.Constants;

public class UserDTOValidator implements Validator {

   public boolean supports(Class<?> clazz) {
      return UserDTO.class.equals(clazz);
   }

   public UserDTOValidator() {
   }

   @Override
   public void validate(Object target, Errors errors) {
      UserDTO dto = (UserDTO) target;

      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "", "Enter a First Name");
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "", "Enter a Last Name");
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "", "Username is required");

      // Ensure First Name is within minimum length
      if ((dto.getFirstName() != null) && (dto.getFirstName().length() > 0) && (dto.getFirstName().length() < 2)) {
         errors.rejectValue("firstName", "", "First name has less than 2 characters.");
      }
      // Ensure First Name is within maximum length
      if ((dto.getFirstName() != null) && (dto.getFirstName().length() > 255)) {
         errors.rejectValue("firstName", "", "First name is too long. Maximum length 255.");
      }
      // Ensure Last Name is within minimum length
      if ((dto.getLastName() != null) && (dto.getLastName().length() < 2)) {
         errors.rejectValue("lastName", "", "Last name has less than 2 characters.");
      }
      // Ensure Last Name is within maximum length
      if ((dto.getLastName() != null) && (dto.getLastName().length() > 255)) {
         errors.rejectValue("lastName", "", "Last name is too long. Maximum length 255.");
      }
      // Ensure Username is within minimum length
      if ((dto.getUsername() != null) && (dto.getUsername().length() > 0) && (dto.getUsername().length() < 6)) {
         errors.rejectValue("username", "", "Email has less than 6 characters.");
      }
      // Ensure Phone Number is valid
      if ((dto.getPhoneNumber() != null) && (!dto.getPhoneNumber().matches(Constants.REGEX_PHONE_NUMBER))) {
         errors.rejectValue("phoneNumber", "", "Invalid phone number.");
      }
      // Ensure Driver's License is within minimum length
      if ((dto.getDriverLicense() != null) && (dto.getDriverLicense().length() > 0)
              && (dto.getDriverLicense().length() < 8)) {
         errors.rejectValue("driverLicense", "", "Driver's license has less than 8 characters.");
      }
      // Ensure Driver's License is within maximum length
      if ((dto.getDriverLicense() != null && (dto.getDriverLicense().length() > 255))
              || ((dto.getDriverLicense() != null && !dto.getDriverLicense().matches(Constants.REGEX_LICENSE)))) { //MAR99740614BC2TL
         errors.rejectValue("driverLicense", "", "Invalid driver's license.");
      }
      // Ensure Password is strong
      if (dto.getPassword() != null && dto.getMatchingPassword() != null) {
         // Check password strength using Zxcvbn4j library
         Zxcvbn zxcvbn = new Zxcvbn();
         Strength passwordStrength = zxcvbn.measure(dto.getPassword());  

         if (passwordStrength.getScore() < 2) {
            errors.rejectValue("password", "", "Password is too weak.");
         }
         // Ensure Password is entered
         if ((dto.getPassword() != null) && (dto.getPassword().length() > 0) && (dto.getPassword().length() < 6)) {
            errors.rejectValue("password", "", "Password must have at least 6 characters.");
         }
         // Ensure Matching Password is entered
         if ((dto.getMatchingPassword() != null) && (dto.getMatchingPassword().length() > 0)
               && (dto.getMatchingPassword().length() < 6)) {
            errors.rejectValue("matchingPassword", "", "Password must have at least 6 characters.");
         }
         // Ensure Passwords match
         if ((dto.getPassword() != null) && (dto.getMatchingPassword() != null)
               && !(dto.getPassword().equals(dto.getMatchingPassword()))) {
            errors.rejectValue("password", "", "Password must match.");
            errors.rejectValue("matchingPassword", "", "Password must match.");
         }
      }
   }

}
// UserDTOValidator
