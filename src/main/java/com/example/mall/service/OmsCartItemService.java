package com.example.mall.service;

import com.example.mall.domain.CartProduct;
import com.example.mall.domain.CartPromotionItem;
import com.example.mall.model.OmsCartItem;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 购物车管理Service
 * Created by macro on 2018/8/2.
 */
public interface OmsCartItemService {
    /**
     * 查询购物车中是否包含该商品，有增加数量，无添加到购物车
     */

    int add(OmsCartItem cartItem) throws  Exception;

    /**
     * 根据会员编号获取购物车列表
     */
    List<OmsCartItem> list(String memberId);

    /**
     * 根据会员编号获取购物车选中列表
     */

    List<OmsCartItem> listiselect(String memberId);


    List<OmsCartItem> listiselectgift(String memberId);

    /**
     * 获取包含促销活动信息的购物车列表
     */
    List<CartPromotionItem> listPromotion(String memberId);

    /**
     * 修改某个购物车商品的数量
     */
    int updateQuantity(Long id, String memberId, Integer quantity);

    /**
     * 修改某个购物车中的选中状态
     */
    int updateChecked(Long id, String memberId, String operate,String checkType,Long besId);

    /**
     * 批量删除购物车中的商品
     */
    int delete(String memberId, List<Long> ids);

    int  deleteCartItems(String memberId, List<Long> ids) throws  Exception;

    /**
     *获取购物车中用于选择商品规格的商品信息
     */
    CartProduct getCartProduct(Long productId);

    /**
     * 修改购物车中商品的规格
     */
    int updateAttr(OmsCartItem cartItem) throws Exception;

    /**
     * 清空购物车
     */
    int clear(String memberId);


    //购物车总件数
    int countCart(String mobile);

    int deleteCartItems();


    List<OmsCartItem> selectAllCartItems(String memberId);

    Integer countByProductId(Map<String,Object> map);
}
