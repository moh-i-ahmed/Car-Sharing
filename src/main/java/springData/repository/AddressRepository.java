package springData.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import springData.domain.Address;
import springData.domain.User;

public interface AddressRepository extends JpaRepository<Address, Integer> {

   Address findById(int id);

   @Query("Select r from Role r where r.role = :role")
   Address findByRoleName(@Param("role") String role);

   @Query("Select e from Address e where e.user= :user")
   List<Address> findAllByUser(@Param("user") User user);

   @SuppressWarnings("unchecked")
   Address save(Address address);
}
