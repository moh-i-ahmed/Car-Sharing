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

   @SuppressWarnings("unchecked")
   Request save(Request request);
}
