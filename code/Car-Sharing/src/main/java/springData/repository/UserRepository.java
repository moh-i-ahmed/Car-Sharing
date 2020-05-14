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

   @Query("Select count(*) from User u where u.enabled = TRUE")
   Integer findAllVerified();

   @Query("Select new springData.domain.User(u.firstName, u.lastName, u.isActive, u.enabled) "
         + "from User u where u.username = :username")
   User getUserDetails(@Param("username") String username);

   @SuppressWarnings("unchecked")
   User save(User user);
}
