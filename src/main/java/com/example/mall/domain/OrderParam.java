package com.example.mall.domain;

import lombok.Data;

/**
 * 生成订单时传入的参数
 * Created by macro on 2018/8/30.
 */

@Data
public class OrderParam {
    //收货地址id
    private String memberReceiveAddressId;
    //优惠券id
    private Long couponId;
    //使用的积分数
    private Integer useIntegration;
    //支付方式
    private Integer payType;
    //买家备注
    private String remark;


    //自提点id 当前即为商家ID
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
