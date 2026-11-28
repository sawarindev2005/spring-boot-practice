package com.example.rest_api.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.rest_api.dtos.Message;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
public class HelloController {

    @GetMapping("/hello")
    public Message sayHello() {
        return new Message("Hello world");
    }

    @PostMapping("/hello")
    public Message postHello(@RequestBody Message message) {

        return message;
    }
    
    
}
