package com.example.springexample.cloudeservice.service;


import com.example.springexample.cloudeservice.dto.UsersDTO;
import com.example.springexample.cloudeservice.model.Users;
import com.example.springexample.cloudeservice.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UsersRepository usersRepository;

    public boolean signUp(Users user) {
        Users users = usersRepository.findByUsername(user.getUsername());
        if (users == null) {
            usersRepository.save(user);
            return true;
        }
        else {
            return false;
        }
    }


    public void validateUser(UsersDTO user) {
        Users users = usersRepository.findByUsername(user.getUsername());

    }

}
