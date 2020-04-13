package springData.scheduler;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import springData.constants.Constants;
import springData.domain.Car;
import springData.domain.CarAvailability;
import springData.domain.Request;
import springData.domain.User;
import springData.repository.CarAvailabilityRepository;
import springData.repository.CarRepository;
import springData.repository.RequestRepository;
import springData.repository.UserRepository;

@Component
public class ScheduledTasks {

   private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

   @Autowired private UserRepository userRepo;
   @Autowired private RequestRepository requestRepo;
   @Autowired private CarRepository carRepo;
   @Autowired private CarAvailabilityRepository carAvailabilityRepo;

   // TODO update localtime to localdatetime
   @Scheduled(fixedRate = 5000)
   public void updateRequestTime() {
      logger.info("Time is " + LocalTime.now().toString());

      List<Request> activeRequests = requestRepo.findAllByEndTime(LocalDateTime.now(), Constants.REQUEST_STATUS_IN_PROGRESS);

      for (Request request: activeRequests) {
      //for (int i = 0; i < activeRequests.size(); i++) {
        // Request request = activeRequests.get(i);
        // if (request.getStatus().equalsIgnoreCase(Constants.REQUEST_STATUS_IN_PROGRESS)) {
            // Update user status
            User user = request.getUser();
            user.setActive(false);
            userRepo.save(user);

            // Update car
            Car car = request.getCar();
            car.setIsActive(false);
            car.setLocation(request.getDropoffLocation());
            carRepo.save(car);

            // Update request
            request.setStatus(Constants.REQUEST_STATUS_COMPLETE);
            requestRepo.save(request);

            logger.info("Request Ended " + request.getRequestID());
         //}
      }
   }

   //@Scheduled(fixedRate = 2000)
   public void removeCarAvailabilities() {
      List<CarAvailability> availabilites = carAvailabilityRepo.findAll();

      logger.info("------------- CarAvailabilities Updated ------------");
   }

}
// ScheduledTasks
