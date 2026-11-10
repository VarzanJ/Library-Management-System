
package lk.seusl.library_management.service;

import lk.seusl.library_management.model.Loan;
import lk.seusl.library_management.model.Fine;
import lk.seusl.library_management.repository.FineRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class FineService {

    private static final BigDecimal DAILY_FINE_RATE =
            new BigDecimal("100.00");

    private final FineRepository fineRepository;

    public FineService(FineRepository fineRepository) {
        this.fineRepository = fineRepository;
    }

    public BigDecimal calculateFine(
            Loan loan, LocalDate returnDate) {

        long overdueDays = ChronoUnit.DAYS.between(
                loan.getDueDate(), returnDate);

        if (overdueDays <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal amount = DAILY_FINE_RATE.multiply(
                BigDecimal.valueOf(overdueDays));

        Fine fine = fineRepository
                .findByLoan(loan)
                .orElse(new Fine());

        fine.setLoan(loan);
        fine.setOverdueDays(overdueDays);
        fine.setFineAmount(amount);
        fine.setCalculationDate(returnDate);

        fineRepository.save(fine);

        return amount;
    }
}
