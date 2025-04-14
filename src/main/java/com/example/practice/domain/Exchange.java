package com.example.practice.domain;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name="exchanges")
public class Exchange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "bookreqid")
    private Book bookReq;
    @ManyToOne
    @JoinColumn(name = "bookrequester")
    private User bookRequester;
    @ManyToOne
    @JoinColumn(name = "bookinsteadid")
    private Book bookInstead;
    @ManyToOne
    @JoinColumn(name = "bookowner")
    private User bookOwner;

    @Column(length = 50)
    private String status = "Availible";
    @Column(name="exchange_date")
    private Date exchangeDate=new Date();

    public Exchange(Book bookReq, User bookRequester, Book bookInstead, User bookOwner, String status) {
        this.bookReq = bookReq;
        this.bookRequester = bookRequester;
        this.bookInstead=bookInstead;
        this.bookOwner = bookOwner;
        this.status = status;
    }

    public Exchange() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBookInstead() {
        return bookInstead;
    }

    public void setBookInstead(Book bookInstead) {
        this.bookInstead = bookInstead;
    }

    public Book getBookReq() {
        return bookReq;
    }

    public void setBookReq(Book bookReq) {
        this.bookReq = bookReq;
    }

    public User getBookRequester() {
        return bookRequester;
    }

    public void setBookRequester(User bookRequester) {
        this.bookRequester = bookRequester;
    }

    public User getBookOwner() {
        return bookOwner;
    }

    public void setBookOwner(User bookOwner) {
        this.bookOwner = bookOwner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
