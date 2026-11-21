package com.example.demo.exception;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Long id) {
        super("ไม่พบหนังสือ id " + id);
    }
}