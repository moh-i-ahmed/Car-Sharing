package springData.algorithm;

import java.text.DecimalFormat;
import java.util.List;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import springData.domain.Car;
import springData.domain.CarAvailability;
import springData.domain.Request;
import springData.repository.CarAvailabilityRepository;
import springData.repository.CarRepository;
import springData.utils.AccessCodeGenerator;

@Component
public class CarAllocation {

   @Autowired private CarRepository carRepo;
   @Autowired private CarAvailabilityRepository carAvailabilityRepo;

   private static DecimalFormat decimalFormatter = new DecimalFormat("#.##");

   public TreeMap<Double, Car> carAllocator(Request request) {
      //Initial radius
      double radius = 2;

      //Find all available cars
      List<Car> availableCars = carRepo.findAllAvailable();

      //Cars near request pickup location
      TreeMap<Double, Car> carsWithinRadius = findCars(radius, request, availableCars);

      //Cars by distance (ascending order)
      return carsWithinRadius;
   }

   /**
    * Car allocation algorithm that finds available cars within radius
    *
    * @param radius
    * @param request
    * @param availableCars
    * @return TreeMap
    */
   private TreeMap<Double, Car> findCars(double radius, Request request, List<Car> availableCars) {
      // Cars near request pickup location
      TreeMap<Double, Car> carsWithinRadius = new TreeMap<Double, Car>();

      // Find distance between cars & request pickup location
      for (Car car: availableCars) {
         // No location data for car
         if (car.getLocation() == null) {
            continue;
         }
         // Car is within radius
         double distance = Haversine.haversine(
                  Double.parseDouble(request.getPickupLocation().getLatitude()),
                  Double.parseDouble(request.getPickupLocation().getLongitude()),
                  Double.parseDouble(car.getLocation().getLatitude()),
                  Double.parseDouble(car.getLocation().getLongitude()));

         distance = Double.valueOf(decimalFormatter.format(distance));

         // Add cars within radius
         if (distance <= radius) {
            // Car is available
            if (car.getCarAvailabilities().size() == 0) {
               carsWithinRadius.put(distance, car);
            }
            // Car has bookings
            else {
               // Check availability times
               for (CarAvailability availability: car.getCarAvailabilities()) {
                  // Car Available for request time slot
                  if (isOverlapping(availability, request) == false) {
                     carsWithinRadius.put(distance, car);
                  }
               }
               // Add cars it's within radius
               if (distance <= radius) {
                  carsWithinRadius.put(distance, car);
               }
            }
         }
      }
      // Expand radius if no Cars found
      while (carsWithinRadius.isEmpty() && radius <= 2) {
         // Expand radius by 0.25km
         radius += 0.25;

         // Recursive call
         carsWithinRadius = findCars(radius, request, availableCars);
      }
      // No suitable cars
      //ask user if they want a different time

      return carsWithinRadius;
   }

   //Check overlapping times
   private boolean isOverlapping(CarAvailability availability, Request request) {
      // Overlapping end & start times
      if (request.getStartTime().equals(availability.getEndTime())) {
         // S1 BEFORE E2 && S2 BEFORE E1
         return !availability.getStartTime().isAfter(request.getEndTime())
               && !request.getStartTime().isAfter(availability.getEndTime());
      } else {
         // S1 BEFORE E2 && S2 BEFORE E1
         return availability.getStartTime().isBefore(request.getEndTime())
               && request.getStartTime().isBefore(availability.getEndTime());
      }
   }
   // Adapted from: https://stackoverflow.com/questions/17106670/how-to-check-a-timeperiod-is-overlapping-another-
   // time-period-in-java

   //Function that finds first available Car
   public Car findCar(Request request) {
      try {
         Car car = new Car();
         List<Car> availableCars = carRepo.findAllAvailable();

         car = availableCars.get(0);
         car.setIsActive(true);
         carRepo.save(car);

         CarAvailability availability = new CarAvailability(request.getStartTime(), request.getEndTime(),
                 AccessCodeGenerator.generateAccessCode(), car);

         request.setAccessCode(availability.getAccessCode());
         carAvailabilityRepo.save(availability);

         car.addCarAvailability(availability);

         carRepo.save(car);

         return car;
      } catch (Exception e) {
         return null;
      }

   }

}
// CarAllocation
