package springData.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author mia17
 */
@Entity
@Table(name = "CAR")
@JsonPropertyOrder({"carColor", "registrationNumber", "carName", "carType", "currentLocation", "fuelLevel",
   "isActive", "last_location", "request"})
public class Car {

   @Column(length = 10)
   @Id
   private String registrationNumber;

   @Basic
   private String carName;

   @Basic
   private String carType;

   @Basic
   private String carColor;

   /**
    * Starting location latitude
    */
   @Basic
   private String latitude;

   /**
    * Starting location longitude
    */
   @Basic
   private String longitude;

   /**
    * Is the car active or not?
    */
   @Type(type = "yes_no")
   @Column(name = "isActive", nullable = false)
   private boolean isActive;

   @Basic(optional = false)
   private int fuelLevel;

   @OneToOne(mappedBy = "car")
   private Request request;

   @OneToOne
   private CarAvailability carAvailability;

   public Car() {
   }
   
   public Car(String registrationNumber, String carColor, String carName, boolean isActive) {
      super();
      this.registrationNumber = registrationNumber;
      this.carColor = carColor;
      this.carName = carName;
      this.isActive = isActive;
   }

   public String getRegistrationNumber() {
       return this.registrationNumber;
   }

   public void setRegistrationNumber(String registrationNumber) {
       this.registrationNumber = registrationNumber;
   }

   public String getCarName() {
       return this.carName;
   }

   public void setCarName(String carName) {
       this.carName = carName;
   }

   public String getCarType() {
       return this.carType;
   }

   public void setCarType(String carType) {
       this.carType = carType;
   }

   public String getCarColor() {
       return this.carColor;
   }

   public void setCarColor(String carColor) {
       this.carColor = carColor;
   }

   public String getLatitude() {
      return latitude;
   }

   public void setLatitude(String latitude) {
      this.latitude = latitude;
   }

   public String getLongitude() {
      return longitude;
   }

   public void setLongitude(String longitude) {
      this.longitude = longitude;
   }

   public boolean isIsActive() {
       return this.isActive;
   }

   public void setIsActive(boolean isActive) {
       this.isActive = isActive;
   }

   public int getFuelLevel() {
       return this.fuelLevel;
   }

   public void setFuelLevel(int fuelLevel) {
       this.fuelLevel = fuelLevel;
   }

   public Request getRequest() {
       return this.request;
   }

   public void setRequest(Request request) {
       this.request = request;
   }

   public CarAvailability getCarAvailability() {
       return this.carAvailability;
   }

   public void setCarAvailability(CarAvailability carAvailability) {
       this.carAvailability = carAvailability;
   }

}

