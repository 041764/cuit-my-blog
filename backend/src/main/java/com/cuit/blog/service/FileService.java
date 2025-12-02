package com.cuit.blog.service;

import org.springframework.web.multipart.MultipartFile;

// 文件服务接口
public interface FileService {

    /**
     * 上传文件
     *
     * @param file 文件
     * @param subDir 子目录
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file, String subDir);

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @return 头像访问URL
     */
    String uploadAvatar(MultipartFile file);

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    boolean deleteFile(String fileUrl);
}
