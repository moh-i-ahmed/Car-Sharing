package springData.domain;

import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "persistent_logins")
public class Persistent_logins {

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private String series;

   @Basic
   @NotNull
   private String username;

   @Basic
   @NotNull
   private String token;

   @Basic
   @Temporal(TemporalType.TIMESTAMP)
   @NotNull
   private Date last_used;

   public String getSeries() {
      return this.series;
   }

   public void setSeries(String series) {
      this.series = series;
   }

   public String getUsername() {
      return this.username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getToken() {
      return this.token;
   }

   public void setToken(String token) {
      this.token = token;
   }

   public Date getLast_used() {
      return this.last_used;
   }

   public void setLast_used(Date last_used) {
      this.last_used = last_used;
   }

}