package springData.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import springData.domain.User;
import springData.domain.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer> {

   @Query("Select t from VerificationToken t where t.user = :user")
   VerificationToken findByUser(@Param("user") User user);

   @Query("Select t from VerificationToken t where t.token= :token")
   VerificationToken findByToken(@Param("token") String token);

   @Modifying
   @Transactional
   @Query("Delete from VerificationToken e where e.expiryDate < :currentTime")
   void deleteAllExpiredVerificationTokens(@Param("currentTime") LocalDateTime currentTime);
}
// VerificatonToken

