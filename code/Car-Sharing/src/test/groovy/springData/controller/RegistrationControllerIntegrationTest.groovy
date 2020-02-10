package springData.domain

import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest()
@AutoConfigureMockMvc
@TestPropertySource(locations="classpath:test.properties")
class RegistrationControllerIntegrationTest extends Specification {

   @Autowired private MockMvc mockMvc
   
   /*def setup() {
      
   }*/
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

   def "registration complete"(){
      when: "registration form is posted"
            result = mockMvc.perform(post("/register/create").secure(true)
               .with(csrf())
               .param("userID", "1")
               .param("firstName", "firstName")
               .param("lastName", "lastName")
               .param("username", "xxxx@xyz.com")
               .param("password", "password")
               .param("matchingPassword", "password")
               .param("phoneNumber", "phoneNumber")
               .param("roleName", "roleName")
               .param("driverLicense", "driverLicense"))
      then: "expect redirected to login page"
            result.andExpect(status().is3xxRedirection())
                  .andExpect(redirectedUrl("/"))
                  .andDo(print())
   }

   def "registration with existing username fails"(){
      when: "attempting to register with existing username"
            result = mockMvc.perform(post("/register/create").secure(true)
               .with(csrf())
               .param("userID", "1")
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

   def "registration with incomplete fields fails"(){
      when: "attempting to register with missing fields"
            result = mockMvc.perform(post("/register/create").secure(true)
               .with(csrf())
               .param("userID", "1")
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
   
}
//RegistrationControllerIntegrationTest
