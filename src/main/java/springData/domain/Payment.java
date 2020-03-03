package springData.domain;

import java.time.LocalDate;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
public class Payment {

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private int paymentID;

   @Basic
   @DateTimeFormat(iso = ISO.DATE)
   private LocalDate paymentDate;

   @Basic
   private double paymentAmount;

   @OneToOne(mappedBy = "payment")
   private Invoice invoice;

   @ManyToOne
   private PaymentType paymentType;

   public int getPaymentID() {
      return this.paymentID;
   }

   public void setPaymentID(int paymentID) {
      this.paymentID = paymentID;
   }

   public LocalDate getPaymentDate() {
      return this.paymentDate;
   }

   public void setPaymentDate(LocalDate paymentDate) {
      this.paymentDate = paymentDate;
   }

   public double getPaymentAmount() {
      return this.paymentAmount;
   }

   public void setPaymentAmount(double paymentAmount) {
      this.paymentAmount = paymentAmount;
   }

   public Invoice getInvoice() {
      return this.invoice;
   }

   public void setInvoice(Invoice invoice) {
      this.invoice = invoice;
   }

   public PaymentType getPaymentType() {
      return this.paymentType;
   }

   public void setPaymentType(PaymentType paymentType) {
      this.paymentType = paymentType;
   }

}
//Payment
