package lk.seusl.library_management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import lk.seusl.library_management.model.Book;
import lk.seusl.library_management.repository.BookRepository;

@Controller
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    // List all books, or search by title/author/isbn/category
    @GetMapping("/admin/books")
    public String listBooks(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false, defaultValue = "title") String type,
                            HttpSession session,
                            Model model) {
        if (!AccessHelper.isStaff(session)) {
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
        return "admin-books";
    }

    // Show empty form to add a book
    @GetMapping("/admin/books/add")
    public String showAddForm(HttpSession session, Model model) {
        if (!AccessHelper.isStaff(session)) {
            return "redirect:/login";
        }
        model.addAttribute("book", new Book());
        return "book-form";
    }

    // Show form filled with an existing book
    @GetMapping("/admin/books/edit/{id}")
    public String showEditForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!AccessHelper.isStaff(session)) {
            return "redirect:/login";
        }
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            return "redirect:/admin/books";
        }
        model.addAttribute("book", book);
        return "book-form";
    }

    // Save a new or edited book
    @PostMapping("/admin/books/save")
    public String saveBook(Book book, HttpSession session) {
        if (!AccessHelper.isStaff(session)) {
            return "redirect:/login";
        }
        bookRepository.save(book);
        return "redirect:/admin/books";
    }

    // Delete a book
    @GetMapping("/admin/books/delete/{id}")
    public String deleteBook(@PathVariable Long id, HttpSession session) {
        if (!AccessHelper.isStaff(session)) {
            return "redirect:/login";
        }
        bookRepository.deleteById(id);
        return "redirect:/admin/books";
    }
}