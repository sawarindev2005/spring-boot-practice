package com.example.demo.model;

import jakarta.persistence.Entity;            // ใหม่
import jakarta.persistence.GeneratedValue;    // ใหม่
import jakarta.persistence.GenerationType;    // ใหม่
import jakarta.persistence.Id;                // ใหม่
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity                                       // ใหม่
public class Book {

    @Id                                                    // ใหม่
    @GeneratedValue(strategy = GenerationType.IDENTITY)    // ใหม่
    private Long id;                                       // ใหม่

    // private String title;
    // private double price;
    @NotBlank(message = "ชื่อหนังสือห้ามว่าง")
    @Size(max = 100, message = "ชื่อยาวเกินไป")
    private String title;

    @Positive(message = "ราคาต้องมากกว่า 0")         // ใหม่
    private double price;

    @NotBlank(message = "ชื่อผู้แต่งห้ามว่าง")
    private String author;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Book() {
    }

    public Book(String title, double price) {
        this.title = title;
        this.price = price;
    }

    public Long getId() {                      // ใหม่
        return id;
    }

    public void setId(Long id) {               // ใหม่
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
