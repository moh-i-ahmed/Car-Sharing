package springData.controller;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import springData.DTO.RequestDTO;
import springData.repository.RequestRepository;

public class RequestDTOValidator implements Validator {

   public boolean supports(Class<?> clazz) {
      return RequestDTO.class.equals(clazz);
   }

   private RequestRepository requestRepo;

   public RequestDTOValidator(RequestRepository requestRepo) {
      this.requestRepo = requestRepo;
   }

   @Override
   public void validate(Object target, Errors errors) {
      RequestDTO dto = (RequestDTO) target;

      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "startTime", "", "Pick a Starting Time");
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "endTime", "", "Pick an Ending Time");

      //Ensure Start Time is valid
      if ((dto.getStartTime() == null)) {
         errors.rejectValue("startTime", "", "Pick a Start time");
      }
      //Ensure Start Time is valid
      if ((dto.getEndTime() == null)) {
         errors.rejectValue("endTime", "", "Pick an End time");
      }
      //Ensure StartTime != EndTime
      if ((dto.getStartTime() == dto.getEndTime())) {
         errors.rejectValue("endTime", "", "Pick different times");
      }
      //Ensure Difference isn't < 1 StartTime  EndTime

   }
}
//RequestDTOValidator
