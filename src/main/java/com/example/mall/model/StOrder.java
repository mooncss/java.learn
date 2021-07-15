package com.example.mall.model;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 驿站存放快件订单
 * 驿站存放订单
 * */

@TableName("st_order")
@Data
public class StOrder implements Serializable {

    @TableId("order_id")
    private String orderId;

    //订单入库人 - 驿站负责人
    @TableField("user_id")
    private String userId;

    @TableField("stage_id")
    private String stageId;

    @TableField("stage_name")
    private String stageName;


    //0待支付 1 已支付 2 已取消
    @TableField("state")
    private Integer state;

    @TableField("sigle_price")
    private BigDecimal siglePrice;

    @TableField("pay_amount")
    private BigDecimal payAmount;

    @TableField("qunatity") //件数
    private Integer qunatity;

    //订单创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private Date createTime;

    @TableField("deviler_id")
    private String devilerId;

}
