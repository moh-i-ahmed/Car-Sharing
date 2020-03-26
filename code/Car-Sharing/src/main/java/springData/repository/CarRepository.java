package springData.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import springData.domain.Car;

public interface CarRepository extends JpaRepository<Car, Integer> {

   @Query("Select u from Car u where u.registrationNumber = :registrationNumber")
   Car findByRegistrationNumber(@Param("registrationNumber") String registrationNumber);

   @Query("Select e from Car e where e.isActive = FALSE")
   List<Car> findAllAvailable();

   @Query("Select count(*) from Car e where e.isActive = TRUE")
   Integer findAllInUse();

   @Query(value="Select e.registrationNumber, l.latitude, l.longitude from Car e INNER JOIN Location l on l.id = e.location")
   List<Car> findAllCarLocations();

   @SuppressWarnings("unchecked")
   Car save(Car car);
}
