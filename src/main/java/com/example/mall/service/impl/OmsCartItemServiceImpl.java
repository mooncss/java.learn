package com.example.mall.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.example.mall.dao.BesProductDao;
import com.example.mall.dao.PmsSkuStockDao;
import com.example.mall.dao.PmsTacticsDao;
import com.example.mall.dao.PortalProductDao;
import com.example.mall.domain.CartProduct;
import com.example.mall.domain.CartPromotionItem;
import com.example.mall.mapper.OmsCartItemMapper;
import com.example.mall.model.*;
import com.example.mall.service.OmsCartItemService;
import com.example.mall.service.OmsPromotionService;
import com.example.mall.service.PmsSkuStockService;
import com.example.mall.service.SmsGroupBuyProductService;
import com.zhihui.uj.management.common.entity.UjOwner;
import com.zhihui.uj.management.common.service.IUjOwnerService;
import io.swagger.annotations.ApiModelProperty;
import org.apache.shiro.crypto.hash.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 购物车管理Service实现类
 * Created by macro on 2018/8/2.
 */
@Service
public class OmsCartItemServiceImpl implements OmsCartItemService {
    @Autowired
    private OmsCartItemMapper cartItemMapper;
    @Autowired
    private PortalProductDao productDao;
    @Autowired
    BesProductDao besProductDao;
    @Autowired
    private OmsPromotionService promotionService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    IUjOwnerService ownerService;

    @Autowired
    PmsSkuStockService pmsSkuStockService;
    @Autowired
    private SmsGroupBuyProductService smsGroupBuyProductService;


    @Override
    @Transactional
    public int add(OmsCartItem cartItem)  throws Exception{
        int count;
        //根据商品ID
        String mobile = request.getHeader("mobile");
        //查询商家ID
//        PmsProduct selectpro =  new PmsProduct();
//        selectpro.setId(cartItem.getProductId());
//        System.out.println("添加的购物车 商品ID "+cartItem.getProductId());
        PmsProduct pp = besProductDao.selectById(cartItem.getProductId());
        if(pp == null){
            throw  new Exception("未找到此商品");
        }
        EntityWrapper enw = new EntityWrapper();
        enw.eq("product_id",pp.getId()); //拼团活动ID
        enw.ge("end_time", new Date());  //还没结束的活动
        SmsGroupBuyProduct gy = smsGroupBuyProductService.selectOne(enw);
        if (gy == null || org.apache.commons.lang.StringUtils.isBlank(gy.getId())) {
        }else{
            throw new Exception("抱歉，此商品不能加入购物车");
        }

        if(pp.getStock() < cartItem.getQuantity()){
            throw  new Exception("库存不足，添加失败");
        }
        UjOwner o = ownerService.selectOne(new EntityWrapper<UjOwner>().eq("mobile",mobile));
        cartItem.setBesId(pp.getBesId());
        cartItem.setMemberId(mobile);
        cartItem.setMemberNickname(o.getNickName());  //设置
        cartItem.setDeleteStatus(0);
        cartItem.setIschecked("0"); //默认选中
        cartItem.setProductPic(pp.getPic());
        cartItem.setQuantity(cartItem.getQuantity()==null?1:cartItem.getQuantity());
        cartItem.setProductName(pp.getName());
        cartItem.setProductSubTitle(pp.getSubTitle());
        cartItem.setProductCategoryId(pp.getProductCategoryId());
        cartItem.setProductSn(pp.getProductSn());
        if(cartItem.getProductSkuId() != null && cartItem.getProductSkuId().intValue() <= 0  ){
            cartItem.setProductSkuId(null);
        }
        //查询SKU信息
        if(cartItem.getProductSkuId() != null){  //查询sku信息
            PmsSkuStock sku  =pmsSkuStockService.selectById(cartItem.getProductSkuId());
            if(sku !=null){
                cartItem.setSp1(sku.getSp1());
                cartItem.setSp2(sku.getSp2());
                cartItem.setSp3(sku.getSp3());
                cartItem.setProductSkuCode(sku.getSkuCode());
                //设置购物车价格
                cartItem.setPrice(sku.getPrice());
            }else{
                cartItem.setPrice(pp.getPrice());
            }
        }else{
            cartItem.setPrice(pp.getPrice());
        }
        OmsCartItem existCartItem = getCartItem(cartItem);
        if (existCartItem == null) {
            cartItem.setCreateDate(new Date());
            count = cartItemMapper.insert(cartItem);
        } else {
            cartItem.setModifyDate(new Date());
            existCartItem.setQuantity(existCartItem.getQuantity() + cartItem.getQuantity());
            count = cartItemMapper.updateByPrimaryKey(existCartItem);
        }
        return count;
    }

