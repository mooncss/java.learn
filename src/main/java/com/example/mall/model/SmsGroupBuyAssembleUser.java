package com.example.mall.model;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;

@Data
@TableName("sms_groupbuy_assemble_user")
public class SmsGroupBuyAssembleUser {

    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("group_id")
    private String groupId;
    @TableField("type")
    private Integer type =0;  //发起人 0  跟团人 1
    @TableField("userid")
    private String userId;   //用户ID
    @TableField("username")
    private String username;  //用户名
    @TableField("userico")
    private String userico;   //用户头像
    @TableField("phone_type")
    private String phoneType;   //手机型号
    @TableField("join_time")
    private Date joinTime; //参团时间
}
