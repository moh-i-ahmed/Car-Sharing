package springData.validator;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import springData.DTO.RequestDTO;

public class RequestDTOValidator implements Validator {

   public boolean supports(Class<?> clazz) {
      return RequestDTO.class.equals(clazz);
   }

   public RequestDTOValidator() {
   }

   @Override
   public void validate(Object target, Errors errors) {
      RequestDTO dto = (RequestDTO) target;

      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pickupLatitude", "", "Pick a valid pickup address");
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pickupLongitude", "", "Pick a valid pickup address");
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dropoffLatitude", "", "Pick a valid dropoff address");
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dropoffLongitude", "", "Pick a valid dropoff address");

      // Time of validation request
      LocalDateTime currentTime = LocalDateTime.now();

      // Request duration
      Duration duration = Duration.between(dto.getStartTime(), dto.getEndTime());

      // Ensure request time is in the future
      if (dto.getStartTime().isBefore(currentTime) || dto.getEndTime().isBefore(currentTime)) {
         errors.rejectValue("startTime", "", "Invalid: Time selected is in the past");
      }
      // Ensure startTime is before endTime
      if (dto.getStartTime().isAfter(dto.getEndTime()) || (dto.getStartTime() == dto.getEndTime())) {
         errors.rejectValue("startTime", "", "Invalid: End time must be at least 30 mins after Start time");
      }
      // Ensure request time is at least 30 minutes
      if (duration.toMinutes() < 30) {
         errors.rejectValue("startTime", "", "Minimum usage time is 30 minutes");
      }
      // Ensure request time is not more than 24 hours
      if (duration.toHours() >= 24) {
         errors.rejectValue("startTime", "", "Maximum usage time is 24 hours");
      }
      // Ensure endTime is after
      if (dto.getEndTime().isBefore(dto.getStartTime())) {
         errors.rejectValue("endTime", "", "End time is before start time");
      }
   }
}
// RequestDTOValidator
