package com.example.mall.service;

import com.baomidou.mybatisplus.service.IService;
import com.example.mall.model.SmsGroupBuyAssemble;

import java.util.List;
import java.util.Map;

public interface SmsGroupBuyAssembleService extends IService<SmsGroupBuyAssemble> {
    String selectMaxSid();
    List<SmsGroupBuyAssemble> selectAutoComList(Map<String,Object> map);
}
