package com.example.springexample.cloudeservice.controller;


import com.example.springexample.cloudeservice.dto.MinIODTO;
import com.example.springexample.cloudeservice.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resource")
public class ResourceController {

    private final MinioService minioService;


    @DeleteMapping
    public ResponseEntity<?> deleteResource(@RequestParam String path,
                                            @AuthenticationPrincipal UserDetails userDetails){

        minioService.deleteResource(path, userDetails.getUsername());
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadResource(@RequestParam("object") MultipartFile file,
                                        @RequestParam String path,
                                        @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(minioService.uploadFile(file, path, userDetails.getUsername()));
    }

}
