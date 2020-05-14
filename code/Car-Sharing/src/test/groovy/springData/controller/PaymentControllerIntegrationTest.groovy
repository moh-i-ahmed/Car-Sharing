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
 * Integration testing of PaymentController features using mock objects
 *
 */
@ActiveProfiles("test")
@Rollback
@SpringBootTest
@AutoConfigureMockMvc
@EnableTransactionManagement
@TestPropertySource(locations="classpath:application.properties")
class PaymentControllerIntegrationTest extends Specification {

   @Autowired private MockMvc mockMvc

   private ResultActions result;

   def "wallet loads"() {
      when: "wallet is called"
         result = mockMvc.perform(get("/payment/wallet")
                         .secure(true)
                           .with(user(user)
                              .roles(role)))
      then: "expect model attributes exists"
         result.andExpect(model().attributeExists("storedCards"))
               .andExpect(model().attributeExists("stripePublicKey"))
               .andExpect(model().attributeExists("username"))
               .andExpect(model().size(3))
      and: "status 200 & correct view"
         result.andExpect(status().is2xxSuccessful())
               .andExpect(view().name("/user/wallet"))
               .andDo(print())
      where:
           user | role
           'bob@bobmail.com' | 'USER'
   }

   def "remove card fails"() {
      when: "remove card is called"
         result = mockMvc.perform(get("/payment/remove-card/" + "card")
                         .secure(true)
                           .with(user(user)
                              .roles(role)))
      then: "expect redirect attributes exists"
         result.andExpect(flash().attributeExists("notificationHeader"))
               .andExpect(flash().attributeExists("notificationBody"))
      and: "status 200 & correct view"
         result.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/payment/wallet"))
                .andDo(print())
      where:
           user | role
           'bob@bobmail.com' | 'USER'
   }

}
// PaymentControllerIntegrationTest
