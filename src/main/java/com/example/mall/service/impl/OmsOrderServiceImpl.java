package com.example.mall.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.example.mall.common.CommonResult;
import com.example.mall.controller.AppGroupBuyAssembleController;
import com.example.mall.dao.OmsOrderDao;
import com.example.mall.dao.OmsOrderItemDao;
import com.example.mall.dao.OmsOrderOperateHistoryDao;
import com.example.mall.dto.*;
import com.example.mall.emun.OrderStatus;
import com.example.mall.mapper.OmsOrderItemMapper;
import com.example.mall.mapper.OmsOrderMapper;
import com.example.mall.mapper.OmsOrderOperateHistoryMapper;
import com.example.mall.model.*;
import com.example.mall.service.*;
import com.zhihui.uj.management.BaseController.BaseController;
import com.zhihui.uj.management.Merchant.entity.BalanceRequest;
import com.zhihui.uj.management.Merchant.entity.BalanceResponse;
import com.zhihui.uj.management.app.vo.TradeConstant;
import com.zhihui.uj.management.charging.entity.PaymentRecord;
import com.zhihui.uj.management.charging.entity.TMagneticCard;
import com.zhihui.uj.management.charging.service.ITMagneticCardService;
import com.zhihui.uj.management.charging.service.PaymentRecordService;
import com.zhihui.uj.management.common.entity.UjOwner;
import com.zhihui.uj.management.common.service.IUjOwnerService;
import com.zhihui.uj.management.push.huawei.util.CollectionUtils;
import com.zhihui.uj.management.utils.IdUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单管理Service实现类
 * Created by macro on 2018/10/11.
 */
@Service
public class OmsOrderServiceImpl extends BaseController implements OmsOrderService   {
	
	private final static Logger logger = LoggerFactory.getLogger(OmsOrderServiceImpl.class);

	@Autowired
	private OmsOrderMapper orderMapper;
	@Autowired
	private OmsOrderDao orderDao;
	@Autowired
	private OmsOrderOperateHistoryDao orderOperateHistoryDao;
	@Autowired
	private OmsOrderOperateHistoryMapper orderOperateHistoryMapper;

//	@Autowired
//	private PortalOrderDao portalOrderDao;

	@Autowired
	AppGroupBuyAssembleController appGroupBuyAssembleController;

	@Autowired
	OmsOrderItemDao omsOrderItemDao;

	@Autowired
	OmsOrderItemMapper omsOrderItemMapper;

	@Autowired
	IUjOwnerService ujOwnerService;

	@Autowired
	SmsGroupBuyAssembleService smsGroupBuyAssembleService;

	@Autowired
	private ITMagneticCardService tMagneticCardService;

	@Autowired
	ITMagneticCardService itMagneticCardService;

	@Autowired
	BesUserService besUserService;
	@Autowired
	private PaymentRecordService paymentRecordService;

	@Override
	public Page<OmsOrder> list(OmsOrderQueryParam queryParam, Integer limit, Integer page,String mobile) {
		Map<String,Object> map = new HashMap<>();
		map.put("limit",limit);
		map.put("page",(page-1)*limit);
		if(mobile != null){
			map.put("mobile",mobile);
		}
		List<OmsOrder> list =  orderDao.getList(queryParam,map);
		Page<OmsOrder> pageres= new Page<>();
		pageres.setRecords(list);

		int count =orderDao.selectCount(queryParam,map);
		pageres.setTotal(count);
		pageres.setSize(list.size());
		return pageres;
	}

	@Override
	public Page<OmsOrderDetail> listApp(OmsOrderQueryParam queryParam, Integer limit, Integer page,String mobile) {
		Map<String,Object> map = new HashMap<>();
		map.put("limit",limit);
		map.put("page",(page-1)*limit);
		map.put("mobile",mobile);
		List<OmsOrderDetail> list = null;
		//代发货
		list =  orderDao.getAppOrderList(queryParam,map);
//		if(queryParam.getStatus() == 1 && queryParam.getGroupState() == null){
//			list =  orderDao.getAppOrderList(queryParam,map);
//		}else{
//			list =  orderDao.getAppOrderList(queryParam,map);
//		}

		//查询订单内商品总数
		for(OmsOrderDetail o : list){
			int count = 0;
			List<OmsOrderItem> itemlist = orderDao.getItemlist(o.getId());
			o.setOrderItemList(itemlist);
			for(OmsOrderItem oo:itemlist){
				count+= oo.getProductQuantity();
			}
			o.setItemcount(count);
			String cnStatus  = turnCnstatus(o);
			o.setCNStatus(cnStatus);
			BigDecimal bdc = o.getPayAmount();
			o.setPayAmountStr(bdc == null?"0.0":bdc.toString());
		}
		int count =orderDao.selectCount(queryParam,map);
		Page<OmsOrderDetail> pageres= new Page<>();
		pageres.setRecords(list);
		pageres.setTotal(count);
		pageres.setSize(list.size());
		return pageres;
	}

