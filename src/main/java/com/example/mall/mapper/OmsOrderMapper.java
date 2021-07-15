package com.example.mall.mapper;

import com.example.mall.common.CommonResult;
import com.example.mall.dto.OmsOrderLimitToday;
import com.example.mall.model.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OmsOrderMapper {
    long countByExample(OmsOrderExample example);

    int deleteByExample(OmsOrderExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OmsOrder record);

    int insertSelective(OmsOrder record);

    List<OmsOrder> selectByExample(OmsOrderExample example);

    OmsOrder selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OmsOrder record, @Param("example") OmsOrderExample example);

    int updateByExample(@Param("record") OmsOrder record, @Param("example") OmsOrderExample example);

    int updateByPrimaryKeySelective(OmsOrder record);

    int updateByPrimaryKey(OmsOrder record);

    int orderCancle();

    int orderReceive(@Param("orderId") Long orderId);

    List<OmsOrder> selectCancleing();

    int updateProLockStockBatch(@Param("list") List<OmsOrderItem>  list);

    int updateSkuLockStockBatch(@Param("list") List<OmsOrderItem>  list);

    //释放锁定库存， 增加销量   减少库存
    int releaseLockStockPro(@Param("list") List<OmsOrderItem>  list);

    int releaseLockStockSku(@Param("list") List<OmsOrderItem>  list);

    int userCancleOrder(Long orderId) throws  Exception;

    //拼单后， 更新拼购ID到订单
    int updateGroupInfo(@Param("map") Map<String,Object> map);

    //更新拼团的状态
    int updateGroupState(@Param("map") Map<String,Object> map);

    //批量更新在一个团内的订单 为拼团完成状态
    int updateGroupStateBatch(@Param("map") Map<String,Object> map);


    //系统自动确认订单
    int autoreceiveorder();


    //查询可以取消的系统订单
    List<OmsOrder> getautoreceiveorder();
    /*
    * 订单支付用
    * 根据订单编号或者 订单头查询
    * */
    List<OmsOrderBean> getOrderPayInfo(@Param("map") Map<String,Object> map);

    List<OmsOrderLimitToday>  getPurchseToday(@Param("map") Map<String,Object> map);


    List<OmsOrderShowStageModel> selDaiqh(@Param("list") List<String> list);

    List<PhoneAndNameModel> pnlist(@Param("keyword") String keyword);

    //查询个人有效订单数量
    Integer countMemberOrders(@Param("map") Map<String,Object> map);
}