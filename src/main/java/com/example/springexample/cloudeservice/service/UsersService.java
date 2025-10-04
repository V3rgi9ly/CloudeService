package com.example.springexample.cloudeservice.service;

import com.example.springexample.cloudeservice.model.Users;
import com.example.springexample.cloudeservice.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;

    public void save(Users user) {
        if (usersRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Login already exists");
        }
        else {
            usersRepository.save(user);
        }
    }
}
