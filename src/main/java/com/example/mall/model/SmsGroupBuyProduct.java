package com.example.mall.model;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.annotation.security.DenyAll;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

//拼团商品
@Data
@TableName("sms_groupbuy_promotion_products")
public class SmsGroupBuyProduct implements Serializable {

    @TableId("id")
    private String id;

    @TableField("groupbuy_promotion_id")
    private String  groupbuyPromotionId;

    @TableField("product_id")
    private Long productId;

    @TableField("promotion_price")
    private BigDecimal promotionPrice;  //活动价格

    @TableField("promotion_count")
    private Integer promotionCount; //活动库存

    @TableField("promotion_limit")
    private Integer   promotionLimit;  //促销限购

    @TableField("sort")
    private Integer  sort;

    @TableField("teamnum")
    private Integer  teamnum;  //成团人数

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("end_time")
    private Date endTime;
    //已拼团件数
    @TableField("promotion_count_al")
    private Integer promotionCountAl;

    //是否暂停 0 正常 1 暂停
    @TableField("status")
    private Integer status;

    //0 正常 1停止
    @TableField("statusadmin")
    private Integer statusadmin;

    //0已审核  1 未审核
    @TableField("ischecked")
    private Integer ischecked;

    //审核结果 0通过 1 未通过
    @TableField("checkres")
    private Integer checkres;

    //备注
    @TableField("remark")
    private String remark;

    //审核人
    @TableField("checkuser")
    private String checkuser;

    //审核时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("checktime")
    private Date checktime;

    //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private Date createTime;
}
