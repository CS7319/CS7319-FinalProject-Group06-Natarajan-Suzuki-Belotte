package com.CS7319.Group06.eventual.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Serves uploaded files (profile pictures, event pictures) as static resources.
 *
 * Files saved under the configured upload directory are accessible at:
 *   GET /uploads/profile-pictures/<email>/<filename>
 *   GET /uploads/event-pictures/<organizerEmail>/<filename>
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final FileStorageConfig fileStorageConfig;

    public WebMvcConfig(FileStorageConfig fileStorageConfig) {
        this.fileStorageConfig = fileStorageConfig;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadDir = fileStorageConfig.getUploadDir();
        // Ensure the path ends with a separator
        String resourceLocation = uploadDir.endsWith("/")
                ? "file:" + uploadDir
                : "file:" + uploadDir + "/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourceLocation);
    }
}
