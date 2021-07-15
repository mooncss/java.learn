package com.example.mall.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.example.mall.common.CommonResult;
import com.example.mall.model.SmsStage;
import com.example.mall.model.StageDeliverQueryVo;
import com.example.mall.service.SmsStageService;
import com.zhihui.uj.management.BaseController.BaseController;
import com.zhihui.uj.management.common.entity.UjOwner;
import com.zhihui.uj.management.utils.IdUtils;
import com.zhihui.uj.management.utils.Query;
import com.zhihui.uj.management.utils.R;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/stagemanage")
@Api("驿站管理")
@RequiresAuthentication
public class SmsStageController extends BaseController {

    @Autowired
    SmsStageService smsStageService;

    @ApiModelProperty("新增驿站")
    @PostMapping("/add")
    @ResponseBody
    public CommonResult add(@RequestBody SmsStage stage){
        if(stage != null){
            stage.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            stage.setStageCode(IdUtils.createID());
            stage.setCreateTime(new Date());
        }
        boolean a = smsStageService.insert(stage);
        if(a){
            return CommonResult.success("添加成功");
        }
        return CommonResult.failed("添加失败");
    }

    @ApiModelProperty("编辑驿站")
    @PostMapping("/edit")
    @ResponseBody
    public CommonResult edit(@RequestBody SmsStage stage){
        if(smsStageService.updateById(stage)){
            return CommonResult.success("编辑完成");
        }
        return CommonResult.failed("编辑失败");
    }

    @ApiModelProperty("驿站列表")
    @PostMapping("/list")
    @ResponseBody
    public CommonResult list(String name){
        EntityWrapper ew=  new EntityWrapper();
        if(!StringUtils.isBlank(name)){
            ew.like("stage_name",name);
        }
       List<SmsStage> list =smsStageService.selectList(ew);
        return CommonResult.success(list);
    }


    @ApiModelProperty("驿站列表")
    @PostMapping("/page")
    @ResponseBody
    public CommonResult edit(@RequestParam int page, @RequestParam int limit,String name){
        Map<String,Object> params = new HashMap<>();
        params.put("page",page);
        params.put("limit",limit);
        EntityWrapper ew=  new EntityWrapper();
        if(!StringUtils.isBlank(name)){
            ew.like("stage_name",name);
        }
        Page<SmsStage> pages =smsStageService.selectPage(new Query<>(params), ew);
        return CommonResult.success(pages);
    }

    @ApiModelProperty("根据ID查询驿站")
    @GetMapping("/selectByid")
    @ResponseBody
    public CommonResult edit(String id){
        return CommonResult.success(smsStageService.selectById(id));
    }

    @ApiModelProperty("删除驿站")
    @PostMapping("/delete")
    @ResponseBody
    public CommonResult delete(String id){
        return CommonResult.success(smsStageService.deleteById(id));
    }


    /**
     *  查询驿站的信息
     * @param id
     * @return
     */

    @ApiModelProperty("查询驿站的信息")
    @PostMapping("/info")
    @ResponseBody
    public CommonResult info(String id){
        SmsStage stage = smsStageService.selectById(id);
        return CommonResult.success(stage);
    }

	@GetMapping("/getDeliverListByStageId")
	@ResponseBody
	public R<List<StageDeliverQueryVo>> getDeliverListByStageId(String stageId) {
		if (StrUtil.isBlank(stageId)) {
			stageId= "error";
		}
		List<StageDeliverQueryVo> list = smsStageService.getDeliverListByStageId(stageId);
		return new R<>(list);
	}


}
