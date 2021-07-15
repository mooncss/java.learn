package com.example.mall.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.example.mall.common.CommonResult;
import com.example.mall.dto.StDeliverVO;
import com.example.mall.dto.StStageDeliverRelaVo;
import com.example.mall.emun.OrderStatus;
import com.example.mall.model.*;
import com.example.mall.service.SmsStageService;
import com.example.mall.service.StDeliverService;
import com.example.mall.service.StLogsticIcoService;
import com.example.mall.service.StStageDeliverRelaService;
import com.zhihui.uj.management.BaseController.BaseController;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;

/**
 *  快递员控制器
 */

@RestController
@RequestMapping("/stDeliver")
public class AppStDeliverController extends BaseController {


    @Autowired
    SmsStageService smsStageService;

    @Autowired
    StDeliverService stDeliverService;

    @Autowired
    StStageDeliverRelaService stStageDeliverRelaService;

    @Autowired
    StLogsticIcoService stLogsticIcoService;

    /**
     * 查询合作快递员
     * @return
     */

//    @ApiOperation("驿站管理员查看合作快递员")
//    @RequestMapping(value = "/getRelaDeliver", method = RequestMethod.GET)
//    @ResponseBody
//    public CommonResult getRelaDeliver(@RequestParam Integer page,@RequestParam Integer limit){
//
//        String mobile = getUserMobile();
//        if(StringUtils.isBlank(mobile)){
//            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
//        }
//        EntityWrapper ew = new EntityWrapper(); //驿站管理员
//        ew.eq("stage_phone",mobile);
//        SmsStage stage = smsStageService.selectOne(ew);
//        if(stage == null){
//            return CommonResult.failed("此账号非驿站管理员");
//        }
//        Map<String,Object> map = new HashMap<>();
//        map.put("page",(page-1)* limit);
//        map.put("limit",limit);
//        map.put("stageId",stage.getId());
//        List<StStageDeliverRelaVo> list = stStageDeliverRelaService.selectRelaByStageId(map);
//        int count = stStageDeliverRelaService.selectRelaByStageIdCount(map);
//        Page<StStageDeliverRelaVo> pageres = new Page<>();
//        pageres.setTotal(count);
//        pageres.setRecords(list);
//        if(list !=null && list.size() > 0){
//            return CommonResult.success(pageres);
//        }
//        else{
//            return CommonResult.success(new ArrayList<StStageDeliverRela>());
//        }
//    }


    @ApiOperation("添加快递员")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult add(StDeliver stDeliver,String price){

        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        EntityWrapper ew = new EntityWrapper(); //驿站管理员
        ew.eq("stage_phone",mobile);
        SmsStage stage = smsStageService.selectOne(ew);
        if(stage == null){
            return CommonResult.failed("此账号非驿站管理员");
        }

        if(stDeliver == null || stDeliver.getDPhone() == null){
            return CommonResult.failed("请输入快递员手机号");
        }
        StDeliver stex = stDeliverService.selectOne(
                new EntityWrapper<StDeliver>().eq("d_phone",stDeliver.getDPhone()));
        if(stex != null){
            return CommonResult.failed("快递员已经存在");
        }
        String reId = UUID.randomUUID().toString().replaceAll("-","");
        stDeliver.setCreate_time(new Date());
        stDeliver.setDId(reId);
        stDeliverService.insert(stDeliver);

        if(!isNumeric(price)){
            return CommonResult.failed("价格必须是数字");
        }
        //协议价格
        BigDecimal pr = new BigDecimal(price == null?"0":price);
        StStageDeliverRela rela = new StStageDeliverRela();
        rela.setId(UUID.randomUUID().toString().replaceAll("_",""));
        rela.setDeliverId(reId);
        rela.setPrice(pr);
        rela.setStageId(stage.getId()); //设置驿站ID
        rela.setState("1");//达成合作
        rela.setCreateTime(new Date());
        stStageDeliverRelaService.insert(rela);

        return CommonResult.success("保存成功");
    }

