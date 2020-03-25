package springData.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class StripeCustomer {

   @Column(unique = true)
   @Id
   private String tokenID;

   @OneToOne
   private User user;

   public String getTokenID() {
       return this.tokenID;
   }

   public void setTokenID(String tokenID) {
       this.tokenID = tokenID;
   }

   public User getUser() {
       return this.user;
   }

   public void setUser(User user) {
       this.user = user;
   }

}
//StripeCustomer
