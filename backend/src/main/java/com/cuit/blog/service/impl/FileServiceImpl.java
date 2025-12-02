package com.cuit.blog.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.cuit.blog.common.exception.BusinessException;
import com.cuit.blog.common.result.ResultCode;
import com.cuit.blog.config.FileUploadConfig;
import com.cuit.blog.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// 文件服务实现类
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileUploadConfig fileUploadConfig;

    // 头像子目录
    private static final String AVATAR_DIR = "avatars";

    @Override
    public String uploadFile(MultipartFile file, String subDir) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.FILE_UPLOAD_FAILED, "文件不能为空");
        }

        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);

        // 检查文件类型
        if (!fileUploadConfig.isAllowedType(extension)) {
            throw new BusinessException(ResultCode.FILE_TYPE_NOT_SUPPORT,
                    "不支持的文件类型: " + extension + "，允许的类型: " + fileUploadConfig.getAllowedTypes());
        }

        // 生成新的文件名：UUID + 扩展名
        String newFileName = IdUtil.fastSimpleUUID() + "." + extension;

        // 按日期创建子目录
        LocalDate today = LocalDate.now();
        String year = today.format(DateTimeFormatter.ofPattern("yyyy"));
        String month = today.format(DateTimeFormatter.ofPattern("MM"));
        String day = today.format(DateTimeFormatter.ofPattern("dd"));

        String relativePath = String.join("/", subDir, year, month, day);
        Path storagePath = Paths.get(fileUploadConfig.getPath(), subDir, year, month, day);
        Path destination = storagePath.resolve(newFileName);
        try {
            Files.createDirectories(storagePath);
            file.transferTo(destination);
            log.info("文件上传成功: {}", destination.toAbsolutePath());
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ResultCode.FILE_UPLOAD_FAILED, "文件保存失败: " + e.getMessage());
        }

        // 返回访问URL（相对路径）
        return "/uploads/" + relativePath + "/" + newFileName;
    }

    @Override
    public String uploadAvatar(MultipartFile file) {
        return uploadFile(file, AVATAR_DIR);
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }

        try {
            // 将URL转换为实际文件路径
            String relativePath = fileUrl.replace("/uploads/", "");
            Path filePath = Paths.get(fileUploadConfig.getPath(), relativePath.split("/")).normalize();

            if (Files.exists(filePath)) {
                boolean deleted = FileUtil.del(filePath.toFile());
                if (deleted) {
                    log.info("文件删除成功: {}", filePath);
                }
                return deleted;
            }
            return false;
        } catch (Exception e) {
            log.error("文件删除失败: {}", fileUrl, e);
            return false;
        }
    }

    // 获取文件扩展名
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
