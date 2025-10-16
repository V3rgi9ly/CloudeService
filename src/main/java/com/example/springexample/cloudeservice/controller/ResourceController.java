package com.example.springexample.cloudeservice.controller;


import com.example.springexample.cloudeservice.config.MiniConfig;
import com.example.springexample.cloudeservice.model.Users;
import com.example.springexample.cloudeservice.repository.UsersRepository;
import com.example.springexample.cloudeservice.service.MinioService;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resource")
public class ResourceController {

    private final MinioService minioService;

    @GetMapping
    public ResponseEntity<?> getResources(@RequestParam("path") String path,
                                          @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(minioService.getResources(path, userDetails.getUsername()));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteResource(@RequestParam("path") String path,
                                            @AuthenticationPrincipal UserDetails userDetails) {

        minioService.deleteResource(path, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadResource(@RequestParam("object") MultipartFile file,
                                            @RequestParam String path,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(minioService.uploadFile(file, path, userDetails.getUsername()));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchResource(@RequestParam("query") String query,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(minioService.searchFile(query, userDetails.getUsername()));
    }

    @GetMapping("/download")
    public ResponseEntity<?> downloadResource(@RequestParam("path") String path,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        InputStream downloadFile=minioService.downloadResource(path, userDetails.getUsername());

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(new InputStreamResource(downloadFile));

    }

    @GetMapping("/move")
    public ResponseEntity<?> moveResources(@RequestParam("from") String from,
                                           @RequestParam("to")String to,
                                           @AuthenticationPrincipal UserDetails userDetails){

        return ResponseEntity.ok(minioService.moveResources(from,to,userDetails.getUsername()));
    }


}
