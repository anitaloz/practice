package com.example.practice.servingwebcontent;

import com.example.practice.domain.Book;
import com.example.practice.domain.Exchange;
import com.example.practice.domain.User;
import com.example.practice.repositories.BookRepo;
import com.example.practice.repositories.ExchangeRepo;
import com.example.practice.repositories.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    @Autowired
    BookRepo bookRepo;

    @Autowired
    UserRepo userRepo;
    @Autowired
    private ExchangeRepo exchangeRepo;

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

        return "login.html";
    }

    @GetMapping("/")
    public String Log(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/main";
        }
        return "log.html";
    }

    private final int DEFAULT_PAGE_SIZE = 8; // Можно настроить

    @GetMapping("/main")
    public String main(@RequestParam(required = false) String title,
                       @RequestParam(required = false) String genre,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepo.findByUsername(auth.getName());

        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE); // Создаем объект Pageable для пагинации

        Page<Book> bookPage;
        if (title != null && !title.isEmpty() && genre != null && !genre.isEmpty()) {
            // Ищем по названию и жанру
            bookPage = bookRepo.findBooksByOwnerIsNotAndTitleContainingIgnoreCaseAndGenreContainingIgnoreCase(user, title, genre, pageable);
        } else if (title != null && !title.isEmpty()) {
            // Ищем только по названию
            bookPage = bookRepo.findBooksByOwnerIsNotAndTitleContainingIgnoreCase(user, title, pageable);
        } else if (genre != null && !genre.isEmpty()) {
            // Ищем только по жанру
            bookPage = bookRepo.findBooksByOwnerIsNotAndGenreContainingIgnoreCase(user, genre, pageable);
        } else {
            // Если нет параметров поиска, возвращаем все книги (с пагинацией)
            bookPage = bookRepo.findBooksByOwnerIsNot(user, pageable);
        }

        List<Book> books = bookPage.getContent(); // Получаем список книг с текущей страницы
        Map<Book, String> exchangeMessages=new HashMap<>();
        for(Book b:books)
        {
            List<Exchange> e=exchangeRepo.findExchangeByBookReqAndBookRequester(b, user);
            if(!e.isEmpty())
            {
                for(Exchange ex:e)
                {
                    if(ex.getStatus().equals("Requested"))
                        b.setRequested(true);
                }
            }
        }
        model.addAttribute("NotMyBook", books);
        model.addAttribute("title", title);  // Передаем параметры поиска обратно в шаблон
        model.addAttribute("genre", genre);  // чтобы сохранить значения в полях
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        return "main.html";
    }

}

