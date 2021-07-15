package com.example.mall.model;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.omg.CORBA.IDLType;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


@Api("三方订单 物流进程")
@Data
@TableName("oms_third_order_trace")
public class OmsThirdOrderTrace implements Serializable {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    @TableField("logistic_code")
    private String logisticCode;
    @TableField("accept_station")
    private String  acceptStation;

    @ApiModelProperty("进程时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("accept_time")
    private Date acceptTime;
    @TableField("remark")
    private String remark;
}
