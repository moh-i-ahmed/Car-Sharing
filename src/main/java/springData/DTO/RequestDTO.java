package springData.DTO;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public class RequestDTO {

   @DateTimeFormat(iso = ISO.DATE)
   private LocalDate requestDate;

   @DateTimeFormat(iso = ISO.TIME)
   private LocalTime startTime;

   @DateTimeFormat(iso = ISO.TIME)
   private LocalTime endTime;

   private String longitude;

   private String latitude;

   // default and parameterized constructors
   public RequestDTO() {
      this.requestDate = LocalDate.now();
      //this.startTime = LocalTime.of(9, 0);
      //this.endTime = LocalTime.of(5, 0);
   }

   public LocalDate getRequestDate() {
      return requestDate;
   }

   public void setRequestDate(LocalDate requestDate) {
      this.requestDate = requestDate;
   }

   public LocalTime getStartTime() {
      return startTime;
   }

   public void setStartTime(LocalTime startTime) {
      this.startTime = startTime;
   }

   public LocalTime getEndTime() {
      return endTime;
   }

   public void setEndTime(LocalTime endTime) {
      this.endTime = endTime;
   }

   public String getLongitude() {
      return longitude;
   }

   public void setLongitude(String longitude) {
      this.longitude = longitude;
   }

   public String getLatitude() {
      return latitude;
   }

   public void setLatitude(String latitude) {
      this.latitude = latitude;
   }

}
//RequestDTO
