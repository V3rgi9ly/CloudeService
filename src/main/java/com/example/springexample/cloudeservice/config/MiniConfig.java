package com.example.springexample.cloudeservice.config;


import io.minio.MinioClient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "minio.server")
public class MiniConfig {

    private String url;
    private String accesskey;
    private String secretkey;
    private String bucket;

    @Bean
    public MinioClient minioClient(){
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accesskey, secretkey)
                .build();
    }
}
