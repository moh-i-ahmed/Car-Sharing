package springData.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import springData.domain.Address;

public interface AddressRepository extends JpaRepository<Address, Integer> {

   Address findById(int id);

   @SuppressWarnings("unchecked")
   Address save(Address address);
}
// AddressRepository
