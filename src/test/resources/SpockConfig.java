/*import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import springData.SecurityConfig;
import springData.WebApp;

@EnableWebMvc
@Configuration
@Import({ SecurityConfig.class })
@SpringBootTest(classes = { WebApp.class, SpockConfig.class })
public class SpockConfig {

   @Profile("test")
   @Bean(name = "dataSource")
   public DriverManagerDataSource dataSource() {
      DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
      driverManagerDataSource.setDriverClassName("org.h2.Driver");
      driverManagerDataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
      driverManagerDataSource.setUsername("test");
      driverManagerDataSource.setPassword("test");
      return driverManagerDataSource;
   }

}
// SpockConfig

*/