	//转化为中文状态
	public static String turnCnstatus(OmsOrderDetail o){
		String status = null;
		switch (o.getStatus()){
			case 0:
				status = "待付款";
				break;
			case 1:
				if(o.getOrderType()==2){
					if(o.getGroupState() == null){
						o.setGroupState(7);
					}
					if(o.getGroupState() == 7){
						status = "订单已付款,等待成团";
					}else if(o.getGroupState() == 8){
						status = "订单已付款,拼团成功";
					}
					else if(o.getGroupState() == 9){
						status = "订单已付款,拼团失败";
					}
				}else{
					status = "订单已支付,等待发货";
				}
				break;
			case 2:
				status = "已发货";
				if(o.getStageStatus()!= null && o.getStageStatus() == 5){ //到达驿站
					status+= "并到达驿站";
				}else if(o.getStageStatus()!= null && o.getStageStatus() == 6){
					status = "已离开驿站";
				}
				break;
			case 3:
				status = "已完成";
				break;
			case 4:
				status = "订单已取消";
				break;
//			case 5:
//				status = "已发货并到达驿站";
//				break;
//			case 6:
//				status = "已离开驿站";
//				break;
			default:
				break;
		}
		return status;
	}


	/**
	 * 订单余额支付
	 */
	@Override
	@Transactional
	public CommonResult payOrder(PayBodyDto dto) throws  Exception{  //支付密码
		String mobile = dto.getMobile();
		PayRes pr = new PayRes();
		BigDecimal total = new BigDecimal("0");
		String payInfo = "支付成功";
		List<OmsOrderItem> orderitemlist = new ArrayList<>();
		for(Long orderID : dto.getOrderIds()){
			//订单状态校验
			OmsOrder ordersel = orderMapper.selectByPrimaryKey(orderID);
			if(ordersel == null ){
				throw new Exception("无效订单");
			}
			if(ordersel.getStatus()== 1){
				throw new Exception("订单已支付，无需重新支付");
			}
			if(ordersel.getStatus()== 4 || ordersel.getStatus() == 5){
				throw new Exception("订单失效或者已关闭");
			}
			//校验支付密码
			TMagneticCard card = itMagneticCardService.selectOne(
					new EntityWrapper<TMagneticCard>().eq("uid",mobile));
			String pw = card.getPayword();
			if(StringUtils.isBlank(pw) || !card.getPayword().equals(dto.getPayword())){
				throw new Exception("密码错误");
			}
			BalanceRequest request= new BalanceRequest();
			BesUser buser =besUserService.selectById(ordersel.getBesId());
			if(ObjectUtil.isEmpty(buser)){
				throw new Exception("订单无效");
			}
			request.setAmount(Double.parseDouble(""+ordersel.getPayAmount()));
			request.setMerPhone(buser.getPhone());
			request.setMerName(buser.getShopname());
			request.setMsg("购物消费");
			request.setPayer(dto.getMobile());
			request.setPayType("UJ");
			request.setOrderNo(ordersel.getOrderSn());
			request.setCredit(ordersel.getIntegration());
			BalanceResponse br = tMagneticCardService.consumeShoppingMall(request);

			if(!br.getRspCode().equals("000000")){
				throw new Exception(br.getRspMsg());
			}
			// 2 生成消费记录
        	String orderId = ordersel.getOrderSn();
			PaymentRecord paymentRecord = new PaymentRecord();
			paymentRecord.setOrderid(orderId);
			paymentRecord.setOrdertype("2");//消费
			paymentRecord.setPaytype("UJ");//余额支付，U家app余额
			paymentRecord.setPaystatus("1");
			paymentRecord.setUserid(dto.getMobile());
			paymentRecord.setMobile(dto.getMobile());
			paymentRecord.setProducttype(TradeConstant.PRODUCT_TYPE_13);//商城购物
			paymentRecord.setProductname("购物支付");
			paymentRecord.setTotalprice(ordersel.getPayAmount());
			paymentRecord.setOrderstatus("1");//1为交易成功
			paymentRecord.setCreatetime(new Date());
			paymentRecord.setUpdatetime(new Date());
			paymentRecord.setBizNo(orderId);
			paymentRecordService.insertSelective(paymentRecord);
			logger.info("用户"+dto.getMobile()+",商城购物余额支付正式订单已生成，订单号:" + orderId);
			
			// 3 改变订单状态
			OmsOrder order = new OmsOrder();
			order.setId(orderID);
			order.setStatus(1);  //订单状态设置为已支付
			order.setPaymentTime(new Date());
			orderMapper.updateByPrimaryKeySelective(order);
			//恢复所有下单商品的锁定库存，扣减真实库存
//            com.example.mall.domain.OmsOrderDetail orderDetail = portalOrderDao.getDetail(orderID);
//            portalOrderDao.updateSkuStock(orderDetail.getOrderItemList());
			total = total.add(ordersel.getPayAmount());
			//        int state = 1; //已支付 待发货
			//        orderDao.updateState(orderID,state);
			//支付完成后，如果用户是拼团
			int orderType = ordersel.getOrderType();
			EntityWrapper enw = new EntityWrapper();
			enw.eq("order_sn",ordersel.getOrderSn());
			List<OmsOrderItem> item = omsOrderItemDao.selectList(enw);
			orderitemlist.addAll(item);

			//插入操作记录
			OmsOrderOperateHistory history = new OmsOrderOperateHistory();
			history.setOrderId(orderID);
			history.setCreateTime(new Date());
			history.setOperateMan(ordersel.getMemberUsername()); //付款人
			history.setOrderStatus(1);
			history.setNote("支付订单");
			int r = orderOperateHistoryMapper.insert(history);

			if(orderType == 2){  //拼团订单
				pr.setOmsOrderItem(item.get(0));
				if(StringUtils.isBlank(ordersel.getGroupId())){
					//发起拼团
					SmsGroupBuyAssemble group = new SmsGroupBuyAssemble();
					group.setProductId(item.get(0).getProductId());
					group.setSkuid(item.get(0).getProductSkuId());
					String groupid = IdUtils.createID();
					group.setGroupId(groupid);
					pr.setGroupId(groupid);  //返回结果设置 GROUPID
					//                group.setGroupbuyPromotionId(ordersel.getGroupbuyId()); //活动ID
					createGroup(group,item.get(0),item.get(0).getProductId(),mobile,item.get(0).getProductQuantity());
					payInfo = "发起拼团成功";

					//订单发起后 把订单拼团的ID更新上去
					Map<String,Object> map = new HashMap<>();
					map.put("orderId",orderID);
					map.put("groupId",groupid);
					orderMapper.updateGroupInfo(map);
				}
				else{
					String groupId= ordersel.getGroupId();
					pr.setGroupId(groupId); //返回结果设置GROUPID
					UjOwner owner= ujOwnerService.selectOne(new EntityWrapper<UjOwner>().eq("mobile",mobile));
					//参与拼团 ？可否重复参与拼团
					SmsGroupBuyAssembleUser user= new SmsGroupBuyAssembleUser();
					user.setGroupId(groupId);
					user.setUsername(owner.getNickName());
					user.setJoinTime(new Date());
					user.setUserico(owner.getHeadUrl());
					user.setType(1);
					user.setUserId(owner.getMobile());
					join(user,mobile);
					//更新拼团的信息
					SmsGroupBuyAssemble group = smsGroupBuyAssembleService.selectById(groupId);
					int nowper= group.getNowPerson();
					nowper ++;
					group.setNowPerson(nowper);
					if(nowper >= group.getGroupNeed()){
						group.setIsFull(0);
						group.setOverTime(new Date());
						Map<String,Object> mapp = new HashMap<>();
						mapp.put("groupId",groupId);
						mapp.put("gState",8); //拼团完成
						orderMapper.updateGroupStateBatch(mapp);
					}
					else{
						group.setIsFull(0);
						group.setOverTime(new Date());
						Map<String,Object> mapp = new HashMap<>();
						mapp.put("groupId",groupId);
						mapp.put("gState",7); //拼团完成
						orderMapper.updateGroupStateBatch(mapp);
					}
					smsGroupBuyAssembleService.updateById(group);
					payInfo = "参团成功";
//                    return CommonResult.success(ordersel);
					EntityWrapper en = new EntityWrapper();
					en.eq("product_id",item.get(0).getProductId());
//                    en.ge("end_time",new Date());  //还没结束的活动
					SmsGroupBuyProduct gy = smsGroupBuyProductService.selectOne(en);
					//已拼数量更新
					int a = gy.getPromotionCountAl()+ item.get(0).getProductQuantity();
					gy.setPromotionCountAl(a);
					if(gy!=null){
						smsGroupBuyProductService.updateById(gy);
					}
				}
			}
		}

		/**
		 * 更新成功后，释放锁定库存
		 * */
		updateLockstockStock(orderitemlist);

		logger.info("支付完成,释放库存完成**");
		pr.setPayAmount(total);
		pr.setPayTime(new Date());
		pr.setPayinfo(payInfo);
		return CommonResult.success(pr);
	}

