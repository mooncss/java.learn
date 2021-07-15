package com.example.mall.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.github.pagehelper.PageInfo;
import com.example.mall.common.CommonPage;
import com.example.mall.common.CommonResult;
import com.example.mall.dto.OmsOrderDetail;
import com.example.mall.dto.OmsOrderQueryParam;
import com.example.mall.model.OmsOrder;
import com.example.mall.service.OmsOrderService;
import com.zhihui.uj.management.BaseController.BaseController;
import com.zhihui.uj.management.config.LoggingProcessFilter;
import com.zhihui.uj.management.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "后台订单管理", description = "后台订单管理")
@Controller
@RequestMapping("/besorder")
@RequiresAuthentication
public class OmsOrderController extends BaseController {

    @Autowired
    private OmsOrderService orderService;

    @GetMapping("/page")
    @ResponseBody
    @ApiOperation("分页查询所有订单")
    public R page(@RequestParam(value = "limit", defaultValue = "10") Integer limit,
                  @RequestParam(value = "page", defaultValue = "1") Integer page,OmsOrderQueryParam orderQueryParam){
            Page<OmsOrder> list  =  orderService.list(orderQueryParam, limit, page,null);
        return new R(list);
    }


    @ApiOperation("获取订单详情:订单信息、商品信息、操作记录 传入订单编号")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<OmsOrderDetail> detail(@PathVariable Long id) {
        OmsOrderDetail orderDetailResult = orderService.detail(id);
        return CommonResult.success(orderDetailResult);
    }


}
