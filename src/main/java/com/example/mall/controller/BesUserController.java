package com.example.mall.controller;

import cn.hutool.db.PageResult;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.example.mall.model.BesUser;
import com.example.mall.service.BesUserService;
import com.zhihui.uj.management.utils.Query;
import com.zhihui.uj.management.utils.R;
import com.zhihui.uj.management.utils.ShiroUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Api("后台商家控制器")
@Controller
@RequestMapping("/besuser")
@RequiresAuthentication
public class BesUserController {

    @Autowired
    BesUserService besUserService;

    //查询待审核的商家
    @RequestMapping(value = "/page",method = RequestMethod.GET)
    @ResponseBody
    public R page(@RequestParam int page,@RequestParam int limit,String phone,String shopname){
        EntityWrapper<BesUser> en = new EntityWrapper<BesUser>();
        //可添加搜索查询条件
        en.eq("checked","1");
        en.like("phone",phone);
        en.like("shopname",shopname);
        en.orderBy("applytime",false);  //按照申请时间倒序排列
        Page<BesUser> pr =
                besUserService.selectPage(
                        new Query<>(pageAndLimit(page,limit)),en);
        return new R(pr);
    }

    //已审核商家
    @RequestMapping(value = "/pages",method = RequestMethod.GET)
    @ResponseBody
    public R pages(@RequestParam int page,@RequestParam int limit, String phone,String shopname,String status){
        EntityWrapper<BesUser> en = new EntityWrapper<>();
        //可添加搜索查询条件
        en.eq("checked","2"); //已审核商家
        if(!StringUtils.isBlank(status)){
            en.eq("status",status);
        }
        en.like("phone",phone);
        en.like("shopname",shopname);
        en.orderBy("applytime",false);  //按照申请时间倒序排列
        Page<BesUser> pr =
                besUserService.selectPage(
                        new Query<>(pageAndLimit(page,limit)),en);
        return new R(pr);
    }

    @ApiOperation("商家审核/反审核")
    @RequestMapping(value = "/checkbes",method = RequestMethod.POST)
    @ResponseBody
    @RequiresAuthentication
    public R checkbes(@RequestBody Map<String,Object> params){  //operate ids[]
        //可添加搜索查询条件
        String userid = ShiroUtils.getUserId();
        params.put("checktime",new Date());
        params.put("checkuser",userid);
        int a =besUserService.batchUpdate(params);
        if(a>0){
            return new R("审核成功",R.SUCCESS);
        }else{
            return new R("审核失败",R.FAIL);
        }
    }

    @ApiOperation("设置商家不可用")
    @RequestMapping(value = "/statubes",method = RequestMethod.POST)
    @ResponseBody
    public R statubes(@RequestBody Map<String,Object> map){
        EntityWrapper<BesUser> en = new EntityWrapper<>();
        //可添加搜索查询条件
        int a =besUserService.batchUpdatestatus(map);
        if(a>0){
            return new R("审核成功",R.SUCCESS);
        }else{
            return new R("审核失败",R.FAIL);
        }
    }

    public static Map<String, Object> pageAndLimit(int page, int limit) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", page);
        params.put("limit", limit);
        return params;
    }

}
