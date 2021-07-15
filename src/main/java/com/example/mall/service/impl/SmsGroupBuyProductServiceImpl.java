package com.example.mall.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.example.mall.dao.SmsGroupBuyProductDao;
import com.example.mall.dto.SmsGroupBuyBesPronameDto;
import com.example.mall.model.SmsGroupBuyProduct;
import com.example.mall.service.SmsGroupBuyProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SmsGroupBuyProductServiceImpl extends ServiceImpl<SmsGroupBuyProductDao, SmsGroupBuyProduct>
        implements SmsGroupBuyProductService {

    @Autowired
    SmsGroupBuyProductDao smsGroupBuyProductDao;
    @Override
    public int batchUpdate(Map<String, Object> map) {
        return smsGroupBuyProductDao.batchUpdate(map);
    }

    @Override
    public List<SmsGroupBuyBesPronameDto> searhAllGroupPro(Map<String, Object> map) {
        return smsGroupBuyProductDao.searhAllGroupPro(map);
    }
}
