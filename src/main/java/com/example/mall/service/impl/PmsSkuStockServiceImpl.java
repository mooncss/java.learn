package com.example.mall.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.example.mall.dao.PmsSkuStockDao;
import com.example.mall.dto.PmsAttributeVO;
import com.example.mall.mapper.PmsSkuStockMapper;
import com.example.mall.model.PmsSkuStock;
import com.example.mall.model.PmsSkuStockExample;
import com.example.mall.service.PmsSkuStockService;
import com.zhihui.uj.management.charging.entity.TChargeConfig;
import com.zhihui.uj.management.charging.mapper.TChargeConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 商品sku库存管理Service实现类
 * Created by macro on 2018/4/27.
 */
@Service
public class PmsSkuStockServiceImpl extends ServiceImpl<PmsSkuStockMapper, PmsSkuStock> implements PmsSkuStockService {
    @Autowired
    private PmsSkuStockMapper skuStockMapper;
    @Autowired
    private PmsSkuStockDao skuStockDao;

    @Override
    public List<PmsSkuStock> getList(Long pid, String keyword) {
        PmsSkuStockExample example = new PmsSkuStockExample();
        PmsSkuStockExample.Criteria criteria = example.createCriteria().andProductIdEqualTo(pid);
        if (!StringUtils.isEmpty(keyword)) {
            criteria.andSkuCodeLike("%" + keyword + "%");
        }
        return skuStockMapper.selectByExample(example);
    }

    @Override
    public int update(Long pid, List<PmsSkuStock> skuStockList) {
        return skuStockDao.replaceList(skuStockList);
    }

    @Override
    public List<PmsAttributeVO> getAttrValueList(Long productid) {
        return skuStockDao.getAttrValueList(productid);
    }
}
