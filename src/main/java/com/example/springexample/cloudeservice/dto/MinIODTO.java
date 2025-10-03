package com.example.springexample.cloudeservice.dto;

public record MinIODTO(String path, String name, Byte size, String type ) {
    public MinIODTO(String path, String name, String type) {
        this(path, name, null, type);
    }
}
