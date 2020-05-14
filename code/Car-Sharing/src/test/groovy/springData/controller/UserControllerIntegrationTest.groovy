package springData.controller

import spock.lang.Shared
import spock.lang.Specification
import java.time.LocalDateTime;
import java.time.LocalDate;

import springData.WebApp;
import springData.domain.User
import springData.domain.Car
import springData.domain.Request
import springData.algorithm.CarAllocation
import springData.constants.Constants;
import springData.repository.CarAvailabilityRepository
import springData.repository.CarRepository
import springData.repository.LocationRepository
import springData.repository.RequestRepository;
import springData.repository.RoleRepository
import springData.repository.UserRepository;
import springData.services.InvoiceService
import springData.services.StripeService

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.context.WebApplicationContext

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.transaction.Transactional
import org.springframework.test.annotation.Rollback;

/**
 * Integration tests of UserController features
 */
//@Rollback
//@Transactional
//@ActiveProfiles("test")
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@EnableTransactionManagement
@TestPropertySource(locations="classpath:application.properties")
class UserControllerIntegrationTest extends Specification {

   @Autowired private MockMvc mockMvc

   @Autowired private UserRepository userRepo
   @Autowired private RequestRepository requestRepo
   @Autowired private LocationRepository locationRepo
   @Autowired private RoleRepository roleRepo
   @Autowired private CarRepository carRepo
   @Autowired private CarAvailabilityRepository carAvailabilityRepo
   @Autowired private CarAllocation carAllocator

   @Autowired private StripeService stripeService
   @Autowired private InvoiceService invoiceService

   private ResultActions result

   @Shared int userId = -1
   @Shared int requestId = -1
   @Shared private User u
   @Shared private Request r

   @Shared LocalDateTime startTime
   @Shared LocalDateTime endTime

   def setup() {
      u = new User()
      u.setUsername('user@mail.com')
      u.setRole(roleRepo.findByRoleName("USER"))
      u.setFirstName("Test")
      u.setLastName("Tester")
      u.setPhoneNumber("07834572877")
      u.setDriverLicense("MAR99740614BC2TL")
      u.setEnabled(true)
      userRepo.save(u);

      Car c = carRepo.findByRegistrationNumber("ALIDJGLSA")

      r = new Request()
      r.setRequestDate(LocalDate.now())
      r.setStartTime(LocalDateTime.now())
      r.setEndTime(LocalDateTime.now().plusHours(2))
      r.setStatus(Constants.REQUEST_STATUS_IN_PROGRESS)
      r.setPickupLocation(c.getLocation())
      r.setDropoffLocation(c.getLocation())
      r.setUser(u)
      r.setCar(c)
      requestRepo.save(r)

      u.addRequest(r)
      userRepo.save(u)

      userId = u.getUserID()
      requestId = r.getRequestID()
      startTime = LocalDateTime.now()
      endTime = LocalDateTime.now().plusHours(2)
   }

   def "user dashboard loads"() {
      when: "user dashboard is called"
         result = mockMvc.perform(get("/user/dashboard")
                         .secure(true)
                           .with(user(user)
                              .roles(role)))
      then: "expect requestDTO & username model attributes exists"
         result.andExpect(model().attributeExists("requestDTO"))
               .andExpect(model().attributeExists("username"))
               .andExpect(model().size(2))
      and: "status 200 & correct view"
         result.andExpect(status().is2xxSuccessful())
               .andExpect(view().name("/user/dashboard"))
               .andDo(print())
      where:
           user | role
           'bob@bobmail.com' | 'USER'
   }

   def "car request is successful"() {
      when: "request form is posted"
            result = mockMvc.perform(post("/user/requestCar")
                            .secure(true)
                              .with(user(user)
                                 .roles(role))
                              .with(csrf())
                     .param("requestDate", "2020-02-02")
                     .param("startTime", "09:30 AM 02/03/2020")
                     .param("endTime", "11:30 AM 02/03/2020")
                     .param("pickupLatitude", "12")
                     .param("pickupLongitude", "32")
                     .param("dropoffLatitude", "12")
                     .param("dropoffLongitude", "32")
                     .param("distance", "3"))
      then: "expect redirection"
            result.andExpect(flash().attributeExists("message"))
                  .andExpect(status().is3xxRedirection())
                  .andDo(print())
      where:
           user | role
           'bob@bobmail.com' | 'USER'
   }

