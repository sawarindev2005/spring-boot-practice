package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Book;

@RestController
public class HelloApplication {

    @GetMapping("/hello")
    public String getMethodName() {
        return "Hello";
    }

    @GetMapping("/book")
    public Book getBook() {
        return new Book("Java เบื้องต้น", 299.0);
    }

    // @GetMapping("/books")
    // public List<Book> getBooks() {
    //     return List.of(
    //             new Book("Java เบื้องต้น", 299.0),
    //             new Book("Spring Boot", 450.0)
    //     );
    // }

    // @GetMapping("/books/{id}")
    // public Book getBookById(@PathVariable int id) {
    //     List<Book> books = List.of(
    //             new Book("Java เบื้องต้น", 299.0),
    //             new Book("Spring Boot", 450.0)
    //     );
    //     return books.get(id);
    // }

    // @PostMapping("/books")
    // public Book createBook(@RequestBody Book book) {
    //     //TODO: process POST request
    //     return book;
    // }

}
