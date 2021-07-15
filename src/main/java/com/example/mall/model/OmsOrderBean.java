package com.example.mall.model;


import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OmsOrderBean  implements Serializable {
    //订单编号
    private String orderSn;
    private BigDecimal payAmount;
    private String phone;
    private String shopname;
    private String memberId;
    //订单头
    private String orderTh;
    private int integration;
}
