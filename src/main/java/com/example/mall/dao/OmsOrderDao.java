package com.example.mall.dao;

import com.example.mall.dto.OmsOrderDeliveryParam;
import com.example.mall.dto.OmsOrderDetail;
import com.example.mall.dto.OmsOrderQueryParam;
import com.example.mall.model.OmsOrder;
import com.example.mall.model.OmsOrderItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 订单自定义查询Dao
 * Created by macro on 2018/10/12.
 */

@Repository
public interface OmsOrderDao {
    /**
     * 条件查询订单
     */
    List<OmsOrder> getList(@Param("queryParam") OmsOrderQueryParam queryParam,@Param("map") Map<String,Object> map);

    /**
     * 批量发货
     */
    int delivery(@Param("list") List<OmsOrderDeliveryParam> deliveryParamList);

    /**
     * 获取订单详情
     */
    OmsOrderDetail getDetail(@Param("id") Long id,@Param("mobile") String mobile);

    int selectCount(@Param("queryParam") OmsOrderQueryParam queryParam,@Param("map") Map<String,Object> map);

    List<OmsOrderDetail> getAppOrderList(@Param("queryParam") OmsOrderQueryParam queryParam,
                                         @Param("map") Map<String,Object> map);

    OmsOrder getOrder(@Param("id")Long id,@Param("mobile") String mobile);

    List<OmsOrderItem> getItemlist(@Param("id") Long id);

    int updateState(@Param("id") Long id,@Param("state") int state);
}
