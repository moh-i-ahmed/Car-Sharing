package springData;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import it.ozimov.springboot.mail.configuration.EnableEmailTools;
import springData.repository.RoleRepository;
import springData.repository.StripeCustomerRepository;
import springData.repository.UserRepository;
import springData.repository.AddressRepository;
import springData.repository.CarAvailabilityRepository;
import springData.repository.CarRepository;
import springData.repository.LocationRepository;
import springData.repository.RequestRepository;
import springData.constants.Constants;
import springData.domain.Address;
import springData.domain.Car;
import springData.domain.Location;
import springData.domain.Request;
import springData.domain.Role;
import springData.domain.StripeCustomer;
import springData.domain.User;

@EnableEmailTools
@SpringBootApplication
public class WebApp implements CommandLineRunner {

   @Autowired UserRepository userRepo;
   @Autowired RoleRepository roleRepo;
   @Autowired RequestRepository requestRepo;
   @Autowired AddressRepository addressRepo;
   @Autowired CarRepository carRepo;
   @Autowired LocationRepository locationRepo;
   @Autowired CarAvailabilityRepository carAvailabilityRepo;
   @Autowired StripeCustomerRepository stripeCustomerRepo;

   public static void main(String[] args) {
      SpringApplication.run(WebApp.class, args);
   }

   @Override
   public void run(String... args) throws Exception {
      
      BCryptPasswordEncoder pe = new  BCryptPasswordEncoder();

      Role role = new Role(1, "USER");
      Role admin = new Role(2, "ADMIN");

      roleRepo.save(role);
      roleRepo.save(admin);

      // Sample User account
      User user = new User();
      user.setFirstName("Scott");
      user.setLastName("Malkinson");
      user.setUsername("bob@bobmail.com");
      user.setPhoneNumber("07834572877");
      user.setDriverLicense("MAR99740614BC2TL");
      user.setPassword(pe.encode("password"));
      user.setEnabled(true);
      user.setRole(role);
      userRepo.save(user);
      
      // Sample Stripe Customer
      StripeCustomer customer = new StripeCustomer();
      customer.setTokenID("cus_HEfFSAOpO4sfTl");
      customer.setUser(user);
      stripeCustomerRepo.save(customer);

      user.setStripeCustomer(customer);
      userRepo.save(user);

      // Sample Admin Account
      User user2 = new User();
      user2.setFirstName("John");
      user2.setLastName("Smith");
      user2.setUsername("smithy@mail.com");
      user2.setPassword(pe.encode("password2"));
      user.setUsername("bob@bobmail.com");
      user2.setPhoneNumber("07906577492");
      user2.setDriverLicense("FARME100165AB5EW");
      user2.setRole(admin);

      // Sample address
      Address ad = new Address();
      ad.setLine1("60 London Street");
      ad.setLine2("Flat 214 Chapel Court");
      ad.setCity("Birmingham");
      ad.setPostcode("B12 0LD");
      ad.setCountry("UK");
      //ad.setUser(user);
      addressRepo.save(ad);

      //ad.setUser(user2);
      user.setAddress(ad);
      user2.setAddress(ad);

      userRepo.save(user);
      userRepo.save(user2);

      // Sample cars
      Car redPrius = new Car("ALIDJGLSA", "Red", "Toyota", "Prius", false);
      Car silverHilux = new Car("DBIDJLI89", "Silver", "Toyota", "Hilux", false);
      Car blackCorolla = new Car("MQ3DJLI33", "Black", "Toyota", "Corolla", false);
      Car blueCivic = new Car("JS8DJGLGB", "Blue", "Honda", "Civic", false);
      // New sample
      Car redYaris = new Car("ZBIDJGLSA", "Red", "Toyota", "Yaris", false);
      Car silverPrius = new Car("TUIDJLI89", "Silver", "Toyota", "Prius", false);
      Car blueCorolla = new Car("QS3DJLI33", "Blue", "Toyota", "Corolla", false);
      Car blackAygo = new Car("JH8DJGLPL", "Black", "Toyota", "Aygo", false);

      // Sample locations/Parked together
      Location defaultLocation1 = new Location("52.6197362", "-1.1263900999999805");
      locationRepo.save(defaultLocation1);

      Location defaultLocation2 = new Location("52.6292318", "-1.1355588999999781");
      locationRepo.save(defaultLocation2);

      Location defaultLocation3 = new Location("52.6197362", "-1.13987");
      locationRepo.save(defaultLocation3);

      Location defaultLocation4 = new Location("52.637440", "-1.146380");
      locationRepo.save(defaultLocation4);

      Location defaultLocation5 = new Location("52.640830", "-1.108340");
      locationRepo.save(defaultLocation5);

      Location defaultLocation6 = new Location("52.637440", "-1.156380");
      locationRepo.save(defaultLocation6);

      Location defaultLocation7 = new Location("52.626140", "-1.151260");
      locationRepo.save(defaultLocation7);

      // Set Car Locations
      redPrius.setLocation(defaultLocation1);
      silverHilux.setLocation(defaultLocation2);
      blueCivic.setLocation(defaultLocation3);
      blackCorolla.setLocation(defaultLocation3);

      redYaris.setLocation(defaultLocation4); //
      silverPrius.setLocation(defaultLocation5);
      blueCorolla.setLocation(defaultLocation6); //
      blackAygo.setLocation(defaultLocation7);

      // Save Cars
      carRepo.save(redPrius);
      carRepo.save(silverHilux);
      carRepo.save(blackCorolla);
      carRepo.save(blueCivic);

      carRepo.save(redYaris);
      carRepo.save(silverPrius);
      carRepo.save(blueCorolla);
      carRepo.save(blackAygo);

      // Sample Request
      Request request = new Request();
      request.setRequestDate(LocalDate.of(2020, 02, 02));
      request.setStartTime(LocalDateTime.of(2020, 02, 02, 7, 30));
      request.setEndTime(LocalDateTime.of(2020, 02, 02, 9, 30));
      request.setStatus(Constants.REQUEST_STATUS_COMPLETE);
      request.setUser((user));
      request.setDistance(2000);

      request.setPickupLocation(defaultLocation1);
      request.setDropoffLocation(defaultLocation2);
      request.setCar(redPrius);
      requestRepo.save(request);
   }

}
