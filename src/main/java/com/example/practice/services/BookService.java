package com.example.practice.services;

import com.example.practice.domain.Book;
import com.example.practice.domain.User;
import com.example.practice.repositories.BookRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class BookService {
    private final BookRepo bookRepo;
    
    public BookService(BookRepo bookRepo) {
        this.bookRepo = bookRepo;
    }

    public List<Book> getBooksByUser(User user) {
        return bookRepo.findBooksByOwnerIs(user);
    }

    public void saveBook(Book book) {
        bookRepo.save(book);
    }

    public Book getBookById(Long id)
    {
        return bookRepo.findBookById(id);
    }

    public void deleteBook(Book book)
    {
        bookRepo.delete(book);
    }

    public List<Book> getBooksByUserNot(User user){
        return bookRepo.findBooksByOwnerIsNot(user);
    };


    public Page<Book> getBooksByOwnerIsNotAndTitleContainingIgnoreCaseAndGenreContainingIgnoreCase(User owner, String title, String genre, Pageable pageable)
    {
        return bookRepo.findBooksByOwnerIsNotAndTitleContainingIgnoreCaseAndGenreContainingIgnoreCase(owner, title, genre, pageable);
    }

    public Page<Book> getBooksByOwnerIsNotAndTitleContainingIgnoreCase(User owner, String title, Pageable pageable)
    {
        return bookRepo.findBooksByOwnerIsNotAndTitleContainingIgnoreCase(owner, title, pageable);
    }

    public Page<Book> getBooksByOwnerIsNotAndGenreContainingIgnoreCase(User owner, String genre, Pageable pageable)
    {
        return bookRepo.findBooksByOwnerIsNotAndGenreContainingIgnoreCase(owner, genre, pageable);
    }

    public Page<Book> getBooksByOwnerIsNot(User owner, Pageable pageable)
    {
        return bookRepo.findBooksByOwnerIsNot(owner, pageable);
    }

    public String validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "Название не может быть пустым.";
        } else if (title.length() > 255) {
            return "Название не может быть длиннее 255 символов.";
        }
        return null;
    }
    public String validateAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            return "Автор не может быть пустым.";
        } else if (author.length() > 255) {
            return "Автор не может быть длиннее 255 символов.";
        }
        return null;
    }

    public String validateIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return "ISBN не может быть пустым.";
        }
        else if (!isbn.matches("^(?:ISBN(?:-13)?:?\\s*)?(978|979)[-\\s]?[0-9]{1,5}[-\\s]?[0-9]{1,7}[-\\s]?[0-9]{1,6}[-\\s]?[0-9X]$")) {
            return "Неверный формат ISBN.";
        }
        return null;
    }


    public String validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            return "Описание не может быть пустым.";
        } else if (description.length() > 1000) {
            return "Описание не может быть длиннее 1000 символов.";
        }
        return null;
    }

    public String validateGenre(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            return "Жанр не может быть пустым.";
        } else if (genre.length() > 50) {
            return "Жанр не может быть длиннее 50 символов.";
        }
        return null;
    }

    public String validatePublicationYear(Integer publicationYear) {
        if (publicationYear == null) {
            return "Год публикации не может быть пустым.";
        } else if (publicationYear < 1000 || publicationYear > 2024) {
            return "Год публикации должен быть между 1000 и 2024.";
        }
        return null;
    }

    public void deleteOldCoverImage(Book book) {
        if(book.getImageUrl()!=null && !book.getImageUrl().isEmpty()) {
            String oldCoverImage = book.getImageUrl().substring(7);
            if (oldCoverImage != null && !oldCoverImage.isEmpty()) {
                Path oldFilePath = Paths.get(uploadPath, oldCoverImage);
                try {
                    Files.deleteIfExists(oldFilePath);
                } catch (IOException e) {
                    // Логируем ошибку, но не прерываем процесс
                    System.err.println("Failed to delete old cover image: " + e.getMessage());
                }
            }
        }
    }

    @Value("${upload.directory}") // Читаем путь из application.properties
    private String uploadPath;
    public void saveCoverImage(Book book, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            // 1. Генерируем уникальное имя файла
            deleteOldCoverImage(book);
            String uuidFile = UUID.randomUUID().toString();
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String fileName = uuidFile + fileExtension;

            // 2. Создаем путь для сохранения файла
            Path filePath = Paths.get(uploadPath, fileName);

            // 3. Сохраняем файл
            try {
                Files.write(filePath, file.getBytes());
            } catch (IOException e) {
                throw new IOException("Failed to save cover image: " + e.getMessage());
            }
            fileName="/images/"+fileName;
            book.setImageUrl(fileName);
        }
    }
}
