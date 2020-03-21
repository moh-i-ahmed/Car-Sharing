package springData.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import springData.domain.CarAvailability;

public interface CarAvailabilityRepository extends JpaRepository<CarAvailability, Integer> {

   CarAvailability findById(int id);

   @Query("Select u from CarAvailability u where u.accessCode = :accessCode")
   CarAvailability findByAccessCode(@Param("accessCode") String accessCode);

   @SuppressWarnings("unchecked")
   CarAvailability save(CarAvailability carAvailability);
}
