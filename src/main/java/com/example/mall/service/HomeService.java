package com.example.mall.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.github.pagehelper.PageInfo;
import com.example.mall.domain.HomeContentResult;
import com.example.mall.model.CmsSubject;
import com.example.mall.model.PmsProduct;
import com.example.mall.model.PmsProductCategory;
import com.example.mall.vo.SmsGroupBuyProductVO;

import java.util.List;

/**
 * 首页内容管理Service
 * Created by macro on 2019/1/28.
 */
public interface HomeService {

    /**
     * 获取首页内容
     */
    HomeContentResult content();

    /**
     * 首页商品推荐
     */
    Page<PmsProduct> recommendProductList(Integer limit, Integer page);


    //查询
    Page<SmsGroupBuyProductVO> smsGroupBuyProductList(Integer page, Integer limit);
    /**
     * 获取商品分类
     * @param parentId 0:获取一级分类；其他：获取指定二级分类
     */
    List<PmsProductCategory> getProductCateList(Long parentId);
        //获取所有分类
    List<PmsProductCategory> getCateGoryList();

    /**
     * 根据专题分类分页获取专题
     * @param cateId 专题分类id
     */
    List<CmsSubject> getSubjectList(Long cateId, Integer pageSize, Integer pageNum);
}
