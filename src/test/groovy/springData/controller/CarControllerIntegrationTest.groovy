package springData.controller

import spock.lang.Specification
import spock.lang.Shared

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print

import org.springframework.test.web.servlet.result.MockMvcResultMatchers

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.context.WebApplicationContext

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.*
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

import org.springframework.test.annotation.Rollback

import springData.domain.StripeCustomer
import springData.domain.Car;
import springData.repository.RequestRepository
import springData.repository.RoleRepository
import springData.repository.StripeCustomerRepository
import springData.repository.CarRepository

/**
 * Integration testing of CarController features using mock objects
 *
 */
@ActiveProfiles("test")
@Rollback
@SpringBootTest
@AutoConfigureMockMvc
@EnableTransactionManagement
@TestPropertySource(locations="classpath:application.properties")
class CarControllerIntegrationTest extends Specification {

   @Autowired private MockMvc mockMvc
   @Autowired private CarRepository carRepo
   @Autowired private RoleRepository roleRepo
   @Autowired private RequestRepository requestRepo
   @Autowired StripeCustomerRepository stripeCustomerRepo
   @Autowired private StripeCustomerRepository stripeCustomerRepo;

   private ResultActions result;

   @Shared String registrationNumber = ""
   @Shared private Car c

   def setup() {
      c = new Car("PK8DLGBGEQ", "Blue", "Honda", "Civic", false)
      //c.setRegistrationNumber('xyz@xyz.com')
      carRepo.save(c);

      registrationNumber = c.getRegistrationNumber()
   }

   def "add car page"() {
      when: "add car is called"
         result = mockMvc.perform(get("/car/add-car")
                         .secure(true)
                           .with(user(user)
                              .roles(role)))
      then: "expect model attributes exists"
         result.andExpect(model().attributeExists("true"))
               .andExpect(model().attributeExists("false"))
               .andExpect(model().attributeExists("carDTO"))
               .andExpect(model().attributeExists("username"))
               .andExpect(model().size(4))
      and: "status 200 & correct view"
         result.andExpect(status().is2xxSuccessful())
               .andExpect(view().name("admin/car/add-car"))
               .andDo(print())
      where:
           user | role
           'smithy@mail.com' | 'ADMIN'
   }

   def "add car successfully"() {
      when: "add car form is posted"
            result = mockMvc.perform(post("/car/add-car")
                            .secure(true)
                              .with(user(user)
                                 .roles(role))
                              .with(csrf())
                     .param("registrationNumber", "5KJDGOSGD")
                     .param("carName", "name")
                     .param("carMake", "make")
                     .param("carColor", "Blue")
                     .param("isActive", "false")
                     .param("fuelLevel", "100"))
      then: "redirected to /view-all-users"
            result.andExpect(status().is3xxRedirection())
                  .andExpect(redirectedUrl("/car/view-all-cars"))
                  .andDo(print())
      where:
           user | role
           'smithy@mail.com' | 'ADMIN'
   }

   def "add car with empty fields fails"() {
      when: "empty add car form is posted"
            result = mockMvc.perform(post("/car/add-car")
                            .secure(true)
                              .with(user(user)
                                 .roles(role))
                              .with(csrf())
                     .param("registrationNumber", "")
                     .param("carName", "")
                     .param("carMake", "")
                     .param("carColor", "")
                     .param("isActive", "")
                     .param("fuelLevel", ""))
      then: "expect validator error and return the same view"
            result.andExpect(model().attributeHasErrors("carDTO"))
                  .andExpect(view().name("admin/car/add-car"))
                  .andDo(print())
      where:
           user | role
           'smithy@mail.com' | 'ADMIN'
   }

   def "add car with existing registration number fails"() {
      when: "add car form with existing registrationNumber is posted"
            result = mockMvc.perform(post("/admin/createUser")
                            .secure(true)
                              .with(user(user)
                                 .roles(role))
                              .with(csrf())
                     .param("registrationNumber", registrationNumber)
                     .param("carName", "name")
                     .param("carMake", "make")
                     .param("carColor", "Blue")
                     .param("isActive", "false")
                     .param("fuelLevel", "100"))
      then: "expect validator error and return the same view"
            result.andExpect(model().attributeHasErrors("userDTO"))
                  .andExpect(view().name("admin/createUser"))
                  .andDo(print())
      where:
           user | role
           'smithy@mail.com' | 'ADMIN'
   }

