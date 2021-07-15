package com.example.mall.domain;

import com.github.pagehelper.PageInfo;
import com.example.mall.model.*;
import com.example.mall.vo.SmsGroupBuyProductVO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 首页内容返回信息封装
 * Created by macro on 2019/1/28.
 */
@Getter
@Setter
public class HomeContentResult {
    //轮播广告
    private List<SmsHomeAdvertise> advertiseList;
    //推荐品牌
    private List<PmsBrand> brandList;
    //当前秒杀场次
    private HomeFlashPromotion homeFlashPromotion;
    //新品推荐
    private List<PmsProduct> newProductList;
    //人气推荐
    private List<PmsProduct> hotProductList;
    //推荐专题
    private List<CmsSubject> subjectList;
    //分类导航
    private List<BesPlate> besPlateList;

    private List<PmsProductCategory> categoryList;

//    private List<SmsGroupBuyProductVO> smsGroupBuyList;

//    private PageInfo<SmsGroupBuyProductVO> smsGroupBuyList;

}
