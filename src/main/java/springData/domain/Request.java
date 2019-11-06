package springData.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author mia17
 */
@Entity
@Table(name = "REQUEST")
@JsonPropertyOrder({"car", "accessCode", "dropoff", "endTime", "pickup", "requestID", "startTime"})
public class Request {

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private Long requestID;

   @Basic
   @DateTimeFormat(iso = ISO.DATE)
   private LocalDate requestDate;

   @Basic
   @DateTimeFormat(iso = ISO.TIME)
   private LocalTime startTime;

   @Basic
   @DateTimeFormat(iso = ISO.TIME)
   private LocalTime endTime;

   /**
    * Pickup location
    */
   @Basic
   private float pickup;

   /**
    * Dropoff location
    */
   @Basic
   private float dropoff;

   @Basic
   private String accessCode;

   @OneToOne
   private Car car;

   @OneToOne(mappedBy = "request")
   private Invoice invoice;

   @ManyToOne
   private User user;

   public Long getRequestID() {
      return this.requestID;
   }

   public void setRequestID(Long requestID) {
      this.requestID = requestID;
   }

   public LocalDate getRequestDate() {
      return this.requestDate;
   }

   public void setRequestDate(LocalDate requestDate) {
      this.requestDate = requestDate;
   }

   public LocalTime getStartTime() {
      return this.startTime;
   }

   public void setStartTime(LocalTime startTime) {
      this.startTime = startTime;
   }

   public LocalTime getEndTime() {
      return this.endTime;
   }

   public void setEndTime(LocalTime endTime) {
      this.endTime = endTime;
   }

   public float getPickup() {
      return this.pickup;
   }

   public void setPickup(float pickup) {
      this.pickup = pickup;
   }

   public float getDropoff() {
      return this.dropoff;
   }

   public void setDropoff(float dropoff) {
      this.dropoff = dropoff;
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

   public Invoice getInvoice() {
      return this.invoice;
   }

   public void setInvoice(Invoice invoice) {
      this.invoice = invoice;
   }

   public User getUser() {
      return this.user;
   }

   public void setUser(User user) {
      this.user = user;
   }

}
