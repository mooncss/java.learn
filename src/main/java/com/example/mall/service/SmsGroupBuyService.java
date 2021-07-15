package com.example.mall.service;

import com.baomidou.mybatisplus.service.IService;
import com.example.mall.model.SmsGroupBuy;

import java.util.Map;

public interface SmsGroupBuyService extends IService<SmsGroupBuy> {

    int batchUpdate(Map<String,Object> map);
}
