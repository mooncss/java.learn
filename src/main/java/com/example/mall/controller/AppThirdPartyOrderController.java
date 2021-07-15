package com.example.mall.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.example.mall.common.CONSTANT;
import com.example.mall.common.CommonResult;
import com.example.mall.dto.StStageDeliverRelaVo;
import com.example.mall.emun.OrderStatus;
import com.example.mall.mapper.OmsOrderMapper;
import com.example.mall.mapper.OmsThirdOrderMapper;
import com.example.mall.model.*;
import com.example.mall.service.*;
import com.zhihui.uj.management.BaseController.BaseController;
import com.zhihui.uj.management.charging.entity.PaymentRecord;
import com.zhihui.uj.management.charging.service.PaymentRecordService;
import com.zhihui.uj.management.push.huawei.util.CollectionUtils;
import com.zhihui.uj.management.utils.IdUtils;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 *  驿站入库
 *
 */
@Api("三方订单")
@RestController
@RequestMapping("/thirdOrderprise")
public class AppThirdPartyOrderController extends BaseController {

    @Autowired
    private OmsThirdOrderMapper omsThirdOrderMapper;
    @Autowired
    private OmsThirdOrderTraceService omsThirdOrderTraceService;

    @Autowired
    OmsThirdOrderService omsThirdOrderService;

    @Autowired
    SmsStageService smsStageService;

    @Autowired
    private OmsOrderStageRelaService omsOrderStageRelaService;

    @Autowired
    private OmsOrderMapper omsOrderMapper;

    @Autowired
    private StOrderService stOrderService;

    @Autowired
    PaymentRecordService paymentRecordService;

    private WxPayService wxService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    StLogsticIcoService stLogsticIcoService;

    @Autowired
    StStageDeliverRelaService stStageDeliverRelaService;

    @Autowired
    StDeliverService stDeliverService;


    @Autowired
    public AppThirdPartyOrderController(WxPayService wxService) {
        this.wxService = wxService;
    }

    @Value("${wxpay.pay.notifyUrl}")
    String wxpayCallBackUrl;//回调通知地址

    protected org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private String EBusinessID= "1604192";
    private String AppKey= "e8c2bc4b-f29a-4269-81c6-baa3a1ee904b";
    private String ReqURL = "http://api.kdniao.com/Ebusiness/EbusinessOrderHandle.aspx";
    /**
     * app端展示用户是否有快递待取出
     * */
    @ApiOperation("获取用户的待取件数量")
    @RequestMapping(value = "/getshipperNum", method = RequestMethod.POST)
    @Transactional
    public CommonResult getshipperNum(){
        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        List<OmsThirdOrder> list =
                omsThirdOrderService.selectList(new EntityWrapper<OmsThirdOrder>()
                        .eq("reciver_phone",mobile)
                        .eq("o_type",0)
                        .eq("is_leave",0));  //未取件
        Map<String,Object> map = new HashMap<>();
        map.put("size",list.size()); //件数
        map.put("list",list);        //快件详情
        return CommonResult.success(map);
    }


    @ApiOperation("查询用户是否是 驿站管理员")
    @RequestMapping(value = "/checkUser", method = RequestMethod.GET)
    public CommonResult checkUser(){
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
        return CommonResult.success("成功");
    }

//    @ApiOperation("获取取货码，10分钟失效")
//    @RequestMapping(value = "/getCalccode", method = RequestMethod.GET)
//    @Transactional
//    public CommonResult getQcode(){
//        String mobile = getUserMobile();
//        if(StringUtils.isBlank(mobile)){
//            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
//        }
//        //生成 取货码
//        String codec = codeKey();
//        stringRedisTemplate.opsForValue().set(codec,mobile,20, TimeUnit.MINUTES);
//        return CommonResult.success(codec);
//    }

    //快件管理
    @ApiOperation("快件管理")
    @RequestMapping(value = "/logsticManage", method = RequestMethod.GET)
    public CommonResult logsticManage(Integer page,Integer limit,Integer isLeave,String receivePhone)  {
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
        if(page == null || limit == null){
            page=1; limit = 15;
        }
        String stageid = stage.getId();
        Map<String,Object> map = new HashMap<>();
        map.put("page",(page-1)*limit);
        map.put("limit",limit);
        map.put("stageId",stageid);
        map.put("isLeave",isLeave);
        if(!StringUtils.isBlank(receivePhone)){
            map.put("receivePhone",receivePhone);
        }
        List<OmsThirdOrder> listOrder = omsThirdOrderService.selectOrderLog(map);
        int count = omsThirdOrderService.countOrderLog(map);
        Page<OmsThirdOrder> pageres =  new Page<>();
        pageres.setRecords(listOrder);
        pageres.setTotal(count);
        return  CommonResult.success(pageres);
    }

