package cn.bzeal.schoolblog.web;

import cn.bzeal.schoolblog.common.AppConst;
import cn.bzeal.schoolblog.common.ResponseCode;
import cn.bzeal.schoolblog.model.QueryModel;
import cn.bzeal.schoolblog.service.FileService;
import cn.bzeal.schoolblog.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * 文件控制器
 * Created by Godbobo on 2019/4/26.
 */
@RestController
@RequestMapping("/file")
public class FileController extends BaseController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService){
        this.fileService = fileService;
    }

    @RequestMapping("/upload")
    public String upload(MultipartFile file, Integer queryType, Long id, HttpServletRequest request){
        if (queryType == null || id == null || file == null) {
            return defaultResult();
        }
        return fileService.upload(queryType, id, file, request);
    }

    @RequestMapping("/remove")
    public String remove(QueryModel model){
        // 得到所属文章或话题的id
        Long id = null;
        if (model.getQueryType() == AppConst.FILE_TYPE_ARTICLE && model.getArticle() !=null) {
            id = model.getArticle().getId();
        }else if (model.getTopic()!=null){
            id = model.getTopic().getId();
        }
        if (id == null || model.getFile() == null || model.getFile().getId() == null) {
            return ResponseUtil.getResult(ResponseCode.T_APP_FAIL_DELETE);
        }
        return fileService.remove(model.getQueryType(), id, model.getFile().getId());
    }

}
