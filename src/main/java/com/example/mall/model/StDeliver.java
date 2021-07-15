package com.example.mall.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 *  快递员
 */

@Data
@TableName("st_deliver")
public class StDeliver implements Serializable {

    @TableId("d_id")
    private String dId;
    @TableField("d_name")
    private String dName;
    @TableField("d_phone")
    private String dPhone;
    @TableField("d_shipper_code")
    private String  dShipperCode;
    @TableField("d_shipper_name")
    private String dShipperName;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private  Date create_time;

}
