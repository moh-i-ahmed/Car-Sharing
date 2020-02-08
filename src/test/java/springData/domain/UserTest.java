package springData.domain;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import springData.domain.Address;
import springData.domain.Car;
import springData.domain.Invoice;
import springData.domain.Payment;
import springData.domain.PaymentType;
import springData.domain.Request;
import springData.domain.Role;
import springData.domain.User;

class UserTest {

   User user;
   
   @BeforeEach
   void setUpUser() {
      //Create user
      user = new User();
      user.setFirstName("Scott");
      user.setLastName("Malkinson");
      user.setUsername("bob@mail.com");
      user.setPassword("testpassword");
      user.setPhoneNumber("07834572877");
      user.setDate_of_birth(LocalDate.of(1985, 10, 31));
      user.setDriverLicense("MORQ6785368M01");
      user.setPhoneNumber("07435882466");
      user.setActive(false);
      
      Role role = new Role(1, "USER");
      user.setRole(role);
   }

   @Test
   void testUser() {
      //Test constructor
      assertFalse((user == null));
   }

   @Test
   void testGetSetUsername() {
      //Test Username
      assertThat(user, hasProperty("username", equalTo("bob@mail.com")));
      
      //Test SetUsername
      user.setUsername("scott@hotmail.com");
      assertThat(user, hasProperty("username", equalTo("scott@hotmail.com")));
   }

   @Test
   void testGetSetFirstName() {
      //Test GetFirstName
      assertThat(user, hasProperty("firstName", equalTo("Scott")));
      
      //Test SetFirstName
      user.setFirstName("John");
      assertThat(user, hasProperty("firstName", equalTo("John")));
   }

   @Test
   void testGetSetLastName() {
      //Test GetLastName
      assertThat(user, hasProperty("lastName", equalTo("Malkinson")));
      
      //Test SetLastName
      user.setLastName("John");
      assertThat(user, hasProperty("lastName", equalTo("John")));
   }

   @Test
   void testGetSetPassword() {
      //Test GetPassword
      assertThat(user, hasProperty("password", equalTo("testpassword")));
      
      //Test SetPassword
      user.setPassword("12345678");
      assertThat(user, hasProperty("password", equalTo("12345678")));
   }

   @Test
   void testGetSetDriverLicense() {
      //Test GetDriverLicense
      assertThat(user, hasProperty("driverLicense", equalTo("MORQ6785368M01")));
      
      //Test SetDriverLicense
      user.setDriverLicense("RQ548MJGSD1");
      assertThat(user, hasProperty("driverLicense", not(equalTo("MORQ6785368M01"))));
      assertThat(user, hasProperty("driverLicense", equalTo("RQ548MJGSD1")));
   }

   @Test
   void testGetSetPhoneNumber() {
      //Test GetPhoneNumber
      assertThat(user, hasProperty("phoneNumber", equalTo("07435882466")));
      
      //Test SetPhoneNumber
      user.setPhoneNumber("07956452466");
      assertThat(user, hasProperty("phoneNumber", not(equalTo("07435882466"))));
      assertThat(user, hasProperty("phoneNumber", equalTo("07956452466")));
   }

   @Test
   void testGetSetDate_of_birth() {
      //Test GetDate_of_birth
      assertThat(user, hasProperty("date_of_birth", equalTo(LocalDate.of(1985, 10, 31))));
      
      //Test SetDate_of_birth
      user.setDate_of_birth(LocalDate.of(1990, 04, 15));
      assertThat(user, hasProperty("date_of_birth", equalTo(LocalDate.of(1990, 04, 15))));
   }

   @Test
   void testActive() {
      //Test isActive
      assertFalse(user.isActive());

      //Test setActive
      user.setActive(true);
      assertTrue(user.isActive());
   }

