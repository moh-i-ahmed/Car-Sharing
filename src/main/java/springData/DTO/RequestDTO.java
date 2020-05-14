package springData.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public class RequestDTO {

   //Format of time input
   final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a dd/MM/yyyy");

   @DateTimeFormat(iso = ISO.DATE)
   private LocalDate requestDate;

   private LocalDateTime startTime;

   private LocalDateTime endTime;

   private String pickupLatitude;

   private String pickupLongitude;

   private String dropoffLatitude;

   private String dropoffLongitude;

   private double distance;

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

   public LocalDateTime getStartTime() {
      return startTime;
   }

   public void setStartTime(String startTime) {
      if (!startTime.isEmpty()) {
         this.startTime = LocalDateTime.parse(startTime, DATE_FORMATTER);
      }
   }

   public LocalDateTime getEndTime() {
      return endTime;
   }

   public void setEndTime(String endTime) {
      if (!endTime.isEmpty()) {
         this.endTime = LocalDateTime.parse(endTime, DATE_FORMATTER);
      }
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

   public double getDistance() {
      return distance;
   }

   public void setDistance(double distance) {
      this.distance = distance;
   }

}
// RequestDTO
