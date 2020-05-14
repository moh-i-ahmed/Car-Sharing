package springData.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import springData.domain.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

   @Query("Select i from Invoice i where i.invoiceID = :invoiceID")
   Invoice findByInvoiceID(@Param("invoiceID") String invoiceID);

   @SuppressWarnings("unchecked")
   Invoice save(Invoice invoice);
}
// InvoiceRepository
