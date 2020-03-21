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

   @Query("Select e from Request e where (e.requestDate = (Select MAX(s.requestDate) from Request s where s.user = e.user)) "
                                 + "and (e.endTime = (Select MAX(s.endTime) from Request s where s.user = e.user))")
   Request findLatestByDate();

/*   SELECT *
   FROM permlog
   ORDER BY id DESC
   LIMIT 1
   */
   @Query("Select e from Request e where e.requestID = (Select MAX(e.requestID) from Request e)")
   //@Query("Select MAX(e.requestID) from Request e")
   Request findByTopIdDesc();

   @SuppressWarnings("unchecked")
   Request save(Request request);
}
