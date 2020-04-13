package springData.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;


@Entity
public class VerificationToken {

   private static final int EXPIRATION = 60 * 24;

   @Id
   private String token;

   @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
   @JoinColumn(nullable = false, name = "userID")
   private User user;

   private LocalDateTime expiryDate;

   public VerificationToken() {

   }

   public VerificationToken(User user) {
      super();
      this.token =  UUID.randomUUID().toString();
      this.user = user;
      this.expiryDate = LocalDateTime.now().plusMinutes(EXPIRATION);
   }

   public String getToken() {
      return token;
   }

   public void setToken(String token) {
      this.token = token;
   }

   public User getUser() {
      return user;
   }

   public void setUser(User user) {
      this.user = user;
   }

   public LocalDateTime getExpiryDate() {
      return expiryDate;
   }

   public void setExpiryDate(LocalDateTime expiryDate) {
      this.expiryDate = expiryDate;
   }

   public static int getExpiration() {
      return EXPIRATION;
   }


}
// VerificationToken
