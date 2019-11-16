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
public class Car_Availability {

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private int availabilityID;

   @Basic
   private LocalTime availability_time;

   @Basic
   private String accessCode;

   @OneToOne(mappedBy = "car_Availability")
   private Car car;

   public int getAvailabilityID() {
      return this.availabilityID;
   }

   public void setAvailabilityID(int availabilityID) {
      this.availabilityID = availabilityID;
   }

   public LocalTime getAvailability_time() {
      return this.availability_time;
   }

   public void setAvailability_time(LocalTime availability_time) {
      this.availability_time = availability_time;
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
