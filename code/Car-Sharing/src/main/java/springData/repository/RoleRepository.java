package springData.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import springData.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

   Role findById(int id);

   @Query("Select r from Role r where r.role = :role")
   Role findByRoleName(@Param("role") String role);

}
// RoleRepository
