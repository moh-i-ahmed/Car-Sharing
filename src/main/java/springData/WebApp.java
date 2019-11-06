package springData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import springData.repository.RoleRepository;
import springData.repository.UserRepository;
import springData.domain.Role;
import springData.domain.User;

@SpringBootApplication
public class WebApp implements CommandLineRunner {

   @Autowired UserRepository userRepo;
   @Autowired RoleRepository roleRepo;

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
      user.setPassword(pe.encode("password"));
      user.setRole(role);

      User user2 = new User();
      user2.setFirstName("John");
      user2.setLastName("Smith");
      user2.setUsername("smithy@mail.com");
      user2.setPassword(pe.encode("password2"));
      user2.setRole(admin);

      userRepo.save(user);
      userRepo.save(user2);
   }

}
