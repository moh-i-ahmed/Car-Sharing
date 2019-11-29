package springData.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class PaymentType {

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private String paymentTypeCode;

   @Basic
   private String paymentTypeDescription;

   @OneToMany(mappedBy = "paymentType")
   private List<Payment> payments;

   public String getPaymentTypeCode() {
      return this.paymentTypeCode;
   }

   public void setPaymentTypeCode(String paymentTypeCode) {
      this.paymentTypeCode = paymentTypeCode;
   }

   public String getPaymentTypeDescription() {
      return this.paymentTypeDescription;
   }

   public void setPaymentTypeDescription(String paymentTypeDescription) {
      this.paymentTypeDescription = paymentTypeDescription;
   }

   public List<Payment> getPayments() {
      if (payments == null) {
         payments = new ArrayList<>();
      }
      return this.payments;
   }

   public void setPayments(List<Payment> payments) {
      this.payments = payments;
   }

   public void addPayment(Payment payment) {
      getPayments().add(payment);
      payment.setPaymentType(this);
   }

   public void removePayment(Payment payment) {
      getPayments().remove(payment);
      payment.setPaymentType(null);
   }

}