package springData.controller

import spock.lang.Shared
import spock.lang.Specification
import springData.domain.Role
import springData.domain.User
import springData.repository.RoleRepository
import springData.repository.UserRepository

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

@SpringBootTest
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
      when: "landing page is called"
         result = mockMvc.perform(get("/success-login")
                         .secure(true)
                         .with(user(user)
                           .roles(role)))
      then: "expect status 200 & correct view"
         result.andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/dashboard"))
               .andDo(print())
      where:
           user | role
           'bob@bobmail.com' | 'USER'
           'smithy@mail.com' | 'ADMIN'
   }

   def "dashboard"(){
      when: "after a successful login"
            result = mockMvc.perform(get("/dashboard")
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

   def "error 404 page"(){
      when: "error occurs"
         result = mockMvc.perform(get("/error")
                         .secure(true)
                            .with(user(user)
                               .roles(role)))
      then: "expect status 404"
         result.andExpect(status().is4xxClientError())
               .andExpect(model().attributeExists("username"))
               .andExpect(view().name("404"))
               .andDo(print())
      where:
           user | role
           'bob@bobmail.com' | 'USER'
   }
/*   @Autowired
   UserRepository userRepo;

   @Autowired
   RoleRepository roleRepo
   @Shared Role userr
   @Shared Role admin
   @Shared User u1
   @Shared User u2
   private MockMvc mockMvc;
   private ResultActions result;
  
   def setup() {
      this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build()
      
      userr = new Role(1, "USER");
      admin = new Role(2, "ADMIN");

      roleRepo.save(userr);
      roleRepo.save(admin);
      
       u1 = new User()
      u1.setUsername("bob@mail.com")
      u1.setPassword("password")
      u1.setRole(userr)
      userRepo.save(u1)
      
       u2 = new User()
      u2.setUsername("smith@mail.com")
      u2.setPassword("password")
      u2.setRole(admin)
      userRepo.save(u2)
   }
   
   def "1: Demo"() {
     given: "dsafas"
        if (userRepo.findByUsername("smith@mail.com"))
      and: "the context of is setup"
           result = this.mockMvc.perform(get("/success-login").secure(true).with(user(u2).roles('ADMIN')))
     expect:
         result.andExpect(redirectedUrl(redirect))

  }
  
  def "2: Demo"() {
     given: "the context of is setup"
           result = this.mockMvc.perform(get("/dashboard"))
     when:
           
     expect: "I see a view dashboard"
           result.andExpect(view().name("login"))
  }*/
  
}
//AuthenticationControllerIntegrationTest
