package cn.bzeal.schoolblog.service.impl;

import cn.bzeal.schoolblog.common.AppConst;
import cn.bzeal.schoolblog.common.ResponseCode;
import cn.bzeal.schoolblog.domain.*;
import cn.bzeal.schoolblog.service.FileService;
import cn.bzeal.schoolblog.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Godbobo on 2019/4/26.
 */
@Service
@Transactional
public class FileServiceImpl implements FileService {

    private final ArticleRepository articleRepository;
    private final TopicRepository topicRepository;
    private final ResourceRepository resourceRepository;

    @Value("${upload.root}")
    private String uploadRoot; // 文件上传根路径

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

    @Autowired
    public FileServiceImpl(ArticleRepository articleRepository, TopicRepository topicRepository, ResourceRepository resourceRepository){
        this.articleRepository = articleRepository;
        this.topicRepository = topicRepository;
        this.resourceRepository = resourceRepository;
    }

    @Override
    public String upload(int type, Long id, MultipartFile file, HttpServletRequest request) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis()); // 上传时间
        String name = file.getOriginalFilename(); // 文件名
        Long size = file.getSize(); // 文件大小
        String url = ""; // 访问地址
        String format = simpleDateFormat.format(new Date());
        String path = uploadRoot + "file/" + format;
        File folder = new File(path);
        if (!folder.isDirectory()) {
            folder.mkdirs();
        }
        String newName = "/" + UUID.randomUUID().toString() + name.substring(name.lastIndexOf(".")); // path+newName为实际路径
        try {
            // 上传文件
            file.transferTo(new File(folder, newName));
            url = request.getScheme() + "://" + request.getServerName() + "/upload/file/" + format + newName;
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseUtil.getResult(ResponseCode.T_APP_FAIL_UPLOAD);
        }
        // 构建文件对象
        Resource myFile = new Resource();
        myFile.setName(name);
        myFile.setSize(size);
        myFile.setUpt(timestamp);
        myFile.setPath(path + newName);
        myFile.setUrl(url);
        // 将信息更新到数据库
        if (type == AppConst.FILE_TYPE_ARTICLE) {
            Article article = articleRepository.findById(id).orElse(null);
            if(article!= null) {
                myFile.setUploader(article.getAuthor());
                article.getFiles().add(myFile);
                if (articleRepository.save(article)!= null) {
                    return ResponseUtil.getResult(ResponseCode.T_APP_SUCCESS_UPLOAD);
                }
            }
        }else {
            Topic topic = topicRepository.findById(id).orElse(null);
            if (topic!= null) {
                myFile.setUploader(topic.getCreator());
                topic.getFiles().add(myFile);
                if (topicRepository.save(topic) != null) {
                    return ResponseUtil.getResult(ResponseCode.T_APP_SUCCESS_UPLOAD);
                }
            }
        }
        return ResponseUtil.getResult(ResponseCode.T_APP_FAIL_UPLOAD);
    }

    @Override
    public String remove(int type, Long id, Long fileId) {
        if (type == AppConst.FILE_TYPE_ARTICLE) {
            Article article = articleRepository.findById(id).orElse(null);
            if (article != null) {
                for (Resource r: article.getFiles()) {
                    if (r.getId().equals(fileId) ){
                        article.getFiles().remove(r);
                        deleteFile(r.getPath());
                        resourceRepository.delete(r);
                        if (articleRepository.save(article) != null) {
                            return ResponseUtil.getResult(ResponseCode.T_APP_SUCCESS_DELETE);
                        }
                        break;
                    }
                }
            }
        }else {
            Topic topic = topicRepository.findById(id).orElse(null);
            if (topic != null) {
                for (Resource r: topic.getFiles()) {
                    if (r.getId().equals(fileId) ){
                        topic.getFiles().remove(r);
                        if (topicRepository.save(topic) != null) {
                            deleteFile(r.getPath());
                            resourceRepository.delete(r);
                            return ResponseUtil.getResult(ResponseCode.T_APP_SUCCESS_DELETE);
                        }
                        break;
                    }
                }
            }
        }
        return ResponseUtil.getResult(ResponseCode.T_APP_FAIL_DELETE);
    }

    private void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()){
            file.delete();
        }
    }
}

