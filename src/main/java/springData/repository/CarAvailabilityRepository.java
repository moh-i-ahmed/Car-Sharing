package springData.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import springData.domain.CarAvailability;

public interface CarAvailabilityRepository extends JpaRepository<CarAvailability, Integer> {

   CarAvailability findById(int id);

   @SuppressWarnings("unchecked")
   CarAvailability save(CarAvailability carAvailability);
}
