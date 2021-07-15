package com.example.mall.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.example.mall.model.SmsGroupBuyAssemble;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SmsGroupBuyAssembleDao extends BaseMapper<SmsGroupBuyAssemble> {
        String  selectMaxSid();
        List<SmsGroupBuyAssemble> selectAutoComList(@Param("map") Map<String,Object> map);
}
