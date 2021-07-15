package com.example.mall.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.example.mall.model.BesUser;
import com.example.mall.vo.CartItemBesGroup;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

//商家Dao
@Service
public interface BesUserDao extends BaseMapper<BesUser> {

//    List<BesUser> getbess(Map<String,Object> map);
    int batchUpdate(@Param("map") Map<String,Object>  map);
    int batchUpdatestatus(@Param("map") Map<String,Object>  map);
    List<CartItemBesGroup> selectBesFromcart(@Param("map") Map<String, Object> map);
    List<CartItemBesGroup> selectBesFromcart0(@Param("map") Map<String, Object> map);
    Long selectmaxId();
}
