package com.example.mall.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.example.mall.dto.SmsGroupBuyBesPronameDto;
import com.example.mall.model.SmsGroupBuy;
import com.example.mall.model.SmsGroupBuyProduct;
import com.example.mall.service.SmsGroupBuyProductService;
import com.example.mall.service.SmsGroupBuyService;
import com.zhihui.uj.management.BaseController.BaseController;
import com.zhihui.uj.management.utils.Query;
import com.zhihui.uj.management.utils.R;
import com.zhihui.uj.management.utils.ShiroUtils;
import io.swagger.annotations.Api;
import io.swagger.models.auth.In;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 拼团活动
*/
@Controller
@RequestMapping("/groupbugpromotion")
@Api("管理员拼团活动管理")
@RequiresAuthentication
public class GroupBuyPromotionController  extends BaseController {
    //查询所有拼团活动  分页  page,limit
    @Autowired
    SmsGroupBuyService smsGroupBuyService;

    @Autowired
    SmsGroupBuyProductService smsGroupBuyProductService;

    @GetMapping("/page")
    @ResponseBody
    public R page(@RequestParam int page, @RequestParam int limit, String name,
                  String shopname, Integer checkres,Integer ischecked){
        //查询所有活动
//        EntityWrapper en = new EntityWrapper();
//        if(!StringUtils.isBlank((String)map.get("proname"))){
//            en.eq("proname",map.get("proname"));
//        }
//        if(!StringUtils.isBlank((String)map.get("besId"))){
//            en.eq("bes_id",map.get("besId"));
//        }
        Map<String,Object> map  = new HashMap<>();
        map.put("page",(page-1)* limit);
        map.put("limit", limit);
        if(StringUtils.isBlank(name)){
            map.put("name", name);
        }
        if(StringUtils.isBlank(shopname)){
            map.put("shopname", shopname);
        }
        if(checkres != null){
            map.put("checkres", checkres);
        }
        if(ischecked != null){
            map.put("ischecked", ischecked);
        }
        List<SmsGroupBuyBesPronameDto> list = smsGroupBuyProductService.searhAllGroupPro(map);
        Page<SmsGroupBuyBesPronameDto> pr = new Page<>();
        pr.setRecords(list);
        int count = smsGroupBuyProductService.selectCount(new EntityWrapper<>());
        pr.setTotal(count);
        return new R(pr);
    }

    //活动审核
//    @PostMapping("/check")
//    @ResponseBody
//    public R update(@RequestBody Map<String,Object> params){
//        //查询所有活动
//        params.put("checkuser",getUserId());
//        params.put("checktime",new Date());
//        smsGroupBuyService.batchUpdate(params);
//        return new R("操作成功",R.SUCCESS);
//    }

    //商品审核  支持批量
    @PostMapping("/checkGroupPros")
    @ResponseBody
    public R checkGroupPros(@RequestBody Map<String,Object> params){
        //查询所有活动
        params.put("checkuser",getUserId());
        params.put("checktime",new Date());
        smsGroupBuyProductService.batchUpdate(params);
        return new R("操作成功",R.SUCCESS);
    }


    //审核失败  非批量审核
    @PostMapping("/checkGroupProFail")
    @ResponseBody
    public R checkGroupProFail(@RequestParam String id,String remark){
        //查询所有活动
        SmsGroupBuyProduct g= new SmsGroupBuyProduct();
        g.setId(id);
        g.setCheckres(1); //未通过
        g.setCheckuser(getUserMobile()); //审核人
        g.setChecktime(new Date());
        g.setIschecked(1); //已审核
        g.setRemark(remark);
        boolean b =  smsGroupBuyProductService.updateById(g);
        if(b){
           return new R("操作成功",R.SUCCESS);
        }
        return new R("操作成功",R.FAIL);
    }

    //admin暂停活动
//    @PostMapping("/status/admin")
//    @ResponseBody
//    public R statusAdmin(@RequestBody Map<String,Object> params){
//        //查询所有活动
//        params.put("checkuser",getUserId());
//        params.put("checktime",new Date());
////        EntityWrapper en = new EntityWrapper();
//        smsGroupBuyService.batchUpdate(params);
//        // 更新商品状态
////      boolean a = smsGroupBuyProductService.updateBatchById();
//        return new R("操作成功",R.SUCCESS);
//    }

}
