package com.example.mall.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.example.mall.common.CommonResult;
import com.example.mall.dao.PmsTacticsDao;
import com.example.mall.domain.CartProduct;
import com.example.mall.domain.CartPromotionItem;
import com.example.mall.emun.OrderStatus;
import com.example.mall.mapper.OmsOrderMapper;
import com.example.mall.model.BesUser;
import com.example.mall.model.OmsCartItem;
import com.example.mall.model.PmsTactics;
import com.example.mall.service.BesUserService;
import com.example.mall.service.OmsCartItemService;
import com.example.mall.vo.CartItemBesGroup;
import com.zhihui.uj.management.BaseController.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车管理Controller
 * Created by macro on 2018/8/2.
 */
@Controller
@Api(tags = "OmsCartItemController", description = "App购物车管理")
@RequestMapping("/cart")
public class AppOmsCartItemController extends BaseController {
    @Autowired
    private OmsCartItemService cartItemService;

    @Autowired
    private BesUserService besUserService;

    @Autowired
    private PmsTacticsDao pmsTacticsDao;

    @Autowired
    private OmsOrderMapper orderMapper;

    @ApiOperation("添加商品到购物车")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult add(@RequestBody OmsCartItem cartItem) {
        String mobile  = super.getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        if(cartItem.getProductId() == null){
            return CommonResult.failed("商品Id为空");
        }
        //添加购物车筛选
        /*********商品只能新用户才能买 ******/
        CommonResult  t = canMemberPchase(cartItem.getQuantity(),mobile,cartItem.getProductId());
        if(t.getCode() != 0){
            return CommonResult.failed(t.getMsg());}

        //查看购物车下

//        EntityWrapper ew = new EntityWrapper();
//        ew.eq("product_id",productId);
//        List<PmsTactics> list = pmsTacticsDao.selectList(ew);
//        if(list != null && list.size() > 0){

        //查询用户购物车的商品数量
        int count =  cartItemService.countCart(super.getUserMobile());
        if(count > 99){
            return CommonResult.failed("购物车已满，快去下单吧");
        }
        //查询商家
        try{
            cartItemService.add(cartItem);
            return CommonResult.success("添加成功");
        }
        catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }

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
                //购物车现在的数量
                map.put("productId",productId);
                Integer a = cartItemService.countByProductId(map);
                if(a == null){
                    a=0;
                }
                Integer limit = Integer.parseInt(list.get(0).getRemark() == null?"100":list.get(0).getRemark());
                if(quantity+a > limit){
                    return CommonResult.failed("抱歉，每人限购"+limit+"份");
            }
                return CommonResult.success(null);
            }
            return CommonResult.failed("抱歉，此商品暂时只对新用户开放购买");
        }else{
            return CommonResult.success(null);
        }
    }

    @ApiOperation("获取某个会员的购物车列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult list() {
        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        Map map = new HashMap<String,Object>();
        map.put("memberId",getUserMobile());
        List<CartItemBesGroup> beslist = besUserService.selectBesFromcart(map);
        List<OmsCartItem> cartItemList = cartItemService.selectAllCartItems(getUserMobile());
        BigDecimal cartTotalPrice=new BigDecimal("0");
        int total = 0;
        for(CartItemBesGroup b:beslist){
            boolean bescheck = true;
            List<OmsCartItem> list = new ArrayList<>();
            Long besid= b.getBesId();
            for(OmsCartItem o:cartItemList){
                if(besid.longValue() == o.getBesId().longValue()){
                    list.add(o);
                    if(o.getIschecked().equals("0")){
                        if(o.getQuantity() != null){
                            cartTotalPrice=cartTotalPrice.add(
                                    new BigDecimal(""+o.getQuantity()).multiply(o.getPrice()));
                        }
                        total++;
                    }else{
                        bescheck = false;
                    }
                }
            }
            b.setItemlist(list);
            b.setBeschecked(bescheck);
        }
        String allselect="1";
        if(total == cartItemList.size()){
            allselect = "0";
        }
        if(cartItemList.size() == 0){
            allselect = "1";
            beslist = null;
        }
        return CommonResult.success(beslist,total+";"+cartTotalPrice+";"+allselect);
    }

    @ApiOperation("获取某个会员的购物车列表,包括促销信息")
    @RequestMapping(value = "/list/promotion", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult listPromotion() {
        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        List<CartPromotionItem> cartPromotionItemList = cartItemService.listPromotion(getUserMobile());
        return CommonResult.success(cartPromotionItemList);
    }

    @ApiOperation("修改购物车中某个商品的数量")
    @RequestMapping(value = "/update/quantity", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateQuantity(@RequestParam Long id,
                                       @RequestParam Integer quantity) {
        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        int a = cartItemService.updateQuantity(id, getUserMobile(), quantity);
        return list();
    }

    @ApiOperation("修改购物车中项目选中状态")
    @RequestMapping(value = "/update/checked", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateChecked(Long id,
                                       @RequestParam String operate,
                                      @RequestParam String checktype,
                                      Long besId) {
        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        cartItemService.updateChecked(id,getUserMobile(),operate,checktype,besId);
        return list();
    }

    @ApiOperation("获取购物车中某个商品的规格,用于重选规格")
    @RequestMapping(value = "/getProduct/{productId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getCartProduct(@PathVariable Long productId) {
        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        CartProduct cartProduct = cartItemService.getCartProduct(productId);
        return CommonResult.success(cartProduct);
    }

    @ApiOperation("修改购物车中商品的规格")
    @RequestMapping(value = "/update/attr", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateAttr(@RequestBody OmsCartItem cartItem) {
        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        try {
            int count = cartItemService.updateAttr(cartItem);
            return CommonResult.success(count);
        }
        catch (Exception e){
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation("删除购物车中的某个商品")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@RequestParam("ids") List<Long> ids) {
        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        int count = cartItemService.delete(getUserMobile(), ids);
        if (count > 0) {
            CommonResult com =  list();
            return CommonResult.success(com);
        }
        return CommonResult.failed();
    }

    @ApiOperation("清空购物车")
    @RequestMapping(value = "/clear", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult clear() {
        String mobile = getUserMobile();
        if(StringUtils.isBlank(mobile)){
            return CommonResult.unauthorized(OrderStatus.USERINFOERROR.getName());
        }
        int count = cartItemService.clear(getUserMobile());
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
}
