package springData.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @author mia17
 */
@Entity
@Table(name = "REQUEST")
public class Request {

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private int requestID;

   @Basic
   private LocalDate requestDate;

   @Column(nullable = false)
   @Basic
   private LocalDateTime startTime;

   @Column(nullable = false)
   @Basic
   private LocalDateTime endTime;

   @Basic
   private String accessCode;

   @Basic
   private String status;

   @Basic
   private double distance;

   @OneToOne(fetch = FetchType.LAZY)
   private Invoice invoice;

   @ManyToOne
   private Location pickupLocation;

   @ManyToOne
   private Location dropoffLocation;

   @ManyToOne
   private User user;

   @ManyToOne
   private Car car;

   public Request() {      
   }

   public Request(LocalDate requestDate, LocalDateTime startTime, LocalDateTime endTime, String status,
         Location dropoffLocation, User user, Car car) {
      super();
      this.requestDate = requestDate;
      this.startTime = startTime;
      this.endTime = endTime;
      this.status = status;
      this.dropoffLocation = dropoffLocation;
      this.user = user;
      this.car = car;
   }

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

   public LocalDateTime getStartTime() {
      return this.startTime;
   }

   public void setStartTime(LocalDateTime startTime) {
      this.startTime = startTime;
   }

   public LocalDateTime getEndTime() {
      return this.endTime;
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

   public String getStatus() {
      return this.status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public double getDistance() {
      return distance;
   }

   public void setDistance(double distance) {
      this.distance = distance;
   }

   public Invoice getInvoice() {
      return this.invoice;
   }

   public void setInvoice(Invoice invoice) {
      this.invoice = invoice;
   }

   public Location getPickupLocation() {
      return this.pickupLocation;
   }

   public void setPickupLocation(Location pickupLocation) {
      this.pickupLocation = pickupLocation;
   }

   public Location getDropoffLocation() {
      return this.dropoffLocation;
   }

   public void setDropoffLocation(Location dropoffLocation) {
      this.dropoffLocation = dropoffLocation;
   }

   public User getUser() {
      return this.user;
   }

   public void setUser(User user) {
      this.user = user;
   }

   public Car getCar() {
      return this.car;
   }

   public void setCar(Car car) {
      this.car = car;
   }

}
//Request
