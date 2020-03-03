package springData.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Type;

/**
 * @author mia17
 */
@Entity
public class User {

   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userID_generator")
   private int userID;

   @Basic
   private String username;

   @Basic
   private String firstName;

   @Basic
   private String lastName;

   @Basic
   private String password;

   @Basic
   private String driverLicense;

   @Basic
   private String phoneNumber;

   @Basic
   private LocalDate date_of_birth;

   @Type(type = "yes_no")
   @Column(name = "isActive", nullable = false)
   private boolean isActive;

   @ManyToOne
   private Role role;

   @OneToMany(mappedBy = "user")
   private List<Request> requests;

   @OneToMany(mappedBy = "user")
   private List<Address> addresses;

   public int getUserID() {
      return this.userID;
   }

   public void setUserID(int userID) {
      this.userID = userID;
   }

   public String getUsername() {
      return this.username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getFirstName() {
      return this.firstName;
   }

   public void setFirstName(String firstName) {
      this.firstName = firstName;
   }

   public String getLastName() {
      return this.lastName;
   }

   public void setLastName(String lastName) {
      this.lastName = lastName;
   }

   public String getPassword() {
      return this.password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getDriverLicense() {
      return this.driverLicense;
   }

   public void setDriverLicense(String driverLicense) {
      this.driverLicense = driverLicense;
   }

   public String getPhoneNumber() {
      return this.phoneNumber;
   }

   public void setPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
   }

   public LocalDate getDate_of_birth() {
      return this.date_of_birth;
   }

   public void setDate_of_birth(LocalDate date_of_birth) {
      this.date_of_birth = date_of_birth;
   }

   public boolean isActive() {
      return isActive;
   }

   public void setActive(boolean isActive) {
      this.isActive = isActive;
   }

   public Role getRole() {
      return this.role;
   }

   public void setRole(Role role) {
      this.role = role;
   }

   public List<Request> getRequests() {
      if (requests == null) {
         requests = new ArrayList<>();
      }
      return this.requests;
   }

   public void setRequests(List<Request> requests) {
      this.requests = requests;
   }

   public void addRequest(Request request) {
      getRequests().add(request);
      request.setUser(this);
   }

   public void removeRequest(Request request) {
      getRequests().remove(request);
      request.setUser(null);
   }

   public List<Address> getAddresses() {
      if (addresses == null) {
         addresses = new ArrayList<>();
      }
      return this.addresses;
   }

   public void setAddresses(List<Address> addresses) {
      this.addresses = addresses;
   }

   public void addAddress(Address address) {
      getAddresses().add(address);
      address.setUser(this);
   }

   public void removeAddress(Address address) {
      getAddresses().remove(address);
      address.setUser(null);
   }

}
