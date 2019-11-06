package springData.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author mia17
 */
@Entity
public class Address {

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private Long addressID;

   @Basic
   private String line_1;

   @Basic
   private String line_2;

   @Basic
   private String line_3;

   @Basic
   private String city;

   @Basic
   private String postcode;

   @Basic
   private String country;

   public Long getAddressID() {
      return this.addressID;
   }

   public void setAddressID(Long addressID) {
      this.addressID = addressID;
   }

   public String getLine_1() {
      return this.line_1;
   }

   public void setLine_1(String line_1) {
      this.line_1 = line_1;
   }

   public String getLine_2() {
      return this.line_2;
   }

   public void setLine_2(String line_2) {
      this.line_2 = line_2;
   }

   public String getLine_3() {
      return this.line_3;
   }

   public void setLine_3(String line_3) {
      this.line_3 = line_3;
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

}
