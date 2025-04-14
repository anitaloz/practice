package com.example.practice.services;

import com.example.practice.domain.Book;
import com.example.practice.domain.Exchange;
import com.example.practice.domain.User;
import com.example.practice.repositories.ExchangeRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExchangeService {
    private final ExchangeRepo exchangeRepo;

    public ExchangeService(ExchangeRepo exchangeRepo)
    {
        this.exchangeRepo=exchangeRepo;
    }

    public List<Exchange> getExchangeByBookOwnerAndStatus(User bookOwner, String status){
        return exchangeRepo.findExchangeByBookOwnerAndStatus(bookOwner, status);
    }

    public List<Exchange> getExchangeByBookReqAndBookRequester(Book b, User user){
        return exchangeRepo.findExchangeByBookReqAndBookRequester(b, user);
    }

    public List<Exchange> getExchangeByBookReqAndStatus(Book b, String status){
        return exchangeRepo.findExchangeByBookReqAndStatus(b, status);
    }
    public List<Exchange> getExchangeByBookInsteadAndStatus(Book b, String status) {
        return exchangeRepo.findExchangeByBookInsteadAndStatus(b, status);
    }

    public List<Exchange> getExchangeByBookReq(Book b){
        return exchangeRepo.findExchangeByBookReq(b);
    }
    public List<Exchange> getExchangeByBookInstead(Book b){
        return exchangeRepo.findExchangeByBookInstead(b);
    }
    public Exchange getExchangeById(Long id){
        return exchangeRepo.findExchangeById(id);
    }

    public void deleteAllExchanges(List<Exchange> b)
    {
        exchangeRepo.deleteAll(b);
    }

    public void saveExchange(Exchange e)
    {
        exchangeRepo.save(e);
    }

}
