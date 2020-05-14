package springData.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import springData.domain.Location;

public interface LocationRepository extends JpaRepository<Location, Integer> {

   Location findById(int id);

   @Query("Select l from Location l where l.latitude = :latitude")
   Location findByLatitude(@Param("latitude") String latitude);

   @SuppressWarnings("unchecked")
   Location save(Location location);
}
// LocationRepository
