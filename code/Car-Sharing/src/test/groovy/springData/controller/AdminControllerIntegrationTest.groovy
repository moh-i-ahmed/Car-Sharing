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
import springData.domain.User;
import springData.repository.RequestRepository
import springData.repository.RoleRepository
import springData.repository.StripeCustomerRepository
import springData.repository.UserRepository

/**
 * Integration tests of AdminController features
 *
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@EnableTransactionManagement
@TestPropertySource(locations="classpath:application.properties")
class AdminControllerIntegrationTest extends Specification {

   @Autowired private MockMvc mockMvc
   @Autowired private UserRepository userRepo
   @Autowired private RoleRepository roleRepo
   @Autowired private RequestRepository requestRepo
   @Autowired StripeCustomerRepository stripeCustomerRepo
   @Autowired private StripeCustomerRepository stripeCustomerRepo;

   private ResultActions result;

   @Shared int id = -1
   @Shared private User u

   def setup() {
      u = new User()
      u.setUsername('xyz@xyz.com')
      u.setRole(roleRepo.findByRoleName("USER"));
      userRepo.save(u);

      StripeCustomer customer = new StripeCustomer();
      customer.setTokenID("cus_HEfFSAOpO4sfTl");
      customer.setUser(u);
      stripeCustomerRepo.save(customer);

      u.setStripeCustomer(customer);
      userRepo.save(u);

      id = u.getUserID()
   }

   def "admin dashboard loads"() {
      when: "admin dashboard is called"
         result = mockMvc.perform(get("/admin/dashboard")
                         .secure(true)
                           .with(user(user)
                              .roles(role)))
      then: "expect model attributes exists"
         result.andExpect(model().attributeExists("username"))
               .andExpect(model().attributeExists("userCount"))
               .andExpect(model().attributeExists("verifiedUsers"))
               .andExpect(model().attributeExists("activeRequests"))
               .andExpect(model().attributeExists("completedRequests"))
               .andExpect(model().attributeExists("cancelledRequests"))
               .andExpect(model().attributeExists("unfulfilledRequests"))
               .andExpect(model().attributeExists("locations"))
               .andExpect(model().attributeExists("totalCars"))
               .andExpect(model().attributeExists("activeCars"))
               .andExpect(model().attributeExists("requests"))
               .andExpect(model().size(11))
      and: "status 200 & correct view"
         result.andExpect(status().is2xxSuccessful())
               .andExpect(view().name("/admin/dashboard"))
               .andDo(print())
      where:
           user | role
           'smithy@mail.com' | 'ADMIN'
   }

   def "admin createUser page"() {
      when: "admin createUser is called"
         result = mockMvc.perform(get("/admin/createUser")
                         .secure(true)
                           .with(user(user)
                              .roles(role)))
      then: "expect model attributes exists"
         result.andExpect(model().attributeExists("username"))
               .andExpect(model().attributeExists("userDTO"))
               .andExpect(model().attributeExists("roles"))
               .andExpect(model().size(3))
      and: "status 200 & correct view"
         result.andExpect(status().is2xxSuccessful())
               .andExpect(view().name("admin/createUser"))
               .andDo(print())
      where:
           user | role
           'smithy@mail.com' | 'ADMIN'
   }

   def "admin creates user successfully"() {
      when: "create-user form is posted"
            result = mockMvc.perform(post("/admin/createUser")
                            .secure(true)
                              .with(user(user)
                                 .roles(role))
                              .with(csrf())
                     .param("userID", "5")
                     .param("firstName", "firstName")
                     .param("lastName", "lastName")
                     .param("username", "xxxx@xyz.com")
                     .param("roleName", "USER"))
      then: "redirected to /view-all-users"
            result.andExpect(status().is3xxRedirection())
                  .andExpect(redirectedUrl("/admin/view-all-users"))
                  .andDo(print())
      where:
           user | role
           'smithy@mail.com' | 'ADMIN'
   }

   def "admin creating user with empty fields fails"() {
      when: "empty create-user form is posted"
            result = mockMvc.perform(post("/admin/createUser")
                            .secure(true)
                              .with(user(user)
                                 .roles(role))
                              .with(csrf())
                     .param("userID", "")
                     .param("firstName", "")
                     .param("lastName", "")
                     .param("username", "xxxx@xyz.com")
                     .param("password", "")
                     .param("matchingPassword", "")
                     .param("phoneNumber", "")
                     .param("roleName", "USER")
                     .param("driverLicense", ""))
      then: "expect validator error and return the same view"
            result.andExpect(model().attributeHasErrors("userDTO"))
                  .andExpect(view().name("admin/createUser"))
                  .andDo(print())
      where:
           user | role
           'smithy@mail.com' | 'ADMIN'
   }

   def "admin creating user with existing username fails"() {
      when: "create-user form with existing username is posted"
            result = mockMvc.perform(post("/admin/createUser")
                            .secure(true)
                              .with(user(user)
                                 .roles(role))
                              .with(csrf())
                     .param("userID", "")
                     .param("firstName", "")
                     .param("lastName", "")
                     .param("username", "bob@bobmail.com")
                     .param("password", "")
                     .param("matchingPassword", "")
                     .param("phoneNumber", "")
                     .param("roleName", "USER")
                     .param("driverLicense", ""))
      then: "expect validator error and return the same view"
            result.andExpect(model().attributeHasErrors("userDTO"))
                  .andExpect(view().name("admin/createUser"))
                  .andDo(print())
      where:
           user | role
           'smithy@mail.com' | 'ADMIN'
   }

   def "admin edit user loads"() {
      when: "edit user is called"
         result = mockMvc.perform(get("/admin/edit-user/" + "$id")
                         .secure(true)
                           .with(user(user)
                              .roles(role)))
      then: "expect model attributes exists"
         result.andExpect(model().attributeExists("userID"))
               .andExpect(model().attributeExists("userDTO"))
               .andExpect(model().attributeExists("roles"))
               .andExpect(model().attributeExists("username"))
               .andExpect(model().size(4))
      and: "status 200 & correct view"
         result.andExpect(status().is2xxSuccessful())
               .andExpect(view().name("admin/edit-user"))
               .andDo(print())
      where:
           user | role
           'smithy@mail.com' | 'ADMIN'
   }

   def "admin view user loads"() {
      when: "view user is called"
         result = mockMvc.perform(get("/admin/view-user/" + "$id")
                         .secure(true)
                           .with(user(user)
                              .roles(role)))
      then: "expect model attributes exists"
         result.andExpect(model().attributeExists("userID"))
               .andExpect(model().attributeExists("userDTO"))
               .andExpect(model().attributeExists("stripeCustomerID"))
               .andExpect(model().attributeExists("username"))
               .andExpect(model().size(4))
      and: "status 200 & correct view"
         result.andExpect(status().is2xxSuccessful())
               .andExpect(view().name("admin/view-user"))
               .andDo(print())
      where:
           user | role
           'smithy@mail.com' | 'ADMIN'
   }

   def "admin updates user successfully"() {
      when: "update-user form is posted"
            result = mockMvc.perform(post("/admin/update-user/" + "$id")
                            .secure(true)
                              .with(user(user)
                                 .roles(role))
                              .with(csrf())
                     .param("userID", "$id")
                     .param("firstName", "firstName")
                     .param("lastName", "lastName")
                     .param("username", "2xyz@xyz.com")
                     .param("roleName", "USER"))
      then: "redirected to /view-all-users"
            result.andExpect(status().is3xxRedirection())
                  .andExpect(redirectedUrl("/admin/view-all-users"))
                  .andDo(print())
      where:
           user | role
           'smithy@mail.com' | 'ADMIN'
   }

   def "admin updates user with partially filled form"() {
      when: "update-user form is posted"
            result = mockMvc.perform(post("/admin/update-user/" + "$id")
                            .secure(true)
                              .with(user(user)
                                 .roles(role))
                              .with(csrf())
                     .param("userID", "$id")
                     .param("firstName", "firstName")
                     .param("lastName", "lastName")
                     .param("username", "xxxx@xyz.com")
                     .param("password", "password")
                     .param("matchingPassword", "password")
                     .param("phoneNumber", "phoneNumber")
                     .param("roleName", "USER")
                     .param("driverLicense", "driverLicense"))
      then: "redirected to /view-all-users"
            result.andExpect(model().attributeHasErrors("userDTO"))
                  .andExpect(view().name("admin/edit-user"))
                  .andDo(print())
      where:
           user | role
           'smithy@mail.com' | 'ADMIN'
   }

   def "admin updating user with empty fields fails"() {
      when: "update-user form is posted"
            result = mockMvc.perform(post("/admin/update-user/" + "$id")
                            .secure(true)
                              .with(user(user)
                                 .roles(role))
                              .with(csrf())
                     .param("userID", "$id")
                     .param("firstName", "")
                     .param("lastName", "")
                     .param("username", "")
                     .param("password", "password")
                     .param("matchingPassword", "password")
                     .param("phoneNumber", "phoneNumber")
                     .param("roleName", "USER")
                     .param("driverLicense", "driverLicense"))
      then: "expect validator error and return the same view"
            result.andExpect(model().attributeHasErrors("userDTO"))
                  .andExpect(view().name("admin/edit-user"))
                  .andDo(print())
      where:
           user | role
           'smithy@mail.com' | 'ADMIN'
   }

   def "admin resets user password"() {
      when: "admin resets user password"
         result = mockMvc.perform(get("/admin/reset-password/" + "$id")
                         .secure(true)
                           .with(user(user)
                              .roles(role)))
      then: "redirected to /view-all-users"
         result.andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/admin/view-all-users"))
               .andDo(print())
      where:
           user | role
           'smithy@mail.com' | 'ADMIN'
   }

   def "admin views all users"() {
      when: "/admin/view-all-users is called"
         result = mockMvc.perform(get("/admin/view-all-users")
                         .secure(true)
                           .with(user(user)
                              .roles(role)))
      then: "expect model attributes exists"
         result.andExpect(model().attributeExists("username"))
               .andExpect(model().attributeExists("users"))
      and: "status 200 & correct view"
         result.andExpect(status().is2xxSuccessful())
               .andExpect(view().name("admin/view-all-users"))
               .andDo(print())
      where:
           user | role
           'smithy@mail.com' | 'ADMIN'
   }

   def "admin deletes user"() {
      when: "admin deletes user"
         result = mockMvc.perform(get("/admin/delete-user/" + "$id")
                         .secure(true)
                           .with(user(user)
                              .roles(role)))
      then: "redirected to /view-all-users"
         result.andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/admin/view-all-users"))
               .andDo(print())
      where:
           user | role
           'smithy@mail.com' | 'ADMIN'
   }

   def "view all requests"() {
      when: "view all requests is called"
         result = mockMvc.perform(get("/admin/view-all-requests")
                         .secure(true)
                           .with(user(user)
                              .roles(role)))
      then: "expect model attributes exists"
         result.andExpect(model().attributeExists("username"))
               .andExpect(model().attributeExists("requests"))
               .andExpect(model().size(2))
      and: "status 200 & correct view"
         result.andExpect(status().is2xxSuccessful())
               .andExpect(view().name("admin/view-all-requests"))
               .andDo(print())
      where:
           user | role
           'smithy@mail.com' | 'ADMIN'
   }

   def "stripe dashboard loads"() {
      when: "stripe dashboard is called"
         result = mockMvc.perform(get("/admin/stripe-dashboard")
                         .secure(true)
                           .with(user(user)
                              .roles(role)))
      then: "expect model attributes exists"
         result.andExpect(model().attributeExists("username"))
               .andExpect(model().attributeExists("requests"))
               .andExpect(model().attributeExists("stripeCustomers"))
               .andExpect(model().size(3))
      and: "status 200 & correct view"
         result.andExpect(status().is2xxSuccessful())
               .andExpect(view().name("admin/stripe-dashboard"))
               .andDo(print())
      where:
           user | role
           'smithy@mail.com' | 'ADMIN'
   }

}
//AdminControllerIntegrationTest
