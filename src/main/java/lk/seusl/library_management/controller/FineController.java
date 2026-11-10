package lk.seusl.library_management.controller;

import jakarta.servlet.http.HttpSession;

import lk.seusl.library_management.model.Fine;
import lk.seusl.library_management.repository.FineRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class FineController {

    @Autowired
    private FineRepository fineRepository;

    @GetMapping("/admin/fines")
    public String viewFines(HttpSession session, Model model) {

        if (!AccessHelper.isStaff(session)) {
            return "redirect:/login";
        }

        model.addAttribute("fines", fineRepository.findAll());
        model.addAttribute("unpaidFines", fineRepository.findByPaidFalse());

        return "admin-fines";
    }

    @PostMapping("/admin/fines/pay/{id}")
    public String markAsPaid(
            @PathVariable Long id,
            HttpSession session) {

        if (!AccessHelper.isStaff(session)) {
            return "redirect:/login";
        }

        Fine fine = fineRepository.findById(id).orElse(null);

        if (fine != null && !fine.isPaid()) {
            fine.setPaid(true);
            fineRepository.save(fine);
        }

        return "redirect:/admin/fines";
    }
}