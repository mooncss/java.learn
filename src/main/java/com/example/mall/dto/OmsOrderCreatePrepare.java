package com.example.mall.dto;

import lombok.Data;
import me.chanjar.weixin.common.annotation.Required;

import java.io.Serializable;

@Data
public class OmsOrderCreatePrepare implements Serializable {
    @Required
    private Long productId;  //商品ID

    @Required
    private int quantity;  //下单数量

    private Long skuId;     //指定的sku

    private String memberReceiveAddressId;  //用户收货地址ID

    @Required
    private Integer orderType;  //0 正常订单 2拼团订单

    private String groupId;  // 已经在团 的拼团ID

    private String groupBuyPromotionID;//拼团活动ID

    private Integer isPrime ; //是否是发起人   0发起人 1参团
    //手机机型
    private String phoneType;

    //买家备注
    private String remark;

    //自提点 即商家ID
    private Long mentionBesId;

    /**
     * 配送方式 0商城配送 1自提
     */
    private Integer deliverType;

    //自提时间
    private String mentionTime;

    //自提人
    private String  reciveName;

    //提货人手机
    private String receivePhone;

}
