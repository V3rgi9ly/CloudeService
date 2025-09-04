package com.example.springexample.cloudeservice.controller;

import com.example.springexample.cloudeservice.AuthServiceGrpc;
import com.example.springexample.cloudeservice.RequestRegistre;
import com.example.springexample.cloudeservice.dto.UsersDTO;
import com.example.springexample.cloudeservice.model.Users;
import com.example.springexample.cloudeservice.service.AuthService;
import com.example.springexample.cloudeservice.service.AuthServiceServer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ControllerAuth {

    private final AuthService authService;

//    private final AuthServiceGrpc.AuthServiceBlockingStub blockingStub;

    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsersDTO usersDTO) {

        authService.validateUser(usersDTO);

        return ResponseEntity.ok("sdsdsd");
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody Users user) {
        UsersDTO usersDTO = new UsersDTO();
        usersDTO.setUsername(user.getUsername());
        boolean auth = authService.signUp(user);
        return auth ? ResponseEntity.ok(usersDTO) : ResponseEntity.ok("User already exists");

    }

}
