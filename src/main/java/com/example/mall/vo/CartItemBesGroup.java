package com.example.mall.vo;


import com.example.mall.model.BesUser;
import com.example.mall.model.OmsCartItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

//购物车展示类
@Data
public class CartItemBesGroup  {

    private Long besId;
    private String  shopname;
    private String shopico;
    private List<OmsCartItem> itemlist;
    private boolean beschecked; // 0  选中  1  未选中

    private BigDecimal besFeightAmount;
    private String feightInfo;

    //商家的满减优惠信息
    private String besReduceInfo;

//    private BigDecimal cartTotalPrice;
}
