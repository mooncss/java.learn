package com.example.mall.service.impl;

import cn.hutool.db.sql.Order;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.example.mall.common.CONSTANT;
import com.example.mall.common.CommonResult;
import com.example.mall.dao.*;
import com.example.mall.domain.*;
import com.example.mall.domain.OmsOrderDetail;
import com.example.mall.dto.*;
import com.example.mall.emun.MallConstant;
import com.example.mall.emun.OrderStatus;
import com.example.mall.mapper.*;
import com.example.mall.model.*;
import com.example.mall.service.*;
import com.example.mall.vo.CartItemBesGroup;
import com.zhihui.uj.management.DrDoorPhone.vo.OwnerVo;
import com.zhihui.uj.management.common.entity.UjOwner;
import com.zhihui.uj.management.common.entity.UjQuarters;
import com.zhihui.uj.management.common.entity.UjRoomOwner;
import com.zhihui.uj.management.common.service.IUjOwnerService;
import com.zhihui.uj.management.common.service.IUjQuartersService;
import com.zhihui.uj.management.common.service.IUjRoomOwnerService;
import com.zhihui.uj.management.utils.IdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 前台订单管理Service
 * Created by macro on 2018/8/30.
 */
@Service
public class OmsPortalOrderServiceImpl implements OmsPortalOrderService {
    @Autowired
    private OmsCartItemService cartItemService;
    @Autowired
    private UmsMemberReceiveAddressService memberReceiveAddressService;
    @Autowired
    private UmsMemberCouponService memberCouponService;
    @Autowired
    private UmsIntegrationConsumeSettingMapper integrationConsumeSettingMapper;
//    private PmsSkuStockMapper skuStockMapper;
    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private PortalOrderItemDao orderItemDao;
    @Autowired
    private SmsCouponHistoryMapper couponHistoryMapper;
//    @Autowired
//    private RedisService redisService;
    //    @Value("${redis.key.prefix.orderId}")
//    private String REDIS_KEY_PREFIX_ORDER_ID = "";
    @Autowired
    private PortalOrderDao portalOrderDao;
//    @Autowired
//    private OmsOrderSettingMapper orderSettingMapper;
    @Autowired
    private OmsOrderItemMapper orderItemMapper;
    @Autowired
    private SmsGroupBuyProductService smsGroupBuyProductService;

    @Autowired
    IUjOwnerService iUjOwnerService;

    @Autowired
    HttpServletRequest request;

    @Autowired
    PmsProductMapper pmsProductMapper;

    @Autowired
    SmsGroupbuyPromotionProreduceService smsGroupbuyPromotionProreduceService;

    @Autowired
    PmsFeightTemplateDao pmsFeightTemplateDao;

    @Autowired
    PmsSkuStockMapper pmsSkuStockMapper;

    @Autowired
    IUjQuartersService iUjQuartersService;

    @Autowired
    IUjRoomOwnerService iUjRoomOwnerService;


    @Autowired
    BesUserService besUserService;

    @Autowired
    SmsGroupBuyAssembleDao smsGroupBuyAssembleDao;

    @Autowired
    PmsTacticsDao pmsTacticsDao;

    private final static Logger logger = LoggerFactory.getLogger(OmsPortalOrderServiceImpl.class);


    //购物车 订单确认信息
    @Override
    public ConfirmOrderResult generateConfirmOrder(String mobile) throws Exception {
        EntityWrapper en = new EntityWrapper();
        en.eq("mobile", mobile);
        UjOwner owner = iUjOwnerService.selectOne(en);
        if (owner == null) {
            return null;
        }

        ConfirmOrderResult result = new ConfirmOrderResult();
        //获取用户购物车信息
        List<OmsCartItem> cartPromotionItemList = cartItemService.listiselectgift(mobile);
        Map map = new HashMap<String, Object>();
        map.put("memberId", mobile);
        List<CartItemBesGroup> besItemlist = besUserService.selectBesFromcart0(map);
        if(CollectionUtils.isEmpty(besItemlist)){
            return null;
        }
        List<Long> listbesid = new ArrayList<>();
        for(CartItemBesGroup oci:besItemlist){
            listbesid.add(oci.getBesId());
        }
        List<BesUser> listbes = besUserService.selectList(new EntityWrapper<BesUser>().in("bes_id",listbesid));
        //查询所有的自营商家
        List<BesUser> listb = besUserService.selectList(new EntityWrapper<BesUser>().eq("isautarky",0).in("bes_id",listbesid)); //自营商家
        result.setBesusersIsautar(listb);
        if(listbes != null && listbes.size() > 0 && listb.size() == listbes.size()){
            //购物车商品全部为 直营商家 展示自提
            result.setIsMention(0);
        }else{
            //不展示自提选项
            result.setIsMention(1);
        }

//        result.setDeliverFeeStart("满"+ MallConstant.MALL_NOFEE_START+"元免配送费");

        int total = 0;
        BigDecimal cartTotalPrice = new BigDecimal("0");
        for (CartItemBesGroup b : besItemlist) {
            List<OmsCartItem> list = new ArrayList<>();
            for (OmsCartItem o : cartPromotionItemList) {
                Long besid = b.getBesId();
                if (besid.longValue() == o.getBesId().longValue()) {
                    list.add(o);
                    cartTotalPrice = cartTotalPrice.add(new BigDecimal("" + o.getQuantity()).multiply(o.getPrice()));
                    total++;
                }
            }
            b.setItemlist(list);
        }
        result.setTotal(total);
        result.setBesItemlist(besItemlist);
        //获取用户收货地址列表
        List<UmsMemberReceiveAddress> memberReceiveAddressList = new ArrayList<>();
        List<UmsMemberReceiveAddress> memberRecei = memberReceiveAddressService.list();
        for (UmsMemberReceiveAddress a : memberRecei) {
            if (a.getDefaultStatus() == 0) {
                memberReceiveAddressList.add(a);
            }
        }
        if (memberRecei != null && memberRecei.size() > 0) {
            if (memberReceiveAddressList == null || memberReceiveAddressList.size() == 0) {
                memberReceiveAddressList.add(memberRecei.get(0));

            }
        }
        result.setMemberReceiveAddressList(memberReceiveAddressList);
        if (memberReceiveAddressList == null || memberReceiveAddressList.size() == 0) {
            //查询获取用户的小区地址
            if (owner != null) {
                UjQuarters ujQuarters = iUjQuartersService.selectById(owner.getQuartersId());
                if (ujQuarters != null) {
                    UmsMemberReceiveAddress address = new UmsMemberReceiveAddress();
                    address.setId(IdUtils.createID());
                    address.setDefaultStatus(0);  //设置默认
                    address.setProvince(ujQuarters.getProvince());
                    address.setCity(ujQuarters.getCity());
                    address.setRegion(ujQuarters.getArea());
                    address.setMemberId(mobile);
                    address.setPhoneNumber(mobile);
                    address.setName(owner.getFullName()); //设置收货人姓名
                    String detailaddr = ujQuarters.getAddress() + ujQuarters.getName();

                    EntityWrapper ent = new EntityWrapper();
                    ent.eq("quarters_id", ujQuarters.getQuartersId());
                    ent.eq("owner_id", owner.getOwnerId());
                    UjRoomOwner ujRoomOwner = iUjRoomOwnerService.selectOne(ent);

                    if(ujRoomOwner!=null) {
                        OwnerVo ov = iUjRoomOwnerService.getOwnerVoByOwnerId(owner.getOwnerId(), ujRoomOwner.getRoomId());

                        detailaddr += (ov.getBuildsName() + ov.getUnitName() + ov.getRoomName());

                        address.setLat(null);
                        address.setLon(null);
                        address.setDetailAddress(detailaddr);
                        memberReceiveAddressService.insert(address);
                        memberReceiveAddressList.add(address);
                    }
                }
            }
        }
        ConfirmOrderResult.CalcAmount calcAmount = calcCartAmount(besItemlist,result);

        for(CartItemBesGroup oci:besItemlist){
            for(UserCartFeightCalc calc:calcAmount.getUserCartTotalFeight().getFlist()){
                if(oci.getBesId().longValue() == calc.getBesId().longValue()){
                    oci.setBesFeightAmount(calc.getFeightAmount());
                    oci.setFeightInfo(calc.getFeightInfo());
                    break;
                }
            }
        }

        result.setCalcAmount(calcAmount);
        return result;
    }



