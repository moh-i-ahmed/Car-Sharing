package springData.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.stripe.exception.StripeException;

import springData.constants.Constants;
import springData.domain.Car;
import springData.domain.Request;
import springData.domain.User;
import springData.repository.CarAvailabilityRepository;
import springData.repository.CarRepository;
import springData.repository.RequestRepository;
import springData.repository.UserRepository;
import springData.repository.VerificationTokenRepository;
import springData.services.InvoiceService;

@Component
public class ScheduledTasks {

   private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTasks.class);

   @Autowired private InvoiceService invoiceService;

   @Autowired private UserRepository userRepo;
   @Autowired private RequestRepository requestRepo;
   @Autowired private CarRepository carRepo;
   @Autowired private CarAvailabilityRepository carAvailabilityRepo;
   @Autowired private VerificationTokenRepository verificationTokenRepo;

   /**
    * Checks for requests completed within past second and updates their status
    */
   @Scheduled(fixedRate = 1000)
   private void updateRequestTime() {
      // Active requests
      List<Request> activeRequests = requestRepo.findAllByEndTime(LocalDateTime.now(),
              Constants.REQUEST_STATUS_IN_PROGRESS, Constants.REQUEST_STATUS_UNSTARTED);

      for (Request request: activeRequests) {
         // Update user status
         User user = request.getUser();
         user.setActive(false);
         userRepo.save(user);

         // Update car status & location
         Car car = request.getCar();
         car.setIsActive(false);
         car.setLocation(request.getDropoffLocation());
         carRepo.save(car);

         // Generate Invoice for completed request
         try {
            invoiceService.generateInvoice(request);
         } catch (StripeException e) {
            LOGGER.info(e.toString());
         }
         // Update request status
         request.setStatus(Constants.REQUEST_STATUS_COMPLETE);
         requestRepo.save(request);
      }
      LOGGER.info("------------ Completed Requests Updated ----------- ");
   }

   /**
    * Deletes expired car time slots every two seconds
    */
   @Scheduled(fixedRate = 2000)
   private void deleteExpiredCarAvailabilities() {
      // Delete unfulfilled requests
      carAvailabilityRepo.deleteAllExpired(LocalDateTime.now());

      LOGGER.info("------------ Expired CarAvailabilities Deleted ----------- " + LocalDateTime.now());
   }

   /**
    * Deletes unfulfilled requests at 12AM & 12PM
    */
   @Scheduled(cron = "0 0 0,12 * * *")
   private void deleteUnfulfilledRequests() {
      // Delete expired time slots
      //requestRepo.deleteAllUnfulfilled(LocalDateTime.now().plusDays(1).plusHours(1));

      LOGGER.info("------------ Unfulfilled Requests Deleted ----------- " + LocalDateTime.now());
   }

   /**
    * Deletes expired verification tokens every hour
    */
   @Scheduled(cron = "0 0 * * * *")
   private void deleteExpiredVerificationToken() {
      // Delete expired verification tokens
      verificationTokenRepo.deleteAllExpiredVerificationTokens(LocalDateTime.now());

      LOGGER.info("------------ Expired Verification Tokens Deleted ----------- " + LocalDateTime.now());
   }

}
// ScheduledTasks
