package springData.domain;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;

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
      request.setStartTime(LocalTime.of(9, 30));
      request.setEndTime(LocalTime.of(11, 30));
      request.setLatitude("51.5074N");
      request.setLongitude("0.1278W");
      request.setStatus(Constants.STATUS_COMPLETE);
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
      assertThat(request, Matchers.hasProperty("startTime", Matchers.equalTo(LocalTime.of(9, 30))));
      
      //Test SetStartTime
      request.setStartTime(LocalTime.of(7, 46));
      assertThat(request, Matchers.hasProperty("startTime", Matchers.equalTo(LocalTime.of(7, 46))));
   }

   @Test
   void testGetSetEndTime() {
      //Test GetEndTime
      assertThat(request, Matchers.hasProperty("endTime", Matchers.equalTo(LocalTime.of(11, 30))));
      
      //Test SetEndTime
      request.setEndTime(LocalTime.of(13, 35));
      assertThat(request, Matchers.hasProperty("endTime", Matchers.equalTo(LocalTime.of(13, 35))));
   }

   @Test
   void testGetSetLatitude() {
      //Test GetLatitude
      assertThat(request, Matchers.hasProperty("latitude", Matchers.equalTo("51.5074N")));
      
      //Test SetLatitude
      request.setLatitude("53.5674S");
      assertThat(request, Matchers.hasProperty("latitude", Matchers.equalTo("53.5674S")));
   }

   @Test
   void testGetSetLongitude() {
      //Test GetLongitude
      assertThat(request, Matchers.hasProperty("longitude", Matchers.equalTo("0.1278W")));
      
      //Test SetLongitude
      request.setLongitude("3.1578W");
      assertThat(request, Matchers.hasProperty("longitude", Matchers.equalTo("3.1578W")));
   }

   @Test
   void testGetSetStatus() {
      //Test GetStatus
      assertThat(request, Matchers.hasProperty("status", Matchers.equalTo(Constants.STATUS_COMPLETE)));
      
      //Test SetStatus
      request.setStatus(Constants.STATUS_CANCELLED);
      assertThat(request, Matchers.hasProperty("status", Matchers.equalTo(Constants.STATUS_CANCELLED)));
   }
   
}
//RequestTest

