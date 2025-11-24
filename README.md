# Cloude Service

#### A multi-user file cloud. Users can use it to upload and store files. The project is inspired by Google Drive.

## Features

- **Frontend**: javascript/React,
- **Backend**: Spring Boot, Spring Security, Spring Sessions, REST, Swagger, Upload files, Cookies, sessions
- **Database**: SQL, Spring Data JPA, Liquibase, Redis
- **Tests**: Integration tests, JUnit5, TestContainers


## Project motivation

- Using Spring Boot Features
- Introduction to NoSQL Storage - S3 for Files, Redis for Sessions
- REST integration with a single-page frontend application using React

## Application functionality
#### Working with users :
* Registration
* Authorization
* Logout

#### Working with files and folders :
* Upload files and folders
* Create a new empty folder (similar to creating a new folder in Explorer)
* Delete files and folders
* Renaming and moving
* download


## REST API

* The architectural style is RPC for authorization and registration, and REST for everything else.
* All endpoints exist under the common /api path. The paths below are relative to it; for example, /api/auth/sign-up.
* The authorization mechanism is sessions.
* The request and response format is JSON, except for file downloads and uploads.


#### Example authorization in controller
``` 
   @Tag(name = "Контроллер для входа в УЗ пользователя", description = "Позволяет войти в УЗ пользователя с помощью логина и пароля")
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody UsersSignUpDto usersSignIn, HttpServletRequest request) {

        var user = authService.autentifiactionUser(usersSignIn);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(usersSignIn.username());
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);
        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        return ResponseEntity.ok(user);
    }
``` 


## Database


#### Table `Users`
| Колонка    | Тип     | Комментарий |
|------------|---------|-------------|
| `ID`       | int     | User ID, auto-increment, primary key |
| `username` | varchar | User login — username or email |
| `Password` | varchar | Hashed password|


## Getting folders and files information using the S3 API and MinIo storage
For file storage, we'll use S3, a simple storage service. Developed by Amazon Cloud Services, it provides a cloud service and protocol for file storage.
To work with S3, we'll use the Minio Java SDK

Learned how to use the library to:
* Create files
* Rename files
* Rename” folders
* Delete files

#### Enable API MinIO in application.yml
```
minio:
  server:
    url: http://localhost:9000
    access-key: minioadmin
    secret-key: minioadmin
    bucket: user-files
```

#### Example service for realization API S3
```
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
            throw new RuntimeException("Error create folder: " + e.getMessage());
        }
    }
```


## Docker

In this project, I used Docker to easily launch the necessary applications—an SQL database, MinIO file storage, and Redis session storage.

#### Example docker-compose
```
  version: '3.8'

services:
  minio:
    image: minio/minio:latest
    ports:
      - "9000:9000"
      - "9001:9001"
    volumes:
      - minio_data:/data
    environment:
      MINIO_ROOT_USER: minioadmin
      MINIO_ROOT_PASSWORD: minioadmin
    command: server --console-address ":9001" /data
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9000/minio/health/live"]
      interval: 30s
      timeout: 10s
      retries: 3
  app:
    build: .
    ports:
      - "8084:8084"
    depends_on:
      - db
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/cloude
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: Serega9900
      SPRING_DATASOURCE_DRIVER_CLASS_NAME: org.postgresql.Driver

  db:
    image: postgres:latest
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: cloude
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: Serega9900
    volumes:
      - postgres_data:/var/lib/postgresql/data


volumes:
  minio_data:
  postgres_data:
```