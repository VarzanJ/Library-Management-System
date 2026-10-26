package lk.seusl.library_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

    // Admin / Librarian dashboard
    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session) {
        if (!AccessHelper.isStaff(session)) {
            return "redirect:/login";
        }
        return "admin-dashboard";
    }

    // Member dashboard
    @GetMapping("/member/dashboard")
    public String memberDashboard(HttpSession session) {
        if (!AccessHelper.isMember(session)) {
            return "redirect:/login";
        }
        return "member-dashboard";
    }
}