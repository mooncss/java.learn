package com.example.mall.dto;


import lombok.Data;

import java.util.List;

@Data
public class PayBodyDto {
    private List<Long> orderIds;
    private String payamount;
    private String payword;
    private String mobile; //付款人手机号
}
