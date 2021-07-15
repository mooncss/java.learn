package com.example.mall.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.example.mall.common.CommonResult;
import com.example.mall.model.BesMotion;
import com.example.mall.service.BesMotionService;
import com.zhihui.uj.management.BaseController.BaseController;
import io.swagger.annotations.Api;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/bes/motion")
@Api("配送区域设置")
@RequiresAuthentication
public class BesMotionController extends BaseController {

    @Autowired
    BesMotionService besMotionService;

    @PostMapping("/add")
    @ResponseBody
    public CommonResult add(@RequestBody BesMotion point){
        if(point!= null){
            point.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        }
        boolean a = besMotionService.insert(point);
        if(a){
            return CommonResult.success("新增成功");
        }
          return CommonResult.success("授权失败");

    }
    //删除
    @PostMapping("/delete")
    @ResponseBody
    public CommonResult delete(String id){
        boolean b = besMotionService.deleteById(id);
        return CommonResult.success(b);
    }


    //列表查询
    @GetMapping("/list")
    @ResponseBody
    public CommonResult getMotionList(){
        List<BesMotion> list = besMotionService.selectList(new EntityWrapper<>());
        return CommonResult.success(list);
    }

}
