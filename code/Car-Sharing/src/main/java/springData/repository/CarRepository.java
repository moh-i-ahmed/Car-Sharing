package springData.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import springData.domain.Car;

public interface CarRepository extends JpaRepository<Car, Integer> {

   Car findById(int id);

   @Query("Select e from Car e where e.isActive = FALSE")
   List<Car> findAllAvailable();

   @SuppressWarnings("unchecked")
   Car save(Car car);
}
