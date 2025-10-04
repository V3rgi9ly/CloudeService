package com.example.springexample.cloudeservice.config;

import com.example.springexample.cloudeservice.service.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinioInitializer implements CommandLineRunner {

    private final MinioService minioService;

    @Override
    public void run(String... args) throws Exception {
        minioService.createBucket();
        log.info("Бакет создан");
    }
}
