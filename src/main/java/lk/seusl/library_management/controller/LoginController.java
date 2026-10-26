package lk.seusl.library_management.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import lk.seusl.library_management.model.Member;
import lk.seusl.library_management.repository.MemberRepository;

@Controller
public class LoginController {

    @Autowired
    private MemberRepository memberRepository;

    // Home page redirects to login
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    // Show the login page
    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    // Handle the login form
    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {

        Optional<Member> found =
                memberRepository.findByUsernameAndPassword(username, password);

        if (found.isEmpty()) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }

        Member member = found.get();
        session.setAttribute("loggedUser", member);

        if (member.getRole().equals("MEMBER")) {
            return "redirect:/member/dashboard";
        }
        return "redirect:/admin/dashboard";
    }

    // Log out and clear the session
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}