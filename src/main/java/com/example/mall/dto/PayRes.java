package com.example.mall.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.mall.model.OmsOrderItem;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PayRes {
    private BigDecimal payAmount;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;
    //拼团ID
    private String groupId;
    //支付成功的提示信息
    private String payinfo;

    private OmsOrderItem omsOrderItem;
}
