package com.example.mall.dto;

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
 * 商家设置运费
 *
 */
@TableName("bes_feight_plate")
@Data
public class BesFeightPlate implements Serializable {

    @TableId("id")
    private String id;

    @TableField("bes_id")
    private long besId;

    @TableField("is_available")
    private Integer isAvailable;

    @TableField("start_no_fee")
    private BigDecimal startNoFee;

    @TableField("fee")
    private BigDecimal fee;


    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private Date createTime;


}
