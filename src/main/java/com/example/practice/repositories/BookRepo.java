package com.example.practice.repositories;

import com.example.practice.domain.Book;
import com.example.practice.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

public interface BookRepo extends JpaRepository<Book, Long> {
    List<Book> findBooksByOwnerIsNot(User owner);
    List<Book> findBooksByOwnerIs(User owner);
    int countBookByIsbn(String isbn);
    Book findBookById(Long id);
    Book getBookById(Long id);

    Page<Book> findBooksByOwnerIsNotAndTitleContainingIgnoreCaseAndGenreContainingIgnoreCase(User owner, String title, String genre, Pageable pageable);
    Page<Book> findBooksByOwnerIsNotAndTitleContainingIgnoreCase(User owner, String title, Pageable pageable);
    Page<Book> findBooksByOwnerIsNotAndGenreContainingIgnoreCase(User owner, String genre, Pageable pageable);
    Page<Book> findBooksByOwnerIsNot(User owner, Pageable pageable);

   // void deleteBookById(Long id);
    //void saveCoverImage(Book existingBook, MultipartFile file);
}
