package com.cuit.blog.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

// 文件上传配置类
@Data
@Component
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig {

    // 文件存储路径
    private static final String DEFAULT_RELATIVE_PATH = "uploads";

    private String path = DEFAULT_RELATIVE_PATH;

    // 允许的文件类型（逗号分隔）
    private String allowedTypes = "jpg,jpeg,png,gif,webp";

    @PostConstruct
    public void init() {
        this.path = resolvePath(this.path);
        ensureBaseDirectory();
    }

    public void setPath(String path) {
        this.path = resolvePath(path);
    }

    public String getPath() {
        return path;
    }

    // 获取允许的文件类型列表
    public List<String> getAllowedTypeList() {
        return Arrays.asList(allowedTypes.split(","));
    }

    /**
     * 检查文件类型是否允许
     *
     * @param extension 文件扩展名
     * @return 是否允许
     */
    public boolean isAllowedType(String extension) {
        if (extension == null) {
            return false;
        }
        return getAllowedTypeList().contains(extension.toLowerCase());
    }

    private String resolvePath(String configuredPath) {
        String candidate = configuredPath;
        if (candidate == null || candidate.isBlank()) {
            candidate = DEFAULT_RELATIVE_PATH;
        }

        Path resolved = Paths.get(candidate.trim());
        if (!resolved.isAbsolute()) {
            resolved = Paths.get(System.getProperty("user.dir")).resolve(resolved);
        }
        return resolved.normalize().toAbsolutePath().toString();
    }

    private void ensureBaseDirectory() {
        try {
            Files.createDirectories(Paths.get(path));
        } catch (IOException e) {
            throw new IllegalStateException("无法创建文件上传目录: " + path, e);
        }
    }
}
