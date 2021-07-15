package com.example.mall.dto;

import lombok.Data;

@Data
public class BesUserPro {
    //商品详情使用的 商家model
    private Long besId;
    private String shopname;
    private String  shopico;
    //店铺 手机号
    private String shopphone;
    private String isautarky;

}
