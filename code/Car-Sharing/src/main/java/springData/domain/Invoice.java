package springData.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Invoice {

   @Id
   private String invoiceID;

   @Basic
   private double totalAmount;

   @Basic
   private double distanceCharge;

   @Basic
   private double timeCharge;

   @OneToOne(mappedBy = "invoice")
   private Request request;

   public String getInvoiceID() {
      return this.invoiceID;
   }

   public void setInvoiceID(String invoiceID) {
      this.invoiceID = invoiceID;
   }

   public double getTotalAmount() {
      return this.totalAmount;
   }

   public void setTotalAmount(double totalAmount) {
      this.totalAmount = totalAmount;
   }

   public double getDistanceCharge() {
      return this.distanceCharge;
   }

   public void setDistanceCharge(double distanceCharge) {
      this.distanceCharge = distanceCharge;
   }

   public double getTimeCharge() {
      return this.timeCharge;
   }

   public void setTimeCharge(double timeCharge) {
      this.timeCharge = timeCharge;
   }

   public Request getRequest() {
      return this.request;
   }

   public void setRequest(Request request) {
      this.request = request;
   }

}
// Invoice
