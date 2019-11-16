package springData;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import springData.repository.RoleRepository;
import springData.repository.UserRepository;
import springData.repository.AddressRepository;
import springData.domain.Address;
import springData.domain.Role;
import springData.domain.User;

@SpringBootApplication
public class WebApp implements CommandLineRunner {

   @Autowired UserRepository userRepo;
   @Autowired RoleRepository roleRepo;
   @Autowired AddressRepository addressRepo;

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
      user.setFirstName("Bob");
      user.setLastName("Bobson");
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
      ad.setLine1("62 London Road");
      ad.setLine2("Flat 14 Bourj Towers");
      ad.setCity("Leicester");
      ad.setPostcode("LE2 0QD");
      ad.setCountry("UK");
      addressRepo.save(ad);

      user.getAddresses().add(ad);

      userRepo.save(user);
      userRepo.save(user2);
   }

}
