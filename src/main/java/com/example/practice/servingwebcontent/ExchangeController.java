package com.example.practice.servingwebcontent;

import com.example.practice.domain.Book;
import com.example.practice.domain.Exchange;
import com.example.practice.domain.User;
import com.example.practice.repositories.BookRepo;
import com.example.practice.repositories.ExchangeRepo;
import com.example.practice.repositories.UserRepo;
import com.example.practice.services.BookService;
import com.example.practice.services.ExchangeService;
import com.example.practice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ExchangeController {

    @Autowired
    private ExchangeService exchangeService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @GetMapping("/exchange")
    String exchangeGet(Model model)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getByUsername(auth.getName());
        List<Exchange> xch=exchangeService.getExchangeByBookOwnerAndStatus(user, "Requested");
        model.addAttribute("Xchange", xch);
        return "exchange";
    }

    @PostMapping("/exchange/accept")
    String exchangePost(@RequestParam Long excId)
    {
        Exchange e=exchangeService.getExchangeById(excId);
        if(e.getStatus().equals("Requested") && e.getBookReq().getOwner()==e.getBookOwner() && e.getBookInstead().getOwner()==e.getBookRequester()) {
            Book br = e.getBookReq();
            List<Exchange> el = exchangeService.getExchangeByBookReqAndStatus(br, "Requested");
            for (Exchange xchnge : el) {
                xchnge.setStatus("Cancelled");
            }
            List<Exchange> el2 = exchangeService.getExchangeByBookInsteadAndStatus(br, "Requested");
            for (Exchange xchnge : el2) {
                xchnge.setStatus("Cancelled");
            }
            e.setStatus("Exchanged");
            br.setOwner(e.getBookRequester());
            br.setRequested(false);
            Book bo = e.getBookInstead();
            bo.setOwner(e.getBookOwner());
            bookService.saveBook(br);
            bookService.saveBook(bo);
            exchangeService.saveExchange(e);
        }
        return "redirect:/exchange";
    }

    @PostMapping("/exchange/reject")
    String exchangeRejectPost(@RequestParam Long excId)
    {
        Exchange e=exchangeService.getExchangeById(excId);
        e.setStatus("Cancelled");
        exchangeService.saveExchange(e);
        return "redirect:/exchange";
    }

}
