package com.example.mall.dao;

import com.example.mall.dto.PmsAttributeVO;
import com.example.mall.model.PmsSkuStock;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 自定义商品sku库存Dao
 * Created by macro on 2018/4/26.
 */
@Service
public interface PmsSkuStockDao {
    /**
     * 批量插入操作
     */
    int insertList(@Param("list") List<PmsSkuStock> skuStockList);

    /**
     * 批量插入或替换操作
     */
    int replaceList(@Param("list") List<PmsSkuStock> skuStockList);


    //查询商品属性
    List<PmsAttributeVO>  getAttrValueList(Long id);
}
