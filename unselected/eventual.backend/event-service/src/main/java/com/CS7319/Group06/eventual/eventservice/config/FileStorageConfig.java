package com.CS7319.Group06.eventual.eventservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * FileStorageConfig - configuration class for file storage.
 */
@Configuration
public class FileStorageConfig {

    @Value("${upload.dir:uploads}")
    private String uploadDir;

    public String getUploadDir() {
        return uploadDir;
    }
}
