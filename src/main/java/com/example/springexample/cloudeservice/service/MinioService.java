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
import org.mapstruct.control.MappingControl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.resps.StreamGroupInfo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
            throw new RuntimeException("User not found: " + username);
        }
        String userid = String.valueOf(user.getId());
        String folderPath = "user-" + userid + "-files" + "/";

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(miniConfig.getBucket())
                            .prefix(folderPath + path)
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

    public MinIODTO createFolder(String folderName, String username) {
        Users user = usersRepository.findByUsername(username);
        String folderPath = "user-" + user.getId() + "-files" + "/" + folderName;
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(miniConfig.getBucket())
                            .object(folderPath)
                            .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                            .build()
            );
            log.info("Folder created: " + folderPath);
            MinIODTO minIODTO = new MinIODTO(folderPath, folderName, "DIRECTORY");
            return minIODTO;
        } catch (MinioException e) {
            log.error("Ошибка MinIO: " + e.getMessage());
        } catch (Exception e) {
            log.error("Ошибка: " + e.getMessage());
        }

        return null;
    }


    public MinIODTO uploadFile(MultipartFile file, String path, String userName) {
        log.info(path);
        try {
            Users user = usersRepository.findByUsername(userName);
            String folderPath = "user-" + user.getId() + "-files" + "/";
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(miniConfig.getBucket()).build());

            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(miniConfig.getBucket()).build());
            }
            String objectName = file.getOriginalFilename();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(miniConfig.getBucket())
                            .object(folderPath + objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            log.info(String.valueOf(file.getSize()));
            return new MinIODTO(folderPath, objectName, (byte) file.getSize(), "FILE");

        } catch (Exception e) {
            log.error("Ошибка: " + e.getMessage());
            throw new RuntimeException("Ошибка при загрузке файла");
        }
    }

    public void deleteResource(String path, String username) {
        try {
            Users user = usersRepository.findByUsername(username);
//            String folderPath = "user-" + user.getId() + "-files" + "/";
//            String objectKey = folderPath + path;

            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(miniConfig.getBucket())
                    .object(path)
                    .build());


            log.info("Buket deleted");
        } catch (Exception e) {
            log.error("Ошибка: " + e.getMessage());
            throw new RuntimeException("Ошибка при загрузке файла");
        }
    }

    public List<MinIODTO> searchFile(String query, String username) {
        List<MinIODTO> minIODTOList = new ArrayList<>();
        Users users = usersRepository.findByUsername(username);
        String userid = String.valueOf(users.getId());

        String userFolder = "user-" + userid + "-files/";
        try {

            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(miniConfig.getBucket())
                            .prefix(userFolder + query)
                            .recursive(true)
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

    public InputStream downloadResource(String path, String username) {
        try {
            Users user = usersRepository.findByUsername(username);
//            String folderPath = "user-" + user.getId() + "-files/" + path;

            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(miniConfig.getBucket())
                            .object(path)
                            .build());

            return stream;

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке файла: " + e.getMessage(), e);
        }

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
