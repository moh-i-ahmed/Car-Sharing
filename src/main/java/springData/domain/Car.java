package springData.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * @author mia17
 */
@Entity
@Table(name = "CAR")
public class Car {

   @Column(unique = true, length = 10)
   @Id
   private String registrationNumber;

   @Basic
   private String carName;

   @Basic
   private String carMake;

   @Basic
   private String carColor;

   @Basic
   @Type(type = "yes_no")
   @Column(name = "isActive", nullable = false)
   private boolean isActive;

   @Basic(optional = false)
   private int fuelLevel;

   @Basic
   @Type(type = "yes_no")
   @Column(name = "unlocked", nullable = false)
   private boolean unlocked = false;

   @ManyToOne
   private Location location;

   @OneToMany(cascade = {CascadeType.REMOVE}, fetch = FetchType.EAGER, mappedBy = "car")
   private Set<Request> requests;

   @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, mappedBy = "car")
   private List<CarAvailability> carAvailabilities;

   public Car(String registrationNumber, String carColor, String carMake, String carName, boolean isActive) {
      super();
      this.registrationNumber = registrationNumber;
      this.carColor = carColor;
      this.carMake = carMake;
      this.carName = carName;
      this.isActive = isActive;
   }

   public Car(){
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

   public String getCarMake() {
      return this.carMake;
   }

   public void setCarMake(String carMake) {
      this.carMake = carMake;
   }

   public String getCarColor() {
      return this.carColor;
   }

   public void setCarColor(String carColor) {
      this.carColor = carColor;
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

   public boolean isUnlocked() {
      return unlocked;
   }

   public void setUnlocked(boolean unlocked) {
      this.unlocked = unlocked;
   }

   public Location getLocation() {
      return this.location;
   }

   public void setLocation(Location location) {
      this.location = location;
   }

   public Set<Request> getRequests() {
      if (requests == null) {
         requests = new HashSet<>();
      }
      return this.requests;
   }

   public void setRequests(Set<Request> requests) {
      this.requests = requests;
   }

   public void addRequest(Request request) {
      getRequests().add(request);
      request.setCar(this);
   }

   public void removeRequest(Request request) {
      getRequests().remove(request);
      request.setCar(null);
   }

   public List<CarAvailability> getCarAvailabilities() {
      if (carAvailabilities == null) {
         carAvailabilities = new ArrayList<>();
      }
      return this.carAvailabilities;
   }

   public void setCarAvailabilities(List<CarAvailability> carAvailabilities) {
      this.carAvailabilities = carAvailabilities;
   }

   public void addCarAvailability(CarAvailability carAvailability) {
      getCarAvailabilities().add(carAvailability);
      carAvailability.setCar(this);
   }

   public void removeCarAvailability(CarAvailability carAvailability) {
      getCarAvailabilities().remove(carAvailability);
      carAvailability.setCar(null);
   }

}
// CarAvailability
