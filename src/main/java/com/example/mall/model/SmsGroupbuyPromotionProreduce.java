package com.example.mall.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


//商品活动满减促销表
//数据库展示是可以展示给用户： 满xxx元减xx元
@Data
@TableName("sms_groupbuy_promotion_proreduce")
public class SmsGroupbuyPromotionProreduce implements Serializable {
    @TableId("id")
    private int id;
    @TableField("promotion_product_id")
    private String promotionProductId;
    @TableField("product_id")
    private Long productId;
    @ApiModelProperty("减x")
    @TableField("reduce_amount")
    private BigDecimal reduceAmount;
    @ApiModelProperty("满x减x")
    @TableField("limit_amount")
    private BigDecimal limitAmount;
    @TableField("del_flag")
    private int delFlag;
    @TableField("remark")
    private String remark;

}
