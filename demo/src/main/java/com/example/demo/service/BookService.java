package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.exception.BookNotFoundException;
import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // method ช่วย: หาหนังสือ ถ้าไม่เจอให้โยน error ของเรา
    private Book findBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    public Book getById(Long id) {
        return findBook(id);
    }

    public Book add(Book book) {
        return bookRepository.save(book);
    }

    public Book update(Long id, Book newData) {
        Book book = findBook(id);
        book.setTitle(newData.getTitle());
        book.setPrice(newData.getPrice());
        book.setAuthor(newData.getAuthor());
        return bookRepository.save(book);
    }

    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        bookRepository.deleteById(id);
    }
}