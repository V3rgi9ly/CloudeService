package com.example.springexample.cloudeservice.config;


import io.minio.MinioClient;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "minio.server")
public class MiniConfig {

    private String url="http://localhost:9000";
    private String accesskey="minioadmin";
    private String secretkey="minioadmin";

    @Bean
    public MinioClient minioClient(){
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accesskey, secretkey)
                .build();
    }
}
