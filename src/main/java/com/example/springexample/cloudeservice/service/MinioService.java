package com.example.springexample.cloudeservice.service;

import com.example.springexample.cloudeservice.config.MiniConfig;
import com.example.springexample.cloudeservice.dto.MinIODTO;
import com.example.springexample.cloudeservice.model.Users;
import com.example.springexample.cloudeservice.repository.UsersRepository;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MiniConfig miniConfig;
    private final MinioClient minioClient;
    private final UsersRepository usersRepository;

    public void createUserDirectory(String bucketName) {

        String objectName = "user-" + bucketName + "files" + "/";
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(miniConfig.getBucket())
                    .object(objectName)
                    .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                    .build());

            log.info("Директория челика" + bucketName + "создана");
        } catch (MinioException e) {
            log.error("Ошибка MinIO: " + e.getMessage());
        } catch (Exception e) {
            log.error("Ошибка: " + e.getMessage());
        }

    }

    public void createBucket() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket("user-files").build());

            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(miniConfig.getBucket()).build());
            } else {
                log.info("Bucket 'user-files' already exists.");
            }
        } catch (MinioException e) {
            log.error("Error occurred: " + e);
            log.error("HTTP trace: " + e.httpTrace());
        } catch (Exception e) {
            log.error("Ошибка: " + e.getMessage());
        }
    }

    public List<MinIODTO> getDirectory(String path, String username) {
        List<MinIODTO> minIODTOList = new ArrayList<>();
        Users user = usersRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Пользователь не найден: " + username);
        }
        String userid = String.valueOf(user.getId());

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(miniConfig.getBucket())
                            .prefix(userid + "/")
                            .delimiter("/")
                            .recursive(false)
                            .build()
            );


            for (Result<Item> result : results) {
                Item item = result.get();

                if (item.isDir()) {
                    String foldePath = item.objectName();
                    String folderName = extractNameFromPath(foldePath);
                    minIODTOList.add(new MinIODTO(extractPathWithoutName(foldePath, userid), folderName, "DIRECTORY"));
                } else {
                    String foldePath = item.objectName();
                    String fileName = extractNameFromPath(foldePath);
                    minIODTOList.add(new MinIODTO(extractPathWithoutName(foldePath, userid), fileName, (byte) item.size(), "FILE"));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении списка файлов", e);
        }

        return minIODTOList;
    }

    public MinIODTO createFolder(String folderName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String userid = String.valueOf(usersRepository.findByUsername(username));
        String folderPath = "user-" + userid + "files" + "/" + folderName;
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(miniConfig.getBucket())
                            .object(folderPath)
                            .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                            .build()
            );
            log.info("Папка создана: " + folderPath);
            MinIODTO minIODTO = new MinIODTO(folderPath, folderName, "DIRECTORY");
            return minIODTO;
        } catch (MinioException e) {
            log.error("Ошибка MinIO: " + e.getMessage());
        } catch (Exception e) {
            log.error("Ошибка: " + e.getMessage());
        }

        return null;
    }


    private String extractNameFromPath(String fullPath) {
        if (fullPath == null || fullPath.isEmpty()) return "";
        String[] parts = fullPath.split("/");
        return parts.length > 0 ? parts[parts.length - 1] : "";
    }

    private String extractPathWithoutName(String fullPath, String userid) {
        if (fullPath == null || fullPath.isEmpty()) return "";
        String pathWithoutUser = fullPath.replaceFirst("^" + userid + "/", "");
        int lastSlash = pathWithoutUser.lastIndexOf("/");
        return lastSlash > 0 ? pathWithoutUser.substring(0, lastSlash + 1) : "";
    }

}
