package cn.bzeal.schoolblog.web;

import cn.bzeal.schoolblog.common.GlobalResult;
import cn.bzeal.schoolblog.model.QueryModel;
import cn.bzeal.schoolblog.service.TagService;
import cn.bzeal.schoolblog.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tag")
public class TagController extends BaseController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService){
        this.tagService = tagService;
    }

    // 新增标签、
    @RequestMapping("/add")
    public String add(QueryModel model) {
        // 验证参数是否完整
        String id = getRequest().getAttribute("uid").toString();
        String name = model.getTag().getName();
        String color = model.getTag().getColor();
        String bg = model.getTag().getBackground();
        if(StringUtils.isAnyBlank(id, name, color, bg)){ // 缺少任何一个参数均视为无效请求
            return CommonUtil.response(new GlobalResult());
        }else {
            return CommonUtil.response(tagService.add(model, Long.parseLong(id)));
        }
    }

}
