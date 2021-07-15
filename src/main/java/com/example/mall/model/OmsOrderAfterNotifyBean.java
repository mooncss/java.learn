package com.example.mall.model;

import io.swagger.annotations.Api;
import lombok.Data;

import java.io.Serializable;

@Data
@Api("回调后订单更新")
public class OmsOrderAfterNotifyBean implements Serializable {

    private String payamount;
    private String mobile; //付款人手机号
    private String orderTh; //订单头
    private String orderSn; //订单号
    private Integer paytype;//1支付宝  2微信和支付宝
}
