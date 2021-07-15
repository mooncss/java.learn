package com.example.mall.dto;


import io.swagger.annotations.Api;
import lombok.Data;

import java.io.Serializable;

@Api("限购检查使用")
@Data
public class OmsOrderLimitToday implements Serializable {

    private Long productId;
    private Integer productQuantity;
}
