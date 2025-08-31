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
        if (usersRepository.findByLogin(user.getLogin()) != null) {
            throw new IllegalArgumentException("Login already exists");
        }
        else {
            usersRepository.save(user);
        }
    }

    public Users findByLogin(String login) {

        if (usersRepository.findByLogin(login) == null) {
            throw new IllegalArgumentException("Login not found");
        }
        return usersRepository.findByLogin(login);
    }
}
