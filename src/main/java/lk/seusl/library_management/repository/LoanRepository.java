package lk.seusl.library_management.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import lk.seusl.library_management.model.Loan;
import lk.seusl.library_management.model.Member;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    // Books a member has borrowed and not yet returned
    List<Loan> findByMemberAndReturnDateIsNull(Member member);

    // All loan records of one member
    List<Loan> findByMember(Member member);

    // All books currently borrowed
    List<Loan> findByReturnDateIsNull();

    // Overdue loans that have not been returned
    List<Loan> findByReturnDateIsNullAndDueDateBefore(LocalDate date);
}