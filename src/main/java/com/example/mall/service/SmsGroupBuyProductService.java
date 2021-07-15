package com.example.mall.service;

import com.baomidou.mybatisplus.service.IService;
import com.example.mall.dto.SmsGroupBuyBesPronameDto;
import com.example.mall.model.SmsGroupBuyProduct;

import java.util.List;
import java.util.Map;

public interface SmsGroupBuyProductService extends IService<SmsGroupBuyProduct> {

    int batchUpdate(Map<String,Object> map);

    List<SmsGroupBuyBesPronameDto> searhAllGroupPro(Map<String,Object> map);
}
