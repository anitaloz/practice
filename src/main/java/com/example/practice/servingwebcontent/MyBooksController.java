package com.example.practice.servingwebcontent;

import com.example.practice.domain.Book;
import com.example.practice.domain.Exchange;
import com.example.practice.domain.User;
import com.example.practice.repositories.BookRepo;
import com.example.practice.repositories.BookService;
import com.example.practice.repositories.ExchangeRepo;
import com.example.practice.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
public class MyBooksController {
    @Autowired
    BookRepo bookRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    BookService bookService;
    @Value("${upload.directory}") //Укажите путь в application.properties
    private String uploadDirectory;
    @Autowired
    private ExchangeRepo exchangeRepo;

    @GetMapping("/myBooks")
    public String MyBooksGet(Model model)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user=userRepo.findByUsername(auth.getName());
        List<Book> b=bookRepo.findBooksByOwnerIs(user);
        model.addAttribute("MyBook", b);
        return "myBooks";
    }

    @GetMapping("/add")
    public String addGet(Model model)
    {
        return "add";
    }
    private String validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "Название не может быть пустым.";
        } else if (title.length() > 255) {
            return "Название не может быть длиннее 255 символов.";
        }
        return null;
    }
    private String validateAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            return "Автор не может быть пустым.";
        } else if (author.length() > 255) {
            return "Автор не может быть длиннее 255 символов.";
        }
        return null;
    }

    private String validateIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return "ISBN не может быть пустым.";
        } else if (!isbn.matches("^(?:ISBN(?:-13)?:?)(?=[0-9]{13}$)([0-9]{3}-){2}[0-9]{3}[0-9X]$")) {
            return "Неверный формат ISBN.";
        }
        return null;
    }

    private String validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            return "Описание не может быть пустым.";
        } else if (description.length() > 1000) {
            return "Описание не может быть длиннее 1000 символов.";
        }
        return null;
    }

    private String validateGenre(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            return "Жанр не может быть пустым.";
        } else if (genre.length() > 50) {
            return "Жанр не может быть длиннее 50 символов.";
        }
        return null;
    }

    private String validatePublicationYear(Integer publicationYear) {
        if (publicationYear == null) {
            return "Год публикации не может быть пустым.";
        } else if (publicationYear < 1000 || publicationYear > 2024) {
            return "Год публикации должен быть между 1000 и 2024.";
        }
        return null;
    }
    @PostMapping("/add")
    public String add(
            @RequestParam String title,
            @RequestParam String author,
            @RequestParam String isbn,
            @RequestParam String description,
            @RequestParam String genre,
            @RequestParam(defaultValue = "0") Integer publicationYear,
            @RequestParam("coverImage") MultipartFile coverImage, // Получаем загруженный файл
            RedirectAttributes redirectAttributes,
            Model model) {

        String titleError = validateTitle(title);
        String authorError = validateAuthor(author);
        String isbnError = null;//validateIsbn(isbn);
        String descriptionError = validateDescription(description);
        String genreError = validateGenre(genre);
        String publicationYearError = validatePublicationYear(publicationYear);


        if (titleError != null || authorError != null || isbnError != null ||
                descriptionError != null || genreError != null || publicationYearError != null) {

            redirectAttributes.addFlashAttribute("titleError", titleError);
            redirectAttributes.addFlashAttribute("authorError", authorError);
            redirectAttributes.addFlashAttribute("isbnError", isbnError);
            redirectAttributes.addFlashAttribute("descriptionError", descriptionError);
            redirectAttributes.addFlashAttribute("genreError", genreError);
            redirectAttributes.addFlashAttribute("publicationYearError", publicationYearError);

            redirectAttributes.addFlashAttribute("title", title);
            redirectAttributes.addFlashAttribute("author", author);
            redirectAttributes.addFlashAttribute("isbn", isbn);
            redirectAttributes.addFlashAttribute("description", description);
            redirectAttributes.addFlashAttribute("genre", genre);
            redirectAttributes.addFlashAttribute("publicationYear", publicationYear);

            return "redirect:/add";
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepo.findByUsername(auth.getName());
        String imageUrl = "";

        if (!coverImage.isEmpty()) {
            try {
                // 1. Сохраняем файл на сервере
                String originalFilename = coverImage.getOriginalFilename();
                String fileExtension = "";
                int dotIndex = originalFilename.lastIndexOf('.');
                if (dotIndex > 0 && dotIndex < originalFilename.length() - 1) {
                    fileExtension = originalFilename.substring(dotIndex);
                }
                String randomFilename = UUID.randomUUID().toString() + fileExtension;

                byte[] bytes = coverImage.getBytes();
                Path path = Paths.get(uploadDirectory + randomFilename);
                Files.write(path, bytes);

                imageUrl = "/images/" + randomFilename;
            } catch (IOException e) {
                return "add";
            }
        }

        Book b = new Book(title, author, isbn, description, genre, publicationYear, user, imageUrl);
        bookRepo.save(b);
        return "redirect:/myBooks";
    }

    @PostMapping("/update")
    public String updatePost(@RequestParam Long id,
                                 Model model)
    {
        Book b=bookRepo.findBookById(id);

        return "update";
    }

    @GetMapping("/update")
    public String upGet(Model model) {
        return "redirect:/main";
    }

    @GetMapping("/update/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Book book = bookRepo.getBookById(id);
        model.addAttribute("book", book);
        return "update";
    }

    @PostMapping("/update/{id}")
    public String updateBook(@PathVariable("id") Long id, @ModelAttribute("book") Book book, @RequestParam("coverImage") MultipartFile file, Model model, RedirectAttributes redirectAttributes) {
        Book existingBook = bookRepo.getBookById(id);
        if (existingBook == null){
            return "error";
        }
        String titleError = validateTitle(book.getTitle());
        String authorError = validateAuthor(book.getAuthor());
        String isbnError = null;//validateIsbn(book.getIsbn());
        String descriptionError = validateDescription(book.getDescription());
        String genreError = validateGenre(book.getGenre());
        String publicationYearError = validatePublicationYear(book.getPublicationYear());

        if (titleError != null || authorError != null || isbnError != null ||
                descriptionError != null || genreError != null || publicationYearError != null) {

            redirectAttributes.addFlashAttribute("titleError", titleError);
            redirectAttributes.addFlashAttribute("authorError", authorError);
            redirectAttributes.addFlashAttribute("isbnError", isbnError);
            redirectAttributes.addFlashAttribute("descriptionError", descriptionError);
            redirectAttributes.addFlashAttribute("genreError", genreError);
            redirectAttributes.addFlashAttribute("publicationYearError", publicationYearError);

            redirectAttributes.addFlashAttribute("title", book.getTitle());
            redirectAttributes.addFlashAttribute("author", book.getAuthor());
            redirectAttributes.addFlashAttribute("isbn", book.getIsbn());
            redirectAttributes.addFlashAttribute("description", book.getDescription());
            redirectAttributes.addFlashAttribute("genre", book.getGenre());
            redirectAttributes.addFlashAttribute("publicationYear", book.getPublicationYear());

            return "redirect:/update/{id}";
        }
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setDescription(book.getDescription());
        existingBook.setIsbn(book.getIsbn());
        existingBook.setGenre(book.getGenre());
        existingBook.setPublicationYear(book.getPublicationYear());

        if (!file.isEmpty()) {
            try {
                bookService.saveCoverImage(existingBook, file);
            } catch (Exception e) {
                return "error";
            }
        }
        bookRepo.save(existingBook);
        return "redirect:/myBooks";
    }

    @PostMapping("/myBooks")
    public String delete(@RequestParam Long id,  Model model)
    {
        Book b=bookRepo.getBookById(id);
        bookRepo.delete(b);
        bookService.deleteOldCoverImage(b);
        return "redirect:/myBooks";
    }

    @GetMapping("/myBooks/exchange/{id}")
    public String exchange(@PathVariable("id") Long id,  Model model)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user=userRepo.findByUsername(auth.getName());
        List<Book> b=bookRepo.findBooksByOwnerIs(user);
        model.addAttribute("MyBook", b);
        Book req=bookRepo.getBookById(id);
        model.addAttribute("ReqBook", req);
        return "myBooks";
    }

    @PostMapping("/myBooks/exchange")
    public String exchangePost(@RequestParam Long id, @RequestParam Long reqId, Model model)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepo.findByUsername(auth.getName());
        Book b=bookRepo.getBookById(id);
        Book bReq=bookRepo.getBookById(reqId);
        Exchange exchange=new Exchange(bReq,user ,b, bReq.getOwner(), "Requested");
        exchangeRepo.save(exchange);
        return "redirect:/main";
    }
}
