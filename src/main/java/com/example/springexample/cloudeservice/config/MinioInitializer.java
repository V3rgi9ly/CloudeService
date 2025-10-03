package com.example.springexample.cloudeservice.config;

import com.example.springexample.cloudeservice.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinioInitializer implements CommandLineRunner {

    private final MinioService minioService;

    @Override
    public void run(String... args) throws Exception {
        minioService.createBucket();
        System.out.println("Бакет создан");
    }
}
