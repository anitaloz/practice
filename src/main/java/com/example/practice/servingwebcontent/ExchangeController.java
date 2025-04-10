package com.example.practice.servingwebcontent;

import com.example.practice.domain.Book;
import com.example.practice.domain.Exchange;
import com.example.practice.domain.User;
import com.example.practice.repositories.BookRepo;
import com.example.practice.repositories.ExchangeRepo;
import com.example.practice.repositories.UserRepo;
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
    ExchangeRepo exchangeRepo;

    @Autowired
    BookRepo bookRepo;

    @Autowired
    UserRepo userRepo;

    @GetMapping("/exchange")
    String exchangeGet(Model model)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepo.findByUsername(auth.getName());
        List<Exchange> xch=exchangeRepo.findExchangeByBookOwnerAndStatus(user, "Requested");
        model.addAttribute("Xchange", xch);
        return "exchange";
    }

    @PostMapping("/exchange/accept")
    String exchangePost(@RequestParam Long excId)
    {
        Exchange e=exchangeRepo.findExchangeById(excId);
        if(e.getStatus().equals("Requested") && e.getBookReq().getOwner()==e.getBookOwner() && e.getBookInstead().getOwner()==e.getBookRequester()) {
            Book br = e.getBookReq();
            List<Exchange> el = exchangeRepo.findExchangeByBookReqAndStatus(br, "Requested");
            for (Exchange xchnge : el) {
                xchnge.setStatus("Cancelled");
            }
            List<Exchange> el2 = exchangeRepo.findExchangeByBookInsteadAndStatus(br, "Requested");
            for (Exchange xchnge : el2) {
                xchnge.setStatus("Cancelled");
            }
            e.setStatus("Exchanged");
            br.setOwner(e.getBookRequester());
            br.setRequested(false);
            Book bo = e.getBookInstead();
            bo.setOwner(e.getBookOwner());
            bookRepo.save(br);
            bookRepo.save(bo);
            exchangeRepo.save(e);
        }
        return "redirect:/exchange";
    }

    @PostMapping("/exchange/reject")
    String exchangeRejectPost(@RequestParam Long excId)
    {
        Exchange e=exchangeRepo.findExchangeById(excId);
        e.setStatus("Cancelled");
        exchangeRepo.save(e);
        return "redirect:/exchange";
    }

}
