package com.example.mall.model;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sms_stage")
public class SmsStage implements Serializable {

    //驿站ID
    @TableId("id")
    private String id;
    //驿站编码
    @TableField("stage_code")
    private String  stageCode;
    //驿站名
    @TableField("stage_name")
    private String  stageName;
    //驿站联系人
    @TableField("stage_man")
    private String  stageMan;
    //驿站联系人电话
    @TableField("stage_phone")
    private String  stagePhone;

    //经纬度
    @TableField("lat")
    private String lat;
    @TableField("lon")
    private String  lon;
    //详细地址
    @TableField("address")
    private String   address;

    //备注
    @TableField("remark")
    private String  remark;

    //状态 0可用 1 停用
    @TableField("status")
    private int status;

    @TableField("create_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;



}