	//增加销量  解开锁定库存 减少库存
	public void updateLockstockStock(List<OmsOrderItem> list){
		if(list == null || list.size()  == 0){
			return;
		}
		List<OmsOrderItem> pros = new ArrayList<>();
		List<OmsOrderItem> skus = new ArrayList<>();
		if(list != null && list.size() > 0){
			for(OmsOrderItem o:list){
				if (o.getProductSkuId() != null) {
					skus.add(o);
				}
				else{
					pros.add(o);
				}
			}
		}
		// 根据不同的类型释放锁定库存
		if(pros!= null && pros.size() >0 ){
			orderMapper.releaseLockStockPro(pros);
		}
		if(skus != null && skus.size() >0 ){
			orderMapper.releaseLockStockSku(skus);
		}
	}


	//回调方法 更新订单状态
	@Override
	public void  updateOmsOrderafterNotify(OmsOrderAfterNotifyBean bean){

		logger.info("支付回调后续 操作----------------" );
		String orderTh = bean.getOrderTh();
		logger.info("支付回调后续 操作 orderTh----------------" + orderTh );
		String orderSn = bean.getOrderSn();
		logger.info("支付回调后续 操作 orderSn----------------"  + orderSn);
		List<OmsOrder> list = null;
		String mobile = bean.getMobile();
		if(StringUtils.isNotBlank(orderTh)){
			OmsOrderExample example = new OmsOrderExample();
			example.createCriteria().andOrderThEqualTo(orderTh);
			list = orderMapper.selectByExample(example);
		}
		else if(StringUtils.isNotBlank(orderSn)){
			OmsOrderExample example = new OmsOrderExample();
			example.createCriteria().andOrderSnEqualTo(orderSn);
			list = orderMapper.selectByExample(example);
		}
//		BigDecimal total =new BigDecimal(0);
		//此处不再验证支付金额和订单总金额是否一致
		logger.info(" 379 + 订单查询" + list.size() + "个");
		if(!CollectionUtils.isEmpty(list)){
			logger.info(" 380 + 订单查询订单有" + list.size() + "个");
			List<OmsOrderItem> orderitemlist = new ArrayList<>();
			for(OmsOrder o : list){
				//订单状态校验
				Long orderID = o.getId();
				OmsOrder ordersel = o;
				// 2 生成消费记录
				// 3 改变订单状态
				OmsOrder order = new OmsOrder();
				order.setId(orderID);
				order.setStatus(1);  //订单状态设置为已支付
				order.setPaymentTime(new Date());
				order.setPayType(bean.getPaytype());
				orderMapper.updateByPrimaryKeySelective(order);
				logger.info(" 394 + 更新订单状态 单号：" + orderID + "个");
				//支付完成后，如果用户是拼团
				int orderType = ordersel.getOrderType();
				EntityWrapper enw = new EntityWrapper();
				enw.eq("order_sn",ordersel.getOrderSn());
				List<OmsOrderItem> item = omsOrderItemDao.selectList(enw);
				orderitemlist.addAll(item);

				//插入操作记录
				OmsOrderOperateHistory history = new OmsOrderOperateHistory();
				history.setOrderId(orderID);
				history.setCreateTime(new Date());
				history.setOperateMan(ordersel.getMemberUsername()); //付款人
				history.setOrderStatus(1);
				history.setNote("支付订单");
				orderOperateHistoryMapper.insert(history);

				if(orderType == 2){  //拼团订单
//					pr.setOmsOrderItem(item.get(0));
					if(StringUtils.isBlank(ordersel.getGroupId())){
						//发起拼团
						SmsGroupBuyAssemble group = new SmsGroupBuyAssemble();
						group.setProductId(item.get(0).getProductId());
						group.setSkuid(item.get(0).getProductSkuId());
						String groupid = IdUtils.createID();
						group.setGroupId(groupid);
//						pr.setGroupId(groupid);  //返回结果设置 GROUPID
						//                group.setGroupbuyPromotionId(ordersel.getGroupbuyId()); //活动ID
						try {
							createGroup(group,item.get(0),item.get(0).getProductId(),mobile,item.get(0).getProductQuantity());
						}
						catch (Exception e){
							logger.info("发起拼团异常 ********");
						}
						//订单发起后 把订单拼团的ID更新上去
						Map<String,Object> map = new HashMap<>();
						map.put("orderId",orderID);
						map.put("groupId",groupid);
						orderMapper.updateGroupInfo(map);
					}
					else{
						String groupId= ordersel.getGroupId();
						UjOwner owner= ujOwnerService.selectOne(new EntityWrapper<UjOwner>().eq("mobile",mobile));
						//参与拼团 ？可否重复参与拼团
						SmsGroupBuyAssembleUser user= new SmsGroupBuyAssembleUser();
						user.setGroupId(groupId);
						user.setUsername(owner.getNickName());
						user.setJoinTime(new Date());
						user.setUserico(owner.getHeadUrl());
						user.setType(1);
						user.setUserId(owner.getMobile());
						try {
							join(user,mobile);
						}
						catch (Exception e){
							logger.info("发起拼团异常 ********");
						}

						//更新拼团的信息
						SmsGroupBuyAssemble group = smsGroupBuyAssembleService.selectById(groupId);
						int nowper= group.getNowPerson();
						nowper ++;
						group.setNowPerson(nowper);
						if(nowper >= group.getGroupNeed()){
							group.setIsFull(0);
							group.setOverTime(new Date());
							Map<String,Object> mapp = new HashMap<>();
							mapp.put("groupId",groupId);
							mapp.put("gState",8); //拼团完成
							orderMapper.updateGroupStateBatch(mapp);

						}
						smsGroupBuyAssembleService.updateById(group);
//						payInfo = "参团成功";
//                    return CommonResult.success(ordersel);
						EntityWrapper en = new EntityWrapper();
						en.eq("product_id",item.get(0).getProductId());
//                    en.ge("end_time",new Date());  //还没结束的活动
						SmsGroupBuyProduct gy = smsGroupBuyProductService.selectOne(en);
						//已拼数量更新
						int a = gy.getPromotionCountAl()+ item.get(0).getProductQuantity();
						gy.setPromotionCountAl(a);
						if(gy!=null){
							smsGroupBuyProductService.updateById(gy);
						}
					}
				}
			}
			/**
			 * 更新成功后，释放锁定库存
			 * */
			updateLockstockStock(orderitemlist);
		}
		else{
			//未查到订单

		}
	}

