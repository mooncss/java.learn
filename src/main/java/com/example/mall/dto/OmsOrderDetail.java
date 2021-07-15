package com.example.mall.dto;

import com.example.mall.model.OmsOrder;
import com.example.mall.model.OmsOrderItem;
import com.example.mall.model.OmsOrderOperateHistory;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 订单详情信息
 * Created by macro on 2018/10/11.
 */
@Data
public class OmsOrderDetail extends OmsOrder {
    private List<OmsOrderItem> orderItemList;
    private List<OmsOrderOperateHistory> historyList;

    private String shopico;

    private String shopname;

    //商品件数
    private Integer itemcount;

    private String CNStatus;
    //商家电话
    private String shopPhone;

    private String payAmountStr;

}
