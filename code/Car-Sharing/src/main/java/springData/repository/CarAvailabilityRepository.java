package springData.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import springData.domain.CarAvailability;

public interface CarAvailabilityRepository extends JpaRepository<CarAvailability, Integer> {

   CarAvailability findById(int id);

   @SuppressWarnings("unchecked")
   CarAvailability save(CarAvailability carAvailability);
}
