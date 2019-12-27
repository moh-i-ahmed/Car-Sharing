package springData.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
public class Invoice {

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private int invoiceID;

   @Basic
   @DateTimeFormat(iso = ISO.DATE)
   private LocalDate invoiceDate;

   @Basic
   private LocalTime invoiceTime;

   @Basic
   private String invoiceStatus;

   @OneToOne
   @JoinColumn(name = "REQUESTID")
   private Request request;

   @OneToOne
   private Payment payment;

   public int getInvoiceID() {
      return this.invoiceID;
   }

   public void setInvoiceID(int invoiceID) {
      this.invoiceID = invoiceID;
   }

   public LocalDate getInvoiceDate() {
      return this.invoiceDate;
   }

   public void setInvoiceDate(LocalDate invoiceDate) {
      this.invoiceDate = invoiceDate;
   }

   public LocalTime getInvoiceTime() {
      return this.invoiceTime;
   }

   public void setInvoiceTime(LocalTime invoiceTime) {
      this.invoiceTime = invoiceTime;
   }

   public String getInvoiceStatus() {
      return this.invoiceStatus;
   }

   public void setInvoiceStatus(String invoiceStatus) {
      this.invoiceStatus = invoiceStatus;
   }

   public Request getRequest() {
      return this.request;
   }

   public void setRequest(Request request) {
      this.request = request;
   }

   public Payment getPayment() {
      return this.payment;
   }

   public void setPayment(Payment payment) {
      this.payment = payment;
   }

}