package springData.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import springData.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

   /*
    * @Autowired private UserDAO appUserDAO;
    *
    * @Autowired private RoleDAO appRoleDAO;
    */

   @Autowired private UserRepository userRepo;

   @Override
   public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
      //springData.domain.User appUser = this.appUserDAO.findUserAccount(userName);


      /*
       * if (appUser == null) { System.out.println("User not found in Database! : " +
       * userName); throw new UsernameNotFoundException("User " + userName +
       * " was not found in the database"); }
       *
       * System.out.println("Found User: " + appUser);
       *
       * // [ROLE_USER, ROLE_ADMIN,..] List<String> roleNames =
       * this.appRoleDAO.getRoleNames(appUser.getUserId());
       */
      springData.domain.User appUser = userRepo.findByUsername(userName);

      boolean enabled = true;
      boolean accountNonExpired = true;
      boolean credentialsNonExpired = true;
      boolean accountNonLocked = true;

      List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
      authorities.add(new SimpleGrantedAuthority("ROLE_" + appUser.getRole().getRole()));

      /*
       * if (roleNames != null) { for (String role : roleNames) { // ROLE_USER,
       * ROLE_ADMIN,.. GrantedAuthority authority = new SimpleGrantedAuthority(role);
       * grantList.add(authority); } }
       */

      UserDetails userDetails = (UserDetails) new User(appUser.getUsername(), //
              appUser.getPassword(),
              enabled,
              accountNonExpired,
              credentialsNonExpired,
              accountNonLocked,
              authorities);

      return userDetails;
   }

}