	@Autowired
	SmsGroupBuyService smsGroupBuyService;

	@Autowired
	SmsGroupBuyProductService smsGroupBuyProductService;

	@Autowired
	PmsSkuStockService pmsSkuStockService;

	@Autowired
	SmsGroupBuyAssembleUserService smsGroupBuyAssembleUserService;

	@Autowired
	IUjOwnerService iUjOwnerService;

	public void join(SmsGroupBuyAssembleUser user,String mobile) throws Exception{
		EntityWrapper en = new EntityWrapper();
		en.eq("mobile",mobile);
		UjOwner owner =iUjOwnerService.selectOne(en);
		user.setUserId(mobile); //获取手机号
		user.setUsername(owner.getNickName());
		user.setType(1); //参团
		user.setUserico(owner.getHeadUrl());
		user.setJoinTime(new Date());
		smsGroupBuyAssembleUserService.insert(user);
	}

	public void createGroup(SmsGroupBuyAssemble group,OmsOrderItem item,Long productId,String mobile,int quantity) throws Exception{
		UjOwner owner1= ujOwnerService.selectOne(new EntityWrapper<UjOwner>().eq("mobile",mobile));
		if(owner1 !=null){
			group.setManPhone(owner1.getNickName());
		}else{
			group.setManPhone(mobile);
		}
		group.setGroupMan(mobile); //发起人
		group.setCreateTime(new Date());
		EntityWrapper en = new EntityWrapper();
		en.eq("product_id",productId); //拼团活动ID
		en.ge("end_time",new Date());  //还没结束的活动
		SmsGroupBuyProduct gy = smsGroupBuyProductService.selectOne(en);
		if(gy==null|| StringUtils.isBlank(gy.getId())){
			throw new Exception("此商品拼团已经结束");
		}
		if(item.getProductSkuId() != null){
			PmsSkuStock sku = pmsSkuStockService.selectById(item.getProductSkuId());
			if(sku!= null){
				group.setGroupPrice(sku.getGroupbuyPrice());
			}
			if(group.getGroupPrice()== null || group.getGroupPrice().compareTo(new BigDecimal(0)) == 0){
				group.setGroupPrice(sku.getPrice());
			}
		}else{
			group.setGroupPrice(gy.getPromotionPrice());
		}
		//拼团的发起时间和结束时间带上时分秒
//		group.setCreateTime(new Date());
		//此时比较从开始的时间24 小时结束 和 拼团活动的结束时间 哪个大就按照哪个结束
		Date end = gy.getEndTime();
		Date end24 = new Date();
		Long endmills = end24.getTime() + 24*60*60*1000;
		end24.setTime(endmills);
		int res = end.compareTo(end24);
		if(res == -1 || res == 0){ //
			group.setEndTime(gy.getEndTime());
		}else if(res == 1){
			group.setEndTime(end24);
		}
		//查询拼团活动商品信息
		group.setGroupNeed(gy.getTeamnum()); //成团人数
		EntityWrapper en2 = new EntityWrapper();
		en2.eq("mobile",mobile);
		UjOwner owner =iUjOwnerService.selectOne(en2);
		group.setIco(owner.getHeadUrl());
		group.setIsFull(1);
		group.setNowPerson(1);
		String maxid = smsGroupBuyAssembleService.selectMaxSid();
		if(StringUtils.isBlank(maxid)){
			maxid = "1000";
		}
		int max = Integer.parseInt(maxid);
		max++;
		group.setSearchKey(""+max);
		smsGroupBuyAssembleService.insert(group);

		//发起
		SmsGroupBuyAssembleUser user =new SmsGroupBuyAssembleUser();
		user.setUserId(mobile);
		user.setType(0); //发起人
		user.setGroupId(group.getGroupId());
		user.setPhoneType(group.getPhoneType());
		user.setUserico(owner.getHeadUrl());
		user.setUsername(owner.getNickName());
		user.setJoinTime(new Date());  //加入时间
		smsGroupBuyAssembleUserService.insert(user);

		//已拼数量更新
		int a =gy.getPromotionCountAl()+quantity;
		gy.setPromotionCountAl(a);
		smsGroupBuyProductService.updateById(gy);
	}

