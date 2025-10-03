package com.example.springexample.cloudeservice.controller;

import com.example.springexample.cloudeservice.dto.UsersSignUpDto;
import com.example.springexample.cloudeservice.service.AuthService;
import com.example.springexample.cloudeservice.service.MinioService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ControllerAuth {


    private final MinioService minioService;
    private final AuthService authService;

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody UsersSignUpDto usersSignUp, HttpServletRequest request) {
        return ResponseEntity.ok(authService.validateUser(usersSignUp));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody UsersSignUpDto usersSignUp, HttpServletRequest request) {
        return ResponseEntity.ok(authService.signUp(usersSignUp));
    }


}
