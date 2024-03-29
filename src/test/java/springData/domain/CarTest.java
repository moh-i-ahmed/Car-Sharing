package springData.domain;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import springData.constants.Constants;
import springData.domain.Car;
import springData.domain.CarAvailability;
import springData.domain.Request;
import springData.utils.AccessCodeGenerator;

class CarTest {

   Car car;

   @BeforeEach
   void setUpCar() {
      //Create new car
      car = new Car("ALIDJGLSA", "Red", "Toyota", "Prius", false);
      car.setLocation(new Location("51.5074N", "51.5074N"));
      car.setFuelLevel(70);
   }

   @Test
   void testCar() {
      //Test constructor
      assertFalse(car.equals(null));
      assertTrue((car != null));
   }

   @Test
   void testGetSetRegistrationNumber() {
      //Test GetRegistrationNumber
      assertThat(car, Matchers.hasProperty("registrationNumber", Matchers.equalTo("ALIDJGLSA")));
      
      //Test SetRegistrationNumber
      car.setRegistrationNumber("");
      assertThat(car, Matchers.hasProperty("registrationNumber", Matchers.equalTo("")));
   }

   @Test
   void testGetSetCarName() {
      //Test GetCarName
      assertThat(car, Matchers.hasProperty("carName", Matchers.equalTo("Prius")));
      
      //Test SetCarName
      car.setCarName("Highlander");
      assertThat(car, Matchers.hasProperty("carName", Matchers.equalTo("Highlander")));
   }

   @Test
   void testGetSetCarColor() {
      //Test GetCarColor
      assertThat(car, Matchers.hasProperty("carColor", Matchers.equalTo("Red")));
      
      //Test SetCarColor
      car.setCarColor("Black");
      assertThat(car, Matchers.hasProperty("carColor", Matchers.equalTo("Black")));
   }

   @Test
   void testGetSetLocation() {
      //Test GetLocation
      assertThat(car, Matchers.hasProperty("location"));
      
      //Test SetLocation
      Location testLocation = new Location();
      car.setLocation(testLocation);
      assertThat(car, Matchers.hasProperty("location", Matchers.not(Matchers.equalTo("testLocation"))));
   }

   @Test
   void testGetSetIsActive() {
      //Test GetIsActive
      assertThat(car, Matchers.hasProperty("isActive", Matchers.equalTo(false)));
      
      //Test SetIsActive
      car.setIsActive(true);
      assertThat(car, Matchers.hasProperty("isActive", Matchers.equalTo(true)));
   }

   @Test
   void testGetSetFuelLevel() {
      //Test GetFuelLevel
      assertThat(car, Matchers.hasProperty("fuelLevel", Matchers.equalTo(70)));
      
      //Test SetFuelLevel
      car.setFuelLevel(10);
      assertThat(car, Matchers.hasProperty("fuelLevel", Matchers.equalTo(10)));
   }
   
   @Test
   void testAddRemoveRequest() {
      //Test GetRequest
      assertThat(car, Matchers.hasProperty("requests", Matchers.equalTo(car.getRequests())));

      //Create Request
      Request req1 = new Request();
      req1.setStartTime(LocalDateTime.now());
      req1.setEndTime(LocalDateTime.now().plusHours(4));
      req1.setRequestDate(LocalDate.of(2019, 11, 20));
      req1.setStatus(Constants.REQUEST_STATUS_COMPLETE);
      
      //Test AddRequest
      car.addRequest(req1);
      //assertTrue(req1.getCar().equals(car));
   }

   @Test
   void testAddCarAvailability() {
      //Test AddCarAvailabilities
      assertThat(car, Matchers.hasProperty("carAvailabilities"));

      //Create Request
      Request req1 = new Request();
      req1.setEndTime(LocalDateTime.now().plusHours(4));
      
      //Create CarAvailability
      CarAvailability carAvl = new CarAvailability(req1.getEndTime(), req1.getEndTime(),
            AccessCodeGenerator.generateAccessCode(), car);
      
      //Test SetCarAvailability
      car.addCarAvailability(carAvl);
      
      assertThat(car, Matchers.hasProperty("carAvailabilities", Matchers.equalTo(car.getCarAvailabilities())));
   }

}
//CarTest

