package com.example.mall.model;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Api("驿站和订单的入库关系表")
@Data
@TableName("oms_order_stage_rela")
public class OmsOrderStageRela implements Serializable {

    @TableId("id")
    private Long id;
    @TableField("stage_id")
    private String  stageId;
    @TableField("order_sn")
    private String orderSn;
    @TableField("order_id")
    private Long orderId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private Date createTime;
    @TableField("o_type")
    private Integer oType;

    @TableField("remark")
    private String remark;

    @TableField("is_leave")
    private Integer isLeave;   //0 未出库 1 已出库
}