    /**
     * 生成购物车订单
     * 运费计算完毕
     * */
    @Override
    @Transactional
    public CommonResult generateOrder(OrderParam orderParam, String mobile) throws Exception {
        EntityWrapper en = new EntityWrapper();
        en.eq("mobile", mobile);
        List<UjOwner> owners = iUjOwnerService.selectList(en);
        //只查询已经选中的商品
//        List<OmsCartItem> cartList =cartItemService.listiselect(mobile);
        List<OmsCartItem> cartList =cartItemService.listiselectgift(mobile);
        UmsMemberReceiveAddress address = null;
        if(orderParam.getDeliverType() != null && orderParam.getDeliverType() == 1){
            if(StringUtils.isEmpty(orderParam.getReciveName()) || StringUtils.isEmpty(orderParam.getReceivePhone())){
                throw new Exception("请输入提货人信息");
            }
            if(StringUtils.isEmpty(orderParam.getMentionTime())){
                throw new Exception("请选择提货时间");
            }
        }else{
            address = memberReceiveAddressService.getItem(orderParam.getMemberReceiveAddressId());
            if(ObjectUtils.isEmpty(address)){
                throw new Exception("收货地址不能为空");
            }
        }

        OmsCartItem engList = checkStock(cartList);
        if(engList != null){
            throw new Exception("商品库存不足:"+engList.getProductName());
        }
        //商家去重
        Set<Long> beslist = new HashSet<>();
        for (OmsCartItem o : cartList) {
            beslist.add(o.getBesId());
        }
        List<GenOrderResult> orderlist = new ArrayList<>();
        ConfirmOrderResult.CalcAmount calcTotal = new ConfirmOrderResult.CalcAmount();
        CartBussnesOrderRes res = new CartBussnesOrderRes();
        //根据不同商家生成订单
        BigDecimal totalAmount = new BigDecimal(0);
        BigDecimal payAmount = new BigDecimal(0);
        BigDecimal feightAmount = new BigDecimal(0);
        BigDecimal promotionAmount = new BigDecimal(0);
        String order_th =generateOrderTh();
        List<BesUser> besUserList = besUserService.selectList(new EntityWrapper<BesUser>()
                .eq("isautarky",0)
                .eq("status",1)
                .eq("checked",2));
        //返回值增加 订单头
        res.setOrderTh(order_th);
        for (Long l : beslist) {
            List<OmsOrderItem> orderItemList = new ArrayList<>();
            List<CartItemBesGroup> beslistcalc = new ArrayList<>();
            List<OmsCartItem> cartbeslist = new ArrayList<>();
            CartItemBesGroup besg = new CartItemBesGroup();
            besg.setBesId(l);
            for (OmsCartItem oci : cartList) {
                //生成下单商品信息
                if (oci.getBesId().longValue() == l.longValue()) {
//                    OmsCartItem o =new OmsCartItem();
//                    BeanUtils.copyProperties(cartPromotionItem,o);
                    cartbeslist.add(oci);
                    OmsOrderItem orderItem = new OmsOrderItem();
                    orderItem.setProductId(oci.getProductId());
                    orderItem.setProductName(oci.getProductName());
                    orderItem.setProductPic(oci.getProductPic());
                    orderItem.setProductAttr(oci.getProductAttr());
                    orderItem.setProductBrand(oci.getProductBrand());
                    orderItem.setProductSn(oci.getProductSn());
                    orderItem.setProductPrice(oci.getPrice());
                    orderItem.setProductQuantity(oci.getQuantity());
                    orderItem.setProductSkuId(oci.getProductSkuId());
                    orderItem.setProductSkuCode(oci.getProductSkuCode());
                    orderItem.setProductCategoryId(oci.getProductCategoryId());
                    orderItem.setPromotionAmount(new BigDecimal(0));
                    orderItem.setSp1(oci.getSp1());
                    orderItem.setSp2(oci.getSp2());
                    orderItem.setSp3(oci.getSp3());
                    orderItem.setPromotionName("");
                    orderItem.setGiftIntegration(0);
                    orderItem.setGiftGrowth(0);
                    orderItemList.add(orderItem);
                }
            }
            besg.setItemlist(cartbeslist);
            beslistcalc.add(besg);
            //库存判断
            if (!hasStock1(cartList)) {
                return CommonResult.failed("商品库存不足");
            }

            OmsOrder order = new OmsOrder();
            order.setDiscountAmount(new BigDecimal("0"));
            order.setBesId(l);
            /**
             * 开始执行订单分配到直营店
             * */
            if(orderParam.getDeliverType() == null || orderParam.getDeliverType() == 0){
                BesUser curBes = null;
                for(BesUser b:besUserList){
                    if(b.getBesId().longValue() == l.longValue()){
                        if(b.getIsautarky().equals("0")){
                            curBes = b;}
                    }
                }

                if(curBes != null && calcNearestBes(address.getLon(),address.getLat(),besUserList) != null ){
                    logger.info("最近商家 -订单分配给商家>:" + curBes.getShopname());
                    order.setBesId(curBes.getBesId());
                }
                //收货人信息：姓名、电话、邮编、地址
                order.setReceiveAddressId(orderParam.getMemberReceiveAddressId());
                order.setReceiverName(address.getName());
                order.setReceiverPhone(address.getPhoneNumber());
                order.setReceiverPostCode(address.getPostCode());
                order.setReceiverProvince(address.getProvince());
                order.setReceiverCity(address.getCity());
                order.setReceiverRegion(address.getRegion());
                order.setReceiverDetailAddress(address.getDetailAddress());
            }else{ //自提
                order.setBesId(orderParam.getMentionBesId()); //订单应分配给 自提目的直营店

                if(StringUtils.isEmpty(orderParam.getMentionTime())){
                    throw new Exception("请选择提货时间");
                }
                BesUser BU = besUserService.selectById(orderParam.getMentionBesId());

                order.setReceiverProvince(BU.getProvince());
                order.setReceiverCity(BU.getCity());
                order.setReceiverRegion(BU.getArea());
                order.setReceiverDetailAddress(BU.getDetailaddr());

                order.setReceiveAddressId(null);
                order.setReceiverName(orderParam.getReciveName());
                order.setReceiverPhone(orderParam.getReceivePhone());
            }
            order.setDeliveryType(orderParam.getDeliverType()+"");

            //费用计算
            ConfirmOrderResult.CalcAmount calcAmount = calcCartAmount(beslistcalc,null);
            //总计金额
            order.setTotalAmount(calcAmount.getTotalAmount());
            order.setFreightAmount(calcAmount.getFreightAmount());  //运费
            order.setPromotionAmount(calcAmount.getPromotionAmount());
            order.setPromotionInfo(calcAmount.getPromotionInfo());
            if(orderParam.getDeliverType() == null || orderParam.getDeliverType() == 0){
                order.setPayAmount(calcAmount.getPayAmount());
            }
            else if(orderParam.getDeliverType() == 1){
                order.setPayAmount(new BigDecimal(calcAmount.getPayAmountStrZiTi()));
                calcAmount.setPayAmount(new BigDecimal(calcAmount.getPayAmountStrZiTi()));
            }

            order.setIntegration(calcAmount.getCredit()); //积分
            logger.info("797& 购物车信息生成 积分 :" + calcAmount.getCredit());

            //转化为订单信息并插入数据库
            order.setMemberId(mobile);
            order.setCreateTime(new Date());  //下单时间
            order.setMemberUsername(owners.get(0).getFullName());  //会员名
            //支付方式：0->平台支付；1->支付宝；2->微信
            order.setPayType(0);

            //订单来源：0->PC订单；1->app订单
            order.setSourceType(1);

            //订单状态：0->待付款；1->待发货；2->配送中；3->已完成；4->已关闭；5->无效订单
            order.setStatus(0);

            //订单类型：0-> 正常订单；1-> 秒杀订单 2 拼团订单
            order.setOrderType(0);

            //0->未确认；1->已确认
            order.setConfirmStatus(0);
            //订单删除状态
            order.setDeleteStatus(0);
            //生成订单号
            order.setOrderSn(generateOrderSn(order));

            order.setOrderTh(order_th);
            //插入order表和order_item表
            try {
                orderMapper.insert(order);
            }
            catch (Exception e){
                logger.info("创建购物车订单头出错");
                throw  new Exception("出错了，请重试");
            }
            for (OmsOrderItem orderItem : orderItemList) {
                orderItem.setOrderId(order.getId());
                orderItem.setOrderSn(order.getOrderSn());
            }
            try {
                orderItemDao.insertList(orderItemList);
            }catch (Exception e){
                logger.info("创建购物车订单子元素出错");
                throw  new Exception("出错了，请重试");
            }

            totalAmount =  totalAmount.add(order.getTotalAmount());
            payAmount = payAmount.add(order.getPayAmount());
            feightAmount = feightAmount.add(order.getFreightAmount());
            promotionAmount = promotionAmount.add(order.getPromotionAmount());
            GenOrderResult geo = new GenOrderResult();
            geo.setOrder(order);
            geo.setItemlist(orderItemList);
            //删除购物车中的下单商品
            orderlist.add(geo);
        }

        //下单成功后执行库存锁定
        doLockStock(cartList);

        calcTotal.setTotalAmount(totalAmount);
        calcTotal.setPayAmount(payAmount);
        calcTotal.setFreightAmount(feightAmount);
        calcTotal.setPromotionAmount(promotionAmount);
        if(payAmount.compareTo(new BigDecimal("0")) <= 0){
            throw  new Exception("订单支付金额不能为0");
        }
        res.setCalcTotal(calcTotal);
        res.setOrderlist(orderlist);
        try {
            deleteCartItemList(cartList, mobile);
        }
        catch (Exception e){
            throw new Exception("删除购物车商品异常");
        }
        return CommonResult.success(res);
    }

