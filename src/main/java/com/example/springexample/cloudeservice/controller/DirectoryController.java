package com.example.springexample.cloudeservice.controller;


import com.example.springexample.cloudeservice.service.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/directory")
public class DirectoryController {

    private final MinioService minioService;

    @GetMapping
    public ResponseEntity<?> getDirectory(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String path) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        boolean isAuthenticated = auth != null && auth.isAuthenticated()
//                && !(auth instanceof AnonymousAuthenticationToken);
        return ResponseEntity.ok(minioService.getDirectory(path, userDetails.getUsername()));
    }

    @PostMapping()
    public ResponseEntity<?> createDirectory(@RequestParam String path) {
        log.info(path);
        return ResponseEntity.ok(minioService.createFolder(path));
    }
}
