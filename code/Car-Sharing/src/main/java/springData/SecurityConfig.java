package springData;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

   @Autowired UserDetailsService userDetailsService;
   @Autowired DataSource dataSource;

   @Bean
   public BCryptPasswordEncoder passwordEncoder() {
      BCryptPasswordEncoder pe = new BCryptPasswordEncoder();
      return pe;
   }

   @Override
   protected void configure(HttpSecurity http) throws Exception {
      http
      //AUTHORIZATION
      .authorizeRequests()
         .antMatchers("/", "/login", "/register/**").permitAll()
         .antMatchers("/", "/static/**", "/js/**", "/vendor/**", "/css/**").permitAll()
         .antMatchers("/account/**").hasRole("USER")//.hasAnyRole("USER", "ADMIN")
         .antMatchers("/user/**").hasAnyRole("USER", "ADMIN")
         .antMatchers("/admin/**", "/car/**").hasRole("ADMIN")
         .anyRequest().authenticated() // all requests ABOVE this statement require authentication
         .and() // to redirect the user when trying to access a resource to which access is not granted
           .exceptionHandling().accessDeniedPage("/access-denied")
         .and()
      .formLogin()
         .loginPage("/login")
         .loginProcessingUrl("/j_spring_security_check")
         .usernameParameter("username").passwordParameter("password")
         .defaultSuccessUrl("/success-login", true)
         .permitAll()
         .and()
      .logout()
         .logoutSuccessUrl("/login")
         .deleteCookies("JSESSIONID")
         .invalidateHttpSession(true)
         .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
         .permitAll();

      //Managing Session
      http.sessionManagement()
         .invalidSessionUrl("/login/form?expired")
         .maximumSessions(1)
         .expiredUrl("/login/form?expired");
      
      http.authorizeRequests().and() //
         .rememberMe()
            .key("uniqueAndSecret")
            .rememberMeCookieName("rememberme")
            .useSecureCookie(true) //
            .tokenRepository(this.persistentTokenRepository()) //
            .tokenValiditySeconds(1 * 24 * 60); // 30mins
   }

   @Autowired
   public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
      // Setting Service to find User in the database.
      // And Setting PassswordEncoder
      auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
   }

   @Bean
   public PersistentTokenRepository persistentTokenRepository() {
      JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
      db.setDataSource(dataSource);
      return db;
   }
}
