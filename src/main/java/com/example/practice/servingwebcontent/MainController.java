package com.example.practice.servingwebcontent;

import com.example.practice.domain.Book;
import com.example.practice.domain.User;
import com.example.practice.repositories.BookRepo;
import com.example.practice.repositories.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class MainController {

    @Autowired
    BookRepo bookRepo;

    @Autowired
    UserRepo userRepo;

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false); // Не создаем новую сессию, если ее нет

        if (request.getParameter("error") != null) {
            model.addAttribute("error", true); // Передаем флаг error в шаблон
        }

        if (session != null && session.getAttribute("loginError") != null) {
            model.addAttribute("loginError", session.getAttribute("loginError")); // Передаем сообщение об ошибке
            session.removeAttribute("loginError"); // Очищаем сессию, чтобы сообщение не отображалось постоянно
        }

        return "login";
    }

    @GetMapping("/")
    public String Log(Model model)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/main";
        }
        return "log";
    }

    @GetMapping("/main")
    public String main(Model model)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user=userRepo.findByUsername(auth.getName());
        List<Book> b=bookRepo.findBooksByOwnerIsNot(user);
        model.addAttribute("NotMyBook", b);
        return "main";
    }

    @PostMapping("/main")
    public String mainpost(Model model)
    {
        return "main";
    }


}

