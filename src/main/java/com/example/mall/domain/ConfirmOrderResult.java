package com.example.mall.domain;

import com.example.mall.dto.OmsOrderCreatePrepare;
import com.example.mall.dto.ProductAttrInfo;
import com.example.mall.dto.UserCartTotalFeight;
import com.example.mall.model.*;
import com.example.mall.vo.CartItemBesGroup;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 确认单信息封装
 * Created by macro on 2018/8/30.
 */
public class ConfirmOrderResult extends OmsOrderCreatePrepare {
    //包含优惠信息的购物车信息
    private List<CartPromotionItem> cartPromotionItemList;
    //用户收货地址列表
    private List<UmsMemberReceiveAddress> memberReceiveAddressList;
    //用户可用优惠券列表
    private List<SmsCouponHistoryDetail> couponHistoryDetailList;
    //积分使用规则
    private UmsIntegrationConsumeSetting integrationConsumeSetting;
    //会员持有的积分
    private Integer memberIntegration;
    //计算的金额
    private CalcAmount calcAmount;

    private List<BesUser> besusers;

    //自营商家
    private List<BesUser> besusersIsautar;

    //开始免配送费
    private String deliverFeeStart;

    private Integer credit; //积分

    private int total;

    private Integer isMention; //app界面是否展示自提   0 自提， 1 不能自提
    //购物车
    private List<CartItemBesGroup> besItemlist;

    public List<CartItemBesGroup> getBesItemlist() {
        return besItemlist;
    }

    public void setBesItemlist(List<CartItemBesGroup> besItemlist) {
        this.besItemlist = besItemlist;
    }

    //运费模板 发货时间
    private PmsFeightTemplate pmsFeightTemplate;


    public Integer getIsMention() {
        return isMention;
    }

    public void setIsMention(Integer isMention) {
        this.isMention = isMention;
    }

    public String getDeliverFeeStart() {
        return deliverFeeStart;
    }

    public void setDeliverFeeStart(String deliverFeeStart) {
        this.deliverFeeStart = deliverFeeStart;
    }

    public List<BesUser> getBesusersIsautar() {
        return besusersIsautar;
    }

    public void setBesusersIsautar(List<BesUser> besusersIsautar) {
        this.besusersIsautar = besusersIsautar;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public PmsFeightTemplate getPmsFeightTemplate() {
        return pmsFeightTemplate;
    }

    public void setPmsFeightTemplate(PmsFeightTemplate pmsFeightTemplate) {
        this.pmsFeightTemplate = pmsFeightTemplate;
    }

    public List<BesUser> getBesusers() {
        return besusers;
    }

    public void setBesusers(List<BesUser> besusers) {
        this.besusers = besusers;
    }

    public List<CartPromotionItem> getCartPromotionItemList() {
        return cartPromotionItemList;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public void setCartPromotionItemList(List<CartPromotionItem> cartPromotionItemList) {
        this.cartPromotionItemList = cartPromotionItemList;
    }

    public List<UmsMemberReceiveAddress> getMemberReceiveAddressList() {
        return memberReceiveAddressList;
    }

    public void setMemberReceiveAddressList(List<UmsMemberReceiveAddress> memberReceiveAddressList) {
        this.memberReceiveAddressList = memberReceiveAddressList;
    }

    public List<SmsCouponHistoryDetail> getCouponHistoryDetailList() {
        return couponHistoryDetailList;
    }

    public void setCouponHistoryDetailList(List<SmsCouponHistoryDetail> couponHistoryDetailList) {
        this.couponHistoryDetailList = couponHistoryDetailList;
    }

    public UmsIntegrationConsumeSetting getIntegrationConsumeSetting() {
        return integrationConsumeSetting;
    }

    public void setIntegrationConsumeSetting(UmsIntegrationConsumeSetting integrationConsumeSetting) {
        this.integrationConsumeSetting = integrationConsumeSetting;
    }

    public Integer getMemberIntegration() {
        return memberIntegration;
    }

    public void setMemberIntegration(Integer memberIntegration) {
        this.memberIntegration = memberIntegration;
    }

    public CalcAmount getCalcAmount() {
        return calcAmount;
    }

    public void setCalcAmount(CalcAmount calcAmount) {
        this.calcAmount = calcAmount;
    }

    public static class CalcAmount implements Serializable {
        //订单商品总金额
        private BigDecimal totalAmount;
        //运费
        private BigDecimal freightAmount;
        //活动优惠
        private BigDecimal promotionAmount;
        //应付金额
        private BigDecimal payAmount;
        //应付款金额 字符串
        private String payAmountStr;
        //自提免配送费时的 应付款金额
        private String payAmountStrZiTi;
        //运费信息
        private String feightInfo;
        //促销信息
        private String promotionInfo;
        //积分信息
        private Integer credit;

        private UserCartTotalFeight userCartTotalFeight;

        public UserCartTotalFeight getUserCartTotalFeight() {
            return userCartTotalFeight;
        }

        public void setUserCartTotalFeight(UserCartTotalFeight userCartTotalFeight) {
            this.userCartTotalFeight = userCartTotalFeight;
        }
        public String getPayAmountStrZiTi() {
            return payAmountStrZiTi;
        }

        public void setPayAmountStrZiTi(String payAmountStrZiTi) {
            this.payAmountStrZiTi = payAmountStrZiTi;
        }
        public Integer getCredit() {
            return credit;
        }

        public void setCredit(Integer credit) {
            this.credit = credit;
        }

        public String getPayAmountStr() {
            return payAmountStr;
        }

        public void setPayAmountStr(String payAmountStr) {
            this.payAmountStr = payAmountStr;
        }

        public String getPromotionInfo() {
            return promotionInfo;
        }

        public void setPromotionInfo(String promotionInfo) {
            this.promotionInfo = promotionInfo;
        }

        public String getFeightInfo() {
            return feightInfo;
        }

        public void setFeightInfo(String feightInfo) {
            this.feightInfo = feightInfo;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public BigDecimal getFreightAmount() {
            return freightAmount;
        }

        public void setFreightAmount(BigDecimal freightAmount) {
            this.freightAmount = freightAmount;
        }

        public BigDecimal getPromotionAmount() {
            return promotionAmount;
        }

        public void setPromotionAmount(BigDecimal promotionAmount) {
            this.promotionAmount = promotionAmount;
        }

        public BigDecimal getPayAmount() {
            return payAmount;
        }

        public void setPayAmount(BigDecimal payAmount) {
            this.payAmount = payAmount;
        }
    }
}