   @Test
   void testGetSetRole() {
      //Test GetRole
      assertTrue(user.getRole().getRole().equalsIgnoreCase("user"));
      
      //Test SetRole
      Role role = new Role(2, "ADMIN");
      user.setRole(role);
      
      assertTrue(user.getRole().getRole().equalsIgnoreCase("admin"));
   }
   
   @Test
   public void testGetRequests() {
       // Setup

       // Run the test
       final List<Request> result = user.getRequests();

       // Verify the results
   }

   @Test
   public void testAddRequest() {
       // Setup
       final Request request = new Request();
       request.setRequestID(0);
       request.setRequestDate(LocalDate.of(2017, 1, 1));
       request.setStartTime(LocalTime.of(12, 0, 0));
       request.setEndTime(LocalTime.of(12, 0, 0));
       request.setLatitude("latitude");
       request.setLongitude("longitude");
       request.setCar(new Car("registrationNumber", "carColor", "carName", false));
       final Invoice invoice = new Invoice();
       invoice.setInvoiceID(0);
       invoice.setInvoiceDate(LocalDate.of(2017, 1, 1));
       invoice.setInvoiceTime(LocalTime.of(12, 0, 0));
       invoice.setInvoiceStatus("invoiceStatus");
       invoice.setRequest(new Request());
       final Payment payment = new Payment();
       payment.setPaymentID(0);
       payment.setPaymentDate(LocalDate.of(2017, 1, 1));
       payment.setPaymentAmount(0.0);
       payment.setInvoice(new Invoice());
       payment.setPaymentType(new PaymentType());
       invoice.setPayment(payment);
       request.setInvoice(invoice);
       request.setUser(new User());
       request.setStatus("status");

       // Run the test
       user.addRequest(request);

       // Verify the results
   }

   @Test
   public void testRemoveRequest() {
       // Setup
       final Request request = new Request();
       request.setRequestID(0);
       request.setRequestDate(LocalDate.of(2017, 1, 1));
       request.setStartTime(LocalTime.of(12, 0, 0));
       request.setEndTime(LocalTime.of(12, 0, 0));
       request.setLatitude("latitude");
       request.setLongitude("longitude");
       request.setCar(new Car("registrationNumber", "carColor", "carName", false));
       final Invoice invoice = new Invoice();
       invoice.setInvoiceID(0);
       invoice.setInvoiceDate(LocalDate.of(2017, 1, 1));
       invoice.setInvoiceTime(LocalTime.of(12, 0, 0));
       invoice.setInvoiceStatus("invoiceStatus");
       invoice.setRequest(new Request());
       final Payment payment = new Payment();
       payment.setPaymentID(0);
       payment.setPaymentDate(LocalDate.of(2017, 1, 1));
       payment.setPaymentAmount(0.0);
       payment.setInvoice(new Invoice());
       payment.setPaymentType(new PaymentType());
       invoice.setPayment(payment);
       request.setInvoice(invoice);
       request.setUser(new User());
       request.setStatus("status");

       // Run the test
       user.removeRequest(request);

       // Verify the results
   }

   @Test
   public void testGetAddresses() {
       // Setup

       // Run the test
       final List<Address> result = user.getAddresses();

       // Verify the results
   }

   @Test
   public void testAddAddress() {
       // Setup
       final Address address = new Address();
       address.setAddressID(0);
       address.setLine1("line1");
       address.setLine2("line2");
       address.setLine3("line3");
       address.setCity("city");
       address.setPostcode("postcode");
       address.setCountry("country");
       address.setUser(new User());

       // Run the test
       user.addAddress(address);

       // Verify the results
   }

   @Test
   public void testRemoveAddress() {
       // Setup
       final Address address = new Address();
       address.setAddressID(0);
       address.setLine1("line1");
       address.setLine2("line2");
       address.setLine3("line3");
       address.setCity("city");
       address.setPostcode("postcode");
       address.setCountry("country");
       address.setUser(new User());

       // Run the test
       user.removeAddress(address);

       // Verify the results
   }
}
//UserTest

