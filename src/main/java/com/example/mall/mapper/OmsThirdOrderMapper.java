package com.example.mall.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.example.mall.model.OmsThirdOrder;
import com.example.mall.model.OmsThirdOrderQueryVo;
import io.swagger.annotations.Api;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Api("三方订单的信息")
@Repository
public interface OmsThirdOrderMapper extends BaseMapper<OmsThirdOrder> {

    int batchupdateLeave(@Param("list") List<String> list);
    
    List<OmsThirdOrderQueryVo> selectOmsOrderPageByCondition(Pagination page,@Param("logisticCode") String logisticCode ,
    		@Param("reciverPhone") String reciverPhone);

    int setPaySuccess(@Param("orderId") String orderId);


    List<OmsThirdOrder> selectOrderLog(@Param("map") Map<String,Object> map);

    int countOrderLog(@Param("map") Map<String,Object> map);
    
}