    @ApiOperation("删除入库单")
    @PostMapping(value = "/logsticDelete")
    public CommonResult logsticDelete(String orderId)  {
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
        if(StringUtils.isBlank(orderId)){
            return CommonResult.failed("入库单号不能为空");
        }
        StOrder st= stOrderService.selectById(orderId);
        if(st.getState() == 1 || st.getState().equals("1")){
            return CommonResult.failed("不能删除已支付的入库单");
        }
        stOrderService.deleteById(orderId);
        omsThirdOrderService.delete(new EntityWrapper<OmsThirdOrder>().eq("order_id",orderId));
        return  CommonResult.success("删除完成");
    }


    //创建取货码
    public String codeKey(){
        StringBuilder sbu  = new StringBuilder();
        String code = getVerify(16);  // 每个取货码20分钟有效期
        sbu.append("QH");
        sbu.append(code);
        sbu.append("U");
        String codec = sbu.toString();
        if(stringRedisTemplate.hasKey(codec)){
            logger.info("递归方法 codeKey() ,出现重复取货码KEY值");
            codeKey();
        }
        return sbu.toString();
    }

    //生成取货码
    public static String getVerify(int length){
        String code = "";
        String str = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASFGHJKLZXCVBNM";
        String[] strs = str.split("");
        for(int i = 0;i<length;i++){
            code += strs[(int)(Math.random() * strs.length)];
        }
        return code;
    }


    @ApiOperation("三方订单入库")
    @RequestMapping(value = "/arriveOrder", method = RequestMethod.POST)
    public CommonResult arriveNote(@RequestParam String orderSn,
                                   @RequestParam String receiverPhone,
                                   @RequestParam String receiverName)  {
        //根据订单号查询数据库  看存在不存在这个订单
        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        logger.info("三方订单开始入库" + orderSn);
        EntityWrapper ew = new EntityWrapper(); //驿站管理员
        ew.eq("stage_phone",mobile);
        SmsStage stage = smsStageService.selectOne(ew);
        if(stage == null){
            return CommonResult.failed("此账号非驿站管理员");
        }
        EntityWrapper enw = new EntityWrapper();
        enw.eq("logistic_code",orderSn); //快递编号
        List<OmsThirdOrder> orderList = omsThirdOrderMapper.selectList(enw);
        if(!CollectionUtils.isEmpty(orderList)){
            for(OmsThirdOrder o:orderList){
                if(o.getOType() == -1){
                    o.setOType(0);
                    o.setReciverName(receiverName);
                    o.setReciverPhone(receiverPhone);
                    omsThirdOrderMapper.updateById(o);
                    return CommonResult.success("入库成功");
                }
            }
        }
        return CommonResult.success("入库失败");
    }

    //驿站出库
    @ApiOperation("验证取货码")
    @RequestMapping(value = "/checkQcode", method = RequestMethod.POST)
    @Transactional
    public CommonResult checkQcode(@RequestParam String qCode) {
        String mobile =getUserMobile();
        String phone_red = stringRedisTemplate.opsForValue().get(qCode);
        if(phone_red == null){
            return CommonResult.failed("取货码已失效!");
        }
        EntityWrapper ew = new EntityWrapper(); //驿站管理员
        ew.eq("stage_phone", mobile);
        SmsStage stage = smsStageService.selectOne(ew);
        if (stage == null) {
            return CommonResult.failed("您非驿站管理员");
        }
        //TODO 取货出库 逻辑 2020-01-07 16:52:41
        //查询 个人所有的订单
        //收件人电话 取件人姓名 驿站名 入库时间   单号
        EntityWrapper en = new EntityWrapper();
        en.eq("stage_id",stage.getId());
        en.eq("o_type",0);  //入库记录
        en.eq("is_leave",0);  //查询还未出库的
        List<OmsOrderStageRela> relalist = omsOrderStageRelaService.selectList(en);
        List<String> list = new ArrayList<>();

        List<OmsOrderShowStageModel> showlist = new ArrayList<>();
        if(relalist!=null && relalist.size() > 0){
            for(OmsOrderStageRela o:relalist){
                list.add(o.getOrderSn());
            }
            showlist = omsOrderMapper.selDaiqh(list);
        }

        EntityWrapper enw = new EntityWrapper();
        enw.eq("is_leave",0); //未出库
        enw.eq("o_type",0);
        enw.eq("reciver_phone",phone_red);
        List<OmsThirdOrder> thirdList = omsThirdOrderMapper.selectList(enw);
        for(OmsThirdOrder o:thirdList){
            OmsOrderShowStageModel oms  =new OmsOrderShowStageModel();
            oms.setCreateTime(o.getCreateTime());
            oms.setOrderSn(o.getLogisticCode());
            oms.setLogticName(o.getShipperName());
            oms.setReceiveName(o.getReciverName());
            oms.setReceivePhone(o.getReciverPhone());
            oms.setIsThird(1);
            showlist.add(oms);
        }
        return CommonResult.success(showlist);
    }

