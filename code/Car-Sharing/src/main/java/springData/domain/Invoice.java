package springData.domain;

import java.time.LocalDateTime;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Invoice {

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private int invoiceID;

   @Basic
   private LocalDateTime invoiceTimestamp;

   @Basic
   private String invoiceStatus;

   @OneToOne
   private Payment payment;

   @OneToOne(mappedBy = "invoice")
   private Request request;

   public int getInvoiceID() {
      return this.invoiceID;
   }

   public void setInvoiceID(int invoiceID) {
      this.invoiceID = invoiceID;
   }

   public LocalDateTime getInvoiceTimestamp() {
      return this.invoiceTimestamp;
   }

   public void setInvoiceTimestamp(LocalDateTime invoiceTimestamp) {
      this.invoiceTimestamp = invoiceTimestamp;
   }

   public String getInvoiceStatus() {
      return this.invoiceStatus;
   }

   public void setInvoiceStatus(String invoiceStatus) {
      this.invoiceStatus = invoiceStatus;
   }

   public Payment getPayment() {
      return this.payment;
   }

   public void setPayment(Payment payment) {
      this.payment = payment;
   }

   public Request getRequest() {
      return this.request;
   }

   public void setRequest(Request request) {
      this.request = request;
   }

}
//Invoice
