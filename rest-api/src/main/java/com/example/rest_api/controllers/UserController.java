package com.example.rest_api.controllers;

import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.rest_api.dtos.UserDto;
import com.example.rest_api.repositories.UserRepository;



@RestController 
public class UserController {

    private UserRepository userRepository;
    
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping ("/users")
    public List<UserDto> getUsers() {
        // UserRepository userRepository = new UserRepository();
        return userRepository.getUsers().stream()
            .map(user -> new UserDto(user.getName(), user.getEmail()))
            .toList();
    }

    @GetMapping("/users/{id}")
    public UserDto getUserById(@PathVariable String id) {
        // Implementation for fetching user by ID
        return userRepository.getUsers().stream()
            .filter(user -> user.getId().equals(id))
            .findFirst()
            .map(user -> new UserDto(user.getName(), user.getEmail()))
            .orElse(null);
    }
    
}
