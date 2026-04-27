package com.CS7319.Group06.eventual.userservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for file storage (profile picture uploads)
 */
@Configuration
public class FileStorageConfig {

    @Value("${upload.dir:uploads}")
    private String uploadDir;

    public String getUploadDir() {
        return uploadDir;
    }
}
