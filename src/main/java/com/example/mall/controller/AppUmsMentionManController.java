package com.example.mall.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.example.mall.common.CommonResult;
import com.example.mall.emun.OrderStatus;
import com.example.mall.model.UmsMentionMan;
import com.example.mall.service.UmsMentionManService;
import com.zhihui.uj.management.BaseController.BaseController;
import com.zhihui.uj.management.utils.IdUtils;
import io.swagger.annotations.Api;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@Api("自提联系人")
@RestController
@RequestMapping("/app/mentionman")
public class AppUmsMentionManController extends BaseController {


    @Autowired
    UmsMentionManService umsMentionManService;


    @PostMapping("/add")
    public CommonResult add(UmsMentionMan man){
        String  mobile =getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        if(StringUtils.isBlank(man.getReceiveMan()) || StringUtils.isBlank(man.getReceivePhone())){
            return CommonResult.failed("联系人信息不能为空！");
        }
        man.setCreateTime(new Date());
        man.setMemberId(mobile);
        man.setId(IdUtils.createUUID());
        umsMentionManService.insert(man);
        return CommonResult.success("保存成功");
    }

    @GetMapping("/list")
    public CommonResult list(){
        String  mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        List<UmsMentionMan> list=  umsMentionManService.selectList(new EntityWrapper<UmsMentionMan>().eq("member_id",mobile));
        return CommonResult.success(list);
    }


}