    //生成 单商品/拼团 确定订单
    @Override
    public ConfirmOrderResult generateProConfirmOrder(OmsOrderCreatePrepare p) throws Exception {
        if(p == null || p.getQuantity() <= 0){
            throw new Exception("购买数量不能为0");
        }
        String mobile = request.getHeader("mobile");
        ConfirmOrderResult result = new ConfirmOrderResult();
        BeanUtils.copyProperties(p, result);
        //获取用户购物车信息
        PmsProduct product = pmsProductMapper.selectByPrimaryKey(p.getProductId());
        System.out.println("购买商品ID：" + p.getProductId());
        PmsSkuStock sku = null;
        if (!StringUtils.isEmpty(p.getSkuId())) {
            sku = pmsSkuStockMapper.selectById(p.getSkuId());
        }
        result.setDeliverFeeStart("满"+ MallConstant.MALL_NOFEE_START+"元免配送费");
        /********* 商品只能新用户才能买 ******/
        CommonResult  t = canMemberPchase(p.getQuantity(),mobile,product.getId());
        if(t.getCode() != 0){
            throw new Exception(t.getMsg());
        }
        /********************************** 限购查询开始 *************************************/
        if(p.getOrderType() == 2) {
            EntityWrapper en = new EntityWrapper();
            en.eq("product_id", product.getId());
            en.ge("end_time", new Date());
            List<SmsGroupBuyProduct> listGroupButPro = smsGroupBuyProductService.selectList(en);
            if (listGroupButPro != null && listGroupButPro.size() > 0) {
                SmsGroupBuyProduct mp = listGroupButPro.get(0);
                //限购查询
                Map<String, Object> map = new HashMap<>();
                map.put("memberId", mobile);
                map.put("productId", p.getProductId());
                List<OmsOrderLimitToday> limitlist = orderMapper.getPurchseToday(map);
                Integer limit = mp.getPromotionLimit(); //限购数量
                if (limit == null) {
                    limit = 99;
                }
                if (limitlist == null || limitlist.size() <= 0) { //没有购买记录
                    if (limit < p.getQuantity()) {
                        throw new Exception("此活动每人限购" + limit + "件");
                    }
                } else {
                    //查看今天购买的件数
                    int total = 0;
                    for (OmsOrderLimitToday o : limitlist) {
                        total += o.getProductQuantity();
                    }
                    int a = limit - total;
                    if (limit - total < p.getQuantity()) {
                        throw new Exception("限购，您还可以购买" + (a < 0 ? 0 : a) + "件");
                    }
                }
            } else {
                //拼团活动已结束，
                throw new Exception("拼团活动已结束");
            }
        }
        /********************************** 限购查询结束 *************************************/
        //库存验证
        if(sku != null){
            //验证sku库存
            if(sku.getStock() - sku.getLockStock() - p.getQuantity() < 0){
                throw new Exception("库存不足");
            }
        }else{
            if(product.getStock() -product.getLockStock() - p.getQuantity() < 0){
                throw new Exception("库存不足");
            }
        }
        //店铺信息
        BesUser b = besUserService.selectById(product.getBesId());
        if(b.getIsautarky() != null && b.getIsautarky().equalsIgnoreCase("0")){ // 自营
            result.setIsMention(0);}else{ result.setIsMention(1);} //设置是否自营
        BesUser bes = new BesUser();
        bes.setShopname(b.getShopname());
        bes.setShopico(b.getShopico());
        bes.setBesId(b.getBesId());
        List<BesUser> list = new ArrayList<>();
        list.add(bes);
        result.setBesusers(list);
        //获取用户收货地址列表
        List<UmsMemberReceiveAddress> memberReceiveAddressList = new ArrayList<>();
        List<UmsMemberReceiveAddress> memberRecei = memberReceiveAddressService.list();
        for (UmsMemberReceiveAddress a : memberRecei) {
            if (a.getDefaultStatus() == 0) {
                memberReceiveAddressList.add(a);
            }
        }
        if (memberRecei != null && memberRecei.size() > 0) {
            if (memberReceiveAddressList == null || memberReceiveAddressList.size() == 0) {
                memberReceiveAddressList.add(memberRecei.get(0));

            }
        }
        result.setMemberReceiveAddressList(memberReceiveAddressList);
        if (memberReceiveAddressList.size() == 0) {
            EntityWrapper emn = new EntityWrapper();
            emn.eq("mobile", mobile);
            UjOwner owner = iUjOwnerService.selectOne(emn);
            //查询获取用户的小区地址
            UjQuarters ujQuarters = iUjQuartersService.selectById(owner.getQuartersId());
            if (owner != null && ujQuarters != null) {
                UmsMemberReceiveAddress address = new UmsMemberReceiveAddress();
                address.setDefaultStatus(0);  //设置默认
                address.setProvince(ujQuarters.getProvince());
                address.setCity(ujQuarters.getCity());
                address.setRegion(ujQuarters.getArea());
                address.setMemberId(mobile);
                address.setPhoneNumber(mobile);
                address.setName(owner.getFullName()); //设置收货人姓名
                String detailaddr = ujQuarters.getAddress() + ujQuarters.getName();
                address.setId(IdUtils.createID());
                EntityWrapper ent = new EntityWrapper();
                ent.eq("quarters_id", ujQuarters.getQuartersId());
                ent.eq("owner_id", owner.getOwnerId());
                UjRoomOwner ujRoomOwner = iUjRoomOwnerService.selectOne(ent);
                if(ujRoomOwner!=null){
                    OwnerVo ov = iUjRoomOwnerService.getOwnerVoByOwnerId(owner.getOwnerId(), ujRoomOwner.getRoomId());

                    detailaddr += (ov.getBuildsName() + ov.getUnitName() + ov.getRoomName());

                    address.setDetailAddress(detailaddr);

                    memberReceiveAddressService.insert(address);

                    memberReceiveAddressList.add(address);
                    result.setMemberReceiveAddressList(memberReceiveAddressList);
                }
            }
        }
        //配送费用
        Long feightTempId = product.getFeightTemplateId();
        PmsFeightTemplate pft = pmsFeightTemplateDao.selectById(feightTempId);
        //计算总金额、活动优惠、应付金额
        ConfirmOrderResult.CalcAmount calcAmount = calcProductAmount(
                product, p.getQuantity(), pft, p.getOrderType(), sku);
        result.setCalcAmount(calcAmount);
        if(calcAmount.getPayAmount().compareTo(new BigDecimal(0)) <= 0){
            throw new Exception("订单金额不能为0");
        }
        //商品信息
        CartPromotionItem proItem = new CartPromotionItem();
        if (sku == null) {
            proItem.setRealStock(product.getStock());
            proItem.setPrice(product.getPrice());
        } else {
            proItem.setRealStock(sku.getStock() - sku.getLockStock());
            proItem.setProductSkuId(sku.getId());//设置 skuid
            //设置3个sku属性
            proItem.setSp1(sku.getSp1());
            proItem.setSp2(sku.getSp2());
            proItem.setSp3(sku.getSp3());
            proItem.setPrice(sku.getPrice());
        }
        proItem.setReduceAmount(calcAmount.getPromotionAmount());
        proItem.setQuantity(p.getQuantity());
        proItem.setProductPic(product.getPic());
        proItem.setProductName(product.getName());
        proItem.setProductId(product.getId());
        proItem.setPromotionMessage(calcAmount.getPromotionInfo());
        proItem.setMemberId(request.getHeader("mobile"));

        /**********************商品积分计算*******************************/
        //@TODO 获取系统参数
        BigDecimal SysRate = new BigDecimal(CONSTANT.SYS_CREDIT_PERCENT);
        BigDecimal rate = product.getGiftPoint();
        BigDecimal pay_amount  =  calcAmount.getPayAmount();
        if(pay_amount == null){
            pay_amount = new BigDecimal(0);
        }
        Integer jifen  =(pay_amount.multiply(SysRate).multiply(rate).multiply(new BigDecimal(100))).intValue();
        result.setCredit(jifen);
        result.getCalcAmount().setCredit(jifen);

        logger.info("生成 单商品/拼团的确定订单 积分：" + jifen);

        List<CartPromotionItem> itemList = new ArrayList<>();
        itemList.add(proItem);
        result.setCartPromotionItemList(itemList);
        return result;
    }


    //单品或者拼团商品直接下单  计算购买需要支付的费用， 商品总计费用，运费核算
    public ConfirmOrderResult.CalcAmount calcProductAmount(PmsProduct product, int quantity,
                                                           PmsFeightTemplate pft, Integer orderType,
                                                           PmsSkuStock sku) throws Exception {
        ConfirmOrderResult.CalcAmount calc = new ConfirmOrderResult.CalcAmount();
        calc.setFreightAmount(new BigDecimal(0));

        BigDecimal reduceAmount = new BigDecimal(0);
        BigDecimal bd = new BigDecimal(0);
        //查询促销信息
        String promotionInfo = "";
        if (orderType == 2) {  //拼团订单
            EntityWrapper en = new EntityWrapper();
            en.eq("product_id", product.getId());
            en.ge("end_time", new Date()); //还没结束的拼团活动
            List<SmsGroupBuyProduct> list = smsGroupBuyProductService.selectList(en);
            if (list != null && list.size() > 0) {
                SmsGroupBuyProduct mp = list.get(0);
                BigDecimal big;
                if (sku == null) {
                    big = mp.getPromotionPrice();
                } else {
                    big = (sku.getGroupbuyPrice() == null ? sku.getPrice() : sku.getGroupbuyPrice());
                }
                bd = big.multiply(new BigDecimal(quantity)).setScale(2);
                //查询满减
                EntityWrapper en1 = new EntityWrapper();
                en1.eq("product_id", product.getId());
                en1.orderBy("limit_amount", false);  //降序
                List<SmsGroupbuyPromotionProreduce> reduces = smsGroupbuyPromotionProreduceService.selectList(en1);
                for (SmsGroupbuyPromotionProreduce s : reduces) {
                    if (s.getLimitAmount().compareTo(bd) == -1) {//达到满减的条件
                        reduceAmount = s.getReduceAmount();
                        promotionInfo = "满" + s.getLimitAmount() + "减" + s.getReduceAmount() + "元";
                        break;
                    }
                }
                //促销信息
            } else {
                reduceAmount = new BigDecimal(0);
            }
        }
        else{
            if(sku != null){
                BigDecimal price = sku.getPrice();
                if(price.compareTo(new BigDecimal(0)) == 0){
                    price = product.getPrice();
                }
                bd = bd.add(price.multiply(new BigDecimal(quantity))).setScale(2);
            }
            else{
            //正常单品
                BigDecimal price = product.getPrice();
                bd= bd.add(price.multiply(new BigDecimal(quantity)));
            }
        }

        //查询优惠信息
        List<BesReduce> rlist = besReduceService.selectList(new EntityWrapper<BesReduce>()
                .eq("bes_id",product.getBesId())
                .eq("status",0)
                .orderBy("reduce_amount",false)); // 降序排列 去满减金额最多的一个  只能参与一次活动
        //订单的总价格是商品的所有价格 + 运费
        //店铺满减的价格
        BigDecimal besreduce = new BigDecimal(0);
        if(rlist!= null && rlist.size() > 0 ){
            for(BesReduce r:rlist){
                if(r.getLimitAmount().compareTo(bd)== -1 ||
                        r.getLimitAmount().compareTo(bd)== 0){  //符合满减条件
                    besreduce = besreduce.add(r.getReduceAmount());
                    promotionInfo += ("店铺优惠:满"+ r.getLimitAmount() +"减"+besreduce);
                    break;
                }
            }
        }
        calc.setPromotionInfo(promotionInfo);
        //优惠金额
        calc.setPromotionAmount(reduceAmount.add(besreduce).setScale(2));
        //应付金额
        BigDecimal bdc = bd.subtract(reduceAmount.add(besreduce)).setScale(2);


        //运费金额
        BigDecimal feig = new BigDecimal("0");
        calc.setFreightAmount(feig);
        calc.setFeightInfo("免配送费");
        //配送费
        List<BesFeightPlate> besFeightPlateList = besFeightPlateDao.selectList(
                new EntityWrapper<BesFeightPlate>().eq("bes_id",product.getBesId()));
        if(besFeightPlateList != null && besFeightPlateList.size() >0 ){
            if(besFeightPlateList.get(0).getStartNoFee().compareTo(bdc) > 0){
                feig = besFeightPlateList.get(0).getFee();
                bdc = bdc.add(feig);
                calc.setFreightAmount(feig);
                calc.setFeightInfo("配送费:"+feig+"元,本店满"+besFeightPlateList.get(0).getStartNoFee()+"免配送费");
            }
        }
//        bd.add(feig).subtract(reduceAmount.add(besreduce)).setScale(2);

        calc.setPayAmountStrZiTi(""+bdc.subtract(feig));
        calc.setPayAmount(bdc);
        calc.setPayAmountStr(bdc==null?"0":bdc.toString());

        /**********************积分*************************/
        BigDecimal SysRate = new BigDecimal(CONSTANT.SYS_CREDIT_PERCENT);
        BigDecimal rate = product.getGiftPoint();
        if(rate == null){
            rate = new BigDecimal(0);
        }
        Integer credit  =(bdc.multiply(SysRate).multiply(rate).multiply(new BigDecimal(100))).intValue();
        calc.setCredit(credit);
        return calc;
    }

