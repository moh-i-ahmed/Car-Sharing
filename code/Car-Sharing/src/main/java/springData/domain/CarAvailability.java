package springData.domain;

import java.time.LocalDateTime;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class CarAvailability {

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private int availabilityID;

   @Column(nullable = false)
   @Basic
   private LocalDateTime startTime;

   @Column(nullable = false)
   @Basic
   private LocalDateTime endTime;

   @Basic
   private String accessCode;

   @ManyToOne
   private Car car;

   public CarAvailability() {
   }

   public CarAvailability(LocalDateTime startTime, LocalDateTime endTime, String accessCode, Car car) {
      super();
      this.startTime = startTime;
      this.endTime = endTime;
      this.accessCode = accessCode;
      this.car = car;
   }

   public int getAvailabilityID() {
      return this.availabilityID;
   }

   public void setAvailabilityID(int availabilityID) {
      this.availabilityID = availabilityID;
   }

   public LocalDateTime getStartTime() {
      return this.startTime;
   }

   public void setStartTime(LocalDateTime startTime) {
      this.startTime = startTime;
   }

   public LocalDateTime getEndTime() {
      return endTime;
   }

   public void setEndTime(LocalDateTime endTime) {
      this.endTime = endTime;
   }

   public String getAccessCode() {
      return this.accessCode;
   }

   public void setAccessCode(String accessCode) {
      this.accessCode = accessCode;
   }

   public Car getCar() {
      return this.car;
   }

   public void setCar(Car car) {
      this.car = car;
   }

}
// CarAvailability
