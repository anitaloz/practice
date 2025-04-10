package com.example.practice.domain;

import com.example.practice.repositories.ExchangeRepo;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name="books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    private String isbn;
    @Column(columnDefinition = "TEXT") // Указываем, что это TEXT
    private String description;

    @Column(length = 50)
    private String genre;

    private Integer publicationYear;

    @ManyToOne
    @JoinColumn(name = "owner_id") // Связь с таблицей usr
    private User owner;

    @Transient // Это поле не будет сохранено в базе данных
    private boolean requested;

    // Геттеры и сеттеры для всех полей, включая requested

    public boolean isRequested() {
        return requested;
    }

    public void setRequested(boolean requested) {
        this.requested = requested;
    }
    @Column(length = 255)
    private String imageUrl;

    @Transient // Это поле не будет сохраняться в базе данных
    private String ownerUsername=getOwnerUsername(); // Добавили поле для имени пользователя
    public Book(String title, String author, String isbn, String description, String genre, Integer publicationYear, User owner, String imageUrl){
        this.title=title;
        this.author=author;
        this.isbn=isbn;
        this.genre=genre;
        this.description=description;
        this.owner=owner;
        this.publicationYear=publicationYear;
        //this.status=status;
        this.imageUrl=imageUrl;
    }

    public Book(){}
    public String getOwnerUsername() {
        if (owner != null) {
            return owner.getUsername();
        }
        return null; // Или другое значение по умолчанию
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

//    public String getStatus() {
//        return status;
//    }

//    public void setStatus(String status) {
//        this.status = status;
//    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // --- equals() и hashCode() ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // --- toString() (для отладки) ---
    @Override
    public String toString() {
        return "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", description='" + description + '\'' +
                ", genre='" + genre + '\'' +
                ", publicationYear=" + publicationYear +
                ", owner=" + (owner != null ? owner.getId() : null); // чтобы избежать StackOverflowError;
    }
}
