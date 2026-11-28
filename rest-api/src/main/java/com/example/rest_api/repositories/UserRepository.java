package com.example.rest_api.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.rest_api.model.UserModel;


@Repository 
public class UserRepository {
    // List users array

    private List<UserModel> users = new ArrayList<>(
        List.of(
            new UserModel("1", "John Doe", "john.doe@example.com"),
            new UserModel("2", "Jane Smith", "jane.smith@example.com"),
            new UserModel("3", "Alice Johnson", "alice.johnson@example.com")
        )
    );

    public List<UserModel> getUsers() {
        return users;
    }

    public void addUser(UserModel user) {
        users.add(user);
    }

    public void removeUser(UserModel user) {
        users.remove(user);
    }
    
}
