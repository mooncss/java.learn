package com.example.mall.dao;

import com.example.mall.model.OmsOrderItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单商品信息自定义Dao
 * Created by macro on 2018/9/3.
 */
@Repository
public interface PortalOrderItemDao {
    int insertList(@Param("list") List<OmsOrderItem> list) throws  Exception;
}
