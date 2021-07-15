package com.example.mall.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class UserCartFeightCalc implements Serializable {

    private Long besId;
    private  String feightInfo; //运费描述
    private BigDecimal feightAmount; //运费金额
}
