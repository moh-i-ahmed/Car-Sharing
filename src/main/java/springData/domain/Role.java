package springData.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Role class representing a role held by a user. Has List<Users>
 *
 * @author CO2015 Group-17
 */
@Entity
@Table(name = "role", uniqueConstraints = { @UniqueConstraint(name = "ROLE_UK", columnNames = "role") })
public class Role {

   @Id
   @Column(name = "id")
   private int id;

   @Column(name = "role")
   private String role;

   @OneToMany(mappedBy = "role")
   private Set<User> userRoles;

   public Role() {
   }

   public Role(int id, String roleName) {
      this.id = id;
      this.role = roleName;
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getRole() {
      return role;
   }

   public void setRole(String role) {
      this.role = role;
   }

   public Set<User> getUserRoles() {
      if (userRoles == null) {
         userRoles = new HashSet<User>();
      }
      return userRoles;
   }

   public void setUserRoles(Set<User> userRoles) {
      this.userRoles = userRoles;
   }

}
