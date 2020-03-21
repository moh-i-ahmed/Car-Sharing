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
public class Location {

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private Long id;

   @Basic
   private String latitude;

   @Basic
   private String longitude;

   public Location() {
      this.latitude = "52.6197362";
      this.longitude = "-1.1263900999999805";
   }

   public Location(String latitude, String longitude) {
      super();
      this.latitude = latitude;
      this.longitude = longitude;
   }

   public Long getId() {
       return this.id;
   }

   public void setId(Long id) {
       this.id = id;
   }

   public String getLatitude() {
       return this.latitude;
   }

   public void setLatitude(String latitude) {
       this.latitude = latitude;
   }

   public String getLongitude() {
       return this.longitude;
   }

   public void setLongitude(String longitude) {
       this.longitude = longitude;
   }

}
//Location
