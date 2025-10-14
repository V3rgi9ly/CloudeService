package com.example.springexample.cloudeservice.controller;


import com.example.springexample.cloudeservice.service.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/directory")
public class DirectoryController {

    private final MinioService minioService;

    @GetMapping
    public ResponseEntity<?> getDirectory(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("path") String path) {
        return ResponseEntity.status(HttpStatus.OK).body(minioService.getDirectory(path, userDetails.getUsername()));
    }

    @PostMapping()
    public ResponseEntity<?> createDirectory(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("path") String path) {
        log.info(path);
        return ResponseEntity.status(HttpStatus.CREATED).body(minioService.createFolder(path, userDetails.getUsername()));
    }
}
