package com.example.mall.controller;


import com.baomidou.mybatisplus.plugins.Page;
import com.github.pagehelper.PageInfo;
import com.example.mall.common.CommonPage;
import com.example.mall.common.CommonResult;
import com.example.mall.dto.PmsProductCategoryWithChildrenItem;
import com.example.mall.dto.PmsProductQueryParam;
import com.example.mall.dto.PmsProductResult;
import com.example.mall.model.*;
import com.example.mall.service.*;
import com.zhihui.uj.management.BaseController.BaseController;
import com.zhihui.uj.management.utils.Query;
import com.zhihui.uj.management.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/besproduct")
@Api("管理员管理商家商品@商品")
@RequiresAuthentication
public class BesProductController extends BaseController {

    @Autowired
    PmsProductService pmsProductService;

    @Autowired
    PmsProductAttributeService productAttributeService;

    @Autowired
    PmsProductCategoryService productCategoryService;

    @Autowired
    PmsProductAttributeCategoryService productAttributeCategoryService;

    @Autowired
    private DemoService demoService;

    //获取所有商品   可条件筛选
    @GetMapping("/page")
    @ResponseBody
    public R page(@RequestParam int page, @RequestParam int limit,
                             PmsProductQueryParam productQueryParam){
        PageInfo<PmsProduct> productpage =
                pmsProductService.list(productQueryParam, limit, page);
        return new R(productpage);
    }

    //商品发布后默认为上架 管理员可强制下架
    //oper  0为审核下架   1 . 为审核上架，
    //批量修改审核状态
    @PostMapping("/update/onsaleStatus")
    @ResponseBody    //List<Long> ids,@RequestParam Integer operate,String detail
    @ApiOperation("批量修改  设置商品上下架")
    public R updateonsale(@RequestBody Map<String,Object> params){

        System.out.println(params);

        List<Integer> idlist = (List) params.get("ids");
        List<Long> ids = new ArrayList<>();
        for(Integer a : idlist){
            ids.add(new Long(a));
        }
        int count =  pmsProductService.updateVerifyStatus
                (ids,(Integer)params.get("operate"),(String)params.get("detail"));
        if(count > 0){
            return new R(R.SUCCESS);
        }
        else{
            return new R(R.FAIL);
        }
    }


    @ApiOperation("根据分类查询属性列表或参数列表")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "type", value = "0表示属性，1表示参数",
                    required = true, paramType = "query", dataType = "integer")})
    @RequestMapping(value = "/list/{cid}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsProductAttribute>> getList(@PathVariable Long cid,
                                                                 @RequestParam(value = "type") Integer type) {
        List<PmsProductAttribute> productAttributeList =
                productAttributeService.getList(cid, type, 100, 1);
        return CommonResult.success(CommonPage.restPage(productAttributeList));
    }


    @ApiOperation("查询所有一级分类及子分类")
    @RequestMapping(value = "/list/withChildren", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PmsProductCategoryWithChildrenItem>> listWithChildren() {
        List<PmsProductCategoryWithChildrenItem> list = productCategoryService.listWithChildren();
        return CommonResult.success(list);
    }


    @ApiOperation("分页获取所有商品属性分类")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsProductAttributeCategory>> getList() {
        List<PmsProductAttributeCategory> productAttributeCategoryList = productAttributeCategoryService.getList(1, 100);
        return CommonResult.success(CommonPage.restPage(productAttributeCategoryList));
    }


    //获取所有品牌
    @ApiOperation(value = "分页获取品牌列表")
    @RequestMapping(value = "/brand/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsBrand>> listBrand()
    {
        List<PmsBrand> brandList = demoService.listBrand(1, 101);
        return CommonResult.success(CommonPage.restPage(brandList));
    }
    @ApiOperation("根据商品id获取商品编辑信息")
    @RequestMapping(value = "/updateInfo/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PmsProductResult> getUpdateInfo(@PathVariable Long id) {
        PmsProductResult productResult = pmsProductService.getUpdateInfo(id);
        return CommonResult.success(productResult);
    }

    @ApiOperation("根据ID查询商品详情")
    @RequestMapping("/getproductinfobyid")
    @ResponseBody
    public R getproductinfobyid(@RequestParam Long id){
        if(StringUtils.isBlank(""+id)){
            return new R(R.FAIL);
        }
        return new R( pmsProductService.getproductbyid(id));
    }

    @PostMapping("/setpopular")
    @ResponseBody
    @ApiOperation("批量设置商品爆款") //推荐和取消推荐
    @RequiresAuthentication
    public R setpopular(@RequestBody Map<String,Object> params){  //List<Long> ids, Integer operate
        List<Integer> idlist = (List) params.get("ids");
        List<Long> ids = new ArrayList<>();
        for(Integer a : idlist){
            ids.add(new Long(a));
        }
        int count =  pmsProductService.updateRecommendStatus
                (ids,(Integer)params.get("operate"));
        if(count > 0){
            return new R(R.SUCCESS);
        }
        else{
            return new R(R.FAIL);
        }
    }

    @ApiOperation("批量修改删除状态")
    @RequestMapping(value = "/update/deleteStatus", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateDeleteStatus(@RequestBody Map<String,Object> params) {
        List<Integer> idlist = (List) params.get("ids");
        List<Long> ids = new ArrayList<>();
        for(Integer a : idlist){
            ids.add(new Long(a));
        }
        int count = pmsProductService.updateDeleteStatus(ids, (Integer)params.get("operate"));
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("批量推荐商品")
    @RequestMapping(value = "/update/recommendStatus", method = RequestMethod.POST)
    @ResponseBody
//    @PreAuthorize("hasAuthority('pms:product:update')")
    public CommonResult updateRecommendStatus(@RequestBody Map<String,Object> params) {
        List<Integer> idlist = (List) params.get("ids");
        List<Long> ids = new ArrayList<>();
        for(Integer a : idlist){
            ids.add(new Long(a));
        }
        int count = pmsProductService.updateRecommendStatus(ids, (Integer)params.get("operate"));
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 设置商品 积分率
     * @param map
     * @return
     */

    @ApiOperation("设置商品 积分率")
    @RequestMapping(value = "/update/JFrate", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult JFrate(@RequestBody Map<String,Object> map) {
        logger.info("更新参数"+ map.get("productId") +"比率：" +  map.get("rate"));
        if(map.get("rate") == null || map.get("productId") == null){
            return CommonResult.failed("参数错误");
        }
//        Map<String,Object> map = new HashMap<>();
        BigDecimal bd = new BigDecimal((String)map.get("rate"));
        BigDecimal bdm100 = bd.divide(new BigDecimal(100),2,4);
        map.put("id",map.get("productId"));
        map.put("rate",bdm100);
        int res = pmsProductService.updateJFrate(map);
        if (res > 0) {
            return CommonResult.success("更新完成");
        } else {
            return CommonResult.failed();
        }
    }
}
