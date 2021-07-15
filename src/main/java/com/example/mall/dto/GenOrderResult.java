package com.example.mall.dto;

import com.example.mall.model.OmsOrder;
import com.example.mall.model.OmsOrderItem;
import lombok.Data;

import java.util.List;

@Data
public class GenOrderResult
{
    private OmsOrder order;
    private List<OmsOrderItem> itemlist;
}