	@Override
	@Transactional
	public int delivery(List<OmsOrderDeliveryParam> deliveryParamList) {
		//批量发货
		int count = orderDao.delivery(deliveryParamList);
		//添加操作记录
		List<OmsOrderOperateHistory> operateHistoryList = deliveryParamList.stream()
				.map(omsOrderDeliveryParam -> {
					OmsOrderOperateHistory history = new OmsOrderOperateHistory();
					history.setOrderId(omsOrderDeliveryParam.getOrderId());
					history.setCreateTime(new Date());
					history.setOperateMan("商家发货");
					history.setOrderStatus(2);
					history.setNote("完成发货");
					return history;
				}).collect(Collectors.toList());
		orderOperateHistoryDao.insertList(operateHistoryList);
		return count;
	}

	@Override
	@Transactional
	public int close(List<Long> ids, String note) {
		OmsOrder record = new OmsOrder();
		record.setStatus(4);
		OmsOrderExample example = new OmsOrderExample();
		example.createCriteria().andDeleteStatusEqualTo(0).andIdIn(ids);
		int count = orderMapper.updateByExampleSelective(record, example);
		List<OmsOrderOperateHistory> historyList = ids.stream().map(orderId -> {
			OmsOrderOperateHistory history = new OmsOrderOperateHistory();
			history.setOrderId(orderId);
			history.setCreateTime(new Date());
			history.setOperateMan("后台管理员");
			history.setOrderStatus(4);
			history.setNote("订单关闭");
			return history;
		}).collect(Collectors.toList());
		orderOperateHistoryDao.insertList(historyList);
		return count;
	}