   def "edit car loads"() {
      when: "edit car is called"
         result = mockMvc.perform(get("/car/edit-car/" + "$registrationNumber")
                         .secure(true)
                           .with(user(user)
                              .roles(role)))
      then: "expect model attributes exists"
         result.andExpect(model().attributeExists("true"))
               .andExpect(model().attributeExists("false"))
               .andExpect(model().attributeExists("regNumber"))
               .andExpect(model().attributeExists("carDTO"))
               .andExpect(model().attributeExists("username"))
               .andExpect(model().size(5))
      and: "status 200 & correct view"
         result.andExpect(status().is2xxSuccessful())
               .andExpect(view().name("admin/car/edit-car"))
               .andDo(print())
      where:
           user | role
           'smithy@mail.com' | 'ADMIN'
   }

   def "view car loads"() {
      when: "view car is called"
         result = mockMvc.perform(get("/car/view-car/" + "$registrationNumber")
                         .secure(true)
                           .with(user(user)
                              .roles(role)))
      then: "expect model attributes exists"
         result.andExpect(model().attributeExists("car"))
               .andExpect(model().attributeExists("username"))
               .andExpect(model().size(2))
      and: "status 200 & correct view"
         result.andExpect(status().is2xxSuccessful())
               .andExpect(view().name("admin/car/view-car"))
               .andDo(print())
      where:
           user | role
           'smithy@mail.com' | 'ADMIN'
   }

   def "update car successfully"() {
      when: "update-user form is posted"
            result = mockMvc.perform(post("/car/update-car/" + "$registrationNumber")
                            .secure(true)
                              .with(user(user)
                                 .roles(role))
                              .with(csrf())
                     .param("registrationNumber", "$registrationNumber")
                     .param("carName", "name")
                     .param("carMake", "make")
                     .param("carColor", "Blue")
                     .param("isActive", "false")
                     .param("fuelLevel", "100")
                     .param("latitude", "")
                     .param("longitude", ""))
      then: "redirected to /view-all-users"
            result.andExpect(status().is3xxRedirection())
                  .andExpect(redirectedUrl("/car/view-all-cars"))
                  .andDo(print())
      where:
           user | role
           'smithy@mail.com' | 'ADMIN'
   }

   def "update car with partially filled form fails"() {
      when: "update car form is posted"
            result = mockMvc.perform(post("/car/update-car/" + "$registrationNumber")
                            .secure(true)
                              .with(user(user)
                                 .roles(role))
                              .with(csrf())
                     .param("registrationNumber", "$registrationNumber")
                     .param("carName", "name")
                     .param("carMake", "")
                     .param("carColor", "Blue")
                     .param("isActive", "")
                     .param("fuelLevel", "")
                     .param("latitude", "")
                     .param("longitude", "34"))
      then: "redirected to /view-all-users"
            result.andExpect(model().attributeHasErrors("carDTO"))
                  .andExpect(view().name("admin/car/edit-car"))
                  .andDo(print())
      where:
           user | role
           'smithy@mail.com' | 'ADMIN'
   }

   def "update car with empty fields fails"() {
      when: "update car form is posted"
            result = mockMvc.perform(post("/car/update-car/" + "$registrationNumber")
                            .secure(true)
                              .with(user(user)
                                 .roles(role))
                              .with(csrf())
                     .param("registrationNumber", "$registrationNumber")
                     .param("carName", "")
                     .param("carMake", "")
                     .param("carColor", "")
                     .param("isActive", "")
                     .param("fuelLevel", "")
                     .param("latitude", "")
                     .param("longitude", ""))
      then: "expect validator error and return the same view"
            result.andExpect(model().attributeHasErrors("carDTO"))
                  .andExpect(view().name("admin/car/edit-car"))
                  .andDo(print())
      where:
           user | role
           'smithy@mail.com' | 'ADMIN'
   }

   def "view all cars"() {
      when: "view all cars is called"
         result = mockMvc.perform(get("/car/view-all-cars")
                         .secure(true)
                           .with(user(user)
                              .roles(role)))
      then: "expect model attributes exists"
         result.andExpect(model().attributeExists("cars"))
               .andExpect(model().attributeExists("username"))
               .andExpect(model().size(2))
      and: "status 200 & correct view"
         result.andExpect(status().is2xxSuccessful())
               .andExpect(view().name("admin/car/view-all-cars"))
               .andDo(print())
      where:
           user | role
           'smithy@mail.com' | 'ADMIN'
   }

   def "delete car"() {
      when: "delete car"
         result = mockMvc.perform(get("/car/delete-car/" + "$registrationNumber")
                         .secure(true)
                           .with(user(user)
                              .roles(role)))
      then: "redirected to /view-all-cars"
         result.andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/car/view-all-cars"))
               .andDo(print())
      where:
           user | role
           'smithy@mail.com' | 'ADMIN'
   }

}
// CarControllerIntegrationTest
