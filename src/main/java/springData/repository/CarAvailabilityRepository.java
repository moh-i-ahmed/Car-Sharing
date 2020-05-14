package springData.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import springData.domain.CarAvailability;
import springData.domain.Request;

public interface CarAvailabilityRepository extends JpaRepository<CarAvailability, Integer> {

   CarAvailability findById(int id);

   @Query("Select c from CarAvailability c where c.accessCode = :accessCode")
   CarAvailability findByAccessCode(@Param("accessCode") String accessCode);

   @Query("Select c from CarAvailability c where c.startTime = :#{#request.startTime}"
         + " AND c.endTime = :#{#request.endTime} AND c.car = :#{#request.car}"
         + " AND c.accessCode = :#{#request.accessCode}")
   CarAvailability findByRequest(@Param("request") Request request);

   @Query("Select c from CarAvailability c where c.endTime < currentTime")
   CarAvailability findByEndTime(@Param("currentTime") LocalDateTime currentTime);

   @Modifying
   @Transactional
   @Query("Delete from CarAvailability e where e.endTime < :currentTime")
   void deleteAllExpired(@Param("currentTime") LocalDateTime currentTime);

   @SuppressWarnings("unchecked")
   CarAvailability save(CarAvailability carAvailability);
}
// CarAvailabilityRepository
