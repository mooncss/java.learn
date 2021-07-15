package com.example.mall.service;

import com.baomidou.mybatisplus.service.IService;
import com.example.mall.dto.PmsAttributeVO;
import com.example.mall.model.PmsSkuStock;

import java.util.List;

/**
 * sku商品库存管理Service
 * Created by macro on 2018/4/27.
 */
public interface PmsSkuStockService extends IService<PmsSkuStock> {
    /**
     * 根据产品id和skuCode模糊搜索
     */
    List<PmsSkuStock> getList(Long pid, String keyword);

    /**
     * 批量更新商品库存信息
     */
    int update(Long pid, List<PmsSkuStock> skuStockList);

    //查询
    List<PmsAttributeVO> getAttrValueList(Long productid);
}
