package springData.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import springData.DTO.CarDTO;

public class CarDTOValidator implements Validator {

   public boolean supports(Class<?> clazz) {
      return CarDTO.class.equals(clazz);
   }

   public CarDTOValidator() {
   }

   @Override
   public void validate(Object target, Errors errors) {
      CarDTO dto = (CarDTO) target;

      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "registrationNumber", "", "Enter a Registration Number");
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "carName", "", "Enter a Brand/Name");
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "carMake", "", "Enter a Model/Make");
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "carColor", "", "Enter a color");
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "isActive", "", "Select a status");
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fuelLevel", "", "Enter a fuel level");

      //Ensure Registration Number is valid
      if ((dto.getRegistrationNumber() != null) && (dto.getRegistrationNumber().length() > 0) && (dto.getRegistrationNumber().length() < 6)) {
         errors.rejectValue("registrationNumber", "", "Registration number has less than 2 characters.");
      }
      //Ensure Registration Number is valid
      if ((dto.getRegistrationNumber() != null) && (dto.getRegistrationNumber().length() > 0) && (dto.getRegistrationNumber().length() > 10)) {
         errors.rejectValue("registrationNumber", "", "Registration number is too long.");
      }
      //Ensure carName is within maximum length
      if ((dto.getCarName() != null) && (dto.getCarName().length() > 0) && (dto.getCarName().length() > 255)) {
         errors.rejectValue("carName", "", "Brand name is too long. Maximum length 255.");
      }
      //Ensure carMake is within maximum length
      if ((dto.getCarMake() != null) && (dto.getCarMake().length() > 0) && (dto.getCarMake().length() > 255)) {
         errors.rejectValue("carMake", "", "Make name is too long. Maximum length 255.");
      }
      //Ensure carColor is within maximum length
      if ((dto.getCarColor() != null) && (dto.getCarColor().length() > 0) && (dto.getCarColor().length() > 255)) {
         errors.rejectValue("carColor", "", "Color is too long. Maximum length 255.");
      }
      //Ensure isActive is valid
      if ((dto.getIsActive()) && (dto.getCarColor().length() > 0) && (dto.getCarColor().length() > 255)) {
         errors.rejectValue("isActive", "", "Color name is too long. Maximum length 255.");
      }
      //Ensure fuelLevel is within 0 to 100
      if ((dto.getFuelLevel() < 0) || (dto.getFuelLevel() > 100)) {
         errors.rejectValue("fuelLevel", "", "Fuel level must be between 0 and 100.");
      }

   }
}
//CarDTOValidator
