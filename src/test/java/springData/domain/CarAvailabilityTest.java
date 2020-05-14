package springData.domain;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import springData.domain.Car;
import springData.domain.CarAvailability;

class CarAvailabilityTest {

   Car car;
   CarAvailability carAvl;

   @BeforeEach
   void setUpCarAvailability() {
      //Create Car
      car = new Car("ALIDJGLSA", "Red", "Toyota", "Prius", false);
      car.setCarName("Sedan");
      car.setLocation(new Location("51.5074","0.1278W"));
      car.setFuelLevel(70);

      //Create CarAvailability
      carAvl = new CarAvailability(LocalDateTime.now(), LocalDateTime.now().plusHours(2), "TESTCODE", car);
   }

   @Test
   void testCarAvailability() {
      //Test constructor
      assertFalse(carAvl.equals(null));

      CarAvailability carAvl2 = new CarAvailability();
      assertFalse(carAvl2.equals(null));
   }

   @Test
   void testGetSetStartTime() {
      //Test GetAvailabilityTime
      assertThat(carAvl, Matchers.hasProperty("startTime", Matchers.equalTo(LocalDateTime.now())));

      //Test SetAvailabilityTime
      carAvl.setStartTime(LocalDateTime.now().plusHours(1));
      assertThat(carAvl, Matchers.hasProperty("startTime", Matchers.equalTo(LocalDateTime.now().plusHours(1))));
   }

   @Test
   void testGetSetAccessCode() {
      //Test GetAccessCode
      assertThat(carAvl, Matchers.hasProperty("accessCode", Matchers.equalTo("TESTCODE")));

      //Test SetAccessCode
      carAvl.setAccessCode("TESTCODE2");;
      assertThat(carAvl, Matchers.hasProperty("accessCode", Matchers.equalTo("TESTCODE2")));
   }

   @Test
   void testGetSetCar() {
      //Test GetCar
      assertThat(carAvl, Matchers.hasProperty("car", Matchers.notNullValue()));

      //Create Car
      Car car1 = new Car("ALGNVGLTY", "Black", "Honda", "Accord", true);
      car1.setCarName("Sedan");
      car1.setLocation(new Location("51.5074N", "0.1278W"));
      car1.setFuelLevel(50);

      //Test SetCar
      carAvl.setCar(car1);
      assertThat(carAvl, Matchers.hasProperty("car", Matchers.equalTo(car1)));
      assertThat(carAvl, Matchers.hasProperty("car", Matchers.not(Matchers.equalTo(car))));
   }
}
//CarAvailabilityTest

