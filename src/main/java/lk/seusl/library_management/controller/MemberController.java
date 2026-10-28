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
import lk.seusl.library_management.model.Member;
import lk.seusl.library_management.repository.MemberRepository;

@Controller
public class MemberController {

    @Autowired
    private MemberRepository memberRepository;

    // List all members
    @GetMapping("/admin/members")
    public String listMembers(HttpSession session, Model model) {
        if (!AccessHelper.isStaff(session)) {
            return "redirect:/login";
        }
        List<Member> members = memberRepository.findAll();
        model.addAttribute("members", members);
        return "admin-members";
    }

    // Show empty form to register a member
    @GetMapping("/admin/members/add")
    public String showAddForm(HttpSession session, Model model) {
        if (!AccessHelper.isStaff(session)) {
            return "redirect:/login";
        }
        model.addAttribute("member", new Member());
        return "member-form";
    }

    // Show form filled with an existing member
    @GetMapping("/admin/members/edit/{id}")
    public String showEditForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!AccessHelper.isStaff(session)) {
            return "redirect:/login";
        }
        Member member = memberRepository.findById(id).orElse(null);
        if (member == null) {
            return "redirect:/admin/members";
        }
        model.addAttribute("member", member);
        return "member-form";
    }

    // Save a new or edited member
    @PostMapping("/admin/members/save")
    public String saveMember(Member member, HttpSession session, Model model) {
        if (!AccessHelper.isStaff(session)) {
            return "redirect:/login";
        }

        // Block duplicate usernames (belonging to a different member)
        Member existing = memberRepository.findByUsername(member.getUsername()).orElse(null);
        if (existing != null && !existing.getId().equals(member.getId())) {
            model.addAttribute("error", "Username already exists");
            return "member-form";
        }

        memberRepository.save(member);
        return "redirect:/admin/members";
    }

    // Delete a member
    @GetMapping("/admin/members/delete/{id}")
    public String deleteMember(@PathVariable Long id, HttpSession session) {
        if (!AccessHelper.isStaff(session)) {
            return "redirect:/login";
        }

        // Do not let the logged-in user delete their own account
        Member loggedUser = (Member) session.getAttribute("loggedUser");
        if (loggedUser.getId().equals(id)) {
            return "redirect:/admin/members";
        }

        memberRepository.deleteById(id);
        return "redirect:/admin/members";
    }
}