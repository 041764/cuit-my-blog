package com.cuit.blog.controller;

import com.cuit.blog.common.result.Result;
import com.cuit.blog.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

// 文件控制器
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * 通用文件上传接口
     *
     * @param file 文件
     * @param type 文件类型（avatar-头像, cover-封面图, content-内容图）
     * @return 文件URL
     */
    @PostMapping("/upload")
    public Result<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "content") String type) {
        
        String subDir = switch (type) {
            case "avatar" -> "avatars";
            case "cover" -> "covers";
            default -> "contents";
        };
        
        String fileUrl = fileService.uploadFile(file, subDir);
        
        Map<String, String> data = new HashMap<>();
        data.put("url", fileUrl);
        data.put("type", type);
        
        return Result.success(data);
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     * @return 删除结果
     */
    @DeleteMapping
    public Result<Boolean> deleteFile(@RequestParam("url") String fileUrl) {
        boolean deleted = fileService.deleteFile(fileUrl);
        return Result.success(deleted);
    }
}
