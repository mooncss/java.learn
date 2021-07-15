package com.example.mall.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.example.mall.common.CommonResult;
import com.example.mall.domain.HomeContentResult;
import com.example.mall.model.CmsSubject;
import com.example.mall.model.PmsProduct;
import com.example.mall.model.PmsProductCategory;
import com.example.mall.service.HomeService;
import com.example.mall.service.PmsProductService;
import com.example.mall.service.SmsGroupBuyProductService;
import com.example.mall.vo.SmsGroupBuyProductVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * app获取首页内容
 */

@Controller
@Api(tags = "HomeController", description = "首页内容管理")
@RequestMapping("/app")
public class HomeController {
    @Autowired
    private HomeService homeService;

    @Autowired
    PmsProductService pmsProductService;

    @Autowired
    SmsGroupBuyProductService smsGroupBuyProductService;


    @ApiOperation("首页内容页信息展示")
    @RequestMapping(value = "/indexcontent", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<HomeContentResult> content() {
        HomeContentResult contentResult = homeService.content();
        return CommonResult.success(contentResult);
    }

    @ApiOperation("分页获取爆款商品")
    @RequestMapping(value = "/recommendProductList", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult recommendProductList(@RequestParam(value = "limit", defaultValue = "15") Integer limit,
                                                               @RequestParam(value = "page", defaultValue = "1") Integer page) {
//        int pager = (page-1)*limit;
        Page<PmsProduct> productList = homeService.recommendProductList(limit, page);
        for(PmsProduct p:productList.getRecords()){
            if(p.getMinskuprice() != null && p.getMinskuprice().compareTo(new BigDecimal(0))  == 1){
                p.setPrice(p.getMinskuprice());
            }
        }
        return CommonResult.success(productList);
    }

    @ApiOperation("分页获取拼团")
    @RequestMapping(value = "/smsGroupBuyProductList", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult smsGroupBuyProductList(@RequestParam(value = "limit", defaultValue = "15") Integer limit,
                                             @RequestParam(value = "page", defaultValue = "1") Integer page) {
        int pager = (page-1)*limit;
        Page<SmsGroupBuyProductVO> productList = homeService.smsGroupBuyProductList(limit, pager);
        return CommonResult.success(productList);
    }

    @ApiOperation("获取首页商品分类")
    @RequestMapping(value = "/productCateList/{parentId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PmsProductCategory>> getProductCateList(@PathVariable Long parentId) {
        List<PmsProductCategory> productCategoryList = homeService.getProductCateList(parentId);
        return CommonResult.success(productCategoryList);
    }

    @ApiOperation("根据分类获取专题")
    @RequestMapping(value = "/subjectList", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<CmsSubject>> getSubjectList(@RequestParam(required = false) Long cateId,
                                                         @RequestParam(value = "limit", defaultValue = "4") Integer limit,
                                                         @RequestParam(value = "page", defaultValue = "1") Integer page) {
        List<CmsSubject> subjectList = homeService.getSubjectList(cateId,limit,page);
        return CommonResult.success(subjectList);
    }

}
