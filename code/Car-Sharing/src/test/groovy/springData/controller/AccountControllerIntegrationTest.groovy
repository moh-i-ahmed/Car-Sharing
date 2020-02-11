package springData.controller

import spock.lang.Specification
import spock.lang.Shared;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import javax.transaction.Transactional

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations="classpath:test.properties")
class AccountControllerIntegrationTest extends Specification {

   @Autowired private MockMvc mockMvc
   @Autowired UserRepository userRepo;

   BCryptPasswordEncoder pe = new  BCryptPasswordEncoder();

   private ResultActions result;

   def "profile page loads"() {
      when: "profile is called"
         result = mockMvc.perform(get("/account/profile")
                         .secure(true)
                           .with(user(user)
                              .roles(role)))
      then: "expect model attributes exists"
         result.andExpect(model().attributeExists("username"))
               .andExpect(model().attributeExists("userId"))
               .andExpect(model().attributeExists("passwordDTO"))
               .andExpect(model().attributeExists("userDTO"))
               .andExpect(model().size(4))
      and: "status 200 & correct view"
         result.andExpect(status().is2xxSuccessful())
               .andExpect(view().name("user/profile"))
               .andDo(print())
      where:
           user | role
            'bob@bobmail.com' | 'USER'
   }

   /*
    * Test Change Password feature
    * Redirects to profile page:
    * @Transactional allows repository calls
    */
   @Transactional
   def "change password is successful"() {
      when: "attempting to change password with incorrect details"
            result = mockMvc.perform(post("/account/change-password/submit/" + "$userId")
                            .secure(true)
                              .with(user(user)
                                 .roles(role))
                              .with(csrf())
                     .param("currentPassword", userRepo.findByUsername(user).getPassword())
                     .param("newPassword", 'password123')
                     .param("confirmPassword", 'password123'))
      then: "redirected to /account/profile"
            result.andExpect(status().is3xxRedirection())
                  .andExpect(redirectedUrl("/account/profile"))
                  .andDo(print())
      where:
           user | role | userId
            'bob@bobmail.com' | 'USER' | 1
   }

   /*
    * Tests PasswordDTOValidator
    * Returns error: [currentPassword is incorrect]
    */
   def "changing password with incorrect current password fails"() {
      when: "attempting to change password with incorrect details"
            result = mockMvc.perform(post("/account/change-password/submit/" + "$userId")
                            .secure(true)
                              .with(user(user)
                                 .roles(role))
                              .with(csrf())
                     .param("currentPassword", "wrongPassword")
                     .param("newPassword", "password123")
                     .param("confirmPassword", "password123"))
      then: "validator returns error"
            result.andExpect(model().attributeHasErrors("passwordDTO"))
                  .andExpect(view().name("user/profile"))
                  .andDo(print())
      where:
           user | role | userId
            'bob@bobmail.com' | 'USER' | 1
   }

   /*
    * Tests PasswordDTOValidator
    * Returns errors: [currentPassword]
    *                 [newPassword]
    *                 [confirmPassword]
    */
   def "changing password with empty fields fails"() {
      when: "attempting to change password with incorrect details"
            result = mockMvc.perform(post("/account/change-password/submit/" + "$userId")
                            .secure(true)
                              .with(user(user)
                                 .roles(role))
                              .with(csrf())
                     .param("currentPassword", "")
                     .param("newPassword", "")
                     .param("confirmPassword", ""))
      then: "validator returns error"
            result.andExpect(model().attributeHasErrors("passwordDTO"))
                  .andExpect(view().name("user/profile"))
                  .andDo(print())
      where:
           user | role | userId
            'bob@bobmail.com' | 'USER' | 1
   }

   def "forgot-password page loads"() {
      when: "forgot-password is called"
         result = mockMvc.perform(get("/account/forgot-password")
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

}
//AccountControllerIntegrationTest
