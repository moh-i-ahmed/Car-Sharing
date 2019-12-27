package springData.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import springData.domain.Request;
import springData.domain.User;

public interface RequestRepository extends JpaRepository<Request, Integer> {

   Request findById(int id);

   @Query("Select e from Request e where e.user= :user")
   List<Request> findAllByUser(@Param("user") User user);
   
   /*
   SELECT t1.*
   FROM lms_attendance t1
   WHERE t1.time = (SELECT MAX(t2.time)
                    FROM lms_attendance t2
                    WHERE t2.user = t1.user) */
   
   @Query("Select e from Request e where e.requestDate = (Select MAX(s.requestDate) from Request s where s.user = e.user)")
   Request findLatestByDate();

   @SuppressWarnings("unchecked")
   Request save(Request request);
}
