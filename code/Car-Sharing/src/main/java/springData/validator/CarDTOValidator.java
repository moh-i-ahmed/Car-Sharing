package springData.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import springData.DTO.CarDTO;
import springData.DTO.UserDTO;
import springData.repository.CarRepository;

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
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "carMake", "", "Enter a model/make");

      //Ensure Registration Number is within minimum length
      if ((dto.getRegistrationNumber() != null) && (dto.getRegistrationNumber().length() > 0) && (dto.getRegistrationNumber().length() < 6)) {
         errors.rejectValue("registrationNumber", "", "First name has less than 2 characters.");
      }
      //Ensure carName is within maximum length
      if ((dto.getCarName() != null) && (dto.getCarName().length() > 0) && (dto.getCarName().length() > 255)) {
         errors.rejectValue("carName", "", "First name is too long. Maximum length 255.");
      }

   }

}
//CarDTOValidator
