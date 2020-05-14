package springData;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@Configuration
@Import({ SecurityConfig.class })
public class DbConfig {

   @Bean(name = "dataSource")
   public DriverManagerDataSource dataSource() {
      DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
      driverManagerDataSource.setDriverClassName("com.mysql.jdbc.Driver");
      driverManagerDataSource.setUrl("jdbc:mysql://aa5dhnyfie97k3.ckevzmyw6aey.us-east-2.rds.amazonaws.com:8081/ebdb");
      driverManagerDataSource.setUsername("root");
      driverManagerDataSource.setPassword("2NyKnc7nncu4hC3l");
      return driverManagerDataSource;
   }

   
   @Profile("test")
   @Bean(name = "dataSource")
   public DriverManagerDataSource testDataSource() {
      DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
      driverManagerDataSource.setDriverClassName("org.h2.Driver");
      driverManagerDataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
      driverManagerDataSource.setUsername("test");
      driverManagerDataSource.setPassword("test");
      return driverManagerDataSource;
   }

}
// DBConfig

