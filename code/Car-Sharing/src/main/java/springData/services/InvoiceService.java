package springData.services;

import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;

import springData.constants.Constants;
import springData.domain.Invoice;
import springData.domain.Request;
import springData.repository.InvoiceRepository;
import springData.repository.RequestRepository;
import springData.repository.StripeCustomerRepository;
import springData.repository.UserRepository;

@Service
public class InvoiceService {

   private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);

   @Autowired private StripeService stripeService;

   @Autowired UserRepository userRepo;
   @Autowired InvoiceRepository invoiceRepo;
   @Autowired private RequestRepository requestRepo;
   @Autowired StripeCustomerRepository stripeCustomerRepo;

   /**
    * Generates a Stripe invoice using StripeService & and stores the ID in the database
    * 
    * @param request - contains user request details
    * @throws StripeException
    */
   public void generateInvoice(Request request) throws StripeException {
      // Calculate payment amount
      double paymentAmount = generatePaymentAmount(request);

      // Generate Stripe invoice & retrieve invoice ID
      String invoiceID = stripeService.createInvoice(request.getUser(), paymentAmount);

      if (invoiceID.isEmpty()) {
         logger.info("Error creating Invoice for " + request.getUser().getUsername());
      }
      // Invoice entity
      Invoice invoice = new Invoice();

      // Save invoice
      invoice.setInvoiceID(invoiceID);

      // Completed request
      if (!request.getStatus().equalsIgnoreCase(Constants.REQUEST_STATUS_CANCELLED)) {
         invoice.setDistanceCharge((request.getDistance()/1000 * Constants.PRICE_PER_KILOMETER)/100);
         invoice.setTimeCharge((ChronoUnit.MINUTES.between(request.getStartTime(), request.getEndTime())
               * Constants.PRICE_PER_MINUTE)/100);
      }

      invoice.setTotalAmount(paymentAmount/100);
      invoiceRepo.save(invoice);

      // Update request
      request.setInvoice(invoice);
      requestRepo.save(request);
      
      invoice.setRequest(request);
      invoiceRepo.save(invoice);

      logger.info("Invoice Created for " + request.getUser().getUsername());
   }

   /**
    * Calculates payment amount based on request status, distance & time
    *
    * @param request - contains user request details
    * @return - amount to be paid
    */
   private double generatePaymentAmount(Request request) {
      // Cancellation fee
      if (request.getStatus().equalsIgnoreCase(Constants.REQUEST_STATUS_CANCELLED)) {
         return (int) Constants.PRICE_CANCELLATION;
      }
      // Minutes between Start & End time
      long timeDifference = ChronoUnit.MINUTES.between(request.getStartTime(), request.getEndTime()); 

      // BASE_CHARGE + (DISTANCE *  0.2) + (Time * 0.1)
      double amount = Constants.PRICE_BASE_CHARGE + (request.getDistance()/1000 * Constants.PRICE_PER_KILOMETER)
            + (timeDifference * Constants.PRICE_PER_MINUTE);

      logger.info("Amount is " + amount);
      return amount;
   }

}
// InvoiceService
