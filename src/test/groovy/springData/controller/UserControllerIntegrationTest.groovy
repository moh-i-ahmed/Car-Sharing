package springData.domain

import spock.lang.Specification

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
class UserControllerIntegrationTest extends Specification {

   @Autowired private MockMvc mockMvc

   private ResultActions result;

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
 
   def "user request is sent"() {
      when: "request form is posted"
            result = mockMvc.perform(post("/user/requestCar")
                            .secure(true)
                              .with(user(user)
                                 .roles(role))
                              .with(csrf())
                     .param("requestDate", "2019-12-25")
                     .param("startTime", "07:46")
                     .param("endTime", "09:46")
                     .param("longitude", "12")
                     .param("latitude", "32"))
      then: "expect requestID model attribute exists"
            result.andExpect(model().attributeExists("requestID"))
      and: "expect redirection"
            result.andExpect(status().is3xxRedirection())
                  .andDo(print())
      where:
           user | role
           'bob@bobmail.com' | 'USER'
   }

   def "view request"() {
      when: "user views details of a request"
            result = mockMvc.perform(get("/user/request")
                            .secure(true)
                               .with(user(user)
                                  .roles(role))
                  .param("requestID", "5"))
      then: "expect request & username model attributes exists"
            result.andExpect(model().attributeExists("request"))
                  .andExpect(model().attributeExists("username"))
                  .andExpect(model().size(2))
      and: "status 200 and view is rendered"
            result.andExpect(status().isOk())
                  .andExpect(view().name("/user/requestCar"))
                  .andDo(print())
       where:
            user | role
            'bob@bobmail.com' | 'USER'
   }

   /*
    * Tests
    */
   def "cancel request"() {
      when: "user cancels a request"
            result = mockMvc.perform(get("/user/cancel-request/" + "$requestID")
                            .secure(true)
                               .with(user(user)
                                  .roles(role))
                  .param('requestID', '5'))
      then: "redirected to user/dashboard"
            result.andExpect(status().is3xxRedirection())
                  .andExpect(redirectedUrl("/user/dashboard"))
                  .andDo(print())
       where:
            user | role | requestID
            'bob@bobmail.com' | 'USER' | 5
   }

   def "view request details"() {
      when: "user views the details a request"
            result = mockMvc.perform(get("/user/view-request/" + "$requestID")
                            .secure(true)
                               .with(user(user)
                                  .roles(role))
                  .param('requestID', '5'))
      then: "expect request model attribute exists"
            result.andExpect(model().attributeExists("request"))
                  .andExpect(model().size(1))
      and: "status 200 and view is rendered"
            result.andExpect(status().isOk())
                  .andExpect(view().name("/user/view-request"))
                  .andDo(print())
       where:
            user | role | requestID
            'bob@bobmail.com' | 'USER' | 5
   }

   def "user history loads"() {
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
//UserControllerIntegrationTest
