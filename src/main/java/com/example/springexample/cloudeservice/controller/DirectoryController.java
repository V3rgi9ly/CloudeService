package com.example.springexample.cloudeservice.controller;


import com.example.springexample.cloudeservice.service.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/directory")
public class DirectoryController {

    private final MinioService minioService;

    @GetMapping
    public ResponseEntity<?> getDirectory(@RequestParam String path) {
        minioService.getDirectory(path);
        return ResponseEntity.ok().build();
    }

    @PostMapping()
    public ResponseEntity<?> createDirectory(@RequestParam String path) {
        log.info(path);
        return ResponseEntity.ok(minioService.createFolder(path));
    }
}
