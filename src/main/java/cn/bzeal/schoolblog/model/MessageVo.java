package cn.bzeal.schoolblog.model;

import cn.bzeal.schoolblog.domain.Message;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by Godbobo on 2019/5/1.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MessageVo extends Message {

    private int repeat = 0; // 重复次数

    public void addRepeat() {
        this.repeat++;
    }

}