    /***
     *生成 单品订单 或者拼团订单
     */
    @Override
    public CommonResult generateProOrder(OmsOrderCreatePrepare p, String mobile) throws Exception {
        if(p == null || p.getQuantity() == 0 ){
            throw new Exception("商品数量不能为0");
        }
        PmsProduct product = pmsProductMapper.selectByPrimaryKey(p.getProductId());
        System.out.println("购买商品ID：" + p.getProductId());
        PmsSkuStock sku = null;
        if (!StringUtils.isEmpty(p.getSkuId())) {
            sku = pmsSkuStockMapper.selectById(p.getSkuId());
        }
        UmsMemberReceiveAddress address = null;
        if(p.getDeliverType() != null && p.getDeliverType() == 1){
            if(StringUtils.isEmpty(p.getReciveName()) || StringUtils.isEmpty(p.getReceivePhone())){
                throw new Exception("请输入提货人信息");
            }
            if(StringUtils.isEmpty(p.getMentionTime())){
                throw new Exception("请选择提货时间");
            }
        }else{
            address = memberReceiveAddressService.getItem(p.getMemberReceiveAddressId());
            if(ObjectUtils.isEmpty(address)){
                throw new Exception("收货地址不能为空");
            }
        }

        CommonResult  t = canMemberPchase(p.getQuantity(),mobile,product.getId());
        if(t.getCode() != 0){
            throw new Exception(t.getMsg());
        }
        EntityWrapper en = new EntityWrapper();
        en.eq("mobile", mobile);
        List<UjOwner> owners = iUjOwnerService.selectList(en);

        OmsOrderItem orderItem = new OmsOrderItem();
        orderItem.setProductId(product.getId());
        orderItem.setProductName(product.getName());
        orderItem.setProductPic(product.getPic());
//      orderItem.setProductAttr(cartPromotionItem.getProductAttr());
        orderItem.setProductBrand(product.getBrandName());
        orderItem.setProductSn(product.getProductSn());
        if (p.getOrderType() == 2) {  //拼团购买
            //查询拼团活动商品
            EntityWrapper enw = new EntityWrapper();
            enw.eq("product_id",product.getId()); //拼团活动ID
            enw.ge("end_time", new Date());  //还没结束的活动
            SmsGroupBuyProduct gy = smsGroupBuyProductService.selectOne(enw);
            if (gy == null || org.apache.commons.lang.StringUtils.isBlank(gy.getId())) {
                throw new Exception("抱歉，拼团活动已结束");
            }
//            EntityWrapper en = new EntityWrapper();
//            en.eq("product_id", product.getId());
//            en.ge("end_time", new Date()); //还没结束的拼团活动
            List<SmsGroupBuyProduct> listGroupButPro = smsGroupBuyProductService.selectList(enw);
            SmsGroupBuyProduct mp = listGroupButPro.get(0);
            Map<String,Object> map = new HashMap<>();
            map.put("memberId",mobile);
            map.put("productId",p.getProductId());
            List<OmsOrderLimitToday> limitlist =  orderMapper.getPurchseToday(map);
            Integer limit = mp.getPromotionLimit(); //限购数量
            if(limit == null){ limit = 99; }
            if(limitlist == null || limitlist.size() <= 0){ //没有购买记录
                if(limit < p.getQuantity()){
                    throw new Exception("此活动每人限购"+limit+"件");
                }
            }else{
                //查看今天购买的件数
                int total = 0;
                for(OmsOrderLimitToday o:limitlist){
                    total += o.getProductQuantity();
                }
                int a = limit -total;
                if(limit-total < p.getQuantity()){
                    throw new Exception("限购，您还可以购买"+(a<0?0:a)+"件");
                }
            }

            if (sku == null) {
                orderItem.setProductPrice(gy.getPromotionPrice());
            } else {
                BigDecimal groupbuyPrice = sku.getGroupbuyPrice();
                if(groupbuyPrice!=null && groupbuyPrice.compareTo(new BigDecimal(0)) == 1){
                    orderItem.setProductPrice(sku.getGroupbuyPrice());//拼团价格
                }
                else{
                    orderItem.setProductPrice(sku.getPrice());
                }
            }
        }else if(p.getOrderType() == 0) {  //非拼团订单
            if (sku == null) {
                orderItem.setProductPrice(product.getPrice());
            } else {
                BigDecimal skuPrice = sku.getPrice();
                if(skuPrice!=null && skuPrice.compareTo(new BigDecimal(0)) == 1){
                    orderItem.setProductPrice(skuPrice);//拼团价格
                }
                else{
                    orderItem.setProductPrice(product.getPrice());
                }
            }
        }
        if(sku!=null){
            orderItem.setSp1(sku.getSp1());
            orderItem.setSp2(sku.getSp2());
            orderItem.setSp3(sku.getSp3());
        }
        orderItem.setProductQuantity(p.getQuantity());
        orderItem.setProductSkuId(p.getSkuId());
        orderItem.setProductSkuCode(sku == null ? null : sku.getSkuCode());
        orderItem.setProductCategoryId(product.getProductCategoryId());
        orderItem.setPromotionAmount(new BigDecimal(0));
        orderItem.setPromotionName(""); //促销信息
        orderItem.setGiftIntegration(0);
        orderItem.setGiftGrowth(0);
        orderItem.setIntegrationAmount(new BigDecimal(0));  //积分
//        List<CartPromotionItem> list = new ArrayList<>();
        //进行库存锁定
        boolean lockstate = calcStock(product,sku,p.getQuantity());
        if(!lockstate){
            return CommonResult.failed("商品库存不足!");
        }
        //订单实例
        OmsOrder order = new OmsOrder();
        //折扣金额
        order.setDiscountAmount(new BigDecimal(0));
        //商家ID
        order.setBesId(product.getBesId());
        order.setRemark(p.getRemark()); //订单备注

        Long feightTempId = product.getFeightTemplateId();
        PmsFeightTemplate pft = pmsFeightTemplateDao.selectById(feightTempId);
        ConfirmOrderResult.CalcAmount calcAmount = calcProductAmount(product, p.getQuantity(), pft, p.getOrderType(), sku);
        //@TODO 获取系统参数
        BigDecimal SysRate = new BigDecimal(CONSTANT.SYS_CREDIT_PERCENT);
        BigDecimal rate = product.getGiftPoint();
        if(rate == null){
            rate = new BigDecimal(0);
        }
        BigDecimal pay_amount  =  calcAmount.getPayAmount();
        Integer jifen  =(pay_amount.multiply(SysRate).multiply(rate).multiply(new BigDecimal(100))).intValue();
        order.setIntegration(jifen);

        logger.info("619行 -  单品购买 生成订单 积分" + jifen);
        /**********************积分计算 结束************************/
        order.setTotalAmount(calcAmount.getTotalAmount());
        order.setFreightAmount(calcAmount.getFreightAmount());  //运费
        order.setPromotionAmount(calcAmount.getPromotionAmount());
        order.setPromotionInfo(calcAmount.getPromotionInfo());

        if(p.getDeliverType() == null || p.getDeliverType() == 0){
            order.setPayAmount(calcAmount.getPayAmount());
        }
        else if(p.getDeliverType() == 1){
            order.setPayAmount(new BigDecimal(calcAmount.getPayAmountStrZiTi()));
            calcAmount.setPayAmount(new BigDecimal(calcAmount.getPayAmountStrZiTi()));
        }

        if(calcAmount.getPayAmount().compareTo(new BigDecimal(0)) <= 0){
            throw  new Exception("订单支付金额不能为0");
        }
        //转化为订单信息并插入数据库
        order.setMemberId(mobile);
        order.setCreateTime(new Date());
        order.setMemberUsername(owners.get(0).getNickName());  //会员名
        //支付方式：0->未支付；1->支付宝；2->微信
        order.setPayType(0);
        //订单来源：0->PC订单；1->app订单
        order.setSourceType(1);
        //订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
        order.setStatus(0);  //新生成的订单是 代付款状态
        //订单类型：0->正常订单；1->秒杀订单 2 拼团订单
        order.setOrderType(p.getOrderType());
        order.setDeliveryType(p.getDeliverType()+"");

        List<BesUser> besUserList = besUserService.selectList(new EntityWrapper<BesUser>()
                .eq("isautarky",0)
                .eq("status",1)
                .eq("checked",2));

        if(p.getDeliverType() == null || p.getDeliverType() == 0){ //商家配送订单
            //收货人信息：姓名、电话、邮编、地址
//            BesUser bu = besUserService.selectById(product.getBesId());
            order.setReceiveAddressId(p.getMemberReceiveAddressId());
            order.setReceiverName(address.getName());
            order.setReceiverPhone(address.getPhoneNumber());
            order.setReceiverPostCode(address.getPostCode());
            order.setReceiverProvince(address.getProvince());
            order.setReceiverCity(address.getCity());
            order.setReceiverRegion(address.getRegion());
            order.setReceiverDetailAddress(address.getDetailAddress());
            BesUser curUser = null;
            for(BesUser bu:besUserList){
                if(bu.getBesId().longValue() == product.getBesId() && bu.getIsautarky().equals("0")){
                    curUser = bu;
                }
            }
            if(curUser != null){ //直营
                BesUser bures = calcNearestBes(address.getLon(),address.getLat(),besUserList);
                if(bures != null){
                    order.setBesId(bures.getBesId()); //分配订单给 直营店铺
                }
            }

        }else{
            //设置自提点
            long mention_besid = p.getMentionBesId();
            order.setBesId(mention_besid); //订单分配
            BesUser bu = besUserService.selectById(mention_besid);
            System.out.println("提货时间："+p.getMentionTime());
            if(StringUtils.isEmpty(p.getMentionTime())){
                throw new Exception("请选择提货时间");
            }
//            order.setReceiveAddressId(null);
            order.setReceiverProvince(bu.getProvince());
            order.setReceiverCity(bu.getCity());
            order.setReceiverRegion(bu.getArea());
            order.setReceiverDetailAddress(bu.getDetailaddr());

            order.setReceiverName(p.getReciveName());
            order.setReceiverPhone(p.getReceivePhone());
            order.setMentionTime(p.getMentionTime());
        }

        //0->未确认；1->已确认
        order.setConfirmStatus(1);
        //订单删除状态
        order.setDeleteStatus(0);
        order.setGroupId(p.getGroupId());
        order.setIsPrime(p.getIsPrime());
        //计算赠送积分
        order.setOrderSn(generateOrderSn(order));

        //记录订单头部
        String order_th = generateOrderTh();
        order.setOrderTh(order_th);
        // TODO: 2018/9/3 bill_*, delivery_*
        //使用余额支付  产生消费记录
        orderMapper.insert(order);

        orderItem.setOrderId(order.getId());
        orderItem.setOrderSn(order.getOrderSn());
        List<OmsOrderItem> listitem = new ArrayList<>();
        listitem.add(orderItem);
        orderItemDao.insertList(listitem);

        GenOrderResult geo = new GenOrderResult();
        geo.setOrder(order);
        geo.setItemlist(listitem);
        List<GenOrderResult> lsit= new ArrayList<>();
        lsit.add(geo);
        CartBussnesOrderRes res = new CartBussnesOrderRes();
        res.setOrderlist(lsit);
        res.setCalcTotal(calcAmount);
        res.setOrderTh(order_th);
        return CommonResult.success(res);
    }

