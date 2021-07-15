package com.example.mall.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.example.mall.common.CommonResult;
import com.example.mall.model.BesMotion;
import com.example.mall.service.BesMotionService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/app/motion")
@Api("app显示可配送区域")
public class AppMotionMapController {

    @Autowired
    BesMotionService besMotionService;

    @GetMapping("/list")
    @ResponseBody
    public CommonResult getMotionList(){
        List<BesMotion>  list =besMotionService.selectList(new EntityWrapper<>());
        return CommonResult.success(list);
    }
}
