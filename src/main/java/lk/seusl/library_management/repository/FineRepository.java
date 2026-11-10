
package lk.seusl.library_management.repository;

import lk.seusl.library_management.model.Fine;
import lk.seusl.library_management.model.Loan;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FineRepository extends JpaRepository<Fine, Long> {

    Optional<Fine> findByLoan(Loan loan);

    List<Fine> findByPaidFalse();

    List<Fine> findByPaidTrue();
}
