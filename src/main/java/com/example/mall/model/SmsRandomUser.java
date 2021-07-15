package com.example.mall.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

@Data
@TableName("sms_random_user")
public class SmsRandomUser {

    @TableId("randomid")
    private String randomid;

    @TableField("username")
    private String username;

    @TableField("userico")
    private String userico;

    @TableField("userphone")
    private String userphone;

}
