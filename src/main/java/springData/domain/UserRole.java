package springData.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "user_role", uniqueConstraints = { @UniqueConstraint(name = "USER_ROLE_UK",
        columnNames = { "USER_ID", "ROLE_ID" }) })
public class UserRole {

   @Id
   @GeneratedValue
   @Column(name = "ID", nullable = false)
   private int id;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "USER_ID", nullable = false)
   private User appUser;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "ROLE_ID", nullable = false)
   private Role appRole;

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public User getAppUser() {
      return appUser;
   }

   public void setAppUser(User appUser) {
      this.appUser = appUser;
   }

   public Role getAppRole() {
      return appRole;
   }

   public void setAppRole(Role appRole) {
      this.appRole = appRole;
   }

}