    /**
     * 添加驿站和快递员的合作关系
     * @param stDeliver
     * @return
     */
    @ApiOperation("添加快递员和驿站的合作关系")
    @RequestMapping(value = "/addrela", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult addrela(StDeliver stDeliver,String price){

        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        EntityWrapper ew = new EntityWrapper(); //驿站管理员
        ew.eq("stage_phone", mobile);
        SmsStage stage = smsStageService.selectOne(ew);
        if (stage == null) {
            return CommonResult.failed("您非驿站管理员");}
        StStageDeliverRela re = stStageDeliverRelaService.selectOne(new EntityWrapper<StStageDeliverRela>()
                .eq("stage_id",stage)
                .eq("deliver_id",stDeliver.getDId()));
        if(re != null){
            return CommonResult.failed("已和此快递员进行合作");
        }
        if(!isNumeric(price)){
            return CommonResult.failed("请输入数字");
        }
        //协议价格
        BigDecimal pr = new BigDecimal(price == null?"0":price);
        StStageDeliverRela rela = new StStageDeliverRela();
        rela.setId(UUID.randomUUID().toString().replaceAll("_",""));
        rela.setDeliverId(stDeliver.getDId());
        rela.setPrice(pr);
        rela.setStageId(stage.getId()); //设置驿站ID
        rela.setState("1");//达成合作
        rela.setCreateTime(new Date());
        stStageDeliverRelaService.insert(rela);
        return CommonResult.success("价格:"+pr+"/件,保存成功.");
    }


    public static boolean isNumeric(String str) {
        String bigStr;
        try {
            bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;//异常 说明包含非数字。
        }
        return true;
    }

    /**
     * 更新驿站快递员的合作价格
     * 传ID和价格  这个ID是合作关系的 ID
     * @param rela
     * @return
     */
    @ApiOperation("更新快递员和驿站的合作关系")
    @RequestMapping(value = "/updaterela", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updaterela(StStageDeliverRela rela){
        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        EntityWrapper ew = new EntityWrapper(); //驿站管理员
        ew.eq("stage_phone", mobile);
        SmsStage stage = smsStageService.selectOne(ew);
        if (stage == null) {
            return CommonResult.failed("您非驿站管理员");}
        rela.setState("1"); //达成协议
        stStageDeliverRelaService.updateById(rela);
        return CommonResult.success("更新成功");
    }

    /**
     * 可以传 手机号的某几位或者是快递员部分名称
     * @param keyword
     * @return
     */
    @ApiOperation("获取所有的快递员")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult list(String keyword){

        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        EntityWrapper ew1 = new EntityWrapper(); //驿站管理员
        ew1.eq("stage_phone", mobile);
        SmsStage stage = smsStageService.selectOne(ew1);
        if (stage == null) {
            return CommonResult.failed("您非驿站管理员");}
//        EntityWrapper ew = new EntityWrapper();
//        if(!StringUtils.isBlank(keyword)){
//            ew.like("d_name",keyword).or().like("d_phone",keyword);
//        }
        Map<String,Object> map = new HashMap<>();
        map.put("dName",keyword);
        map.put("stageId",stage.getId());
        List<StDeliverVO> stex = stDeliverService.selectallDelivers(map);
        if(stex == null || stex.size() == 0){
            return CommonResult.failed("暂无数据");
        }
        return CommonResult.success(stex);
    }


    @ApiOperation("获取所有的快递员")
    @RequestMapping(value = "/shiplist", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult shiplist(String keyword){
        EntityWrapper ew = new EntityWrapper();
//        if(!StringUtils.isBlank(keyword)){
//            ew.like("d_name",keyword).or().like("d_phone",keyword);
//        }
        List<StLogsticIco> stex = stLogsticIcoService.selectList(ew);
        return CommonResult.success(stex);
    }

}
