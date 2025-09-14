package com.example.springexample.cloudeservice.mapper;


import com.example.springexample.cloudeservice.dto.UsersDTO;
import com.example.springexample.cloudeservice.dto.UsersSignUpDto;
import com.example.springexample.cloudeservice.model.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GetMapping {



    Users toUsers(UsersSignUpDto usersSignUpDto);
    UsersDTO toUsersDto(Users users);

}
