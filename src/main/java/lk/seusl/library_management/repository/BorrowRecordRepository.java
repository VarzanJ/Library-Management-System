package lk.seusl.library_management.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import lk.seusl.library_management.model.BorrowRecord;
import lk.seusl.library_management.model.Member;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    // books a member has borrowed and not yet returned
    List<BorrowRecord> findByMemberAndReturnDateIsNull(Member member);

    // all records of one member (history)
    List<BorrowRecord> findByMember(Member member);

    // all books currently borrowed
    List<BorrowRecord> findByReturnDateIsNull();

    // overdue: not returned and due date before today
    List<BorrowRecord> findByReturnDateIsNullAndDueDateBefore(LocalDate date);
}