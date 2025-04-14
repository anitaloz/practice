package com.example.practice.servingwebcontent;

import com.example.practice.domain.Book;
import com.example.practice.domain.Exchange;
import com.example.practice.domain.User;
import com.example.practice.services.BookService;
import com.example.practice.repositories.ExchangeRepo;
import com.example.practice.services.ExchangeService;
import com.example.practice.services.UserService;
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
    private BookService bookService;
    @Autowired
    private UserService userService;
    @Value("${upload.directory}")
    private String uploadDirectory;
    @Autowired
    private ExchangeService exchangeService;

    @GetMapping("/myBooks")
    public String MyBooksGet(Model model)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user=userService.getByUsername(auth.getName());
        List<Book> b=bookService.getBooksByUser(user);
        model.addAttribute("MyBook", b);
        return "myBooks";
    }

    @GetMapping("/add")
    public String addGet(Model model)
    {
        return "add";
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

        String titleError = bookService.validateTitle(title);
        String authorError = bookService.validateAuthor(author);
        String isbnError = bookService.validateIsbn(isbn);
        String descriptionError = bookService.validateDescription(description);
        String genreError = bookService.validateGenre(genre);
        String publicationYearError = bookService.validatePublicationYear(publicationYear);


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
        User user = userService.getByUsername(auth.getName());
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
        bookService.saveBook(b);
        return "redirect:/myBooks";
    }

    @PostMapping("/update")
    public String updatePost(@RequestParam Long id,
                                 Model model)
    {
        Book b=bookService.getBookById(id);

        return "update";
    }

    @GetMapping("/update")
    public String upGet(Model model) {
        return "redirect:/main";
    }

    @GetMapping("/update/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        return "update";
    }

    @PostMapping("/update/{id}")
    public String updateBook(@PathVariable("id") Long id, @ModelAttribute("book") Book book, @RequestParam("coverImage") MultipartFile file, Model model, RedirectAttributes redirectAttributes) {
        Book existingBook = bookService.getBookById(id);
        if (existingBook == null){
            return "error";
        }
        String titleError = bookService.validateTitle(book.getTitle());
        String authorError = bookService.validateAuthor(book.getAuthor());
        String isbnError = bookService.validateIsbn(book.getIsbn());
        String descriptionError = bookService.validateDescription(book.getDescription());
        String genreError = bookService.validateGenre(book.getGenre());
        String publicationYearError = bookService.validatePublicationYear(book.getPublicationYear());

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
        bookService.deleteOldCoverImage(existingBook);
        if (!file.isEmpty()) {
            try {
                bookService.saveCoverImage(existingBook, file);
            } catch (Exception e) {
                return "error";
            }
        }
        bookService.saveBook(existingBook);
        return "redirect:/myBooks";
    }

    @PostMapping("/myBooks")
    public String delete(@RequestParam Long id,  Model model)
    {
        Book b=bookService.getBookById(id);
        List<Exchange> x=exchangeService.getExchangeByBookReq(b);
        List<Exchange> x2=exchangeService.getExchangeByBookInstead(b);
        if(!x.isEmpty())
        {
            exchangeService.deleteAllExchanges(x);
        }
        if(x2!=null)
        {
            exchangeService.deleteAllExchanges(x2);
        }
        bookService.deleteBook(b);
        bookService.deleteOldCoverImage(b);
        return "redirect:/myBooks";
    }

    @GetMapping("/myBooks/exchange/{id}")
    public String exchange(@PathVariable("id") Long id,  Model model)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user=userService.getByUsername(auth.getName());
        List<Book> b=bookService.getBooksByUser(user);
        model.addAttribute("MyBook", b);
        Book req=bookService.getBookById(id);
        model.addAttribute("ReqBook", req);
        return "myBooks";
    }

    @PostMapping("/myBooks/exchange")
    public String exchangePost(@RequestParam Long id, @RequestParam Long reqId, Model model)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getByUsername(auth.getName());
        Book b=bookService.getBookById(id);
        Book bReq=bookService.getBookById(reqId);
        Exchange exchange=new Exchange(bReq,user ,b, bReq.getOwner(), "Requested");
        exchangeService.saveExchange(exchange);
        return "redirect:/main";
    }
}
