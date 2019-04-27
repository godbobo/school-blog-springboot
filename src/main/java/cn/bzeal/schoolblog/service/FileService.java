package cn.bzeal.schoolblog.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * 文件服务
 * Created by Godbobo on 2019/4/26.
 */
public interface FileService {

    // 上传文件
    String upload(int type, Long id, MultipartFile file, HttpServletRequest request);

    // 移除文件
    String remove(int type, Long id, Long fileId);

}
