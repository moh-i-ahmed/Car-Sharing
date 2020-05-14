package springData.domain;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import springData.constants.Constants;
import springData.domain.Request;

class RequestTest {

   Request request;
   
   @BeforeEach
   void setUpRequest() {
      //Create Request
      request = new Request();
      request.setRequestDate(LocalDate.of(2019, 11, 23));
      request.setStartTime(LocalDateTime.of(9, 3, 1, 0, 0));
      request.setEndTime(LocalDateTime.of(11, 3, 1, 0, 0));
      request.setPickupLocation(new Location());
      request.setDropoffLocation(new Location());
      request.setStatus(Constants.REQUEST_STATUS_COMPLETE);
   }

   @Test
   void testRequest() {
      //Test constructor
      assertFalse(request.equals(null));
   }

   @Test
   void testGetSetRequestDate() {
      //Test GetRequestDate
      assertThat(request, Matchers.hasProperty("requestDate", Matchers.equalTo(LocalDate.of(2019, 11, 23))));
      
      //Test SetRequestDate
      request.setRequestDate(LocalDate.of(2019, 12, 25));
      assertThat(request, Matchers.hasProperty("requestDate", Matchers.equalTo(LocalDate.of(2019, 12, 25))));
   }

   @Test
   void testGetSetStartTime() {
      //Test GetStartTime
      assertThat(request, Matchers.hasProperty("startTime", Matchers.equalTo(LocalDateTime.of(9, 3, 1, 0, 0))));
      
      //Test SetStartTime
      request.setStartTime(LocalDateTime.of(7, 6, 1, 0, 0));
      assertThat(request, Matchers.hasProperty("startTime", Matchers.equalTo(LocalDateTime.of(7, 6, 1, 0, 0))));
   }

   @Test
   void testGetSetEndTime() {
      //Test GetEndTime
      assertThat(request, Matchers.hasProperty("endTime", Matchers.equalTo(LocalDateTime.of(11, 3, 1, 0, 0))));
      
      //Test SetEndTime
      request.setEndTime(LocalDateTime.of(13, 3, 1, 0, 0));
      assertThat(request, Matchers.hasProperty("endTime", Matchers.equalTo(LocalDateTime.of(13, 3, 1, 0, 0))));
   }

   @Test
   void testGetSetPickupLocation() {
      //Test GetLatitude
      assertThat(request, Matchers.hasProperty("pickupLocation"));
      
      //Test SetLatitude
      request.setPickupLocation(new Location());
      assertThat(request, Matchers.hasProperty("pickupLocation"));
   }

   @Test
   void testGetSetDropoffLocation() {
      //Test GetLongitude
      assertThat(request, Matchers.hasProperty("dropoffLocation"));
      
      //Test SetLongitude
      request.setDropoffLocation(new Location());
      assertThat(request, Matchers.hasProperty("dropoffLocation"));
   }

   @Test
   void testGetSetStatus() {
      //Test GetStatus
      assertThat(request, Matchers.hasProperty("status", Matchers.equalTo(Constants.REQUEST_STATUS_COMPLETE)));
      
      //Test SetStatus
      request.setStatus(Constants.REQUEST_STATUS_CANCELLED);
      assertThat(request, Matchers.hasProperty("status", Matchers.equalTo(Constants.REQUEST_STATUS_CANCELLED)));
   }
   
}
//RequestTest