    //根据经纬度 计算出最近的一个自营店
    BesUser calcNearestBes(String lon,String lat,List<BesUser> users){
        if(StringUtils.isEmpty(lon) || StringUtils.isEmpty(lat)){
            return null;
        }
        if(users == null || users.size() == 0){
            return null;
        }
        Double distemp = new Double(0);
        BesUser bu = null;
        if(users!= null && users.size() > 0){
            for(BesUser user:users){
                if(StringUtils.isEmpty(user.getLon()) || StringUtils.isEmpty(user.getLat())){
                    continue;
                }
                double dis= getDistance(Double.parseDouble(lat),Double.parseDouble(lon),Double.parseDouble(user.getLat())
                        ,Double.parseDouble(user.getLon()));
                if(distemp < dis){
                    distemp = dis;
                    bu = user;
                }
            }
        }
       return bu;
    }


    private static double EARTH_RADIUS = 6378.137;

    /**
     * 角度弧度计算公式 rad:(). <br/>
     * 360度=2π π=Math.PI
     * x度 = x*π/360 弧度
     * @param degree
     * @since JDK 1.6
     */
    private static double getRadian(double degree) {
        return degree * Math.PI / 180.0;
    }


    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = getRadian(lat1);
        double radLat2 = getRadian(lat2);
        double a = radLat1 - radLat2;// 两点纬度差
        double b = getRadian(lng1) - getRadian(lng2);// 两点的经度差
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1)
                * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        return s * 1000;
    }

//    @Override
//    @Transactional
//    public CommonResult paySuccess(Long orderId) {
//        OmsOrder order = new OmsOrder();
//        order.setId(orderId);
//        order.setStatus(1);
//        order.setPaymentTime(new Date());
//        orderMapper.updateByPrimaryKeySelective(order);
//        //恢复所有下单商品的锁定库存，扣减真实库存
//        OmsOrderDetail orderDetail = portalOrderDao.getDetail(orderId);
//        int count = portalOrderDao.updateSkuStock(orderDetail.getOrderItemList());
//        return CommonResult.success(count, "支付成功");
//    }


    @Override
    public ConfirmOrderResult.CalcAmount changeQuantity(OmsOrderCreatePrepare p, String mobile) throws Exception {
        Long pruductid = p.getProductId();
        int orderType = p.getOrderType();
        PmsProduct product = pmsProductMapper.selectByPrimaryKey(pruductid);
        PmsSkuStock sku = null;
        if (!StringUtils.isEmpty(p.getSkuId())) {
            sku = pmsSkuStockMapper.selectById(p.getSkuId());
        }
        //查询改变数量的限制
        EntityWrapper ew = new EntityWrapper();
        ew.eq("product_id",pruductid);
        List<PmsTactics> list = pmsTacticsDao.selectList(ew);
        if(list != null && list.size() > 0){
            Integer limit = Integer.parseInt(list.get(0).getRemark() == null?"100":list.get(0).getRemark());
            if(p.getQuantity() > limit){
                throw new Exception("此商品每人限购"+limit+"份");
            }
        }
        //查询此商家的运费设置

        /**********************************限购查询开始*************************************/
        if(p.getOrderType() == 2){
            EntityWrapper en = new EntityWrapper();
            en.eq("product_id", product.getId());
            en.ge("end_time", new Date()); //还没结束的拼团活动
            List<SmsGroupBuyProduct> listGroupButPro = smsGroupBuyProductService.selectList(en);
            if (listGroupButPro != null && listGroupButPro.size() > 0) {

            }else{
                throw new Exception("拼团活动已结束");
            }
        }
        Long feightTempId = product.getFeightTemplateId();
        if(sku == null){
            if(p.getQuantity() > product.getStock() - product.getLockStock()){
                 throw new Exception(product.getName()+"库存不足");
            }
        }else{
            if(sku != null){
                if(p.getQuantity() > sku.getStock() - sku.getLockStock()){
                    throw new Exception(product.getName()+"库存不足");
                }
            }
        }
//        PmsFeightTemplate pft = pmsFeightTemplateDao.selectById(feightTempId);
        ConfirmOrderResult.CalcAmount calc = calcProductAmount(product, p.getQuantity(),
                null, orderType, sku);

        return calc;
    }

    //锁定库存
    public void doLockStock(List<OmsCartItem> cartList){
        List<OmsCartItem> prolist = new ArrayList<>();
        List<OmsCartItem> skulist = new ArrayList<>();
        for(OmsCartItem o:cartList){
            //锁定库存
            if(o.getProductSkuId()!=null)
            {
                skulist.add(o);
            }else{
                prolist.add(o);
            }
        }
        if(skulist.size() > 0){
            pmsProductMapper.updatebatchsetSkuStock(skulist);
        }
        if(prolist .size() > 0 ){
            pmsProductMapper.updatebatchsetProStock(prolist);
        }
    }

    //取消超时订单
//    @Override
//    @Transactional
//    public CommonResult cancelTimeOutOrder() {
//        Integer minite = 60 * 24;
//        List<OmsOrderDetail> timeOutOrders = portalOrderDao.getTimeOutOrders(minite);
//        if (CollectionUtils.isEmpty(timeOutOrders)) {
//            return CommonResult.failed("暂无超时订单");
//        }
//        String mobile = request.getHeader("mobile");
//        //修改订单状态为交易取消
//        List<Long> ids = new ArrayList<>();
//        for (OmsOrderDetail timeOutOrder : timeOutOrders) {
//            ids.add(timeOutOrder.getId());
//        }
//        portalOrderDao.updateOrderStatus(ids, OrderStatus.ORDER_YGB.getIndex()); //4为关闭交易
//        for (OmsOrderDetail timeOutOrder : timeOutOrders) {
//            //返还使用积分
//            if (timeOutOrder.getUseIntegration() != null) {
//                EntityWrapper en = new EntityWrapper();
//                en.eq("mobile", mobile);
//                List<UjOwner> owners = iUjOwnerService.selectList(en);
//                for (UjOwner o : owners) {  //返回积分
//                    o.setIntegration(o.getIntegration() + timeOutOrder.getUseIntegration());
//                }
//                iUjOwnerService.updateBatchById(owners);
//            }
//        }
//        return CommonResult.success(null);
//    }

