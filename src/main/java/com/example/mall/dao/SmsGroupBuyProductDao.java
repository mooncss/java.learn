package com.example.mall.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.example.mall.dto.SmsGroupBuyBesPronameDto;
import com.example.mall.model.SmsGroupBuy;
import com.example.mall.model.SmsGroupBuyProduct;
import com.example.mall.vo.SmsGroupBuyProductVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SmsGroupBuyProductDao extends BaseMapper<SmsGroupBuyProduct> {
    List<SmsGroupBuyProductVO> getonlist(Map<String,Object> map);

    int getCount();

    int batchUpdate(@Param("map") Map<String,Object> map);

    List<SmsGroupBuyBesPronameDto> searhAllGroupPro(@Param("map") Map<String, Object> map);
}
