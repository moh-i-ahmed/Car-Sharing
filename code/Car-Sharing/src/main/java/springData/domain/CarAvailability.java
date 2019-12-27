package springData.domain;

import java.time.LocalTime;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * @author mia17
 */
@Entity
public class CarAvailability {

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private int availabilityID;

   @Basic
   private LocalTime availabilityTime;

   @Basic
   private String accessCode;

   @OneToOne(mappedBy = "carAvailability")
   private Car car;


   public CarAvailability() {
   }

   public CarAvailability(LocalTime availabilityTime, String accessCode, Car car) {
      this.availabilityTime = availabilityTime;
      this.accessCode = accessCode;
      this.car = car;
   }

   public int getAvailabilityID() {
      return this.availabilityID;
   }

   public void setAvailabilityID(int availabilityID) {
      this.availabilityID = availabilityID;
   }

   public LocalTime getAvailabilityTime() {
      return this.availabilityTime;
   }

   public void setAvailabilityTime(LocalTime availabilityTime) {
      this.availabilityTime = availabilityTime;
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
//CarAvailability
