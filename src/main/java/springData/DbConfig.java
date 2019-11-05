package springData;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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
      driverManagerDataSource.setUrl("jdbc:mysql://localhost:8081/carsharing");
      driverManagerDataSource.setUsername("root");
      driverManagerDataSource.setPassword("2NyKnc7nncu4hC3l");
      return driverManagerDataSource;
   }

}
