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
class AuthenticationControllerIntegrationTest extends Specification {

   @Autowired private MockMvc mockMvc
   
   /*def setup() {
      
   }*/
   private ResultActions result;
   

   def "landing page"(){
      when: "landing page is called"
         result = mockMvc.perform(get("/"))
      then: "expect status 200 & correct view"
         result.andExpect(status().isOk())
               .andExpect(view().name("login"))
               .andDo(print())
   }
   
   def "success-login"(){
      when: "after a successful login"
            result = mockMvc.perform(get("/success-login")
                            .secure(true)
                            .with(user(user)
                               .roles(role)))
      then: "redirected to appropriate dashboard"
         //User
         if (role == 'USER') {
            result.andExpect(status().is3xxRedirection())
                  .andExpect(redirectedUrl("/user/dashboard"))
                  .andDo(print())
         }
         //Admin 
         else {
            result.andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/dashboard"))
            .andDo(print())
         }
       where:
            user | role
            'bob@bobmail.com' | 'USER'
            'smithy@mail.com' | 'ADMIN'
   }
   
   def "authenticated user can view history"(){
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
   
   def "access denied"(){
      when: "access denied is called"
         result = mockMvc.perform(get("/access-denied")
                         .secure(true)
                            .with(user(user)
                               .roles(role)))
      then: "expect status 403-Forbidden"
         result.andExpect(status().is4xxClientError())
               .andExpect(view().name("access-denied"))
               .andDo(print())
      where:
           user | role
           'bob@bobmail.com' | 'USER'
   }
}
