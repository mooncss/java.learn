package com.example.mall.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.example.mall.common.CommonResult;
import com.example.mall.dto.*;
import com.example.mall.model.OmsOrder;
import com.example.mall.model.OmsOrderAfterNotifyBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//订单管理
@Repository
public interface OmsOrderService {
    /**
     * 订单查询
     */
    Page<OmsOrder> list(OmsOrderQueryParam queryParam, Integer pageSize, Integer pageNum,String mobile);


    Page<OmsOrderDetail>  listApp(OmsOrderQueryParam queryParam, Integer limit, Integer page,String mobile);

    /**
     * 批量发货
     */
    int delivery(List<OmsOrderDeliveryParam> deliveryParamList);

    /**
     * 批量关闭订单
     */
    int close(List<Long> ids, String note);

    /**
     * 批量删除订单
     */
    int delete(List<Long> ids);

    /**
     * 获取指定订单详情
     */
    OmsOrderDetail detail(Long id);

    /**
     * 修改订单收货人信息
     */
//    int updateReceiverInfo(OmsReceiverInfoParam receiverInfoParam);

    public CommonResult payOrder(PayBodyDto dto) throws  Exception;
    /**
     * 修改订单费用信息
     */
//    int updateMoneyInfo(OmsMoneyInfoParam moneyInfoParam);

    /**
     * 修改订单备注
     */
//    int updateNote(Long id, String note, Integer status);


    int orderCancle();

    CommonResult orderReceive(Long orderId,String mobile) throws Exception;

    CommonResult userCancleOrder(Long orderId) throws  Exception;

    //自动确认收货
    Integer autoreceiveorder();

    /*
    * 付款回调通知后的订单处理
    * */
    void updateOmsOrderafterNotify(OmsOrderAfterNotifyBean bean);

}
