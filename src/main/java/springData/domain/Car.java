package springData.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author mia17
 */
@Entity
@Table(name = "CAR")
@JsonPropertyOrder({"car_color", "registrationNumber", "car_name", "car_type", "current_location", "fuel_level", "isActive", "last_location", "request"})
public class Car {

   @Column(length = 10)
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private String registrationNumber;

   @Basic
   private String car_name;

   @Basic
   private String car_type;

   @Basic
   private String car_color;

   @Basic
   private float current_location;

   @Basic
   private float last_location;

   /**
    * Is the car active or not?
    */
   @Basic
   private boolean isActive;

   @Basic(optional = false)
   private int fuel_level;

   @OneToOne(mappedBy = "car")
   private Request request;

   @OneToOne
   private Car_Availability car_Availability;

   public String getRegistrationNumber() {
      return this.registrationNumber;
   }

   public void setRegistrationNumber(String registrationNumber) {
      this.registrationNumber = registrationNumber;
   }

   public String getCar_name() {
      return this.car_name;
   }

   public void setCar_name(String car_name) {
      this.car_name = car_name;
   }

   public String getCar_type() {
      return this.car_type;
   }

   public void setCar_type(String car_type) {
      this.car_type = car_type;
   }

   public String getCar_color() {
      return this.car_color;
   }

   public void setCar_color(String car_color) {
      this.car_color = car_color;
   }

   public float getCurrent_location() {
      return this.current_location;
   }

   public void setCurrent_location(float current_location) {
      this.current_location = current_location;
   }

   public float getLast_location() {
      return this.last_location;
   }

   public void setLast_location(float last_location) {
      this.last_location = last_location;
   }

   public boolean isIsActive() {
      return this.isActive;
   }

   public void setIsActive(boolean isActive) {
      this.isActive = isActive;
   }

   public int getFuel_level() {
      return this.fuel_level;
   }

   public void setFuel_level(int fuel_level) {
      this.fuel_level = fuel_level;
   }

   public Request getRequest() {
      return this.request;
   }

   public void setRequest(Request request) {
      this.request = request;
   }

   public Car_Availability getCar_Availability() {
      return this.car_Availability;
   }

   public void setCar_Availability(Car_Availability car_Availability) {
      this.car_Availability = car_Availability;
   }

}
