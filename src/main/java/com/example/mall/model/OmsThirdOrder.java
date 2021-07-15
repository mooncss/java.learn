package com.example.mall.model;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Api("三方快件 bean")
@Data
@TableName("oms_third_order")
public class OmsThirdOrder implements Serializable {

    @TableId("id")
    private String id;
    @TableField("logistic_code")
    private String logisticCode;
    @TableField("order_id")
    private String orderId;

    @TableField("shipper_code")
    private String shipperCode;

    @TableField("shipper_name")
    private String shipperName;

    //快件状态 // 0 未入库  1 已入库
    @TableField("state")
    private Integer state;

    @TableField("ebusiness_id")
    private String ebusinessId;

    @TableField("success")
    private String  success;

    // 0 入库 1 出库
    @TableField("o_type")
    private Integer  oType;

    @TableField("stage_id")
    private String  stageId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty("收件人电话")
    @TableField("reciver_phone")
    private String reciverPhone;

    @ApiModelProperty("收件人名称")
    @TableField("reciver_name")
    private String reciverName;

    @ApiModelProperty("是否已经取件  0 未取件  1 已取件")
    @TableField("is_leave")
    private Integer isLeave;

    /**
     * 出库时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("leave_time")
    private Date leaveTime;


    @TableField(exist = false)
    private String deliverPhone;


}
