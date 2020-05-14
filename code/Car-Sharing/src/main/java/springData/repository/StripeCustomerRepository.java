package springData.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import springData.domain.StripeCustomer;

public interface StripeCustomerRepository extends JpaRepository<StripeCustomer, Integer> {

   @Query("Select c from StripeCustomer c where c.tokenID = :tokenID")
   StripeCustomer findByTokenID(@Param("tokenID") String tokenID);

}
// StripeCustomerRepository
