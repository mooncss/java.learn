package com.example.mall.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.example.mall.common.CommonResult;
import com.example.mall.dao.OmsOrderOperateHistoryDao;
import com.example.mall.domain.ConfirmOrderResult;
import com.example.mall.domain.OrderParam;
import com.example.mall.dto.*;
import com.example.mall.emun.OrderStatus;
import com.example.mall.mapper.OmsOrderMapper;
import com.example.mall.mapper.OmsOrderOperateHistoryMapper;
import com.example.mall.model.*;
import com.example.mall.service.*;
import com.zhihui.uj.management.BaseController.BaseController;
import com.zhihui.uj.management.config.LoggingProcessFilter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.SimpleFormatter;


@Controller
@Api("app订单处理")
@RequestMapping("/app/omsorder")
public class AppOmsOrderController extends BaseController {

    @Autowired
    private OmsPortalOrderService portalOrderService;

    @Autowired
    private OmsOrderService orderService;

    @Autowired
    OmsOrderOperateHistoryDao omsOrderOperateHistoryDao;

    @Autowired
    SmsStageService smsStageService;

    @Autowired
    OmsOrderOperateHistoryMapper omsOrderOperateHistoryMapper;

    @Autowired
    BesUserService besUserService;

    //根据购物车信息确认订单
    @PostMapping("/firmorder")
    @ResponseBody
    public CommonResult comfirmorder(){
        //用户提交订单
        String user =getUserMobile(); //当前登录会员
        if(StringUtils.isBlank(user)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        //查询购物车的商品
        try{
            ConfirmOrderResult res = portalOrderService.generateConfirmOrder(user);
            if(res == null){
                return CommonResult.failed("请先选择商品");
            }
            return CommonResult.success(res);
        }
        catch(Exception e){
            logger.info("确认订单异常："+e.getMessage());
            return CommonResult.failed("出错了"+e.getMessage());
        }
    }

    @ApiOperation("生成订单-购物车订单")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody OrderParam orderParam) {
        String  mobile =getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        try {
            return  portalOrderService.generateOrder(orderParam,getUserMobile());
        }
        catch (Exception e){
            logger.error("订单生成异常："+ e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("APP查询订单")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult list(OmsOrderQueryParam orderQueryParam,
                  @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                  @RequestParam(value = "page", defaultValue = "1") Integer page) {
        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        Page<OmsOrderDetail> pageer =
                orderService.listApp(orderQueryParam, limit, page,mobile);
        return CommonResult.success(pageer);
    }

    @ApiOperation("app所有可供选择自提门店")
    @RequestMapping(value = "/getAllMention", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getMention(){
        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        List<BesUser> mentionlist = besUserService.selectList(new EntityWrapper<BesUser>()
                .eq("isautarky",0)
                .eq("status",1)
                .eq("checked",2));
        List<MentionDto> list = new ArrayList<>();
        for(BesUser bes:mentionlist){
            MentionDto dto = new MentionDto();
            dto.setAddress(bes.getProvince()+bes.getCity()+bes.getArea()+bes.getDetailaddr());
            dto.setBesName(bes.getShopname());
            dto.setBesId(bes.getBesId());
            list.add(dto);
        }
        return CommonResult.success(list);
    }

    //确认订单
    @ApiOperation("拼团商品，直接购买商品等 非购物车商品订单确认")
    @RequestMapping(value = "/omsOrderConfirm",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult otherOrderConfirm(@RequestBody OmsOrderCreatePrepare p) //商品ID  商品的活动ID
    {
        String  mobile =getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        if(p.getProductId() == null ){
            return CommonResult.failed("商品ID不能为空");
        }
        try {
            ConfirmOrderResult res = portalOrderService.generateProConfirmOrder(p);
            return  CommonResult.success(res);
        }
        catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("确认页面 改变商品数量")
    @RequestMapping(value = "/changeQuantity",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult changeQuantity(@RequestBody OmsOrderCreatePrepare p) //参数：商品ID  skuID
    {
        if(p.getProductId() == null){
            return CommonResult.failed("参数错误");
        }
        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        try {
            ConfirmOrderResult.CalcAmount str = portalOrderService.changeQuantity(p,mobile);
            return CommonResult.success(str);
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }

    //订单生成
    @ApiOperation("拼团商品，直接购买商品等 非购物车商品订单生成")
    @RequestMapping(value = "/omsOrderCreate",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult createOtherOrder(@RequestBody OmsOrderCreatePrepare p) //参数：商品ID  skuID
    {
        if(p.getProductId() == null){
            return CommonResult.failed("参数错误");
        }
        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        if(p.getMemberReceiveAddressId() == null){
            return CommonResult.failed(("请先选择收货地址"));
        }
        try{
            CommonResult com = portalOrderService.generateProOrder(p,mobile);
            return com;
        }
        catch(Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 支付接口
     * @param dto
     * @return
     */
    @ApiOperation("订单支付-付款-余额支付")
    @RequestMapping(value = "/payOrder",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult payOrder(@RequestBody PayBodyDto dto) //参数：商品ID  skuID
    {
        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        try{
            dto.setMobile(mobile);
            CommonResult com =  orderService.payOrder(dto);
            return com;
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return CommonResult.failed(e.getMessage());
        }
    }

    // 确认收货
    @ApiOperation("订单签收")
    @RequestMapping(value = "/orderReceive",method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public CommonResult orderReceive(@RequestParam Long orderID) //参数：商品ID  skuID
    {
        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        try{
            CommonResult com = orderService.orderReceive(orderID,mobile);
            return com;
        }
        catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }


    @ApiOperation("用户取消订单")
    @RequestMapping(value = "/cancleOrder",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult userCancleOrder(@RequestParam Long orderId) //参数：商品ID  skuID
    {
        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        try{
            CommonResult com = orderService.userCancleOrder(orderId);
            List<OmsOrderOperateHistory>  list = new ArrayList<>();
            OmsOrderOperateHistory oooh = new OmsOrderOperateHistory();
            oooh.setCreateTime(new Date());
            oooh.setNote("您已取消订单");
            oooh.setOperateMan(mobile);
            oooh.setOrderStatus(OrderStatus.ORDER_YGB.getIndex()); //取消订单
            oooh.setOrderId(orderId);
            list.add(oooh);
            omsOrderOperateHistoryDao.insertList(list);
            return com;
        }
        catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }


    @ApiOperation("根据订单号查询 订单详情 订单商品信息 和物流详情 在app点击订单查看详情时调用")
    @RequestMapping(value = "/getOrderById",method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getOrderByID(String orderSn){
        String mobile = super.getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        if(StringUtils.isBlank(orderSn)){
            return CommonResult.failed("订单编号不能为空");
        }
        EntityWrapper ew = new EntityWrapper();
        ew.eq("stage_phone",mobile);
        SmsStage stage = smsStageService.selectOne(ew);
        //U家的订单
//        if(orderSn.startsWith("UJ20")){
            OmsOrderQueryParam query =  new OmsOrderQueryParam();
            query.setOrderSn(orderSn);
            Page<OmsOrder> orderpage = orderService.list(query,3,1,null);
            List<OmsOrder> orders = orderpage.getRecords();

            Map<String,Object> map =new HashMap<>();
            //根据订单编码查询订单
            if(orders!= null && orders.size() > 0){
                String member_id = orders.get(0).getMemberId();
                int flag = -1;
                if(member_id.equals(mobile)){
                    flag = 0;
                }
                else{
                    if(stage == null){
                        //根据订单号查询 订单是否是当前扫码人的订单
                        return CommonResult.failed("您非驿站管理员");
                    }
                }
                Integer orderStatus = orders.get(0).getStatus();
                if(orderStatus == null ){
                    return CommonResult.failed("订单状态错误");
                }
                if(orderStatus == 1){
                    return CommonResult.failed("此订单还未发货");
                }
                if(orderStatus == 3){
                    return CommonResult.failed("此订单已完成");
                }
                if(orderStatus == 4){
                    return CommonResult.failed("无效订单");
                }
                OmsOrderDetail detail = orderService.detail(orders.get(0).getId());
                detail.setItemcount(detail.getOrderItemList().size());
    //            List<OmsOrderOperateHistory> hislist = omsOrderOperateHistoryMapper.selectHisByOrderId(detail.getId());
                //根据订单编号查询这个订单的入库记录 是否存在  如果存在入库记录，则只能出库
                boolean in = false;
                boolean out = false;
                if(stage != null){
                    EntityWrapper en = new EntityWrapper();
                    en.eq("order_sn",orderSn);
                    en.eq("stage_id",stage.getId());
                    List<OmsOrderStageRela> relalist = omsOrderStageRelaService.selectList(en);
                    if(relalist != null && relalist.size() > 0){
                        for(OmsOrderStageRela o:relalist){
                            if(o.getOType() == 0){
                                in = true;
                            }else {
                                out = true;
                            }
                        }
                    }
                }
                if(flag == -1){
                    if(in && out){
                        flag = 3;
                    }
                    else if(in){
                        flag = 2;
                    }
                    else{ //进出记录都没有
                        flag = 1;
                    }
                }
                map.put("flag",flag); // 0 自己的订单 可确认收货    1可以执行入库  2 已入库，可执行出库  3 进出记录都有 不可执行操作  4 自己的订单已经完成
                map.put("detail",detail);
                map.put("isthird",0); //U平台订单
                return CommonResult.success(map);
            }
//        else{ // 去查三方单号
//            AppThirdPartyOrderController app = new AppThirdPartyOrderController();
//            CommonResult com =  app.getThirdOrdetinfo(orderSn,mobile);
//            return CommonResult.success(com);
//        }
        return CommonResult.failed("订单不存在");
    }

    @ApiOperation("订单物流信息")
    @RequestMapping(value = "/orderOperHistory",method = RequestMethod.GET)
    @ResponseBody
    public CommonResult orderOperHistory(@RequestParam Long orderId) //参数：订单id
    {
        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        OmsOrderDetail detail = orderService.detail(orderId);
        return CommonResult.success(detail);
    }

    @Autowired
    private OmsOrderStageRelaService omsOrderStageRelaService;

    @Autowired
    private OmsOrderMapper omsOrderMapper;

    @ApiOperation("订单入驿站库")
    @RequestMapping(value = "/orderArrivestage",method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public CommonResult orderDeliverInfo(String orderSn){
        String mobile = super.getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());}
        if(StringUtils.isBlank(orderSn)){
            return CommonResult.failed("订单编号不能为空"); }
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andOrderSnEqualTo(orderSn);
        List<OmsOrder> orders = omsOrderMapper.selectByExample(example);
        if(orders ==null || orders.size()  <= 0){
            return CommonResult.failed("未查到此订单");
        }
        if(orders.get(0).getStageStatus() != null){ //入站
            return CommonResult.failed("订单不能重复入库");
        }

        EntityWrapper ew = new EntityWrapper(); //驿站管理员
        ew.eq("stage_phone",mobile);
        SmsStage stage = smsStageService.selectOne(ew);
        if(stage == null){
            //订单是不是收货人的
            return CommonResult.failed("此账号非驿站管理员");
        }
        //查询此订单是否已经入库
        EntityWrapper en = new EntityWrapper();
        en.eq("order_sn",orderSn);
//        en.eq("stage_id",stage.getId());
        en.eq("o_type",0); //订单存在入库记录的
        OmsOrderStageRela relaal = omsOrderStageRelaService.selectOne(en);
        if(relaal  == null){ //不存在入库记录
            // 执行入库
            OmsOrderStageRela rela = new OmsOrderStageRela();
            rela.setCreateTime(new Date());
            rela.setOrderSn(orderSn);
            rela.setOType(0); //入库
            rela.setStageId(stage.getId());
            rela.setOrderId(orders.get(0).getId());
            rela.setIsLeave(0); //未出库
            omsOrderStageRelaService.insert(rela);
            //执行操作记录
            OmsOrderOperateHistory oooh = new OmsOrderOperateHistory();
            oooh.setCreateTime(new Date());
            oooh.setNote("到达驿站:"+stage.getStageName());
            oooh.setOperateMan(mobile);
            oooh.setOrderStatus(OrderStatus.ORDER_DDYZ.getIndex());
            oooh.setOrderId(orders.get(0).getId());
            omsOrderOperateHistoryMapper.insert(oooh);

            //更新订单的驿站状态为 入站
            OmsOrder orderToupdate = orders.get(0);
            orderToupdate.setStageStatus(5);
            omsOrderMapper.updateByPrimaryKey(orderToupdate);

            return CommonResult.success("入库成功");
        }
        else{
            return CommonResult.failed("不能重复入库");
        }
    }

    @ApiOperation("订单驿站出库")
    @RequestMapping(value = "/orderLeavestage",method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public CommonResult orderLeavestage(String orderSn){
        String mobile = super.getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());}
        if(StringUtils.isBlank(orderSn)){
            return CommonResult.failed("订单编号不能为空"); }
        EntityWrapper ew = new EntityWrapper(); //驿站管理员
        ew.eq("stage_phone",mobile);
        SmsStage stage = smsStageService.selectOne(ew);
        if(stage == null){
            return CommonResult.failed("您不是驿站管理员");
        }
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andOrderSnEqualTo(orderSn);
        List<OmsOrder> orders =omsOrderMapper.selectByExample(example);
        if(orders ==null || orders.size()  <= 0){
            return CommonResult.failed("未查到此订单");
        }
        //查询此订单是否已经入库
        EntityWrapper en = new EntityWrapper();
        en.eq("order_sn",orderSn);
        en.eq("stage_id",stage.getId());
        en.eq("o_type",0);
        OmsOrderStageRela relaal = omsOrderStageRelaService.selectOne(en);
        if(relaal != null){  //存在入库记录
            //更新出库状态
            relaal.setIsLeave(1); //出库状态
            omsOrderStageRelaService.updateById(relaal);

            //执行出库
            OmsOrderStageRela rela = new OmsOrderStageRela();
            rela.setCreateTime(new Date());
            rela.setOrderSn(orderSn);
            rela.setOType(1); // 出库
            rela.setIsLeave(1);
            rela.setStageId(stage.getId());
            rela.setOrderId(orders.get(0).getId());
            rela.setRemark("订单离开驿站");
            omsOrderStageRelaService.insert(rela);

            //执行操作记录
            OmsOrderOperateHistory oooh = new OmsOrderOperateHistory();
            oooh.setCreateTime(new Date());
            oooh.setNote("离开驿站:"+stage.getStageName());
            oooh.setOperateMan(mobile);
            oooh.setOrderStatus(OrderStatus.ORDER_YZCK.getIndex());
            oooh.setOrderId(orders.get(0).getId());
            omsOrderOperateHistoryMapper.insert(oooh);

            //更新订单的驿站状态为 入站
            OmsOrder orderToupdate = orders.get(0);
            orderToupdate.setStageStatus(6);  //驿站出库
            omsOrderMapper.updateByPrimaryKey(orderToupdate);

            return CommonResult.success("出库成功");
        }
        else{
            return CommonResult.failed("未找到入库记录,不能出库");
        }
    }

    //入库记录
    @ApiOperation("订单驿站入库记录")
    @RequestMapping(value = "/arriveNote",method = RequestMethod.GET)
    @ResponseBody
    @Transactional
    public CommonResult arriveNote(@RequestParam Integer page,@RequestParam Integer limit) {
        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());}

        EntityWrapper ew = new EntityWrapper(); //驿站管理员
        ew.eq("stage_phone",mobile);
        SmsStage stage = smsStageService.selectOne(ew);
        if(stage == null){
            return CommonResult.failed("您不是驿站管理员");
        }
        Map<String,Object> map = new HashMap<>();
        map.put("page",(page-1)*limit);
        map.put("limit",limit);
        map.put("stagePhone",mobile);
        List<OmsOrderStageRelaNode> list  = omsOrderStageRelaService.getArList(map);
        Page<OmsOrderStageRelaNode> pageres = new Page<>();
        pageres.setRecords(list);
        int count =omsOrderStageRelaService.getArCount(map);
        pageres.setTotal(count);
        return CommonResult.success(pageres);
    }

    @ApiOperation("出库记录")
    @RequestMapping(value = "/leaveNote",method = RequestMethod.GET)
    @ResponseBody
    @Transactional
    public CommonResult leaveNote(@RequestParam Integer page,@RequestParam Integer limit) {
        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());}

        EntityWrapper ew = new EntityWrapper(); //驿站管理员
        ew.eq("stage_phone",mobile);
        SmsStage stage = smsStageService.selectOne(ew);
        if(stage == null){
            return CommonResult.failed("您不是驿站管理员");
        }
        //查询入站记录
        Map<String,Object> map = new HashMap<>();
        map.put("page",(page-1)*limit);
        map.put("limit",limit);
        map.put("stagePhone",mobile);
        List<OmsOrderStageRelaNode> list  = omsOrderStageRelaService.getLeavList(map);
        Page<OmsOrderStageRelaNode> pageres = new Page<>();
        pageres.setRecords(list);
        int count =omsOrderStageRelaService.getLeaveCount(map);
        pageres.setTotal(count);
        return CommonResult.success(pageres);
    }

    //定时器 用来关闭24小时未支付的订单 9分钟执行一次
    @Scheduled(cron = "0 */9 * * * ?")
    @Async
    public void autocloseOrder() {
        String theadId = LoggingProcessFilter.getSessionId();
        LoggingProcessFilter.putSessionId(theadId);
        //查询所有的待支付订单 如果下单时间超过24小时 执行为关闭订单、
        int a= orderService.orderCancle();
        logger.info("已自动关闭过期订单"+a+"条");
    }


    //自动确认收货
    @Scheduled(cron = "0 */10 * * * ?")
    @Async
    public void autoReceive(){

        String theadId = LoggingProcessFilter.getSessionId();
        LoggingProcessFilter.putSessionId(theadId);

        logger.info("执行系统确认收货");
        //查询可以自动收货的订单  发货3天后 自动确认收货
        int a  = orderService.autoreceiveorder();
        logger.info("自动确认收货 数量" + a);
    }


    @ApiOperation("自提时间列表 获取")
    @RequestMapping(value = "/mentionTimeList",method = RequestMethod.GET)
    @ResponseBody
    public CommonResult mentionTimeList() //参数：商品ID  skuID
    {
//        String mobile = getUserMobile();
//        if(StringUtils.isBlank(mobile)){
//            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
//        }
        Map<String,List<String>> map = new HashMap<>();
        LocalDate td= LocalDate.now();
        List<String> strList= new ArrayList<>();
        for(int a = 8; a< 20;a++){
            strList.add(a+":00点-"+(a+1)+":00点");
        }
        map.put("今天("+td.format(DateTimeFormatter.ofPattern("MM-dd"))+")",strList);
        map.put("明天("+td.minusDays(-1).format(DateTimeFormatter.ofPattern("MM-dd"))+")",strList);
        map.put("后天("+td.minusDays(-2).format(DateTimeFormatter.ofPattern("MM-dd"))+")",strList);
        try {
            return CommonResult.success(map);
        }catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }

}