    /**
     * 入库单查询
     *
     * @param page
     * @param limit
     * @return
     */
    @RequestMapping(value = "/logsticRecord", method = RequestMethod.GET)
    public CommonResult logsticRecord(@RequestParam Integer page,@RequestParam Integer limit,
                                      String dPhone){
        String mobile = getUserMobile();
        if (StringUtils.isBlank(mobile)) {
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        EntityWrapper ew = new EntityWrapper(); //驿站管理员
        ew.eq("stage_phone", mobile);
        SmsStage stage = smsStageService.selectOne(ew);
        if (stage == null) {
            return CommonResult.failed("此账号非驿站管理员");
        }
        Map<String,Object> map = new HashMap<>();
        map.put("page",(page-1)*limit);
        map.put("limit",limit);
        map.put("stageId",stage.getId());
        map.put("dPhone",dPhone);
        List<StOrderQueryVo> list  = stOrderService.selectLogisticRecord(map);
        int count = stOrderService.selectLogisticRecordCount(map);
        Page<StOrderQueryVo> pageres = new Page<>();
        pageres.setRecords(list);
        pageres.setTotal(count);
        return CommonResult.success(pageres);
    }

    /**
     * @param orderSn 快递单号
     * */
    @ApiOperation("快件出库")
    @RequestMapping(value = "/leaveOrder", method = RequestMethod.POST)
    @Transactional
    public CommonResult leaveOrder(@RequestParam String orderSn) {
        String mobile = getUserMobile();
        if (StringUtils.isBlank(mobile)) {
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
//        logger.info("订单开始出库" + orderSn);
        EntityWrapper ew = new EntityWrapper(); //驿站管理员
        ew.eq("stage_phone", mobile);
        SmsStage stage = smsStageService.selectOne(ew);
        if (stage == null) {
            return CommonResult.failed("此账号非驿站管理员");
        }
        EntityWrapper enw = new EntityWrapper();
        enw.eq("logistic_code", orderSn); //快递编号
        enw.eq("stage_id", stage.getId());
        List<OmsThirdOrder> orderList = omsThirdOrderMapper.selectList(enw);
        if (!CollectionUtils.isEmpty(orderList)) {
            OmsThirdOrder o = orderList.get(0);
            if (o.getOType() == 0) {
//                OmsThirdOrder omsThirdOrder =new OmsThirdOrder();
//                omsThirdOrder.setId(UUID.randomUUID().toString().replaceAll("-",""));
//                omsThirdOrder.setOType(1); //出库
//                omsThirdOrder.setLogisticCode(orderSn);
//                omsThirdOrder.setCreateTime(new Date());
//                omsThirdOrder.setShipperCode(o.getShipperCode());
//                omsThirdOrder.setShipperName(o.getShipperName());
//                omsThirdOrder.setState(o.getState());
//                omsThirdOrder.setStageId(stage.getId());
//                omsThirdOrder.setReciverPhone(o.getReciverPhone());
//                omsThirdOrder.setReciverName(o.getReciverName());
//                omsThirdOrder.setIsLeave(1); //已经取件
//                int res = omsThirdOrderMapper.insert(omsThirdOrder);
                o.setLeaveTime(new Date());
                //更新入库的记录  设置isleave 为 1表示取件
                o.setIsLeave(1);
                int a = omsThirdOrderMapper.updateById(o);
                if(a > 0){
                    return CommonResult.success("出库成功");
                }
                return CommonResult.failed("出错了,请稍后重试");
            }
        }
        else{
            return CommonResult.failed("未找到此快件的入库记录");
        }
        return CommonResult.failed("出库失败");
    }

    //重新发起支付


    /**
     *  查询驿站合作的快递员
     *  @param page
     *  @param limit
     *  @return
     */
    @ApiOperation("查询驿站合作的快递员")
    @GetMapping(value = "/getdeliversbystage")
    public CommonResult getdeliversbystage(Integer page,Integer limit) {
        String mobile = getUserMobile();
        if (StringUtils.isBlank(mobile)) {
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        EntityWrapper ew = new EntityWrapper(); //驿站管理员
        ew.eq("stage_phone",mobile);
        SmsStage stage = smsStageService.selectOne(ew);
        if(stage == null){
            return CommonResult.failed("此账号非驿站管理员");
        }
//        if(page == null || limit == null){
//            page = 1;
//            limit = 10;
//        }

//        map.put("page",(page-1)* limit);
//        map.put("limit",limit);
        Map<String,Object> map = new HashMap<>();
        map.put("stageId",stage.getId());
        List<StStageDeliverRelaVo> list = stStageDeliverRelaService.selectRelaByStageId(map);
//        int count = stStageDeliverRelaService.selectRelaByStageIdCount(map);
//        Page<StStageDeliverRelaVo> pageres = new Page<>();
//        pageres.setTotal(count);
//        pageres.setRecords(list);
        if(list !=null && list.size() > 0){
            return CommonResult.success(list);
        }
        else{
            return CommonResult.success(new ArrayList<StStageDeliverRela>());
        }
    }


    @ApiOperation("入库记录")
    @RequestMapping(value = "/orderlist", method = RequestMethod.GET)
    public CommonResult getThirdOrdetinfo(@RequestParam Integer page,@RequestParam Integer limit) {
        //查询 入库的订单记录
        Page<OmsThirdOrder> page1 =
                omsThirdOrderService.selectPage(new Page<>(page,limit),
                        new EntityWrapper<OmsThirdOrder>()
                                .eq("o_type",0)//0入库 1出库
                                .orderBy("create_time",false));
        return CommonResult.success(page1);
    }

    @ApiOperation("模糊查询电话 用户")
    @RequestMapping(value = "/phonenamelist", method = RequestMethod.GET)
    public CommonResult phonelist(@RequestParam String phone) {
        String mobile = getUserMobile();
        if (StringUtils.isBlank(mobile)) {
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        if(StringUtils.isBlank(phone)){
            return CommonResult.success(null);
        }
        List<PhoneAndNameModel> list = omsOrderMapper.pnlist(phone);
        return CommonResult.success(list);
    }

    /**
     * @param orderId
     * @return
     */

    @ApiOperation("根据单号查询 入库单明细")
    @RequestMapping(value = "/getShipperOrderDetail", method = RequestMethod.GET)
    public CommonResult getShipperOrderDetail(@RequestParam String orderId) {
        String mobile = getUserMobile();
        if (StringUtils.isBlank(mobile)) {
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        if(StringUtils.isBlank(orderId)){
            return CommonResult.failed("入库单明细");
        }
        List<OmsThirdOrder> list = omsThirdOrderService.selectList(
                new EntityWrapper<OmsThirdOrder>().eq("order_id",orderId));
        return CommonResult.success(list);
    }

    /**
     * @param orderSn 快递单号
     * */

    @ApiOperation("获取三方订单信息")
    @RequestMapping(value = "/getThirdOrdetinfo", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getThirdOrdetinfo(String orderSn) throws Exception  {
        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        logger.info("开始查询订单,单号:" + orderSn);
        EntityWrapper ew = new EntityWrapper(); //驿站管理员
        ew.eq("stage_phone",mobile);
        SmsStage stage = smsStageService.selectOne(ew);
        if(stage == null){
            return CommonResult.failed("此账号非驿站管理员");
        }
        Map<String,Object> map = new HashMap<>();
        EntityWrapper enw = new EntityWrapper();
        enw.eq("logistic_code",orderSn); //快递编号
        List<OmsThirdOrder> orderList = omsThirdOrderMapper.selectList(enw);
        int  flag0 = -1;
        map.put("omsthirdOrder",orderList);
        map.put("isthird",1);
        if(!CollectionUtils.isEmpty(orderList)){
            EntityWrapper enw1 = new EntityWrapper();
            enw1.eq("logistic_code",orderSn); //快递编号
            enw1.orderBy("accept_time",false);
            List<OmsThirdOrderTrace>  tracelist = omsThirdOrderTraceService.selectList(enw1);
            map.put("tracelist",tracelist);
            for(OmsThirdOrder o:orderList){
                if(o.getOType() == null){
                    continue;}
                if(o.getOType() == -1){ // 可执行入库
                    flag0 = 0;
                }
            }
            for(OmsThirdOrder o:orderList){
                if(o.getOType() == 0){ //已经入库  可执行出库
                    flag0 = 1;
                }
            }
            for(OmsThirdOrder o:orderList){
                if(o.getOType() == 1){ //不执行操作
                    flag0 = 2;
                }
            }
        }
        else{ //为空没有记录
                Map<String,Object> mapcomp = getOrderCompcode(orderSn);
                String comcode  = mapcomp.get("ShipperCode").toString();
                logger.info("从三方快递接口取出快递信息 公司编码：" + comcode);
                String comname  = mapcomp.get("ShipperName").toString();
                logger.info("从三方快递接口取出快递信息 公司名称：" + comname);
                if (!StringUtils.isBlank(comcode)) {
                    logger.info("365  快递流程信息 写入本地数据库");
                    Map<String, Object> mapres = getOrderTracesByJson(comcode,comname,orderSn,stage); // 执行新增操作
                    if(mapres == null){
                        return CommonResult.failed("");
                    }
                    map.putAll(mapres);
                }
                flag0 = 0;
        }
        if(flag0 == 2){ //可执行入库
            map.put("oper",-1); //不执行操作
            return CommonResult.success(map);
        }
        else if(flag0 == 1){
            map.put("oper",1);  //执行出库
            return CommonResult.success(map);
        }
        else if(flag0 == 0){
            map.put("oper",0);  //执行入库
            return CommonResult.success(map);
        }
        return CommonResult.failed("订单查询出错,请重试");
    }


    /**
     * 提交入库单
     * @param list 快件列表
     * @return
     */
    @PostMapping("/createOrder")
    @Transactional
    public CommonResult createOrder(@RequestBody List<OmsThirdOrder> list) throws WxPayException{
        String mobile = getUserMobile();
        if (StringUtils.isBlank(mobile)) {
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        SmsStage stStage = smsStageService.selectOne(
                new EntityWrapper<SmsStage>().eq("stage_phone",mobile));
        if(stStage == null){
            return CommonResult.failed("您非驿站管理员");
        }
        if(list  == null || list.size() == 0){
            return CommonResult.failed("请先扫描快件");
        }
        for(OmsThirdOrder o:list){
            if(StringUtils.isBlank(o.getReciverPhone()) || StringUtils.isBlank(o.getLogisticCode())){
                return CommonResult.failed("收件人手机或单号不能为空！");
            }
        }
        //查询快递单号 是否已经入库
        String deliverPhone  = list.get(0).getDeliverPhone();
        StDeliver deliver = stDeliverService.selectOne(new EntityWrapper<StDeliver>()
                .eq("d_phone",deliverPhone));
        if( deliver == null){
            return CommonResult.failed("未找到此快递员");
        }
        StStageDeliverRela rela = stStageDeliverRelaService.selectOne(
                new EntityWrapper<StStageDeliverRela>().eq("deliver_id",deliver.getDId()).eq("stage_id",stStage.getId()));
        if(rela == null){
            return CommonResult.failed("此快递员未和驿站合作");
        }
        //开始创建订单
        StOrder order = new StOrder();
        String orderId = "TD"+IdUtils.createID();
        //快递员提交信息
        order.setCreateTime(new Date());
        order.setOrderId(orderId);
        int quantity = list.size();
        BigDecimal price = rela.getPrice();

        BigDecimal totalFee  =  BigDecimal.valueOf(quantity).multiply(price);
        totalFee = totalFee.setScale(2,BigDecimal.ROUND_HALF_UP);
        order.setPayAmount(totalFee);
        order.setQunatity(quantity);
        order.setState(CONSTANT.STORDER_STATE_DZF);
        order.setSiglePrice(price);
        order.setStageId(stStage.getId());
        order.setStageName(stStage.getStageName());
        order.setUserId(mobile);
        order.setDevilerId(deliver.getDId());

        stOrderService.insert(order);
        logger.info("订单入库完成");
        Date d = new Date();
        for(OmsThirdOrder o: list){
            o.setId(IdUtils.createID8());
            o.setOrderId(orderId);
            o.setCreateTime(d);
            o.setIsLeave(0);
            o.setState(0);
            o.setOType(0);
            o.setStageId(stStage.getId());
        }
        omsThirdOrderService.insertBatch(list);
        String code_url  =  wxunified(orderId,deliverPhone,totalFee); //支付码
        if(code_url == null){
          return  CommonResult.failed("创建失败");
        }
        Map<String,Object> map = new HashMap<>();
        map.put("codeUrl",code_url);
        map.put("orderInfo",order);
        return CommonResult.success(map);
    }

    /**
     * 重新发起支付
     * @param orderId
     * @return
     */
    @PostMapping("/repay")
    public  CommonResult repay(String orderId) throws WxPayException {
        String mobile = getUserMobile();
        if (StringUtils.isBlank(mobile)) {
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        SmsStage stStage = smsStageService.selectOne(
                new EntityWrapper<SmsStage>().eq("stage_phone",mobile));
        if(stStage == null){
            return CommonResult.failed("您非驿站管理员");
        }
        PaymentRecord record = paymentRecordService.selectByorderId(orderId);
        if(record != null){
            if(record.getPaystatus().equals("1")){
                return CommonResult.failed("该入库单已支付");
            }

            WxPayUnifiedOrderRequest request = new WxPayUnifiedOrderRequest();
            BigDecimal totalFee = record.getTotalprice();
            totalFee = totalFee.multiply(new BigDecimal(100));
            request.setTotalFee(totalFee.intValue());
            request.setOutTradeNo(orderId);
            request.setTradeType("NATIVE");
            request.setSpbillCreateIp("0.0.0.0");
            request.setNotifyUrl(wxpayCallBackUrl);
            request.setBody("入库单");
            request.setProductId("0-001");
            WxPayUnifiedOrderResult wxPayUnifiedOrderResult = this.wxService.unifiedOrder(request);
            String code_url = wxPayUnifiedOrderResult== null?null:wxPayUnifiedOrderResult.getCodeURL();
            Map<String,Object> map = new HashMap<>();
            StOrder stOrder = stOrderService.selectById(orderId);
            map.put("codeUrl",code_url);
            map.put("orderInfo",stOrder);
            logger.info("wxpay重新支付" + code_url);
            return CommonResult.success(map);
        }else{
            return CommonResult.failed("未找到此订单");
        }

    }


    /**
     *
     * 微信统一下单接口
     * @param orderId
     * @param deliverPhone
     * @param totalFee
     * @return code_url
     * @throws WxPayException
     */
    public  String wxunified(String orderId,String deliverPhone,BigDecimal totalFee) throws WxPayException {

        WxPayUnifiedOrderRequest request = new WxPayUnifiedOrderRequest();
		logger.info("wxpay 快递单入库 调用支付码");

        WxPayUnifiedOrderResult wxPayUnifiedOrderResult;
        PaymentRecord paymentRecord = new PaymentRecord();

        String orderType = "2";//消费

        paymentRecord.setUserid(deliverPhone);
		paymentRecord.setMobile(deliverPhone);
		paymentRecord.setOrderid(orderId);
		paymentRecord.setPaytype("WXPAY");
//        double aDouble = Double.valueOf();
		paymentRecord.setTotalprice(totalFee); //前端分为单位后台存值时候转换成元
		paymentRecord.setOrdertype(orderType);//订单类型
		paymentRecord.setProducttype("14"); //产品类型  14为驿站入库
		paymentRecord.setProductname("驿站入库单");
		paymentRecord.setCreatetime(new Date());
//		paymentRecord.setBizNo(bizNo);
        int i = paymentRecordService.insertSelective(paymentRecord);
		if (!(i == 1)){
            logger.info("wxpay 快递单入库 创建 payment 失败" );
             return null;
        }
        totalFee = totalFee.multiply(new BigDecimal(100));
		request.setTotalFee(totalFee.intValue()); // 20190918 微信金额前端乘100, 按照分单位上传
		request.setOutTradeNo(paymentRecord.getOrderid());
		request.setTradeType("NATIVE"); // NATIVE原生二维码支付
		request.setSpbillCreateIp("0.0.0.0");
		request.setNotifyUrl(wxpayCallBackUrl);
		request.setBody("入库单");
        request.setProductId("0-001");
        wxPayUnifiedOrderResult = this.wxService.unifiedOrder(request);
        //native支付二维码
		String code_url = wxPayUnifiedOrderResult.getCodeURL();
        logger.info("wxpay 快递单入库 二维码URL" + code_url);
		return code_url;
    }

    /**
     * @param orderId 入库单号
     * @return
     */
    @RequestMapping(value = "/queryOpayState", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult queryOpayState(String orderId){ //传快递单号
        StOrder o =  stOrderService.selectById(orderId);
        if(o.getState() == 1 || o.getState().equals("1")){ //支付成功
            return CommonResult.success("支付成功");
        }
        return CommonResult.failed("暂未支付");
    }

    //设置查询信息

    /**
     * 批量出库
     * 到达驿站
     * @param list 三方快递单ID集合
     */
    @PostMapping(value = "/leave")
    @ResponseBody
    public CommonResult leaveStage(@RequestBody List<String> list){ //传快递单号
        int  a = omsThirdOrderService.batchupdateLeave(list);
        if(a > 0){
            return CommonResult.success("出库成功");
        }
        return CommonResult.failed("出库失败");
    }

    //快件统计
    @GetMapping(value = "/totalinfo")
    @ResponseBody
    public CommonResult totalinfo(){ //统计快件信息
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
        //件数 统计
        //统计当天的信息
        Map<String,Object> map = new HashMap<>();
        map.put("stageId",stage.getId());
        List<StOrderTotalVo> result = stOrderService.selectLast15day(map) ;

        StOrderTotalVo last30total = stOrderService.selectlast30total(map);
        if(last30total != null){
            last30total.setAmount(last30total.getAmount()==null?"0":last30total.getAmount());
            last30total.setQunatity(last30total.getQunatity()== null?0:last30total.getQunatity());
        }else{
            last30total = new StOrderTotalVo();
            last30total.setQunatity(0);
            last30total.setAmount("0");
        }
        StOrderTotalVo alltotal =stOrderService.selectalltotal(map);
        if(alltotal != null){
            alltotal.setAmount(alltotal.getAmount()==null?"0":alltotal.getAmount());
            alltotal.setQunatity(alltotal.getQunatity()== null?0:alltotal.getQunatity());
        }else{
            alltotal = new StOrderTotalVo();
            alltotal.setQunatity(0);
            alltotal.setAmount("0");
        }
        //本月
        map.put("last15total",result);
        map.put("last30total",last30total);
        map.put("alltotal",alltotal);
        return CommonResult.success(map);
    }


    //单号识别接口
    @ApiOperation("获取快递单号的快递公司信息")
    @RequestMapping(value = "/getShipper", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getShipper(String orderSn) throws Exception{

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

        if(StringUtils.isBlank(orderSn)){
            return CommonResult.failed("查询出错");
        }

        List<OmsThirdOrder> torder = omsThirdOrderService.selectList(
                new EntityWrapper<OmsThirdOrder>().eq("logistic_code",orderSn)
                .eq("state",1)); //已入库的单号
        boolean bool = false;
        if(torder!= null && torder.size() > 0){
            bool = true;
        }
        if(bool){
            return CommonResult.failed("此快件已经入库");}

        //查询入库单
        Map<String,Object> map = getOrderCompcode(orderSn);
        if(map != null){
            map.put("orderSn",orderSn);
            map.put("ico",null);
            if(map.get("ShipperCode") != null){
                //查询快递公司信息
                StLogsticIco ico = stLogsticIcoService.selectOne(new EntityWrapper<StLogsticIco>()
                        .eq("shipper_code",map.get("ShipperCode")));
                if(ico !=  null){
                    map.put("ico",ico.getIco());
                }
            }
            return CommonResult.success(map);
        }
        return CommonResult.failed("查询出错");
    }

    /**
     * 三方接口查询快递信息
     * @param expNo 快递单号
     * */

    public Map<String,Object> getOrderCompcode (String expNo) throws Exception{
        String requestData= "{'OrderCode':'','LogisticCode':'" + expNo + "'}";
        Map<String, String> params = new HashMap<>();
        params.put("RequestData", urlEncoder(requestData, "UTF-8"));
        params.put("EBusinessID", EBusinessID);
        params.put("RequestType", "2002");  //识别单号
        String dataSign= encrypt(requestData, AppKey, "UTF-8");
        params.put("DataSign", urlEncoder(dataSign, "UTF-8"));
        params.put("DataType", "2");
        String result=sendPost(ReqURL, params);
        JSONObject jsonob= (JSONObject) JSONObject.parse(result);
        logger.info("&&& 识别信息："+jsonob);
        if(!(boolean)jsonob.get("Success")){
            return null;
        }
        logger.info("单号识别接口：快递公司：" + jsonob.get("Shippers"));
        JSONArray array = JSONObject.parseArray(jsonob.get("Shippers").toString());//
        if(array == null || array.size()  ==  0){
            return null;
        }
        else{
            JSONObject json = array.getJSONObject(0);
            String compcode= json.get("ShipperCode").toString();
            String comname = json.get("ShipperName").toString();
            System.out.println("公司代码：" + compcode);
            Map<String,Object> map = new HashMap<>();
            map.put("ShipperCode",compcode);
            map.put("ShipperName",comname);
            return map;
        }
    }

    /**
     * Json方式  查询订单物流轨迹
     * @param expCode 快递公司编号
     *  @param shipperName 快递公司名称
     * @param stage 驿站
     * @param expNo 快递单号
     * */
    public Map<String,Object> getOrderTracesByJson(String expCode, String shipperName,String expNo,SmsStage stage) throws Exception {
        String requestData = "{'OrderCode':'','ShipperCode':'" + expCode + "','LogisticCode':'" + expNo + "'}";
        Map<String, String> params = new HashMap<>();
        params.put("RequestData", urlEncoder(requestData, "UTF-8"));
        params.put("EBusinessID", EBusinessID);
        params.put("RequestType", "1002"); //查询物流轨迹
        String dataSign = encrypt(requestData, AppKey, "UTF-8");
        params.put("DataSign", urlEncoder(dataSign, "UTF-8"));
        params.put("DataType", "2");
        String result = sendPost(ReqURL, params);
        JSONObject jsonob = (JSONObject) JSONObject.parse(result);
        if(!(boolean)jsonob.get("Success")){
            return null; //不支持的快递公司
        }
        OmsThirdOrder omsthirdOrder =  new OmsThirdOrder();
        omsthirdOrder.setId(UUID.randomUUID().toString().replaceAll("-",""));
        omsthirdOrder.setLogisticCode(jsonob.get("LogisticCode").toString());
        omsthirdOrder.setCreateTime(new Date());
        omsthirdOrder.setEbusinessId(jsonob.get("EBusinessID").toString());
        omsthirdOrder.setOType(-1); //记录,等待入库
        // 物流状态: 0-无轨迹，1-已揽收，2-在途中，3-签收,4-问题件
        omsthirdOrder.setState(Integer.parseInt(jsonob.get("State").toString()));
        omsthirdOrder.setStageId(stage.getId());
        omsthirdOrder.setShipperCode(jsonob.getString("ShipperCode"));
        omsthirdOrder.setShipperName(shipperName);
        List<OmsThirdOrderTrace> tracelist = new ArrayList<>();
        logger.info("tracelist" + tracelist.size());
        String traces = jsonob.getString("Traces");
        Integer a  = omsThirdOrderMapper.insert(omsthirdOrder);
        logger.info("traces 物流信息  " + traces);
        if (!StringUtils.isBlank(traces)) {
            JSONArray array  = JSONArray.parseArray(traces);
            SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for(int i=0; i < array.size();i++) {
                JSONObject jsonstr = (JSONObject) array.get(i);
//                System.out.println(jsonstr.toString());
                OmsThirdOrderTrace trace = new OmsThirdOrderTrace();
                trace.setLogisticCode(expNo);
                trace.setAcceptTime(sdf.parse(jsonstr.getString("AcceptTime")));
                trace.setAcceptStation(jsonstr.getString("AcceptStation"));
                tracelist.add(trace);
            }
//            tracelist.sort(new Comparator<OmsThirdOrderTrace>(){
//                @Override
//                public int compare(OmsThirdOrderTrace arg0, OmsThirdOrderTrace arg1) {
//                //这里是根据ID来排序，所以它为空的要剔除
//                if(arg0.getId()==null || arg1.getId()==null) return 0;
//                    return arg0.getAcceptTime().compareTo(arg1.getAcceptTime());//这是顺序
//                }
//            });
            omsThirdOrderTraceService.insertBatch(tracelist);
        }else{
            logger.info("*******************未查询到物流信息***************" + tracelist.size());
        }
        Map<String,Object> map = new HashMap<>();
        map.put("tracelist",tracelist);
        map.put("omsthirdOrder",omsthirdOrder);
        return map;
    }

    /**
     * MD5加密
     * @param str 内容
     * @param charset 编码方式
     * @throws Exception
     */
    @SuppressWarnings("unused")
    private String MD5(String str, String charset) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes(charset));
        byte[] result = md.digest();
        StringBuffer sb = new StringBuffer(32);
        for (int i = 0; i < result.length; i++) {
            int val = result[i] & 0xff;
            if (val <= 0xf) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(val));
        }
        return sb.toString().toLowerCase();
    }

    /**
     * base64编码
     * @param str 内容
     * @param charset 编码方式
     * @throws UnsupportedEncodingException
     */
    private String base64(String str, String charset) throws UnsupportedEncodingException{
        String encoded = base64Encode(str.getBytes(charset));
        return encoded;
    }

    @SuppressWarnings("unused")
    private String urlEncoder(String str, String charset) throws UnsupportedEncodingException{
        String result = URLEncoder.encode(str, charset);
        return result;
    }

    /**
     * 电商Sign签名生成
     * @param content 内容
     * @param keyValue Appkey
     * @param charset 编码方式
     * @throws UnsupportedEncodingException  Exception
     * @return DataSign签名
     */
    @SuppressWarnings("unused")
    private String encrypt (String content, String keyValue, String charset) throws UnsupportedEncodingException, Exception
    {
        if (keyValue != null)
        {
            return base64(MD5(content + keyValue, charset), charset);
        }
        return base64(MD5(content, charset), charset);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * @param url 发送请求的 URL
     * @param params 请求的参数集合
     * @return 远程资源的响应结果
     */
    @SuppressWarnings("unused")
    private String sendPost(String url, Map<String, String> params) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn =(HttpURLConnection) realUrl.openConnection();
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // POST方法
            conn.setRequestMethod("POST");
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();
            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            // 发送请求参数
            if (params != null) {
                StringBuilder param = new StringBuilder();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if(param.length()>0){
                        param.append("&");
                    }
                    param.append(entry.getKey());
                    param.append("=");
                    param.append(entry.getValue());
                }
                out.write(param.toString());
            }
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result.toString();
    }


    private static char[] base64EncodeChars = new char[] {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', '+', '/' };

    public static String base64Encode(byte[] data) {
        StringBuffer sb = new StringBuffer();
        int len = data.length;
        int i = 0;
        int b1, b2, b3;
        while (i < len) {
            b1 = data[i++] & 0xff;
            if (i == len)
            {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[(b1 & 0x3) << 4]);
                sb.append("==");
                break;
            }
            b2 = data[i++] & 0xff;
            if (i == len)
            {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
                sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);
                sb.append("=");
                break;
            }
            b3 = data[i++] & 0xff;
            sb.append(base64EncodeChars[b1 >>> 2]);
            sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
            sb.append(base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]);
            sb.append(base64EncodeChars[b3 & 0x3f]);
        }
        return sb.toString();
    }

}
