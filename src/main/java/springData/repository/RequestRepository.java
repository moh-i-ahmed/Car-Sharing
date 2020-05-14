package springData.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import springData.domain.Request;
import springData.domain.User;

public interface RequestRepository extends JpaRepository<Request, Integer> {

   Request findById(int id);

   @Query("Select count(*) from Request r where r.status = :status")
   Integer countByStatus(@Param("status") String status);

   @Query("Select e from Request e where e.user= :user")
   List<Request> findAllByUser(@Param("user") User user);

   //@Query("Select new springData.domain.Request(e.requestDate, e.startTime, e.endTime, e.status,"
   //+ " e.dropoffLocation, e.user, e.car) from Request e where e.endTime < :currentTime")
   @Query("Select e from Request e where e.endTime < :currentTime and (e.status = :status or e.status = :unstartedStatus)")
   List<Request> findAllByEndTime(@Param("currentTime") LocalDateTime currentTime, @Param("status") String status, 
         @Param("unstartedStatus") String unstartedStatus);

   @Query("Select e from Request e where"
         + " (e.requestDate = (Select MAX(s.requestDate) from Request s where s.user = e.user))"
         + " and (e.endTime = (Select MAX(s.endTime) from Request s where s.user = e.user))")
   Request findLatestByDate();

   @Query("Select e from Request e where e.requestID = (Select MAX(e.requestID) from Request e where e.user= :user )")
   Request findByTopIdDesc(@Param("user") User user);

   @Query("Select r.requestID, r.requestDate, r.startTime, r.endTime, r.status, c.carName, c.carName "
         + "from Request r INNER JOIN Car c on c.registrationNumber = r.car where r.user= :user")
   List<?> findAllSummary(@Param("user") User user);

   @Modifying
   @Transactional
   @Query("Delete from Request r where r.endTime < :currentTime and r.accessCode IS NULL and r.status IS NULL")
   void deleteAllUnfulfilled(@Param("currentTime") LocalDateTime currentTime);

   @SuppressWarnings("unchecked")
   Request save(Request request);
}
// RequestRepository