   def "car request with empty form fails"() {
      when: "empty request form is posted"
            result = mockMvc.perform(post("/user/requestCar")
                            .secure(true)
                              .with(user(user)
                                 .roles(role))
                              .with(csrf())
                     .param("requestDate", "2020-02-02")
                     .param("startTime", "09:30 AM 02/03/2020")
                     .param("endTime", "09:30 AM 02/03/2020")
                     .param("pickupLatitude", "12")
                     .param("pickupLongitude", "32")
                     .param("dropoffLatitude", "12")
                     .param("dropoffLongitude", "32")
                     .param("distance", "3"))
      then: "expect redirection to dashboard"
            result.andExpect(flash().attributeExists("message"))
                  .andExpect(status().is3xxRedirection())
                  .andExpect(redirectedUrl("/user/dashboard"))
                  .andDo(print())
      where:
           user | role
           'bob@bobmail.com' | 'USER'
   }

   def "car request with partially filled form fails"() {
      when: "empty request form is posted"
            result = mockMvc.perform(post("/user/requestCar")
                            .secure(true)
                              .with(user(user)
                                 .roles(role))
                              .with(csrf())
                     .param("requestDate", "")
                     .param("startTime", "09:30 AM 02/03/2020")
                     .param("endTime", "09:30 AM 02/03/2020")
                     .param("pickupLatitude", "")
                     .param("pickupLongitude", "")
                     .param("dropoffLatitude", "12")
                     .param("dropoffLongitude", "")
                     .param("distance", ""))
      then: "expect redirection to dashboard"
            result.andExpect(flash().attributeExists("message"))
                  .andExpect(status().is3xxRedirection())
                  .andExpect(redirectedUrl("/user/dashboard"))
                  .andDo(print())
      where:
           user | role
           'bob@bobmail.com' | 'USER'
   }

   def "view request details"() {
      when: "user views the details a request"
            result = mockMvc.perform(get("/user/view-request/$requestId")
                            .secure(true)
                               .with(user(user)
                                  .roles(role)))
      then: "expect request model attribute exists"
            result.andExpect(model().attributeExists("baseCharge"))
                  .andExpect(model().attributeExists("duration"))
                  .andExpect(model().attributeExists("request"))
                  .andExpect(model().attributeExists("username"))
                  .andExpect(model().size(4))
      and: "status 200 and view is rendered"
            result.andExpect(status().isOk())
                  .andExpect(view().name("/user/view-request"))
                  .andDo(print())
       where:
            user | role
            'bob@bobmail.com' | 'USER'
   }

   /*
    * When the user cancels an active request
    * then they should be redirected to the dashboard
    */
   def "cancel request"() {
      when: "user cancels a request"
            result = mockMvc.perform(get("/user/cancel-request/$requestId")
                            .secure(true)
                               .with(user(user)
                                  .roles(role)))
      then: "redirected to user/dashboard"
            result.andExpect(status().is3xxRedirection())
                  .andExpect(redirectedUrl("/user/dashboard"))
                  .andDo(print())
       where:
            user | role
            'bob@bobmail.com' | 'USER'
   }

   def "end request"() {
      when: "user ends request"
            result = mockMvc.perform(get("/user/end-request/" + "$requestId")
                            .secure(true)
                               .with(user(user)
                                  .roles(role)))
      then: "redirected to user/dashboard"
            result.andExpect(status().is3xxRedirection())
                  .andExpect(redirectedUrl("/user/dashboard"))
                  .andDo(print())
       where:
            user | role | requestId
            'bob@bobmail.com' | 'USER' | 5
   }

   def "history loads"() {
      when: "logged in user views their history"
            result = mockMvc.perform(get("/user/history")
                            .secure(true)
                               .with(user(user)
                                  .roles(role)))
      then: "status 200 and view is rendered"
            result.andExpect(status().isOk())
                  .andExpect(view().name("/user/history"))
                  .andDo(print())
       where:
            user | role
            'bob@bobmail.com' | 'USER'
   }
   
}
// UserControllerIntegrationTest
