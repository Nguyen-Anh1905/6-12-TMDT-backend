package com.example.backend_tmdt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path imageDir = findProjectImageDir();
        registry.addResourceHandler("/image/**")
                .addResourceLocations(imageDir.toUri().toString());
    }

    private Path findProjectImageDir() {
        Path userDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        return java.util.List.of(
                        userDir.resolve("image"),
                        userDir.resolve("..").resolve("image"),
                        userDir.resolve("..").resolve("..").resolve("image")
                ).stream()
                .map(path -> path.toAbsolutePath().normalize())
                .filter(Files::isDirectory)
                .findFirst()
                .orElse(userDir.resolve("..").resolve("image").toAbsolutePath().normalize());
    }
}
