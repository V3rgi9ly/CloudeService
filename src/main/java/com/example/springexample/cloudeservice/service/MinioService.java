package com.example.springexample.cloudeservice.service;


import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.springframework.stereotype.Service;

@Service
public class MinioService {

    private final MinioClient minioClient;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void createBcuket(String bucketName) {
        try {
            boolean found = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );

            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());

                System.out.println("✅ Bucket '" + bucketName + "' создан.");
            } else {
                System.out.println("ℹ️ Bucket '" + bucketName + "' уже существует.");
            }
        } catch (MinioException e) {
            System.err.println("Ошибка MinIO: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());

        }
    }
}
