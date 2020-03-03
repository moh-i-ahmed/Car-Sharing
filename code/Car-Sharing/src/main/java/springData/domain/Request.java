package springData.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@JsonPropertyOrder({"car", "accessCode", "latitude", "longitude", "endTime", "requestID", "startTime"})
public class Request {

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private int requestID;

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
    * Starting location latitude
    */
   @Basic
   private String latitude;

   /**
    * Starting location longitude
    */
   @Basic
   private String longitude;

   private String status;

   //TODO fix this mapping,
   //it causes errors when trying to get history,
  // @OneToMany(mappedBy = "request", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
   @ManyToOne
   private Car car;

   @OneToOne(mappedBy = "request", fetch = FetchType.LAZY)
   private Invoice invoice;

   @ManyToOne
   private User user;

   public int getRequestID() {
      return this.requestID;
   }

   public void setRequestID(int requestID) {
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

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

}
//Request