    /**
     * 根据会员id,商品id和规格获取购物车中商品
     */
    private OmsCartItem getCartItem(OmsCartItem cartItem) {
        OmsCartItemExample example = new OmsCartItemExample();
        OmsCartItemExample.Criteria criteria = example.createCriteria();
        criteria.andMemberIdEqualTo(cartItem.getMemberId())
                .andProductIdEqualTo(cartItem.getProductId()).andDeleteStatusEqualTo(0);
        if(!StringUtils.isEmpty(cartItem.getProductSkuId())){
            criteria.andProductSkuIdEqualTo(cartItem.getProductSkuId());
        }
        if (!StringUtils.isEmpty(cartItem.getSp1())) {
            criteria.andSp1EqualTo(cartItem.getSp1());
        }
        if (!StringUtils.isEmpty(cartItem.getSp2())) {
            criteria.andSp2EqualTo(cartItem.getSp2());
        }
        if (!StringUtils.isEmpty(cartItem.getSp3())) {
            criteria.andSp3EqualTo(cartItem.getSp3());
        }
        List<OmsCartItem> cartItemList = cartItemMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(cartItemList)) {
            return cartItemList.get(0);
        }
        return null;
    }

    @Override
    public List<OmsCartItem> list(String memberId) {
        OmsCartItemExample example = new OmsCartItemExample();
        example.createCriteria()
                .andDeleteStatusEqualTo(0)
                .andMemberIdEqualTo(memberId);
//                .andIscheckedEqualsTo(0);
        return cartItemMapper.selectByExample(example);
    }

    public List<OmsCartItem> selectAllCartItems(String memberId) {
        return cartItemMapper.selectAllCartItems(memberId);
    }

    @Override
    public Integer countByProductId(Map<String,Object> map) {
        return cartItemMapper.countByProductId(map);
    }


    @Override
    public List<OmsCartItem> listiselect(String memberId) {
        OmsCartItemExample example = new OmsCartItemExample();
        example.createCriteria()
                .andDeleteStatusEqualTo(0)
                .andMemberIdEqualTo(memberId)
                .andIscheckedEqualsTo(0);
        return cartItemMapper.selectByExample(example);
    }

    @Override
    public List<OmsCartItem> listiselectgift(String memberId) {
//        OmsCartItemExample example = new OmsCartItemExample();
//        example.createCriteria()
//                .andDeleteStatusEqualTo(0)
//                .andMemberIdEqualTo(memberId)
//                .andIscheckedEqualsTo(0);
        Map<String,Object> map = new HashMap<>();
        map.put("deletestatus",0);
        map.put("memid",memberId);
        map.put("ischecked",0);
        return cartItemMapper.selectByExampleGift(map);
    }





    @Override
    public List<CartPromotionItem> listPromotion(String memberId) {
        List<OmsCartItem> cartItemList = listiselect(memberId);
        List<CartPromotionItem> cartPromotionItemList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(cartItemList)){
            cartPromotionItemList = promotionService.calcCartPromotion(cartItemList);
        }
        return cartPromotionItemList;
    }

    @Autowired
   private PmsTacticsDao pmsTacticsDao;

    @Override
    public int updateQuantity(Long id, String memberId, Integer quantity) {
        OmsCartItem cartItem = new OmsCartItem();
        cartItem.setQuantity(quantity);
        OmsCartItemExample example = new OmsCartItemExample();
        example.createCriteria().andDeleteStatusEqualTo(0)
                .andIdEqualTo(id).andMemberIdEqualTo(memberId);
        OmsCartItem changeitem =  cartItemMapper.selectByPrimaryKey(id);

        //remark 作为限购字段
        EntityWrapper ew = new EntityWrapper();
        ew.eq("product_id",changeitem.getProductId());
        List<PmsTactics> list = pmsTacticsDao.selectList(ew);
        if(list != null && list.size() > 0){
            Integer limit = Integer.parseInt(list.get(0).getRemark() == null?"100":list.get(0).getRemark());
            if(quantity > limit){
                return 0;
            }
        }
        return cartItemMapper.updateByExampleSelective(cartItem, example);
    }

    @Override
    public int updateChecked(Long id, String memberId, String operate, String checkType,Long besId) {
        OmsCartItem cartItem = new OmsCartItem();
        //判断checktype 如果checktype = bes  商家所有的商品  checktype = item 单个商品   checktype = all 单个商品
        if(checkType!=null&&checkType.equals("item")){
            cartItem.setIschecked(operate);
            OmsCartItemExample example = new OmsCartItemExample();
            example.createCriteria().andDeleteStatusEqualTo(0)
                    .andMemberIdEqualTo(memberId)
                    .andIdEqualTo(id);
            return cartItemMapper.updateByExampleSelective(cartItem, example);
        }
        else if(checkType!=null && checkType.equals("bes")){ //商家
            cartItem.setIschecked(operate);
            OmsCartItemExample example = new OmsCartItemExample();
            example.createCriteria().andDeleteStatusEqualTo(0)
                    .andMemberIdEqualTo(memberId)
                    .andBesIdEqualTo(besId);
            return cartItemMapper.updateByExampleSelective(cartItem, example);
        } else if(checkType!=null&&checkType.equals("all")){
            cartItem.setMemberId(memberId);
            cartItem.setIschecked(operate);
            OmsCartItemExample example = new OmsCartItemExample();
            example.createCriteria().andDeleteStatusEqualTo(0)
                    .andMemberIdEqualTo(memberId);
            return cartItemMapper.updateByExampleSelective(cartItem, example);
        }
        return 0;
    }

    @Override
    public int delete(String memberId, List<Long> ids) {
        OmsCartItem record = new OmsCartItem();
        record.setDeleteStatus(1);  //0 是显示， 1 是删除
        OmsCartItemExample example = new OmsCartItemExample();
        example.createCriteria().andIdIn(ids).andMemberIdEqualTo(memberId);
        return cartItemMapper.updateByExampleSelective(record, example);
    }

    @Override
    public int deleteCartItems(String memberId, List<Long> ids) throws Exception{
        Map<String,Object> map  =new HashMap<>();
        map.put("memberId",memberId);
        map.put("ids",ids);
        int a= cartItemMapper.deleteCartItems(map);
        return a;
    }

    @Override
    public CartProduct getCartProduct(Long productId) {
        return productDao.getCartProduct(productId);
    }

    @Override
    @Transactional
    public int updateAttr(OmsCartItem cartItem) throws Exception {
        //删除原购物车信息
        OmsCartItem updateCart = new OmsCartItem();
        updateCart.setId(cartItem.getId());
        updateCart.setModifyDate(new Date());
        updateCart.setDeleteStatus(1);
        cartItemMapper.updateByPrimaryKeySelective(updateCart);
        cartItem.setId(null);
        add(cartItem);
        return 1;
    }

    @Override
    public int clear(String memberId) {
        OmsCartItem record = new OmsCartItem();
        record.setDeleteStatus(1);
        OmsCartItemExample example = new OmsCartItemExample();
        example.createCriteria().andMemberIdEqualTo(memberId);
        return cartItemMapper.updateByExampleSelective(record,example);
    }

    @Override
    public int countCart(String mobile) {
        return cartItemMapper.countCart(mobile);
    }

    @Override
    public int deleteCartItems() {
        return 0;
    }

}
