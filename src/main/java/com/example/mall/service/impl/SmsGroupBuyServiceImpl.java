package com.example.mall.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.example.mall.dao.SmsGroupBuyDao;
import com.example.mall.model.SmsGroupBuy;
import com.example.mall.service.SmsGroupBuyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SmsGroupBuyServiceImpl extends ServiceImpl<SmsGroupBuyDao, SmsGroupBuy> implements SmsGroupBuyService {
    @Autowired
    SmsGroupBuyDao smsGroupBuyDao;
    @Override
    public int batchUpdate(Map<String, Object> map) {
        return smsGroupBuyDao.batchUpdate(map);
    }
}
