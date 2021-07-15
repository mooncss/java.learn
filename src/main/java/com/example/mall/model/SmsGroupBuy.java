package com.example.mall.model;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

//拼团活动
@TableName("sms_groupbuy_promotion")
@Data
public class SmsGroupBuy {

    @TableId
    private String id;

    @TableField("bes_id")
    private Long besId;

    @TableField("title")
    private String title;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("start_date")
    private Date startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("end_date")
    private Date endDate;

    @TableField("status")
    private String  status;  //  1 不可用 2可用

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private Date createTime;
    //审核状态
    @TableField("checked")
    private String checked;  //0 未审核 1 已审核
    //审核结果
    @TableField("checkres")
    private String checkres;   //0 通过 1 未通过

    @TableField("statusadmin")
    private String statusadmin;   //0 正常 1 暂停

    @TableField("remark")
    private String remark;  //审核备注

    //审核时间
    @TableField("checktime")
    private String checktime;

    //审核人
    @TableField("checkuser")
    private String checkuser;

}
