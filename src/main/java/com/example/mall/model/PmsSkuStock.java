package com.example.mall.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

@TableName("pms_sku_stock")
public class PmsSkuStock implements Serializable {
    @TableId("id")
    private Long id;
    @TableField("product_id")
    private Long productId;

    @TableField("sku_code")
    @ApiModelProperty(value = "sku编码")
    private String skuCode;

    @ApiModelProperty(value = "价格")
    @TableField("price")
    private BigDecimal price;

    @TableField("stock")
    @ApiModelProperty(value = "库存")
    private Integer stock;

    @TableField("low_stock")
    @ApiModelProperty(value = "预警库存")
    private Integer lowStock;

    @TableField("sp1")
    @ApiModelProperty(value = "销售属性1")
    private String sp1;

    @TableField("sp2")
    private String sp2;

    @TableField("sp3")
    private String sp3;

    @TableField("pic")
    @ApiModelProperty(value = "展示图片")
    private String pic;

    @TableField("sale")
    @ApiModelProperty(value = "销量")
    private Integer sale;

    @TableField("promotion_price")
    @ApiModelProperty(value = "单品促销价格")
    private BigDecimal promotionPrice;

    @TableField("lock_stock")
    @ApiModelProperty(value = "锁定库存")
    private Integer lockStock;

    @TableField("groupbuy_price")
    @ApiModelProperty(value = "团购活动价")
    private BigDecimal groupbuyPrice;


    public BigDecimal getGroupbuyPrice() {
        return groupbuyPrice;
    }

    public void setGroupbuyPrice(BigDecimal groupbuyPrice) {
        this.groupbuyPrice = groupbuyPrice;
    }




    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getLowStock() {
        return lowStock;
    }

    public void setLowStock(Integer lowStock) {
        this.lowStock = lowStock;
    }

    public String getSp1() {
        return sp1;
    }

    public void setSp1(String sp1) {
        this.sp1 = sp1;
    }

    public String getSp2() {
        return sp2;
    }

    public void setSp2(String sp2) {
        this.sp2 = sp2;
    }

    public String getSp3() {
        return sp3;
    }

    public void setSp3(String sp3) {
        this.sp3 = sp3;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public Integer getSale() {
        return sale;
    }

    public void setSale(Integer sale) {
        this.sale = sale;
    }

    public BigDecimal getPromotionPrice() {
        return promotionPrice;
    }

    public void setPromotionPrice(BigDecimal promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    public Integer getLockStock() {
        return lockStock;
    }

    public void setLockStock(Integer lockStock) {
        this.lockStock = lockStock;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", productId=").append(productId);
        sb.append(", skuCode=").append(skuCode);
        sb.append(", price=").append(price);
        sb.append(", stock=").append(stock);
        sb.append(", lowStock=").append(lowStock);
        sb.append(", sp1=").append(sp1);
        sb.append(", sp2=").append(sp2);
        sb.append(", sp3=").append(sp3);
        sb.append(", pic=").append(pic);
        sb.append(", sale=").append(sale);
        sb.append(", promotionPrice=").append(promotionPrice);
        sb.append(", lockStock=").append(lockStock);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}