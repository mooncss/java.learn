package com.example.mall.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.example.mall.model.OmsThirdOrder;
import com.example.mall.model.OmsThirdOrderQueryVo;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;


@Repository
public interface OmsThirdOrderService extends IService<OmsThirdOrder> {
    //批量更新 出站
    int batchupdateLeave(List<String> list);
    
    Page<OmsThirdOrderQueryVo> selectPageByCondition(Page<OmsThirdOrderQueryVo> page,String logisticCode, String reciverPhone);

    int setPaySuccess(String orderId);

    List<OmsThirdOrder> selectOrderLog(Map<String,Object> map);

    int countOrderLog(Map<String,Object> map);

}
