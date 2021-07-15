package com.example.mall.mapper;

import com.example.mall.model.OmsCartItem;
import com.example.mall.model.OmsCartItemExample;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface OmsCartItemMapper {
    long countByExample(OmsCartItemExample example);

    int deleteByExample(OmsCartItemExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OmsCartItem record);

    int insertSelective(OmsCartItem record);

    List<OmsCartItem> selectByExample(OmsCartItemExample example);

    /*查询积分比例专用*/
    List<OmsCartItem> selectByExampleGift(@Param("map") Map<String,Object> map);

    List<OmsCartItem> selectAllCartItems(@Param("memberId") String memberId);

    OmsCartItem selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OmsCartItem record, @Param("example") OmsCartItemExample example);

    int updateByExample(@Param("record") OmsCartItem record, @Param("example") OmsCartItemExample example);

    int updateByPrimaryKeySelective(OmsCartItem record);

    int updateByPrimaryKey(OmsCartItem record);

    int countCart(@Param("mobile") String mobile);

    int deleteCartItems(@Param("map") Map<String,Object> map);

    Integer countByProductId(@Param("map") Map<String,Object> map);

}