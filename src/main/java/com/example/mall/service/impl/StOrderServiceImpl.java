package com.example.mall.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.example.mall.common.CommonResult;
import com.example.mall.mapper.StOrderMapper;
import com.example.mall.model.StOrder;
import com.example.mall.model.StOrderQueryVo;
import com.example.mall.model.StOrderTotalVo;
import com.example.mall.service.StOrderService;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@Service
public class StOrderServiceImpl extends ServiceImpl<StOrderMapper, StOrder> implements StOrderService {
	
    @Autowired
    StOrderMapper stOrderMapper;
	
    @Override
    public Page<StOrderQueryVo> selectPageByCondition(Page<StOrderQueryVo> page,String commitMobile, String dPhone){
    	return page.setRecords(stOrderMapper.selectPageByCondition(page,commitMobile, dPhone));
    }

    @Override
    public List<StOrderQueryVo> selectLogisticRecord(Map<String, Object> map) {
        return stOrderMapper.selectLogisticRecord(map);
    }

    @Override
    public int selectLogisticRecordCount(Map<String, Object> map) {
        return stOrderMapper.selectLogisticRecordCount(map);
    }

    @Override
    public List<StOrderTotalVo> selectLast15day(Map<String, Object> map) {
        List<StOrderTotalVo> list =  stOrderMapper.selectLast15day(map);
        LocalDate ld = LocalDate.now();
        for(int a=0;a <15 ;a++){
            String date = ld.minusDays(a).toString();
            try{
                StOrderTotalVo vo = list.get(a);
                if(vo.getCreateTime().equals(date)){ //当天的信息
                }else{
                    StOrderTotalVo vo1 = new StOrderTotalVo();
                    vo1.setAmount("0");
                    vo1.setCreateTime(date);
                    vo1.setQunatity(0);
                    list.add(a,vo1);
                }
            }
            catch(Exception e){
                System.out.println("异常信息：" + e.getMessage());
                StOrderTotalVo vo = new StOrderTotalVo();
                vo.setAmount("0");
                vo.setCreateTime(date);
                vo.setQunatity(0);
                list.add(a,vo);
                continue;
            }

        }
//        Collections.reverse(list);
        return list;
    }

    @Override
    public StOrderTotalVo selectlast30total(Map<String, Object> map) {
        return stOrderMapper.selectlast30total(map);
    }

    @Override
    public StOrderTotalVo selectalltotal(Map<String, Object> map) {
        return stOrderMapper.selectalltotal(map);
    }


}
