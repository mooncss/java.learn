package com.example.mall.service;

import com.baomidou.mybatisplus.service.IService;
import com.example.mall.model.BesUser;
import com.example.mall.vo.CartItemBesGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BesUserService extends IService<BesUser> {
//    List<BesUser> getbess(Map<String,Object> map);
    int batchUpdate(Map<String,Object> map);

    int batchUpdatestatus(Map<String,Object>  map);

    List<CartItemBesGroup> selectBesFromcart(Map<String,Object>  map);

    List<CartItemBesGroup> selectBesFromcart0(Map<String,Object>  map);

    Long selectmaxId();
}
