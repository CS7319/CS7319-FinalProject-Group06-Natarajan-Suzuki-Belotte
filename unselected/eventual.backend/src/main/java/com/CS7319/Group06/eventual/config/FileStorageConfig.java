package com.CS7319.Group06.eventual.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * FileStorageConfig - configuration class for file storage.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "file")
public class FileStorageConfig {

    private String uploadDir;
}