	@Override
	public int delete(List<Long> ids) {
		OmsOrder record = new OmsOrder();
		record.setDeleteStatus(1);
		OmsOrderExample example = new OmsOrderExample();
		example.createCriteria().andDeleteStatusEqualTo(0).andIdIn(ids);
		return orderMapper.updateByExampleSelective(record, example);
	}

	@Override
	public OmsOrderDetail detail(Long id) {
		return orderDao.getDetail(id,null);
	}

//	@Override
//	@Transactional
//	public int updateReceiverInfo(OmsReceiverInfoParam receiverInfoParam) {
//		OmsOrder order = new OmsOrder();
//		order.setId(receiverInfoParam.getOrderId());
//		order.setReceiverName(receiverInfoParam.getReceiverName());
//		order.setReceiverPhone(receiverInfoParam.getReceiverPhone());
//		order.setReceiverPostCode(receiverInfoParam.getReceiverPostCode());
//		order.setReceiverDetailAddress(receiverInfoParam.getReceiverDetailAddress());
//		order.setReceiverProvince(receiverInfoParam.getReceiverProvince());
//		order.setReceiverCity(receiverInfoParam.getReceiverCity());
//		order.setReceiverRegion(receiverInfoParam.getReceiverRegion());
//		order.setModifyTime(new Date());
//		int count = orderMapper.updateByPrimaryKeySelective(order);
//		//插入操作记录
//		OmsOrderOperateHistory history = new OmsOrderOperateHistory();
//		history.setOrderId(receiverInfoParam.getOrderId());
//		history.setCreateTime(new Date());
//		history.setOperateMan("后台管理员");
//		history.setOrderStatus(receiverInfoParam.getStatus());
//		history.setNote("修改收货人信息");
//		orderOperateHistoryMapper.insert(history);
//		return count;
//	}

//	@Override
//	@Transactional
//	public int updateMoneyInfo(OmsMoneyInfoParam moneyInfoParam) {
//		OmsOrder order = new OmsOrder();
//		order.setId(moneyInfoParam.getOrderId());
//		order.setFreightAmount(moneyInfoParam.getFreightAmount());
//		order.setDiscountAmount(moneyInfoParam.getDiscountAmount());
//		order.setModifyTime(new Date());
//		int count = orderMapper.updateByPrimaryKeySelective(order);
//		//插入操作记录
//		OmsOrderOperateHistory history = new OmsOrderOperateHistory();
//		history.setOrderId(moneyInfoParam.getOrderId());
//		history.setCreateTime(new Date());
//		history.setOperateMan("后台管理员");
//		history.setOrderStatus(moneyInfoParam.getStatus());
//		history.setNote("修改费用信息");
//		orderOperateHistoryMapper.insert(history);
//		return count;
//	}

//	@Override
//	@Transactional
//	public int updateNote(Long id, String note, Integer status) {
//		OmsOrder order = new OmsOrder();
//		order.setId(id);
//		order.setNote(note);
//		order.setModifyTime(new Date());
//		int count = orderMapper.updateByPrimaryKeySelective(order);
//		OmsOrderOperateHistory history = new OmsOrderOperateHistory();
//		history.setOrderId(id);
//		history.setCreateTime(new Date());
//		history.setOperateMan(ShiroUtils.getUserId());
//		history.setOrderStatus(status);
//		history.setNote("修改备注信息："+note);
//		orderOperateHistoryMapper.insert(history);
//		return count;
//	}

