package com.example.mall.model;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


@Api("自提联系人")
@TableName("ums_mention_man")
@Data
public class UmsMentionMan implements Serializable {

    @TableId("id")
    private String id;

    @TableField("member_id")
    private String memberId;

    @TableField("receive_man")
    private  String receiveMan;

    @TableField("receive_phone")
    private  String receivePhone;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private Date createTime;

}
