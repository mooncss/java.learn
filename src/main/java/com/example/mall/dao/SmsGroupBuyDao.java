package com.example.mall.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.example.mall.model.SmsGroupBuy;
import org.apache.ibatis.annotations.Param;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface SmsGroupBuyDao extends BaseMapper<SmsGroupBuy> {
    int batchUpdate(@Param("map") Map<String,Object> map);
}
