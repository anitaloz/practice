package com.example.practice.repositories;

import com.example.practice.domain.Book;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class BookService {
    private final BookRepo bookRepo;
    
    public BookService(BookRepo bookRepo) {
        this.bookRepo = bookRepo;
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
            fileName="images/"+fileName;
            book.setImageUrl(fileName);
        }
    }

   public void deleteOldCoverImage(Book book) {
        String oldCoverImage = book.getImageUrl().substring(6);
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
