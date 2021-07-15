package com.example.mall.model;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

//拼团信息
@Data
@TableName("sms_groupbuy_assemble")
public class SmsGroupBuyAssemble {

    @TableId("group_id")
    private String  groupId;

    @TableField("order_id")
    private Long orderId;

    //成团人数
    @TableField("group_need")
    private Integer  groupNeed;

    @TableField("product_id")
    private Long productId;


    //商品SKU
    @TableField("skuid")
    private Long skuid;

    //发起时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private Date createTime;
 //拼团结束时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("end_time")
    private Date endTime;

    @TableField("group_price")
    private BigDecimal groupPrice;

    //发起人 手机号
    @TableField("group_man")
    private String groupMan;

    //发起人 昵称
    @TableField("man_phone")
    private String manPhone;

    //头像
    @TableField("ico")
    private String ico;

    //用户设备机型
    @TableField("phone_type")
    private String phoneType;

    //拼团活动ID
    @TableField("groupbuy_promotion_id")
    private String groupbuyPromotionId;


    //是否满员  0 完成    1未完成
    @TableField("is_full")
    private Integer isFull;

    //目前人数
    @TableField("now_person")
    private Integer nowPerson;

    //拼团完成时间
    @TableField("over_time")
    private Date overTime;

    //检索条件
    @TableField("search_key")
    private String searchKey;

}
