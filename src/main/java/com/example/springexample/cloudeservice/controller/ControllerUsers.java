package com.example.springexample.cloudeservice.controller;


import com.example.springexample.cloudeservice.dto.UsersDTO;
import com.example.springexample.cloudeservice.dto.UsersSignUpDto;
import com.example.springexample.cloudeservice.model.Users;
import com.example.springexample.cloudeservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class ControllerUsers {

    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<?> getUser(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(authService.getCurrenciesUser(new UsersDTO(userDetails.getUsername())));
    }

}
