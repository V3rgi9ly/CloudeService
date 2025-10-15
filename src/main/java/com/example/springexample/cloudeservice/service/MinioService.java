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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MiniConfig miniConfig;
    private final MinioClient minioClient;
    private final UsersRepository usersRepository;

    public void createUserDirectory(String userId) {

        String objectName = "user-" + userId + "files" + "/";
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(miniConfig.getBucket())
                    .object(objectName)
                    .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                    .build());

            log.info("Директория челика" + userId + "создана");
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
                log.info(miniConfig.getBucket());
            }
        } catch (MinioException e) {
            log.error("Error occurred: " + e);
            log.error("HTTP trace: " + e.httpTrace());
        } catch (Exception e) {
            log.error("Ошибка: " + e.getMessage());
        }
    }

    public MinIODTO getResources(String path, String username) {

        Users user = usersRepository.findByUsername(username);
        String userid = String.valueOf(user.getId());
        String folderPath = "user-" + userid + "-files" + "/" + path;

        try {
            if (folderPath.endsWith("/")) {
                GetObjectResponse getObjectResponse = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(miniConfig.getBucket())
                                .object(folderPath)
                                .build()
                );
                log.info(getObjectResponse.toString());

                return new MinIODTO(folderPath, folderPath, "DIRECTORY");
            } else {
                GetObjectResponse getObjectResponse = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(miniConfig.getBucket())
                                .object(folderPath)
                                .build()
                );
                log.info(getObjectResponse.toString());
                return new MinIODTO(folderPath, folderPath, "FILE");
            }


        } catch (Exception e) {
            throw new RuntimeException("dsfdsff" + e.getMessage());
        }
    }

    public List<MinIODTO> getDirectory(String pathRaw, String username) {

        List<MinIODTO> out = new ArrayList<>();
        Users user = usersRepository.findByUsername(username);
        if (user == null) throw new RuntimeException("User not found");

        String userPrefix = "user-" + user.getId() + "-files/";
        String prefix = userPrefix + (pathRaw == null ? "" : pathRaw);

        log.info("getDirectory -> requested pathRaw='{}', prefix='{}'", pathRaw, prefix);

        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(miniConfig.getBucket())
                        .prefix(prefix)
                        .recursive(false)
                        .build()
        );

        for (Result<Item> r : results) {
            try {
                Item item = r.get();
                String objName = item.objectName();


                if (objName.equals(prefix + ".keep")) continue;

                String relative = objName.substring(userPrefix.length());
                String[] parts = relative.split("/");
                String name = parts.length > 0 ? parts[parts.length - 1] : "";

                if (objName.endsWith("/")) {
                    String folderName = name.isEmpty() ? parts[parts.length - 2] : name;
                    out.add(new MinIODTO(pathRaw, folderName + "/", "DIRECTORY"));
                } else {
                    if (!name.equals(".keep")) { // не отображаем технический файл
                        out.add(new MinIODTO(pathRaw, name, (byte) item.size(), "FILE"));
                    }
                }
            } catch (Exception ex) {
                log.error("Error reading item from results: {}", ex.getMessage(), ex);
            }
        }
        return out;

    }

    public MinIODTO createFolder(String folderName, String username) {

        Users user = usersRepository.findByUsername(username);
        String userId = String.valueOf(user.getId());

        if (folderName.isEmpty()) {
            throw new RuntimeException("Имя папки пустое");
        }

        String cleanName = folderName.replaceAll("^/+", "").replaceAll("/+$", "");
        String folderPath = "user-" + userId + "-files/" + cleanName + "/.keep";

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(miniConfig.getBucket())
                            .object(folderPath)
                            .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                            .build()
            );
            log.info("Folder created: {}", folderPath);
            return new MinIODTO("user-" + userId + "-files/", cleanName, "DIRECTORY");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании папки: " + e.getMessage(), e);
        }
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
                            .object(folderPath +path+ objectName)
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
        Users user = usersRepository.findByUsername(username);
        try {
            String folderPath = "user-" + user.getId() + "-files" + "/";

            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(miniConfig.getBucket())
                            .prefix(folderPath + path)
                            .delimiter("/")
                            .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(miniConfig.getBucket())
                        .object(item.objectName())
                        .build());
            }


            log.info("Resource deleted");
        } catch (Exception e) {
            log.error("Error: " + e.getMessage());
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
            String folderPath = "user-" + user.getId() + "-files" + "/";

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zipOut = new ZipOutputStream(baos);

            if (path.endsWith("/")){

                Iterable<Result<Item>> results = minioClient.listObjects(
                        ListObjectsArgs.builder()
                                .bucket(miniConfig.getBucket())
                                .prefix(folderPath + path)
                                .recursive(true)
                                .build()
                );

                for (Result<Item> result : results) {
                    Item item = result.get();
                    String objectName = item.objectName();

                    if (objectName.endsWith("/") || objectName.endsWith(".keep"))
                        continue;

                    String relativeName = objectName.substring(folderPath.length());

                    try (InputStream is = minioClient.getObject(
                            GetObjectArgs.builder()
                                    .bucket(miniConfig.getBucket())
                                    .object(objectName)
                                    .build());
                         BufferedInputStream bis = new BufferedInputStream(is)){
                        ZipEntry entry = new ZipEntry(relativeName);
                        zipOut.putNextEntry(entry);

                        bis.transferTo(zipOut);
                        zipOut.closeEntry();
                    }
                }
                zipOut.finish();
                zipOut.close();

                return new ByteArrayInputStream(baos.toByteArray());

            }else {
                InputStream stream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(miniConfig.getBucket())
                                .object(folderPath+path)
                                .build());

                return stream;
            }

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке файла: " + e.getMessage(), e);
        }

    }

    public MinIODTO moveResources(String from, String to, String username) {
        Users user = usersRepository.findByUsername(username);
        String folderPath = "user-" + user.getId() + "-files" + "/";
        MinIODTO minIODTO=null;

        try {

            if (from.endsWith("/")) {
                Iterable<Result<Item>> results = minioClient.listObjects(
                        ListObjectsArgs.builder()
                                .bucket(miniConfig.getBucket())
                                .prefix(folderPath+from)
                                .delimiter("/")
                                .build());

                minIODTO=createFolder(to, username);


                for (Result<Item> result : results) {
                    Item item = result.get();
                    minioClient.putObject(PutObjectArgs.builder()
                            .bucket(miniConfig.getBucket())
                            .object(folderPath + to + extractNameFromPath(item.objectName()))
                            .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                            .build());

                    minioClient.removeObject(RemoveObjectArgs.builder()
                            .bucket(miniConfig.getBucket())
                            .object(item.objectName())
                            .build());
                }

            } else {
                minioClient.copyObject(CopyObjectArgs.builder()
                        .bucket(miniConfig.getBucket())
                        .object(folderPath + to)
                        .source(
                                CopySource.builder()
                                        .bucket(miniConfig.getBucket())
                                        .object(folderPath + from)
                                        .build()
                        )
                        .build());

                log.info("from" + from + "in" + to);

                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(miniConfig.getBucket())
                        .object(folderPath + from)
                        .build());

                minIODTO=new MinIODTO(folderPath,to,"FILE");
            }
        }catch (Exception e){
            throw new RuntimeException("Ошибка при изменении ресурса" + e.getMessage(), e);
        }

        return minIODTO;
    }


    private String extractNameFromPath(String fullPath) {
        if (fullPath == null || fullPath.isEmpty()) return "";
        String normalized = fullPath.endsWith("/")
                ? fullPath.substring(0, fullPath.length() - 1)
                : fullPath;
        int lastSlash = normalized.lastIndexOf('/');
        return lastSlash >= 0 ? normalized.substring(lastSlash + 1) : normalized;
    }

    private String extractPathWithoutName(String fullPath, String userid) {
        if (fullPath == null || fullPath.isEmpty()) return "";

        String userPrefix = "user-" + userid + "-files/";
        String cleaned = fullPath.startsWith(userPrefix)
                ? fullPath.substring(userPrefix.length())
                : fullPath;

        int lastSlash = cleaned.lastIndexOf('/');
        if (lastSlash < 0) return "";

        String result = cleaned.substring(0, lastSlash + 1);
        return result.replaceAll("//+", "/");
    }

}