//    @Override
//    @Transactional
//    public void cancelOrder(Long orderId) {
//        //查询为付款的取消订单
//        OmsOrderExample example = new OmsOrderExample();
//        example.createCriteria().andIdEqualTo(orderId).andStatusEqualTo(0).andDeleteStatusEqualTo(0);
//        List<OmsOrder> cancelOrderList = orderMapper.selectByExample(example);
//        if (CollectionUtils.isEmpty(cancelOrderList)) {
//            return;
//        }
//        String mobile = request.getHeader("mobile");
//        OmsOrder cancelOrder = cancelOrderList.get(0);
//        if (cancelOrder != null) {
//            //修改订单状态为取消
//            cancelOrder.setStatus(4);
//            orderMapper.updateByPrimaryKeySelective(cancelOrder);
//            OmsOrderItemExample orderItemExample = new OmsOrderItemExample();
//            orderItemExample.createCriteria().andOrderIdEqualTo(orderId);
//            List<OmsOrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);
//            //解除订单商品库存锁定
//            if (!CollectionUtils.isEmpty(orderItemList)) {
//                portalOrderDao.releaseSkuStockLock(orderItemList);
//            }
//            //修改优惠券使用状态
//            updateCouponStatus(cancelOrder.getCouponId(), cancelOrder.getMemberId(), 0);
//            //返还使用积分
//            if (cancelOrder.getUseIntegration() != null) {
//                EntityWrapper en = new EntityWrapper();
//                en.eq("mobile", mobile);
//                List<UjOwner> owners = iUjOwnerService.selectList(en);
//                for (UjOwner o : owners) {  //返回积分
//                    o.setIntegration(o.getIntegration() + cancelOrder.getUseIntegration());
//                }
//                iUjOwnerService.updateBatchById(owners);
//            }
//        }
//    }


    //获取订单超时时间
//    @Override
//    public void sendDelayMessageCancelOrder(Long orderId) {
        //获取订单超时时间
//        OmsOrderSetting orderSetting = orderSettingMapper.selectByPrimaryKey(1L);
//        long delayTimes = orderSetting.getNormalOrderOvertime() * 60 * 1000;
        //发送延迟消息
//        cancelOrderSender.sendMessage(orderId, delayTimes);
//    }

    /**
     * 生成18位订单编号:8位日期+2位平台号码+2位支付方式+6位以上自增id
     */
    private String generateOrderSn(OmsOrder order) {
        StringBuilder sb = new StringBuilder();
        String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
//        String key = REDIS_KEY_PREFIX_ORDER_ID + date;
//        Long increment = redisService.increment(key, 1);
        int random =(int) ((Math.random()+1)*100000);
        sb.append("UJ");
        sb.append(date);
        sb.append(String.format("%02d", order.getSourceType()));
        sb.append(String.format("%02d", order.getPayType()));
        sb.append(random);
        return sb.toString();
    }

    //订单头信息
    private String generateOrderTh() {
        StringBuilder sb = new StringBuilder();
        String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int random =(int) ((Math.random()+1)*1000000);
        sb.append("TH");
        sb.append(date);
        sb.append(random);
        return sb.toString();
    }

    public static void main(String[] args) {


        for(int a = 0 ;a< 300;a++)
        {
            int random=(int) ((Math.random()+1)*100000);
            System.out.println(random);
        }

    }

    /**
     * 删除下单商品的购物车信息
     */
    private void deleteCartItemList(List<OmsCartItem> cartPromotionItemList, String mobile) throws  Exception {
        List<Long> ids = new ArrayList<>();
        for (OmsCartItem o : cartPromotionItemList) {
            ids.add(o.getId());
        }
        cartItemService.deleteCartItems(mobile, ids);
    }

    /**
     * 计算该订单赠送的成长值
     */
    private Integer calcGiftGrowth(List<OmsOrderItem> orderItemList) {
        Integer sum = 0;
        for (OmsOrderItem orderItem : orderItemList) {
            sum = sum + orderItem.getGiftGrowth() * orderItem.getProductQuantity();
        }
        return sum;
    }

    /**
     * 计算该订单赠送的积分
     */
    private Integer calcGifIntegration(List<OmsOrderItem> orderItemList) {
        int sum = 0;
        for (OmsOrderItem orderItem : orderItemList) {
            sum += orderItem.getGiftIntegration() * orderItem.getProductQuantity();
        }
        return sum;
    }

    /**
     * 将优惠券信息更改为指定状态
     *
     * @param couponId  优惠券id
     * @param memberId  会员id
     * @param useStatus 0->未使用；1->已使用
     */
    private void updateCouponStatus(Long couponId, String memberId, Integer useStatus) {
        if (couponId == null) return;
        //查询第一张优惠券
        SmsCouponHistoryExample example = new SmsCouponHistoryExample();
        example.createCriteria().andMemberIdEqualTo(memberId)
                .andCouponIdEqualTo(couponId).andUseStatusEqualTo(useStatus == 0 ? 1 : 0);
        List<SmsCouponHistory> couponHistoryList = couponHistoryMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(couponHistoryList)) {
            SmsCouponHistory couponHistory = couponHistoryList.get(0);
            couponHistory.setUseTime(new Date());
            couponHistory.setUseStatus(useStatus);
            couponHistoryMapper.updateByPrimaryKeySelective(couponHistory);
        }
    }

