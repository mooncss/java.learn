package com.example.mall.model;

import lombok.Data;

import java.io.Serializable;


@Data
public class StOrderTotalVo implements Serializable {

    private String amount;

    private String createTime;

    private Integer qunatity;



}
