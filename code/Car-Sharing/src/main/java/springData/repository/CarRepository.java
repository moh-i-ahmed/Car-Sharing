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

   @Query("Select e from Car e where e.isActive = TRUE")
   List<Car> findAllInUse();

   @SuppressWarnings("unchecked")
   Car save(Car car);
}
