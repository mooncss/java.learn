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
 * 驿站和快递员的合作信息
 */

@Data
@TableName("st_stage_deliver_rela")
public class StStageDeliverRela implements Serializable {

    @TableId("id")
    private String id;

    @TableField("stage_id")
    private String  stageId;

    @TableField("deliver_id")
    private String deliverId;

    @TableField("price")
    private BigDecimal price;

    //  驿站更改价格后，需要快递员来确认价格 消息通知到快递员
    @TableField("state")
    private String state; // 0 未达成协议 1达成协议


    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private Date createTime;

}
