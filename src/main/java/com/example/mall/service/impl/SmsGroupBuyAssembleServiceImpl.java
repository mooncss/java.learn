package com.example.mall.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.example.mall.dao.SmsGroupBuyAssembleDao;
import com.example.mall.model.SmsGroupBuyAssemble;
import com.example.mall.service.SmsGroupBuyAssembleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SmsGroupBuyAssembleServiceImpl extends
        ServiceImpl<SmsGroupBuyAssembleDao,SmsGroupBuyAssemble> implements SmsGroupBuyAssembleService {

    @Autowired
    SmsGroupBuyAssembleDao smsGroupBuyAssembleDao;
    @Override
    public String selectMaxSid() {
        return smsGroupBuyAssembleDao.selectMaxSid();
    }

    @Override
    public List<SmsGroupBuyAssemble> selectAutoComList(Map<String, Object> map) {
        return smsGroupBuyAssembleDao.selectAutoComList(map);
    }
}
