package com.example.mall.dto;

import com.example.mall.domain.ConfirmOrderResult;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CartBussnesOrderRes implements Serializable {
    private List<GenOrderResult> orderlist;
    private ConfirmOrderResult.CalcAmount calcTotal;
    private String orderTh;
}
