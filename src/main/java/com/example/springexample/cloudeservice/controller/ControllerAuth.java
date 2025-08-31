package com.example.springexample.cloudeservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class ControllerAuth {


    @GetMapping("/login")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok("sdsdsd");
    }

    @GetMapping ("/registration")
    public ResponseEntity<String> registration() {
        return ResponseEntity.ok("sdsdsd");
    }

}
