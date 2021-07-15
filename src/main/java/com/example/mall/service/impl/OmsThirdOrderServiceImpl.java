package com.example.mall.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.example.mall.mapper.OmsThirdOrderMapper;
import com.example.mall.model.OmsThirdOrder;
import com.example.mall.model.OmsThirdOrderQueryVo;
import com.example.mall.service.OmsThirdOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class OmsThirdOrderServiceImpl extends ServiceImpl<OmsThirdOrderMapper, OmsThirdOrder>
        implements OmsThirdOrderService {
    @Autowired
    OmsThirdOrderMapper omsThirdOrderMapper;

    @Override
    public int batchupdateLeave(List<String> list) {
        return omsThirdOrderMapper.batchupdateLeave(list);
    }
    
    @Override
    public Page<OmsThirdOrderQueryVo> selectPageByCondition(Page<OmsThirdOrderQueryVo> page,String logisticCode, String reciverPhone){
    	return page.setRecords(omsThirdOrderMapper.selectOmsOrderPageByCondition(page,logisticCode,reciverPhone));
    }

    @Override
    public int setPaySuccess(String orderId) {
        return omsThirdOrderMapper.setPaySuccess(orderId);
    }

    @Override
    public List<OmsThirdOrder> selectOrderLog(Map<String, Object> map) {
        return omsThirdOrderMapper.selectOrderLog(map);
    }

    @Override
    public int countOrderLog(Map<String, Object> map) {
        return omsThirdOrderMapper.countOrderLog(map);
    }

}
