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

   @Basic
   private LocalTime startTime;

   @Basic
   private LocalTime endTime;

   @Basic
   private String accessCode;

   @Basic
   private String status;

   @OneToOne
   private Invoice invoice;

   @ManyToOne
   private Location pickupLocation;

   @ManyToOne
   private Location dropoffLocation;

   @ManyToOne
   private User user;

   @ManyToOne
   private Car car;

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
