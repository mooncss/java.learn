package com.example.mall.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.example.mall.common.DateUtil;
import com.example.mall.dao.BesPlateDao;
import com.example.mall.dao.HomeDao;
import com.example.mall.dao.SmsGroupBuyProductDao;
import com.example.mall.domain.FlashPromotionProduct;
import com.example.mall.domain.HomeContentResult;
import com.example.mall.domain.HomeFlashPromotion;
import com.example.mall.mapper.*;
import com.example.mall.model.*;
//import com.example.mall.portal.dao.HomeDao;
//import com.example.mall.portal.domain.FlashPromotionProduct;
//import com.example.mall.portal.domain.HomeContentResult;
//import com.example.mall.portal.domain.HomeFlashPromotion;
//import com.example.mall.portal.service.HomeService;
import com.example.mall.service.HomeService;
import com.example.mall.vo.SmsGroupBuyProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页内容管理Service实现类
 * Created by macro on 2019/1/28.
 */
@Service
public class HomeServiceImpl implements HomeService {
    @Autowired
    SmsHomeAdvertiseMapper smsHomeAdvertiseMapper;
    @Autowired
    HomeDao homeDao;
    @Autowired
    private SmsFlashPromotionMapper flashPromotionMapper;
    @Autowired
    private SmsFlashPromotionSessionMapper promotionSessionMapper;
    @Autowired
    private PmsProductMapper productMapper;
    @Autowired
    private PmsProductCategoryMapper productCategoryMapper;
    @Autowired
    private CmsSubjectMapper subjectMapper;

    @Autowired
    private SmsGroupBuyProductDao smsGroupBuyProductDao;



    @Override
    public HomeContentResult content() {
        HomeContentResult result = new HomeContentResult();
        //获取首页广告
        result.setAdvertiseList(getHomeAdvertiseList());
        //首页分类
        result.setCategoryList(getCateGoryList());
        //拼团商品列表

        //拼团活动 即将结束  比如距离结束5分钟 10分钟 半小时 1小时
//
//        for(SmsGroupBuyProductVO v:ls){
//            Date endtime  = v.getEndTime(); //活动结束时间
//        }
//        result.setSmsGroupBuyList(ls);
        //获取推荐品牌
//        result.setBrandList(homeDao.getRecommendBrandList(0,10));   //推荐品牌迁移
        //获取秒杀信息
//        result.setHomeFlashPromotion(getHomeFlashPromotion());
        //获取拼团商品信息

        //获取新品推荐
//        result.setNewProductList(homeDao.getNewProductList(0,10));
        //获取人气推荐
//        result.setHotProductList(homeDao.getHotProductList(0,10));
        //获取推荐专题
//        result.setSubjectList(homeDao.getRecommendSubjectList(0,10));
        //获取分类
//        result.setBesPlateList(besPlateDao.selectList(null));
        return result;
    }

    @Override
    public Page<SmsGroupBuyProductVO> smsGroupBuyProductList(Integer limit, Integer page) {
        Map<String,Object> map = new HashMap<>();
        map.put("page",page);
        map.put("limit",limit);
        List<SmsGroupBuyProductVO> ls = smsGroupBuyProductDao.getonlist(map);
        for(SmsGroupBuyProductVO v:ls){
            if(v.getPrice() != null){
                v.setPromotionPrice(v.getPrice());
            }
            if(v.getYiPin() != null){
                v.setPromotionCountAl(v.getYiPin());
            }
        }
        int count = smsGroupBuyProductDao.getCount();
        Page<SmsGroupBuyProductVO> page1 = new Page<>();
        page1.setRecords(ls);
        page1.setTotal(count);
        page1.setSize(ls.size());
        return page1;
    }
    @Override
    public Page<PmsProduct> recommendProductList(Integer limit, Integer page) {
//        PmsProductExample example = new PmsProductExample();
//        example.createCriteria()
//                .andDeleteStatusEqualTo(0)
//                .andPublishStatusEqualTo(1);
//        example.setLimit(limit);
//        example.setPage((page-1)*limit);
//        List<PmsProduct> list= productMapper.selectByExample(example);
//        Page<PmsProduct> page1 = new Page<>();
//        int count = productMapper.selectByExampleCount(example);
//        page1.setRecords(list);
//        page1.setTotal(count);
//        page1.setSize(list!=null?list.size():0);
//        return page1;
        Map<String,Object> map = new HashMap<>();
        map.put("limit",limit);
        map.put("page",(page-1)*limit);
        List<PmsProduct> list = productMapper.selectByExampleRecomand(map);
        for(PmsProduct p:list){
            Integer stock = p.getStock();
            Integer lockstock = p.getLockStock();
            if(stock == null){
                stock = 0;
            }
            if(lockstock == null){
                lockstock = 0;
            }
            if(stock - lockstock <= 0){
                p.setStock(0);
            }else{
                p.setStock(stock - lockstock);
            }
        }
        Page<PmsProduct> page1 = new Page<>();
        int count = productMapper.selectByExampleCountRecomond();
        page1.setRecords(list);
        page1.setTotal(count);
        page1.setSize(list!=null?list.size():0);
        return page1;

    }


