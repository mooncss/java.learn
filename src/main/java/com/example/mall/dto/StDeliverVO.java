package com.example.mall.dto;

import com.example.mall.model.StDeliver;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StDeliverVO extends StDeliver {

    private BigDecimal price;
    private String hasRela;  //是否关联  0  未合作  1  已合作
}
