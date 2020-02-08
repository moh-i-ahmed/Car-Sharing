package springData.domain;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;

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
      car = new Car("ALIDJGLSA", "Red", "Prius", false);
      car.setCarType("Sedan");
      car.setLatitude("51.5074N");
      car.setLongitude("0.1278W");
      car.setFuelLevel(70);

      //Create CarAvailability
      carAvl = new CarAvailability(LocalTime.now(), "TESTCODE", car);
   }

   @Test
   void testCarAvailability() {
      //Test constructor
      assertFalse(carAvl.equals(null));

      CarAvailability carAvl2 = new CarAvailability();
      assertFalse(carAvl2.equals(null));
   }

   @Test
   void testGetSetAvailabilityTime() {
      //Test GetAvailabilityTime
      assertThat(carAvl, Matchers.hasProperty("availabilityTime", Matchers.equalTo(LocalTime.now())));

      //Test SetAvailabilityTime
      carAvl.setAvailabilityTime(LocalTime.NOON);
      assertThat(carAvl, Matchers.hasProperty("availabilityTime", Matchers.equalTo(LocalTime.NOON)));
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
      Car car1 = new Car("ALGNVGLTY", "Black", "Accord", true);
      car1.setCarType("Sedan");
      car1.setLatitude("51.5074N");
      car1.setLongitude("0.1278W");
      car1.setFuelLevel(50);

      //Test SetCar
      carAvl.setCar(car1);
      assertThat(carAvl, Matchers.hasProperty("car", Matchers.equalTo(car1)));
      assertThat(carAvl, Matchers.hasProperty("car", Matchers.not(Matchers.equalTo(car))));
   }
}
//CarAvailabilityTest

