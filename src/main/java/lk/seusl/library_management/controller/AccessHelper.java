package lk.seusl.library_management.controller;

import jakarta.servlet.http.HttpSession;
import lk.seusl.library_management.model.Member;

public class AccessHelper {

    // true if a logged in ADMIN or LIBRARIAN is in the session
    public static boolean isStaff(HttpSession session) {
        Member user = (Member) session.getAttribute("loggedUser");
        return user != null
                && (user.getRole().equals("ADMIN") || user.getRole().equals("LIBRARIAN"));
    }
    // true if a logged in MEMBER is in the session
    public static boolean isMember(HttpSession session) {
        Member user = (Member) session.getAttribute("loggedUser");
        return user != null && user.getRole().equals("MEMBER");
    }
}