package com.example.mall.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.example.mall.common.CommonResult;
import com.example.mall.dao.SmsRandomUserDao;
import com.example.mall.mapper.OmsOrderMapper;
import com.example.mall.model.*;
import com.example.mall.service.*;
import com.zhihui.uj.management.BaseController.BaseController;
import com.zhihui.uj.management.common.service.IUjOwnerService;
import com.zhihui.uj.management.config.LoggingProcessFilter;
import com.zhihui.uj.management.utils.Query;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Api("app用户拼团活动入口")
@Controller
@RequestMapping("/app/assemble")
public class AppGroupBuyAssembleController extends BaseController
{
    @Autowired
    SmsGroupBuyService smsGroupBuyService;
    @Autowired
    SmsGroupBuyProductService smsGroupBuyProductService;
    @Autowired
    SmsGroupBuyAssembleService smsGroupBuyAssembleService;
    @Autowired
    SmsGroupBuyAssembleUserService smsGroupBuyAssembleUserService;
    @Autowired
    IUjOwnerService iUjOwnerService;

    @Autowired
    SmsRandomUserDao smsRandomUserDao;

    @Autowired
    PmsProductService pmsProductService;

    @Autowired
    PmsSkuStockService pmsSkuStockService;

    @Autowired
    OmsOrderMapper omsOrderMapper;


    //查询某商品的商品的成团列表
    @GetMapping("/page")
    @ResponseBody
    public CommonResult page(@RequestParam(required = true,defaultValue = "1") Integer page,
                             @RequestParam (required = true,defaultValue = "20") Integer limit,
                             @RequestParam Long productid){
        EntityWrapper en = new EntityWrapper();
        en.eq("product_id",productid);
        en.eq("is_full",1);         //未满员的
        en.ge("end_time",new Date());       //在结束时间以内的
        Map map = new HashMap<String,Object>();
        map.put("page",page);
        map.put("limit",limit);
        //查询成团列表
        Page<SmsGroupBuyAssemble> pagelist = smsGroupBuyAssembleService.selectPage(new Query<>(map),en);
//        if(pagelist == null || pagelist.getRecords().size() == 0 ){
//            pagelist
//        }
        return CommonResult.success(pagelist);
    }

    @GetMapping("/getGroupInfoById")
    @ResponseBody
    public CommonResult getGroupInfoById(String groupId){
//        EntityWrapper en = new EntityWrapper();
//        en.eq("group_id",groupId);
//        en.eq("is_full",1);
//        en.ge("end_time",new Date());
        //查询成团列表
        SmsGroupBuyAssemble group = smsGroupBuyAssembleService.selectById(groupId);
        EntityWrapper en1 = new EntityWrapper();
        en1.eq("group_id",groupId);
        List<SmsGroupBuyAssembleUser> userlist = smsGroupBuyAssembleUserService.selectList(en1);
        PmsProduct p = null;
        if(group != null){
            p = pmsProductService.selectById(group.getProductId());
        }

        EntityWrapper enp = new EntityWrapper();
        enp.eq("product_id",group.getProductId());
        SmsGroupBuyProduct pms = smsGroupBuyProductService.selectOne(enp);
//        Date endtime  =  group.getCreateTime();
//        Long timestemp = endtime.getTime() + 24*60*60*1000;
        Long timestemp = group.getEndTime().getTime();
        Map<String,Object> map = new HashMap<>();
        map.put("timestemp",timestemp);
        map.put("groupinfo",group);
        map.put("userlist",userlist);
        map.put("proinfo",p);
        map.put("promotio",pms);
        return CommonResult.success(map);
    }



    //查询某个团的成团信息
    @GetMapping("/groupinfo")
    @ResponseBody
    public CommonResult groupinfo(@RequestParam String groupid){
        EntityWrapper en = new EntityWrapper();
        en.eq("group_id",groupid);
        List<SmsGroupBuyAssembleUser> ginfo = smsGroupBuyAssembleUserService.selectList(en);
        SmsGroupBuyAssemble groupbuy = smsGroupBuyAssembleService.selectOne(
                new EntityWrapper<SmsGroupBuyAssemble>().eq("group_id",groupid));
        Map<String,Object> map =new HashMap<>();
        map.put("userlist",ginfo);
        map.put("groupbuy",groupbuy);
        if(CollectionUtils.isEmpty(map)){
            return CommonResult.success(ginfo);
        }
        return CommonResult.failed();
    }

    // 自动完成拼团  10分钟 执行一次
    @Scheduled(cron = "0 */10 * * * ?")
    @Transactional
    @Async
    public void autoCompleteGroupBuy(){

        String theadId = LoggingProcessFilter.getSessionId();
        LoggingProcessFilter.putSessionId(theadId);
        List<SmsRandomUser>  list = smsRandomUserDao.selectList(new EntityWrapper<>());
        //拼团活动结束前1小时系统自动拼团
//        date.setTime(System.currentTimeMillis() - 60*60*1000);
//        Date date1 = new Date();
//        Long times = date1.getTime() - 23*60*60*1000;
//        date1.setTime(times);
//        ent.ge("end_time",date).or().ge("create_time",date1);
//        ent.eq("is_full",1);
        Map<String,Object> map = new HashMap<>();
        map.put("dateEnd",new Date());
//        map.put("dateCreate",date1);
        List<SmsGroupBuyAssemble> prolist = smsGroupBuyAssembleService.selectAutoComList(map);
        if(prolist!=null && prolist.size() > 0){
            for(SmsGroupBuyAssemble s : prolist){
                int a = (s.getGroupNeed() - s.getNowPerson());
                Set<Integer> set = getRandomint(a,list.size());
                Iterator<Integer> it = set.iterator();
                List<SmsGroupBuyAssembleUser> userList = new ArrayList<>();
                while (it.hasNext()){
                    SmsRandomUser randomUser = list.get(it.next());
                    SmsGroupBuyAssembleUser user= new SmsGroupBuyAssembleUser();
                    user.setJoinTime(new Date());
                    user.setUserico(randomUser.getUserico());
                    user.setUsername(randomUser.getUsername());
                    user.setGroupId(s.getGroupId());
                    user.setType(1);
                    userList.add(user);
                }
                if(userList != null && userList.size() > 0){
                    smsGroupBuyAssembleUserService.insertBatch(userList);
                }
                //更新拼团完成时间和人数
                s.setOverTime(new Date());
                s.setNowPerson(s.getGroupNeed());
                s.setIsFull(0); // 完成
                smsGroupBuyAssembleService.updateById(s);
                System.out.println("自动拼单完成");
                //订单拼团成功
                Map<String,Object> mapp = new HashMap<>();
                mapp.put("groupId",s.getGroupId());
                mapp.put("gState",8); //拼团完成
                omsOrderMapper.updateGroupStateBatch(mapp);
            }
        }
        //在用户发起拼单活动最迟10分钟后,帮用户完成拼团
        //拼团完成后推送消息给用户
    }
    //生成指定数值以内的几个不重复的随机数
    public static Set<Integer> getRandomint(int total,int limit){
        Set<Integer> set = new HashSet<>();
        if(limit <= 0){
            return set;
        }
        for(int i=0;i<total*10;i++){
            int tar = new Random().nextInt(limit);
            set.add(tar);
            if(set.size() >= total){
                break;
            }
        }
        return set;
    }
}
