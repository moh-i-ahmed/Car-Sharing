package springData.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * @author mia17
 */
@Entity
public class Address {

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private int addressID;

   @Basic
   private String line1;

   @Basic
   private String line2;

   @Basic
   private String line3;

   @Basic
   private String city;

   @Basic
   private String postcode;

   @Basic
   private String country;

   @ManyToOne
   private User user;

   public int getAddressID() {
      return this.addressID;
   }

   public void setAddressID(int addressID) {
      this.addressID = addressID;
   }

   public String getLine1() {
      return this.line1;
   }

   public void setLine1(String line1) {
      this.line1 = line1;
   }

   public String getLine2() {
      return this.line2;
   }

   public void setLine2(String line2) {
      this.line2 = line2;
   }

   public String getLine3() {
      return this.line3;
   }

   public void setLine3(String line3) {
      this.line3 = line3;
   }

   public String getCity() {
      return this.city;
   }

   public void setCity(String city) {
      this.city = city;
   }

   public String getPostcode() {
      return this.postcode;
   }

   public void setPostcode(String postcode) {
      this.postcode = postcode;
   }

   public String getCountry() {
      return this.country;
   }

   public void setCountry(String country) {
      this.country = country;
   }

   public User getUser() {
      return this.user;
   }

   public void setUser(User user) {
      this.user = user;
   }

}
//Address

