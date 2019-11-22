package springData;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import springData.repository.RoleRepository;
import springData.repository.UserRepository;
import springData.utils.AccessCodeGenerator;
import springData.repository.AddressRepository;
import springData.repository.CarAvailabilityRepository;
import springData.repository.CarRepository;
import springData.domain.Address;
import springData.domain.Car;
import springData.domain.CarAvailability;
import springData.domain.Role;
import springData.domain.User;

@SpringBootApplication
public class WebApp implements CommandLineRunner {

   @Autowired UserRepository userRepo;
   @Autowired RoleRepository roleRepo;
   @Autowired AddressRepository addressRepo;
   @Autowired CarRepository carRepo;
   @Autowired CarAvailabilityRepository carAvailabilityRepo;

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

      User user = new User();
      user.setFirstName("Scott");
      user.setLastName("Malkinson");
      user.setUsername("bob@bobmail.com");
      user.setPhoneNumber("07834572877");
      user.setDate_of_birth(LocalDate.of(1985, 10, 31));
      user.setDriverLicense("MORQ6785368M01");
      user.setPassword(pe.encode("password"));
      user.setRole(role);

      User user2 = new User();
      user2.setFirstName("John");
      user2.setLastName("Smith");
      user2.setUsername("smithy@mail.com");
      user2.setPassword(pe.encode("password2"));
      user.setUsername("bob@bobmail.com");
      user2.setPhoneNumber("07906577492");
      user2.setDriverLicense("PLF53GHT4368TG6");
      user2.setDate_of_birth(LocalDate.of(1995, 02, 3));
      user2.setRole(admin);

      Address ad = new Address();
      ad.setLine1("60 London Street");
      ad.setLine2("Flat 214 Chapel Court");
      ad.setCity("Birmingham");
      ad.setPostcode("B12 0LD");
      ad.setCountry("UK");
      ad.setUser(user);
      addressRepo.save(ad);

      ad.setUser(user2);
      user.getAddresses().add(ad);
      user2.addAddress(ad);

      userRepo.save(user);
      userRepo.save(user2);

      // Add sample cars
      Car redPrius = new Car("ALIDJGLSA", "Red", "Prius", false);
      Car silverHilux = new Car("DBIDJLI89", "Silver", "Hilux", false);
      Car blackCorolla = new Car("MQ3DJLI33", "Black", "Corolla", false);
      Car blueCivic = new Car("JS8DJGLGB", "Blue", "Civic", true);
      
      //Parked together
      redPrius.setLongitude("-1.1263900999999805");
      redPrius.setLatitude("52.6197362");
      
      silverHilux.setLongitude("-1.1355588999999781");
      silverHilux.setLatitude("52.6292318");
       
      carRepo.save(redPrius);
      carRepo.save(silverHilux);
      carRepo.save(blackCorolla);
      carRepo.save(blueCivic);
    
   }

}
