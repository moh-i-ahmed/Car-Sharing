package springData.controller

import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.context.WebApplicationContext

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.annotation.Rollback;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@EnableTransactionManagement
@TestPropertySource(locations="classpath:application.properties")
class RegistrationControllerIntegrationTest extends Specification {

   @Autowired private MockMvc mockMvc

   private ResultActions result;

   def "registration page loads"(){
      when: "registration page is called"
         result = mockMvc.perform(get("/register"))
      then: "expect status 200 & correct view"
         result.andExpect(status().is2xxSuccessful())
               .andExpect(view().name("register"))
      and: "userDTO model attribute exists"
         result.andExpect(model().attributeExists("userDTO"))
               .andDo(print())
   }

   def "registration successful"(){
      when: "registration form is posted"
            result = mockMvc.perform(post("/register/create").secure(true)
                  .with(csrf())
                  .param("firstName", "firstName")
                  .param("lastName", "lastName")
                  .param("username", "registration@xyz.com")
                  .param("password", "v6R:#@/2`5H#{K4-")
                  .param("matchingPassword", "v6R:#@/2`5H#{K4-")
                  .param("phoneNumber", "02844839655")
                  .param("driverLicense", "MAR99740614BC2TL"))
      then: "expect redirected to login page"
            result.andExpect(status().is3xxRedirection())
                  .andExpect(redirectedUrl("/"))
                  .andDo(print())
   }

   def "registration with existing username fails"(){
      when: "attempting to register with existing username"
            result = mockMvc.perform(post("/register/create").secure(true)
                  .with(csrf())
                  .param("firstName", "Bob")
                  .param("lastName", "Bobson")
                  .param("username", "bob@bobmail.com")
                  .param("password", "password")
                  .param("matchingPassword", "password")
                  .param("phoneNumber", "phoneNumber")
                  .param("roleName", "roleName")
                  .param("driverLicense", "driverLicense"))
      then: "validator returns error"
            result.andExpect(model().attributeHasErrors("userDTO"))
                  .andExpect(status().is2xxSuccessful())
                  .andExpect(view().name("register"))
                  .andDo(print())
   }

   def "registration with weak password fails"(){
      when: "attempting to register with existing username"
            result = mockMvc.perform(post("/register/create").secure(true)
                  .with(csrf())
                  .param("firstName", "Bob")
                  .param("lastName", "Bobson")
                  .param("username", "registration@xyz.com")
                  .param("password", "1234")
                  .param("matchingPassword", "1234")
                  .param("phoneNumber", "phoneNumber")
                  .param("roleName", "roleName")
                  .param("driverLicense", "driverLicense"))
      then: "validator returns error"
            result.andExpect(model().attributeHasErrors("userDTO"))
                  .andExpect(status().is2xxSuccessful())
                  .andExpect(view().name("register"))
                  .andDo(print())
   }

   def "registration with incomplete fields fails"(){
      when: "attempting to register with missing fields"
            result = mockMvc.perform(post("/register/create").secure(true)
                  .with(csrf())
                  .param("lastName", "Bobson")
                  .param("username", "xyz@mail.com")
                  .param("password", "password")
                  .param("matchingPassword", "password")
                  .param("phoneNumber", "phoneNumber")
                  .param("roleName", "roleName")
                  .param("driverLicense", "driverLicense"))
      then: "validator returns error"
            result.andExpect(model().attributeHasErrors("userDTO"))
                  .andExpect(view().name("register"))
                  .andDo(print())
   }

   def "forgot-password page loads"() {
      when: "forgot-password is called"
         result = mockMvc.perform(get("/register/forgot-password")
                         .secure(true)
                           .with(user(user)
                              .roles(role)))
      then: "expect model attributes exists"
         result.andExpect(model().attributeExists("userDTO"))
               .andExpect(model().size(1))
      and: "status 200 & correct view"
         result.andExpect(status().is2xxSuccessful())
               .andExpect(view().name("forgot-password"))
               .andDo(print())
      where:
           user | role
            'bob@bobmail.com' | 'USER'
   }

   def "forgot-password submit works unregistered user fails"() {
      when: "forgot-password request sent"
         result = mockMvc.perform(post("/register/forgot-password").secure(true)
                  .with(csrf())
                  .param("username", ""))
      then: "status 200 & correct view"
         result.andExpect(status().is2xxSuccessful())
               .andExpect(view().name("forgot-password"))
               .andDo(print())
      where:
           user | role
            'bob@bobmail.com' | 'USER'
   }

   def "forgot-password submit works with registered user"() {
      when: "forgot-password request sent"
         result = mockMvc.perform(post("/register/forgot-password").secure(true)
                  .with(csrf())
                  .param("username", "bob@bobmail.com"))
      then: "status 200 & correct view"
         result.andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/"))
               .andDo(print())
      where:
           user | role
            'bob@bobmail.com' | 'USER'
   }

}
//RegistrationControllerIntegrationTest
