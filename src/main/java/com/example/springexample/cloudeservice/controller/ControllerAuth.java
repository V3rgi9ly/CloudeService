package com.example.springexample.cloudeservice.controller;

import com.example.springexample.cloudeservice.AuthServiceGrpc;
import com.example.springexample.cloudeservice.RequestRegistre;
import com.example.springexample.cloudeservice.dto.UsersDTO;
import com.example.springexample.cloudeservice.dto.UsersSignUpDto;
import com.example.springexample.cloudeservice.model.Users;
import com.example.springexample.cloudeservice.service.AuthService;
import com.example.springexample.cloudeservice.service.AuthServiceServer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.swing.plaf.PanelUI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ControllerAuth {

    private final AuthService authService;


    @PostMapping("/sign-in")
    public ResponseEntity<?> login(@RequestBody UsersSignUpDto usersSignUp, HttpServletRequest request) {
        request.getSession(true);
        return ResponseEntity.ok(authService.validateUser(usersSignUp));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody UsersSignUpDto usersSignUp, HttpServletRequest request) {
        request.getSession(true);
        return ResponseEntity.ok(authService.signUp(usersSignUp));
    }


}
