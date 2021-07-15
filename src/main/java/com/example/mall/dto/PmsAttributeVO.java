package com.example.mall.dto;

import lombok.Data;


//商品属性和参数查询类
@Data
public class PmsAttributeVO
{

    private Long id;
    //商品id
    private Long productId;

    private Long productAttributeId;

    private String value;
    //名称
    private String name;

    private int type;   //属性的类型；0->规格；1->参数

}
