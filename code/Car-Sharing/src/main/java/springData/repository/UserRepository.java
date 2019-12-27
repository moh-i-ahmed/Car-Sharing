package springData.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import springData.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
   User findById(int id);

   User findByUsername(String username);
   
   @Query("Select u from User u where u.userID = :id")
   User findByUserId(int id);

   @Query("Select u from User u where u.username = :username")
   User getUserByUsername(@Param("username") String username);

   @SuppressWarnings("unchecked")
   User save(User user);
}
