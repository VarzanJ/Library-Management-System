
package lk.seusl.library_management.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

import lk.seusl.library_management.model.Book;
import lk.seusl.library_management.model.Loan;
import lk.seusl.library_management.model.Member;
import lk.seusl.library_management.repository.BookRepository;
import lk.seusl.library_management.repository.LoanRepository;
import lk.seusl.library_management.service.FineService;

@Controller
public class BorrowController {

    private static final int LOAN_DAYS = 14;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private FineService fineService;

    // ---------- MEMBER SIDE ----------

    @GetMapping("/member/books")
    public String memberBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "title") String type,
            HttpSession session,
            Model model) {

        if (!AccessHelper.isMember(session)) {
            return "redirect:/login";
        }

        List<Book> books;

        if (keyword == null || keyword.isBlank()) {
            books = bookRepository.findAll();
        } else if (type.equals("author")) {
            books = bookRepository.findByAuthorContainingIgnoreCase(keyword);
        } else if (type.equals("isbn")) {
            books = bookRepository.findByIsbnContainingIgnoreCase(keyword);
        } else if (type.equals("category")) {
            books = bookRepository.findByCategoryContainingIgnoreCase(keyword);
        } else {
            books = bookRepository.findByTitleContainingIgnoreCase(keyword);
        }

        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);

        return "member-books";
    }

    @PostMapping("/member/borrow/{bookId}")
    @Transactional
    public String borrow(
            @PathVariable Long bookId,
            HttpSession session) {

        if (!AccessHelper.isMember(session)) {
            return "redirect:/login";
        }

        Book book = bookRepository.findById(bookId).orElse(null);

        if (book == null || !book.isAvailable()) {
            return "redirect:/member/books";
        }

        Member member =
                (Member) session.getAttribute("loggedUser");

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setMember(member);
        loan.setIssueDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(LOAN_DAYS));

        loanRepository.save(loan);

        book.setAvailable(false);
        bookRepository.save(book);

        return "redirect:/member/borrow-summary/" + loan.getId();
    }

    @GetMapping("/member/borrow-summary/{id}")
    public String summary(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        if (!AccessHelper.isMember(session)) {
            return "redirect:/login";
        }

        Loan loan = loanRepository.findById(id).orElse(null);

        Member member =
                (Member) session.getAttribute("loggedUser");

        if (loan == null ||
                !loan.getMember().getId().equals(member.getId())) {
            return "redirect:/member/dashboard";
        }

        model.addAttribute("record", loan);

        return "borrow-summary";
    }

    @GetMapping("/member/my-books")
    public String myBooks(
            HttpSession session,
            Model model) {

        if (!AccessHelper.isMember(session)) {
            return "redirect:/login";
        }

        Member member =
                (Member) session.getAttribute("loggedUser");

        model.addAttribute(
                "current",
                loanRepository.findByMemberAndReturnDateIsNull(member));

        model.addAttribute(
                "history",
                loanRepository.findByMember(member));

        return "member-my-books";
    }

    // ---------- ADMIN / LIBRARIAN SIDE ----------

    @GetMapping("/admin/borrow-records")
    public String allRecords(
            HttpSession session,
            Model model) {

        if (!AccessHelper.isStaff(session)) {
            return "redirect:/login";
        }

        model.addAttribute("records", loanRepository.findAll());

        return "admin-borrow-records";
    }

    // Display overdue books

    @GetMapping("/admin/overdue-books")
    public String overdueBooks(
            HttpSession session,
            Model model) {

        if (!AccessHelper.isStaff(session)) {
            return "redirect:/login";
        }

        LocalDate today = LocalDate.now();

        List<Loan> overdueRecords =
                loanRepository
                .findByReturnDateIsNullAndDueDateBefore(today);

        model.addAttribute("overdueRecords", overdueRecords);
        model.addAttribute("today", today);
        model.addAttribute("totalOverdue", overdueRecords.size());

        return "admin-overdue-books";
    }

    // Return a book and calculate its fine

    @GetMapping("/admin/return/{id}")
    @Transactional
    public String returnBook(
            @PathVariable Long id,
            HttpSession session) {

        if (!AccessHelper.isStaff(session)) {
            return "redirect:/login";
        }

        Loan loan = loanRepository.findById(id).orElse(null);

        if (loan != null && loan.getReturnDate() == null) {

            LocalDate returnDate = LocalDate.now();

            fineService.calculateFine(loan, returnDate);

            loan.setReturnDate(returnDate);
            loanRepository.save(loan);

            Book book = loan.getBook();
            book.setAvailable(true);
            bookRepository.save(book);
        }

        return "redirect:/admin/borrow-records";
    }
}
