package springData.domain;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;

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
      car = new Car("ALIDJGLSA", "Red", "Prius", false);
      car.setCarType("saloon");
      car.setLatitude("51.5074N");
      car.setLongitude("0.1278W");
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
   void testGetSetCarType() {
      //Test GetCarType
      assertThat(car, Matchers.hasProperty("carType", Matchers.equalTo("saloon")));
      
      //Test SetCarName
      car.setCarType("Hatchback");
      assertThat(car, Matchers.hasProperty("carType", Matchers.equalTo("Hatchback")));
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
   void testGetSetLatitude() {
      //Test GetLatitude
      assertThat(car, Matchers.hasProperty("latitude", Matchers.equalTo("51.5074N")));
      assertThat(car, Matchers.hasProperty("latitude", Matchers.not(Matchers.equalTo("52.987N"))));
      
      //Test SetLatitude
      car.setLatitude("45.3460N");
      assertThat(car, Matchers.hasProperty("latitude", Matchers.not(Matchers.equalTo("Hatchback"))));
      assertThat(car, Matchers.hasProperty("latitude", Matchers.equalTo("45.3460N")));
   }

   @Test
   void testGetSetLongitude() {
      //Test GetLongitude
      assertThat(car, Matchers.hasProperty("latitude", Matchers.equalTo("51.5074N")));
      assertThat(car, Matchers.hasProperty("latitude", Matchers.not(Matchers.equalTo("52.987N"))));
      
      //Test SetLongitude
      car.setLatitude("45.3460N");
      assertThat(car, Matchers.hasProperty("latitude", Matchers.not(Matchers.equalTo("Hatchback"))));
      assertThat(car, Matchers.hasProperty("latitude", Matchers.equalTo("45.3460N")));
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
   void testGetSetRequest() {
      //Test GetRequest
      assertThat(car, Matchers.hasProperty("request", Matchers.equalTo(null)));

      //Create Request
      Request req1 = new Request();
      req1.setStartTime(LocalTime.NOON);
      req1.setEndTime(LocalTime.NOON.plusHours(4));
      req1.setRequestDate(LocalDate.of(2019, 11, 20));
      req1.setStatus(Constants.STATUS_COMPLETE);
      req1.setCar(car);
      
      //Test SetRequest
      car.setRequest(req1);
      assertThat(car, Matchers.hasProperty("request", Matchers.equalTo(req1)));
      assertTrue(req1.getCar().equals(car));
   }

   @Test
   void testGetCarAvailability() {
      //Test GetCarAvailability
      assertThat(car, Matchers.hasProperty("request", Matchers.equalTo(null)));

      //Create Request
      Request req1 = new Request();
      req1.setStartTime(LocalTime.NOON);
      req1.setEndTime(LocalTime.NOON.plusHours(4));
      req1.setRequestDate(LocalDate.of(2019, 11, 20));
      req1.setStatus(Constants.STATUS_COMPLETE);
      req1.setCar(car);
      
      //Create CarAvailability
      CarAvailability carAvl = new CarAvailability(req1.getEndTime(), AccessCodeGenerator.generateAccessCode(), car);
      
      //Test SetCarAvailability
      car.setCarAvailability(carAvl);
      
      assertThat(car, Matchers.hasProperty("carAvailability", Matchers.equalTo(carAvl)));
      assertTrue(req1.getCar().getCarAvailability().equals(carAvl));
   }

}
//CarTest