	@Autowired
	OmsOrderOperateHistoryDao omsOrderOperateHistoryDao;

	//定时任务 取消订单
	@Override
	@Transactional
	public int orderCancle() {
		List<OmsOrder>  orders = orderMapper.selectCancleing();
		List<Long> idlist = new ArrayList<>();

		if(idlist.size() <= 0){
			return 0;
		}
		List<OmsOrderOperateHistory>  list = new ArrayList<>();
		for(OmsOrder o:orders){
			idlist.add(o.getId());
			//生成日志
			OmsOrderOperateHistory oooh = new OmsOrderOperateHistory();
			oooh.setCreateTime(new Date());
			oooh.setNote("系统已取消");
			oooh.setOperateMan("ADMIN");
			oooh.setOrderStatus(OrderStatus.ORDER_YGB.getIndex()); //取消订单
			oooh.setOrderId(o.getId());
			list.add(oooh);
		}
		//批量新增操作日志
		if(list.size() > 0 ){
			omsOrderOperateHistoryDao.insertList(list);
		}
//        EntityWrapper enw= new EntityWrapper();
//        enw.in("order_id",idlist);
		Map<String,Object> map = new HashMap<>();
		map.put("ids",idlist);
		List<OmsOrderItem> items =omsOrderItemMapper.selectListByMap(map);
		if(items == null || items.size() == 0){
			return 0;
		}
		List<OmsOrderItem> pros = new ArrayList<>();
		List<OmsOrderItem> skus = new ArrayList<>();
		if(items!=null && items.size() > 0){
			for(OmsOrderItem o:items){
				if (o.getProductSkuId() != null) {
					skus.add(o);
				}
				else{
					pros.add(o);
				}
			}
		}
		// 根据不同的类型释放库存
		if(pros!= null && pros.size() >0 ){
			orderMapper.updateProLockStockBatch(pros);
		}
		if(skus != null && skus.size() >0 ){
			orderMapper.updateSkuLockStockBatch(skus);
		}
		//执行取消订单
		int r = orderMapper.orderCancle();
		return r;
	}

