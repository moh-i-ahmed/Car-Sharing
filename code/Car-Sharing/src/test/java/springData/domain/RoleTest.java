package springData.domain;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import springData.domain.Role;
import springData.domain.User;

class RoleTest {

   List<Role> roles;

   @BeforeEach
   public void setUpRole() {
      roles = new ArrayList<Role>();
      //Create & add Roles
      Role user = new Role(1, "USER");
      Role admin = new Role(2,"ADMIN");
      Role tester = new Role(3, "TESTER");
      roles.add(user);
      roles.add(admin);
      roles.add(tester);
   }

   @Test
   public void testRole() {
      //Test constructor
      for (Role role : roles) {
         //Check role is created
         assertFalse(role.equals(null));
      }
   }

   @Test
   public void testRoleName() {
      for (Role role : roles) {
         //Checks that role has given name
         assertThat(role, hasProperty("role", anyOf(containsString("USER"), containsString("ADMIN"),
               containsString("TESTER"))));
      }
   }

   @Test
   public void testGetSetRoleName() {
      //Test GetRole
      assertThat(roles.get(0), hasProperty("role", equalTo("USER")));

      //Test SetRole
      roles.get(0).setRole("MANAGER");
      assertThat(roles.get(0), hasProperty("role", equalTo("MANAGER")));
   }

   @Test
   public void testGetSetUser() {
      //Test GetUsers
      assertTrue(roles.get(0).getUsers().isEmpty());

      //Test SetUsers
      User user1 = new User();
      User user2 = new User();

      List<User> users = new ArrayList<>();
      users.add(user1);
      users.add(user2);
      roles.get(0).setUsers(users);
   
      assertFalse(roles.get(0).getUsers().isEmpty());
   }

   @Test
   public void testAddRemoveUser() {
      //Test AddUsers
      User user1 = new User();
      User user2 = new User();

      roles.get(1).addUser(user1);
      roles.get(2).addUser(user2);

      assertThat(roles.get(1).getUsers(), not(hasSize(0)));
      assertThat(roles.get(1).getUsers(), hasSize(1));

      //Test RemoveUser
      roles.get(1).removeUser(user1);
      assertThat(roles.get(1).getUsers(), hasSize(0));
   }
}
//RoleTest

