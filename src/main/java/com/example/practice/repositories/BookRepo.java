package com.example.practice.repositories;

import com.example.practice.domain.Book;
import com.example.practice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepo extends JpaRepository<Book, Long> {
    List<Book> findBooksByOwnerIsNot(User owner);



}