    @Override
    public List<PmsProductCategory> getProductCateList(Long parentId) {
        PmsProductCategoryExample example = new PmsProductCategoryExample();
        example.createCriteria()
                .andShowStatusEqualTo(1)
                .andParentIdEqualTo(parentId);
        example.setOrderByClause("sort desc");
        return productCategoryMapper.selectByExample(example);
    }

    @Override
    public List<PmsProductCategory> getCateGoryList() {
        PmsProductCategoryExample example = new PmsProductCategoryExample();
        example.createCriteria()
                .andShowStatusEqualTo(1)
                .andParentIdEqualTo(0L);
        example.setOrderByClause("sort asc");
        return productCategoryMapper.selectByExample(example);
    }

    @Override
    public List<CmsSubject> getSubjectList(Long cateId, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum,pageSize);
        CmsSubjectExample example = new CmsSubjectExample();
        CmsSubjectExample.Criteria criteria = example.createCriteria();
        criteria.andShowStatusEqualTo(1);
        if(cateId!=null){
            criteria.andCategoryIdEqualTo(cateId);
        }
        return subjectMapper.selectByExample(example);
    }

    private HomeFlashPromotion getHomeFlashPromotion() {
        HomeFlashPromotion homeFlashPromotion = new HomeFlashPromotion();
        //获取当前秒杀活动
        Date now = new Date();
        SmsFlashPromotion flashPromotion = getFlashPromotion(now);
        if (flashPromotion != null) {
            //获取当前秒杀场次
            SmsFlashPromotionSession flashPromotionSession = getFlashPromotionSession(now);
            if (flashPromotionSession != null) {
                homeFlashPromotion.setStartTime(flashPromotionSession.getStartTime());
                homeFlashPromotion.setEndTime(flashPromotionSession.getEndTime());
                //获取下一个秒杀场次
                SmsFlashPromotionSession nextSession = getNextFlashPromotionSession(homeFlashPromotion.getStartTime());
                if(nextSession!=null){
                    homeFlashPromotion.setNextStartTime(nextSession.getStartTime());
                    homeFlashPromotion.setNextEndTime(nextSession.getEndTime());
                }
                //获取秒杀商品
                List<FlashPromotionProduct> flashProductList = homeDao.getFlashProductList(flashPromotion.getId(), flashPromotionSession.getId());
                homeFlashPromotion.setProductList(flashProductList);
            }
        }
        return homeFlashPromotion;
    }

    //获取下一个场次信息
    private SmsFlashPromotionSession getNextFlashPromotionSession(Date date) {
        SmsFlashPromotionSessionExample sessionExample = new SmsFlashPromotionSessionExample();
        sessionExample.createCriteria()
                .andStartTimeGreaterThan(date);
        sessionExample.setOrderByClause("start_time asc");
        List<SmsFlashPromotionSession> promotionSessionList = promotionSessionMapper.selectByExample(sessionExample);
        if (!CollectionUtils.isEmpty(promotionSessionList)) {
            return promotionSessionList.get(0);
        }
        return null;
    }

    private List<SmsHomeAdvertise> getHomeAdvertiseList() {
        SmsHomeAdvertiseExample example = new SmsHomeAdvertiseExample();
        example.createCriteria().andStatusEqualTo(1); //.andTypeEqualTo(1)  临时去掉
        example.setOrderByClause("sort desc");
        return smsHomeAdvertiseMapper.selectByExample(example);
    }

    //根据时间获取秒杀活动
    private SmsFlashPromotion getFlashPromotion(Date date) {
        Date currDate = DateUtil.getDate(date);
        SmsFlashPromotionExample example = new SmsFlashPromotionExample();
        example.createCriteria()
                .andStatusEqualTo(1)
                .andStartDateLessThanOrEqualTo(currDate)
                .andEndDateGreaterThanOrEqualTo(currDate);
        List<SmsFlashPromotion> flashPromotionList = flashPromotionMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(flashPromotionList)) {
            return flashPromotionList.get(0);
        }
        return null;
    }

    //根据时间获取秒杀场次
    private SmsFlashPromotionSession getFlashPromotionSession(Date date) {
        Date currTime = DateUtil.getTime(date);
        SmsFlashPromotionSessionExample sessionExample = new SmsFlashPromotionSessionExample();
        sessionExample.createCriteria()
                .andStartTimeLessThanOrEqualTo(currTime)
                .andEndTimeGreaterThanOrEqualTo(currTime);
        List<SmsFlashPromotionSession> promotionSessionList = promotionSessionMapper.selectByExample(sessionExample);
        if (!CollectionUtils.isEmpty(promotionSessionList)) {
            return promotionSessionList.get(0);
        }
        return null;
    }
}
