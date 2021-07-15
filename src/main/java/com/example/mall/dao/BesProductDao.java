package com.example.mall.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.example.mall.dto.PmsProductResult;
import com.example.mall.model.PmsProduct;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;


//商家商品DAO
@Service
public interface BesProductDao extends BaseMapper<PmsProduct>  {
    /**
     * 获取商品编辑信息
     */
    PmsProductResult getUpdateInfo(@Param("id") Long id);
}
