package com.example.springexample.cloudeservice.service;


import com.example.springexample.cloudeservice.dto.UsersDTO;
import com.example.springexample.cloudeservice.dto.UsersSignUpDto;
import com.example.springexample.cloudeservice.exception.exceptionClass.NoUserException;
import com.example.springexample.cloudeservice.exception.exceptionClass.UserAlreadyExistsException;
import com.example.springexample.cloudeservice.mapper.GetMapping;
import com.example.springexample.cloudeservice.model.Users;
import com.example.springexample.cloudeservice.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UsersRepository usersRepository;
    private final GetMapping getMapping;

    public UsersDTO signUp(UsersSignUpDto user) {
        Users users = usersRepository.findByUsername(user.username());
        if (users != null) {
            throw new UserAlreadyExistsException("There is a record with this name");
        } else {
            users = getMapping.toUsers(user);
            usersRepository.save(users);
        }
        return getMapping.toUsersDto(users);
    }


    public UsersDTO validateUser(UsersSignUpDto usersSignUpDto) {
        Users users = usersRepository.findByUsername(usersSignUpDto.username());

        if (users!=null){
            UsersDTO usersDTO=getMapping.toUsersDto(users);
            return  usersDTO;
        }
        else {
            throw new NoUserException("There is no user with this username");
        }
    }

}
