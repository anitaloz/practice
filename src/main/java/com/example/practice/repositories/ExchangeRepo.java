package com.example.practice.repositories;

import com.example.practice.domain.Book;
import com.example.practice.domain.Exchange;
import com.example.practice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExchangeRepo extends JpaRepository<Exchange, Long> {
    List<Exchange> findExchangeByBookOwnerAndStatus(User bookOwner, String status);
    List<Exchange> findExchangeByBookReqAndBookRequester(Book b, User user);
    List<Exchange> findExchangeByBookReqAndStatus(Book b, String status);
    List<Exchange> findExchangeByBookInsteadAndStatus(Book b, String status);
    List<Exchange> findExchangeByBookReq(Book b);
    List<Exchange> findExchangeByBookInstead(Book b);
    Exchange findExchangeById(Long id);
}
