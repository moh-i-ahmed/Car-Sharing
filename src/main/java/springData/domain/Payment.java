package springData.domain;

import java.time.LocalDateTime;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class Payment {

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private int paymentID;

   @Basic
   private LocalDateTime paymentTimestamp;

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

   public LocalDateTime getPaymentTimestamp() {
      return this.paymentTimestamp;
   }

   public void setPaymentTimestamp(LocalDateTime paymentTimestamp) {
      this.paymentTimestamp = paymentTimestamp;
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
