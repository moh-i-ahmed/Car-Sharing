package springData.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Role {

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private int roleID;

   @Basic
   private String role;

   @OneToMany(mappedBy = "role")
   private List<User> users;

   public Role() {}

   public Role(int id, String role) {
      this.roleID = id;
      this.role = role;
   }

   public int getRoleID() {
      return this.roleID;
   }

   public void setRoleID(int roleID) {
      this.roleID = roleID;
   }

   public String getRole() {
      return this.role;
   }

   public void setRole(String role) {
      this.role = role;
   }

   public List<User> getUsers() {
      if (users == null) {
         users = new ArrayList<>();
      }
      return this.users;
   }

   public void setUsers(List<User> users) {
      this.users = users;
   }

   public void addUser(User user) {
      getUsers().add(user);
      user.setRole(this);
   }

   public void removeUser(User user) {
      getUsers().remove(user);
      user.setRole(null);
   }

}
//Role