	@Override
	@Transactional
	public CommonResult orderReceive(Long orderId,String mobile) throws Exception {
		int a = orderMapper.orderReceive(orderId);
		OmsOrder order  =orderMapper.selectByPrimaryKey(orderId);
		if(order == null){
			throw new Exception("出了点问题,请稍后重试...");
		}
		tMagneticCardService.confirmOrder(order.getOrderSn());
		//新增签收记录
		List<OmsOrderOperateHistory>  list = new ArrayList<>();
		OmsOrderOperateHistory oooh = new OmsOrderOperateHistory();
		oooh.setCreateTime(new Date());
		oooh.setNote("本人已签收");
		oooh.setOperateMan(mobile);
		oooh.setOrderStatus(OrderStatus.ORDER_YWC.getIndex());
		oooh.setOrderId(orderId);
		list.add(oooh);
		omsOrderOperateHistoryDao.insertList(list);
		if(a > 0){
			return CommonResult.success("确认成功");
		}else{
			throw new Exception("确认失败,请重试！");
		}
	}

	@Override
    @Transactional
	public CommonResult userCancleOrder(Long orderId) throws  Exception {
	    //锁定库存释放
        List<Long> idlist = new ArrayList<>();
        idlist.add(orderId);

        String username = "";
        UjOwner owner = iUjOwnerService.selectOne(new EntityWrapper<UjOwner>().eq("mobile",getUserMobile()));
        if(owner!=null){
            username = owner.getNickName();
        }else {
            username = getUserMobile();
        }
        //生成日志
        OmsOrderOperateHistory oooh = new OmsOrderOperateHistory();
        oooh.setCreateTime(new Date());
        oooh.setNote("已取消");
        oooh.setOperateMan(username);
        oooh.setOrderStatus(OrderStatus.ORDER_YGB.getIndex()); //取消订单
        oooh.setOrderId(orderId);
        orderOperateHistoryMapper.insert(oooh);

        Map<String,Object> map = new HashMap<>();
        map.put("ids",idlist);
        List<OmsOrderItem> items =omsOrderItemMapper.selectListByMap(map);
        if(items == null || items.size() == 0){
            return CommonResult.success("已取消");
        }

        List<OmsOrderItem> pros = new ArrayList<>();
        List<OmsOrderItem> skus = new ArrayList<>();
        if(items!=null && items.size() > 0){
            for(OmsOrderItem o:items){
                if (o.getProductSkuId() != null) {
                    skus.add(o);
                }
                else{
                    pros.add(o);
                }
            }
        }
        // 根据不同的类型释放库存
        if(pros!= null && pros.size() >0 ){
            orderMapper.updateProLockStockBatch(pros);
        }
        if(skus != null && skus.size() >0 ){
            orderMapper.updateSkuLockStockBatch(skus);
        }
		int a= orderMapper.userCancleOrder(orderId);
		//释放锁定库存
		if(a > 0 ){
			return CommonResult.success("取消成功");
		}
		else{
			return CommonResult.failed("取消出错");
		}
	}

	@Override
	public Integer autoreceiveorder() {
		List<OmsOrder> orderlist =orderMapper.getautoreceiveorder();

		List<OmsOrderOperateHistory>  list = new ArrayList<>();
		if(orderlist!= null && orderlist.size() >0){
//			logger.info("自动确认收货的数量为" + orderlist.size());
			for(OmsOrder o:orderlist){
				logger.info("订单确认收货-------" + o.getOrderSn());
				tMagneticCardService.confirmOrder(o.getOrderSn());
				OmsOrderOperateHistory oooh = new OmsOrderOperateHistory();
				oooh.setCreateTime(new Date());
				oooh.setNote("已签收");
				oooh.setOperateMan("U家管理员");
				oooh.setOrderStatus(OrderStatus.ORDER_YWC.getIndex());
				oooh.setOrderId(o.getId());
				list.add(oooh);
			}
			omsOrderOperateHistoryDao.insertList(list); //批量新增 操作记录
		}else{
			logger.info("自动确认收货的数量为0");
		}
		int a =  orderMapper.autoreceiveorder();

		return a;
	}

}
