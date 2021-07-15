package com.example.mall.service;

import com.example.mall.common.CommonResult;
import com.example.mall.domain.ConfirmOrderResult;
import com.example.mall.domain.OrderParam;
import com.example.mall.dto.OmsOrderCreatePrepare;
import com.zhihui.uj.management.utils.R;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 前台订单管理Service
 * Created by macro on 2018/8/30.
 */
public interface OmsPortalOrderService {
    /**
     * 根据用户购物车信息生成确认单信息
     */
    ConfirmOrderResult generateConfirmOrder(String user) throws Exception;

    ConfirmOrderResult.CalcAmount changeQuantity(OmsOrderCreatePrepare p,String mobile) throws Exception;

    /**
     * 根据单商品生成订单确认信息
     * @Param proId
     */
    ConfirmOrderResult generateProConfirmOrder(OmsOrderCreatePrepare p) throws Exception;

    /**
     * 生成订单
     * */
    CommonResult generateProOrder(OmsOrderCreatePrepare p, String mobile) throws  Exception;

    /**
     * 根据提交信息生成订单
     */

    CommonResult generateOrder(OrderParam orderParam,String mobile) throws  Exception;

    /**
     * 支付成功后的回调
     */

//    CommonResult paySuccess(Long orderId);

    /**
     * 自动取消超时订单
     */

//    CommonResult cancelTimeOutOrder();

    /**
     * 取消单个超时订单
     */
//    void cancelOrder(Long orderId);

    /**
     * 发送延迟消息取消订单
     */
//    void sendDelayMessageCancelOrder(Long orderId);
}