//    private void handleRealAmount(List<OmsOrderItem> orderItemList) {
//        for (OmsOrderItem orderItem : orderItemList) {
//            //原价-促销优惠-优惠券抵扣-积分抵扣
//            BigDecimal realAmount =
//                    orderItem.getProductPrice();
////                    .subtract(orderItem.getPromotionAmount());
////                    .subtract(orderItem.getCouponAmount())
////                    .subtract(orderItem.getIntegrationAmount());
//            orderItem.setRealAmount(realAmount);
//        }
//    }

    /**
     * 获取订单促销信息
     */
    private String getOrderPromotionInfo(List<OmsOrderItem> orderItemList) {
        StringBuilder sb = new StringBuilder();
        for (OmsOrderItem orderItem : orderItemList) {
            sb.append(orderItem.getPromotionName());
            sb.append(",");
        }
        String result = sb.toString();
        if (result.endsWith(",")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    /**
     * 计算订单应付金额
     */
    private BigDecimal calcPayAmount(OmsOrder order) {
        BigDecimal payAmount = order.getTotalAmount()
                .add(order.getFreightAmount());
//                .subtract(order.getPromotionAmount())
//                .subtract(order.getCouponAmount())
//                .subtract(order.getIntegrationAmount());
        return payAmount;
    }

    /**
     * 计算订单优惠券金额
     */
    private BigDecimal calcIntegrationAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal integrationAmount = new BigDecimal(0);
        for (OmsOrderItem orderItem : orderItemList) {
            if (orderItem.getIntegrationAmount() != null) {
                integrationAmount = integrationAmount.add(orderItem.getIntegrationAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
            }
        }
        return integrationAmount;
    }

    /**
     * 计算订单优惠券金额
     */
    private BigDecimal calcCouponAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal couponAmount = new BigDecimal(0);
        for (OmsOrderItem orderItem : orderItemList) {
            if (orderItem.getCouponAmount() != null) {
                couponAmount = couponAmount.add(orderItem.getCouponAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
            }
        }
        return couponAmount;
    }

    /**
     * 计算订单活动优惠
     */
    private BigDecimal calcPromotionAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal promotionAmount = new BigDecimal(0);
        for (OmsOrderItem orderItem : orderItemList) {
            if (orderItem.getPromotionAmount() != null) {
                promotionAmount = promotionAmount.add(orderItem.getPromotionAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
            }
        }
        return promotionAmount;
    }

    /**
     * 获取可用抵扣金额
     *
     * @param useIntegration 使用的数量
     * @param totalAmount    订单总金额
     * @param ujowner        使用的用户
     * @param hasCoupon      是否已经使用优惠券
     */
    private BigDecimal getUseIntegrationAmount(Integer useIntegration, BigDecimal totalAmount, UjOwner ujowner, boolean hasCoupon) {
        BigDecimal zeroAmount = new BigDecimal(0);
        //判断用户是否有这么多积分
        if (useIntegration.compareTo(ujowner.getIntegration()) > 0) {
            return zeroAmount;
        }
        //根据积分使用规则判断是否可用
        //是否可与优惠券共用
        UmsIntegrationConsumeSetting integrationConsumeSetting = integrationConsumeSettingMapper.selectByPrimaryKey(1L);
        if (hasCoupon && integrationConsumeSetting.getCouponStatus().equals(0)) {
            //不可与优惠券共用
            return zeroAmount;
        }
        //是否达到最低使用积分门槛
        if (useIntegration.compareTo(integrationConsumeSetting.getUseUnit()) < 0) {
            return zeroAmount;
        }
        //是否超过订单抵用最高百分比
        BigDecimal integrationAmount = new BigDecimal(useIntegration).divide(new BigDecimal(integrationConsumeSetting.getUseUnit()), 2, RoundingMode.HALF_EVEN);
        BigDecimal maxPercent = new BigDecimal(integrationConsumeSetting.getMaxPercentPerOrder()).divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN);
        if (integrationAmount.compareTo(totalAmount.multiply(maxPercent)) > 0) {
            return zeroAmount;
        }
        return integrationAmount;
    }

    /**
     * 获取该用户可以使用的优惠券
     *
     * @param cartPromotionItemList 购物车优惠列表
     * @param couponId              使用优惠券id
     */
//    private SmsCouponHistoryDetail getUseCoupon(List<CartPromotionItem> cartPromotionItemList, Long couponId) {
//        List<SmsCouponHistoryDetail> couponHistoryDetailList = memberCouponService.listCart(cartPromotionItemList, 1);
//        for (SmsCouponHistoryDetail couponHistoryDetail : couponHistoryDetailList) {
//            if (couponHistoryDetail.getCoupon().getId().equals(couponId)) {
//                return couponHistoryDetail;
//            }
//        }
//        return null;
//    }

    /**
     * 计算总金额
     */
//    private BigDecimal calcTotalAmount(List<OmsOrderItem> orderItemList) {
//        BigDecimal totalAmount = new BigDecimal("0");
//        for (OmsOrderItem item : orderItemList) {
//            totalAmount = totalAmount.add(item.getProductPrice().multiply(new BigDecimal(item.getProductQuantity())));
//        }
//        return totalAmount;
//    }

    /**
     * 锁定下单商品的所有库存
     */
//    private void lockStock(List<CartPromotionItem> cartPromotionItemList) throws Exception {
//        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
//            PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(cartPromotionItem.getProductSkuId());
//            if(skuStock != null){
//                skuStock.setLockStock(skuStock.getLockStock() + cartPromotionItem.getQuantity());
//                skuStockMapper.updateByPrimaryKeySelective(skuStock);
//            }
//        }
//    }

    /****
     * @return true 可以下单
     * @return  false 库存不足
     * */
    //库存处理
    private boolean calcStock(PmsProduct pro,PmsSkuStock sku,int qutity){
        if(sku == null){   //商品锁库存
            int stock = pro.getStock();
            int lockstock = pro.getLockStock();
            if(qutity > stock - lockstock){
                //库存不足不做处理，直接返回
                return false;
            }else{ //库存充足
                lockstock+=qutity;
                PmsProduct p = new PmsProduct();
                p.setId(pro.getId());
                p.setLockStock(lockstock);
                pmsProductMapper.updateById(p);
                return true;
            }
        }
        else{
            int stock = sku.getStock();
            int lockstock = sku.getLockStock();
            if(qutity > stock - lockstock){
                return false;
            }
            else{
                lockstock += qutity;
                PmsSkuStock s = new PmsSkuStock();
                s.setId(sku.getId());
                s.setLockStock(lockstock);
                pmsSkuStockMapper.updateById(s);
                return true;
            }
        }
    }

    /**
     *    计算 商品 或者sku库存是否充足
     *    返回true 是充足 false不足
     *    购物车下单使用
     * @param cartItemList
     * @return
     */
    private OmsCartItem checkStock(List<OmsCartItem> cartItemList) {
        if(cartItemList== null){
            return null;
        }
        for(OmsCartItem o:cartItemList){
            if(o.getProductSkuId() != null){
                Map<String,Object> map = new HashMap<>();
                map.put("ids",o.getProductSkuId());
                map.put("quantity",o.getQuantity());
                int a = pmsProductMapper.calcStockSkunotenough(map);
                if(a > 0){
                    return o;
                }
            }
            else{
                Map<String,Object> map = new HashMap<>();
                map.put("ids",o.getProductId());
                map.put("quantity",o.getQuantity());
                int a = pmsProductMapper.calcStockPronotenough(map);
                if(a > 0){
                    return o;
                }
            }
        }
        return null;
    }


    private boolean hasStock1(List<OmsCartItem> cartPromotionItemList) {
        List<Long> listid = new ArrayList<>();
        for(OmsCartItem o:cartPromotionItemList){
            listid.add(o.getProductId());
        }
        List<PmsProduct> list  = pmsProductMapper.selectBatchIds(listid);
        for(OmsCartItem o:cartPromotionItemList){
            if(ObjectUtils.isEmpty(o.getProductSkuId())){ //sku为空
                for(PmsProduct p:list){
                    if ( p.getId() == o.getProductId() && (p.getStock() - o.getQuantity() <= 0)) {
                        return false;
                    }
                }
            }
            else{ //
                Long skuid  = o.getProductSkuId();
                PmsSkuStock sku  = pmsSkuStockMapper.selectById(skuid);
                if(!ObjectUtils.isEmpty(sku)){
                    if(sku.getStock() - o.getQuantity() <= 0){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Autowired
    private BesReduceService besReduceService;


    /**
     * 计算购物车中 商品价格 -包含满减信息,积分信息
     */
    private ConfirmOrderResult calcCart(List<CartItemBesGroup> besItemlist,
                                                         ConfirmOrderResult result) throws Exception  //,List<BesUser> besUserList
    {
        ConfirmOrderResult result1 = new ConfirmOrderResult();
        ConfirmOrderResult.CalcAmount calcAmount = new ConfirmOrderResult.CalcAmount();
        result1.setCalcAmount(calcAmount);
        if(besItemlist ==null || besItemlist.size() <= 0){
            return  result1;
        }
        //计算运费
//        calcFeightAmount(besItemlist, calcAmount);
        //查询当前商家的满减活动
//        Long besId = besItemlist.get(0).getBesId();

        BigDecimal totalAmount = new BigDecimal("0");
        BigDecimal promotionAmount = new BigDecimal("0");
        for (CartItemBesGroup cartPromotionItem : besItemlist) {
            for(OmsCartItem o:cartPromotionItem.getItemlist()){
                totalAmount = totalAmount.add(
                        o.getPrice().multiply(new BigDecimal(o.getQuantity())));
            }
        }

        int  totaljifen = 0;
        String  promotionInfo = "无优惠信息";
        for(CartItemBesGroup cartPromotionItem : besItemlist){
            BigDecimal tempamount = new BigDecimal("0");
//            BigDecimal TEMPReduce = new BigDecimal(0);
            Long besIdfor = cartPromotionItem.getBesId();
            //查询商家满减
            List<BesReduce> rlist = besReduceService.selectList(new EntityWrapper<BesReduce>()
                    .eq("bes_id",besIdfor)
                    .eq("status",0)
                    .orderBy("reduce_amount",false)); //降序排列 去满减金额最多的一个  只能参与一次活动
//            String  promotionInfo = "无优惠信息";
            boolean flag = false;//是否计算过满减
            //计算总额
            for(OmsCartItem o:cartPromotionItem.getItemlist()){
                //计算单个商品的付款金额
                tempamount = tempamount.add(
                        o.getPrice().multiply(new BigDecimal(o.getQuantity())));
                if(rlist!= null && rlist.size() > 0 ){
                    for(BesReduce r:rlist){
                        if(r.getLimitAmount().compareTo(tempamount)<= 0){  //符合满减条件
                            if(!flag){ //同一店铺 计算一次满减
                                promotionAmount = promotionAmount.add(r.getReduceAmount());
                                promotionInfo = "店铺满"+ r.getLimitAmount() +"减"+promotionAmount;
                                flag = true;
                                break;
                            }
                        }
                    }
                }
                tempamount  = tempamount.subtract(promotionAmount);
                /******************计算积分******************/
                //@TODO 积分处理
                BigDecimal SysRate = new BigDecimal(CONSTANT.SYS_CREDIT_PERCENT);
                BigDecimal rate = o.getGiftPoint();
                if(rate == null){ rate = new BigDecimal("0");}
                int jifen = (tempamount.multiply(SysRate).multiply(rate).multiply(new BigDecimal(100))).intValue();
                totaljifen += jifen;
            }
            cartPromotionItem.setBesReduceInfo(promotionInfo);
//            totaljifen.add();
        }
        if(result != null){
            result.setBesItemlist(besItemlist);  //积分信息
        }
        //总计信息
        BigDecimal feightA= calcAmount.getFreightAmount();
        calcAmount.setTotalAmount(totalAmount.add(feightA));
        calcAmount.setPromotionAmount(promotionAmount);
        BigDecimal cv = totalAmount.add(feightA).subtract(promotionAmount);
        calcAmount.setPayAmount(cv);
        calcAmount.setPromotionInfo(promotionInfo);
        calcAmount.setPayAmountStr(cv==null?"0.0":cv.toString());
        logger.info("1739 - 购物车 确认订单 积分数额" + totaljifen);
        calcAmount.setCredit(totaljifen);
        return result1;
    }


    /**
     * 计算购物车中 商品价格 -包含满减信息,积分信息
     */
    private ConfirmOrderResult.CalcAmount calcCartAmount(List<CartItemBesGroup> besItemlist,
                                                         ConfirmOrderResult result) throws Exception  //,List<BesUser> besUserList
    {
        ConfirmOrderResult.CalcAmount calcAmount = new ConfirmOrderResult.CalcAmount();
        if(besItemlist ==null || besItemlist.size() <= 0){
           return  calcAmount;
        }
        //计算运费
        UserCartTotalFeight uct = calcFeight(besItemlist, calcAmount);

        calcAmount.setUserCartTotalFeight(uct);

        //查询当前商家的满减活动
        BigDecimal totalAmount = new BigDecimal("0");
        BigDecimal promotionAmount = new BigDecimal("0");
        for (CartItemBesGroup cartPromotionItem : besItemlist) {
            for(OmsCartItem o:cartPromotionItem.getItemlist()){
                totalAmount = totalAmount.add(
                        o.getPrice().multiply(new BigDecimal(o.getQuantity())));
            }
        }

        int  totaljifen = 0;
        String  promotionInfo = "无优惠信息";
        for(CartItemBesGroup cartPromotionItem : besItemlist){
            BigDecimal tempamount = new BigDecimal("0");
//            BigDecimal TEMPReduce = new BigDecimal(0);
            Long besIdfor = cartPromotionItem.getBesId();
            //查询商家满减
            List<BesReduce> rlist = besReduceService.selectList(new EntityWrapper<BesReduce>()
                    .eq("bes_id",besIdfor)
                    .eq("status",0)
                    .orderBy("reduce_amount",false)); //降序排列 去满减金额最多的一个  只能参与一次活动
//            String  promotionInfo = "无优惠信息";
            boolean flag = false;//是否计算过满减
            //计算总额
            for(OmsCartItem o:cartPromotionItem.getItemlist()){
                //计算单个商品的付款金额
                tempamount = tempamount.add(
                        o.getPrice().multiply(new BigDecimal(o.getQuantity())));
                if(rlist!= null && rlist.size() > 0 ){
                    for(BesReduce r:rlist){
                        if(r.getLimitAmount().compareTo(tempamount)<= 0){  //符合满减条件
                            if(!flag){ //同一店铺 计算一次满减
                                promotionAmount = promotionAmount.add(r.getReduceAmount());
                                promotionInfo = "店铺满"+ r.getLimitAmount() +"减"+promotionAmount;
                                flag = true;
                                break;
                            }
                        }
                    }
                }
                tempamount  = tempamount.subtract(promotionAmount);
                /******************计算积分******************/
                //@TODO 积分处理
                BigDecimal SysRate = new BigDecimal(CONSTANT.SYS_CREDIT_PERCENT);
                BigDecimal rate = o.getGiftPoint();
                if(rate == null){ rate = new BigDecimal("0");}
                int jifen = (tempamount.multiply(SysRate).multiply(rate).multiply(new BigDecimal(100))).intValue();
                totaljifen += jifen;
            }
            cartPromotionItem.setBesReduceInfo(promotionInfo);
//            totaljifen.add();
        }
        if(result != null){
            result.setBesItemlist(besItemlist);  //积分信息
        }

        BigDecimal feightA = uct.getTotalFeight();
        calcAmount.setTotalAmount(totalAmount.add(feightA));
        calcAmount.setPromotionAmount(promotionAmount);
        calcAmount.setFreightAmount(feightA);
        BigDecimal cv = totalAmount.add(feightA).subtract(promotionAmount);
        calcAmount.setPayAmount(cv);
        calcAmount.setPayAmountStrZiTi(""+ cv.subtract(feightA));
        calcAmount.setPromotionInfo(promotionInfo);
        calcAmount.setPayAmountStr(cv==null?"0.0":cv.toString());
        logger.info("1739 - 购物车 确认订单 积分数额" + totaljifen);
        calcAmount.setCredit(totaljifen);
        return calcAmount;
    }

    @Autowired
    BesFeightPlateDao besFeightPlateDao;

    /***
     * 订单相关
     * 运费计算
     * */

    public UserCartTotalFeight calcFeight(List<CartItemBesGroup> besItemlist, ConfirmOrderResult.CalcAmount calc) {
        UserCartTotalFeight uct = new UserCartTotalFeight();
        List<UserCartFeightCalc> calclist= new ArrayList<>();
//        List<Map<Long,Object>> maplist = new ArrayList<Map<Long,Object>>();
        BigDecimal totalfeight  = new BigDecimal("0");
        for(CartItemBesGroup group:besItemlist){
            List<BesFeightPlate> listf = besFeightPlateDao.selectList(new EntityWrapper<BesFeightPlate>()
                    .eq("bes_id",group.getBesId())
                    .eq("is_available",0));
            BigDecimal amount = new BigDecimal(0);
            UserCartFeightCalc calc1 = new UserCartFeightCalc();
            if(listf != null && listf.size() > 0){
                for(OmsCartItem item: group.getItemlist()){
                    amount = amount.add(item.getPrice().multiply(new BigDecimal(""+item.getQuantity())));
                }
                //满足免费配送
                if(amount.compareTo(listf.get(0).getStartNoFee()) < 0){
                    calc1.setBesId(group.getBesId());
                    calc1.setFeightAmount(listf.get(0).getFee());
                    calc1.setFeightInfo("配送费"+listf.get(0).getFee()+"元,满"+listf.get(0).getStartNoFee()+"元免配送费");
                    totalfeight =  totalfeight.add(listf.get(0).getFee());
                }else{
                    calc1.setBesId(group.getBesId());
                    calc1.setFeightAmount(new BigDecimal("0"));
                    calc1.setFeightInfo("免配送费");
                }
            }else{
                calc1.setBesId(group.getBesId());
                calc1.setFeightAmount(new BigDecimal("0"));
                calc1.setFeightInfo("免配送费");
            }
            calclist.add(calc1);
        }
        uct.setFlist(calclist);
        uct.setTotalFeight(totalfeight);
        return uct;
    }

    /***
     * 订单相关
     * 运费计算
     * */
//    public void calcFeightAmount(List<CartItemBesGroup> besItemlist, ConfirmOrderResult.CalcAmount calc) {
//        BigDecimal feightAmount = new BigDecimal(0);
//        for (CartItemBesGroup b : besItemlist) {
//            BigDecimal besfeight = new BigDecimal(0);
//            //每个商家的运费
//            for (OmsCartItem c : b.getItemlist()) {
//                BigDecimal spyf = new BigDecimal(0);
//                if (c.getProductId() != null) {
//                    PmsProduct pmsProduct = pmsProductMapper.selectById(c.getProductId());
//                    if (pmsProduct.getFeightTemplateId() == null) {
//                        PmsFeightTemplate pft = pmsFeightTemplateDao.selectById(pmsProduct.getFeightTemplateId());
//                        if (pft != null) {
//                            if (pft.getFeightType() == 0) { //包邮
//                                spyf = new BigDecimal(0).setScale(2);
//                            } else if (pft.getFeightType() == 1) { //有运费
//                                if (pft.getChargeType() == 0) { //按重量
//                                    // weight
//                                    spyf = new BigDecimal(0).setScale(2);
//                                } else if (pft.getChargeType() == 1) { //按件数
//                                    int j = pft.getFirstWeight().intValue(); //首费
//                                    if (c.getQuantity() >= pft.getOriginalAmount().intValue()) {  //包邮
//                                        spyf = new BigDecimal(0).setScale(2);
//                                    } else if (c.getQuantity() <= j) { //计算首费
//                                        spyf = pft.getFirstFee().setScale(2);
//                                    } else if (c.getQuantity() > j) {
//                                        BigDecimal feightDecimal =
//                                                pft.getFirstFee().add(
//                                                        pft.getContinmeFee().multiply(new BigDecimal(c.getQuantity() - j)));
//                                        spyf = feightDecimal.setScale(2);
//                                    }
//                                }
//                            }
//                        } else {
//                            spyf = new BigDecimal(0);
//                        }
//                    }
//                }
////                //计算最大的商品费用
//                if (spyf.compareTo(besfeight) == 1) {
//                    besfeight = spyf;
//                }
//            }
//
//            //商家运费信息
//            b.setBesFeightAmount(besfeight);
//            if(besfeight.compareTo(new BigDecimal(0)) == 0){
//                b.setFeightInfo("包邮");
//            }else{
//                b.setFeightInfo("运费:"+besfeight+"元");
//            }
//            feightAmount.add(besfeight);
//        }
//        calc.setFreightAmount(feightAmount);
//        calc.setFeightInfo("运费:"+feightAmount+"元");
//    }


    /**
     *
     * 公共方法，检查商品是否支持当前用户下单购买
     * @return true 可以够买 false 不能
     */
    public CommonResult canMemberPchase(Integer quantity,String mobile,Long productId){
        EntityWrapper ew = new EntityWrapper();
        ew.eq("product_id",productId);
        List<PmsTactics> list = pmsTacticsDao.selectList(ew);
            if(list != null && list.size() > 0){
            //查询用户的订单，是否是新用户
            Map<String,Object> map = new HashMap<>();
            map.put("mobile",mobile);
            int count = orderMapper.countMemberOrders(map);
            if(count <= 0){
                //检查限购数量
                Integer limit = Integer.parseInt(list.get(0).getRemark() == null?"100":list.get(0).getRemark());
                if(quantity > limit){
                    return CommonResult.failed("抱歉，每人限购1份");
                }
                return CommonResult.success(null);
            }
            return CommonResult.failed("抱歉，此商品暂时只对新用户开放购买");
        }else{
            return CommonResult.success(null);
        }
    }




    /**
     * 获取与优惠券有关系的下单商品
     *
     * @param couponHistoryDetail 优惠券详情
     * @param orderItemList       下单商品
     * @param type                使用关系类型：0->相关分类；1->指定商品
     */
//    private List<OmsOrderItem> getCouponOrderItemByRelation(SmsCouponHistoryDetail couponHistoryDetail, List<OmsOrderItem> orderItemList, int type) {
//        List<OmsOrderItem> result = new ArrayList<>();
//        if (type == 0) {
//            List<Long> categoryIdList = new ArrayList<>();
//            for (SmsCouponProductCategoryRelation productCategoryRelation : couponHistoryDetail.getCategoryRelationList()) {
//                categoryIdList.add(productCategoryRelation.getProductCategoryId());
//            }
//            for (OmsOrderItem orderItem : orderItemList) {
//                if (categoryIdList.contains(orderItem.getProductCategoryId())) {
//                    result.add(orderItem);
//                } else {
//                    orderItem.setCouponAmount(new BigDecimal(0));
//                }
//            }
//        } else if (type == 1) {
//            List<Long> productIdList = new ArrayList<>();
//            for (SmsCouponProductRelation productRelation : couponHistoryDetail.getProductRelationList()) {
//                productIdList.add(productRelation.getProductId());
//            }
//            for (OmsOrderItem orderItem : orderItemList) {
//                if (productIdList.contains(orderItem.getProductId())) {
//                    result.add(orderItem);
//                } else {
//                    orderItem.setCouponAmount(new BigDecimal(0));
//                }
//            }
//        }
//        return result;
//    }

    /**
     * 判断下单商品是否都有库存
     */
//    private boolean hasStock(List<CartPromotionItem> cartPromotionItemList) {
//        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
//            if (cartPromotionItem.getRealStock() == null || cartPromotionItem.getRealStock() <= 0) {
//                return false;
//            }
//        }
//        return true;
//    }

    /**
     * 对每个下单商品进行优惠券金额分摊的计算
     *
     * @param orderItemList 可用优惠券的下单商品商品
     */
//    private void calcPerCouponAmount(List<OmsOrderItem> orderItemList, SmsCoupon coupon) {
//        BigDecimal totalAmount = calcTotalAmount(orderItemList);
//        for (OmsOrderItem orderItem : orderItemList) {
//            //(商品价格/可用商品总价)*优惠券面额
//            BigDecimal couponAmount = orderItem.getProductPrice().divide(totalAmount, 3, RoundingMode.HALF_EVEN).multiply(coupon.getAmount());
//            orderItem.setCouponAmount(couponAmount);
//        }
//    }


}
