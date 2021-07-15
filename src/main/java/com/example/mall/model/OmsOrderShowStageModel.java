package com.example.mall.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


@Api("订单展示bean")
@Data
public class OmsOrderShowStageModel implements Serializable
{
    //单号
    private Long orderId;
    private String orderSn;
    //入库时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String receiveName;
    private String receivePhone;
    private String proName;

    private String logticName;   //快递名称

    private Integer isThird;   // 0 U家快递  1 三方

}
