package com.example.mall.vo;

import com.example.mall.model.PmsProduct;
import com.example.mall.model.SmsGroupBuyProduct;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SmsGroupBuyProductVO extends SmsGroupBuyProduct {

    private String name;
    private String pic;
    private String albumPics;
    //剩余时间描述
    private String  timeRemain;

    private PmsProduct product;

    private BigDecimal price;

    private String shopname;
    //已拼件数
    private Integer yiPin;
}
