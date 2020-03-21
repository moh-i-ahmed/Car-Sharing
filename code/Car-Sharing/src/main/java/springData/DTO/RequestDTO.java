package springData.DTO;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public class RequestDTO {

   @DateTimeFormat(iso = ISO.DATE)
   private LocalDate requestDate;

   private String startTime;

   private String endTime;

   private String pickupLatitude;

   private String pickupLongitude;

   private String dropoffLatitude;

   private String dropoffLongitude;

   // default and parameterized constructors
   public RequestDTO() {
      this.requestDate = LocalDate.now();
   }

   public LocalDate getRequestDate() {
      return requestDate;
   }

   public void setRequestDate(LocalDate requestDate) {
      this.requestDate = requestDate;
   }

   public String getStartTime() {
      return startTime;
   }

   public void setStartTime(String startTime) {
      this.startTime = startTime;
   }

   public String getEndTime() {
      return endTime;
   }

   public void setEndTime(String endTime) {
      this.endTime = endTime;
   }

   public String getPickupLongitude() {
      return pickupLongitude;
   }

   public void setPickupLongitude(String longitude) {
      this.pickupLongitude = longitude;
   }

   public String getPickupLatitude() {
      return pickupLatitude;
   }

   public void setPickupLatitude(String latitude) {
      this.pickupLatitude = latitude;
   }

   public String getDropoffLatitude() {
      return dropoffLatitude;
   }

   public void setDropoffLatitude(String dropoffLatitude) {
      this.dropoffLatitude = dropoffLatitude;
   }

   public String getDropoffLongitude() {
      return dropoffLongitude;
   }

   public void setDropoffLongitude(String dropoffLongitude) {
      this.dropoffLongitude = dropoffLongitude;
   }

}
//RequestDTO
