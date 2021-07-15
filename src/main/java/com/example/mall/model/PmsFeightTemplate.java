package com.example.mall.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@TableName("pms_feight_template")
@Data
public class PmsFeightTemplate implements Serializable {
    @TableId("id")
    private Long id;

    @ApiModelProperty("商家ID")
    @TableField("bes_id")
    private Long besId;

    @TableField("name")
    private String name; //费率模板

    @TableField("charge_type")
    @ApiModelProperty(value = "计费类型:0->按重量；1->按件数")
    private Integer chargeType;

    @TableField("feight_type")
    @ApiModelProperty("是否包邮   0包邮  1 收费")
    private Integer feightType;

    @ApiModelProperty(value = "首重kg /或者 首件")
    @TableField("first_weight")
    private BigDecimal firstWeight;

    @ApiModelProperty(value = "首费（元）")
    @TableField("first_fee")
    private BigDecimal firstFee;

    @ApiModelProperty(value = "续量（重 / 件）")
    @TableField("continue_weight")
    private BigDecimal continueWeight;
    @ApiModelProperty("续费")
    @TableField("continme_fee")
    private BigDecimal continmeFee;

    @TableField("dest")
    @ApiModelProperty(value = "目的地（省、市）")
    private String dest;

    //发货时间字符  1小时，2小时，4小时，8小时，16小时，1天,2天
    @TableField("consign_limit")
    private String consignLimit;
    //发货时间 小时数
    @TableField("consign_hour")
    private int consignHour;

    @ApiModelProperty("满多少包邮")
    @TableField("original_amount")
    private BigDecimal originalAmount;

    @ApiModelProperty("删除标记")
    @TableField("del_flag")
    private int delFlag;  // 0正常 1删除



    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getChargeType() {
        return chargeType;
    }

    public void setChargeType(Integer chargeType) {
        this.chargeType = chargeType;
    }

    public BigDecimal getFirstWeight() {
        return firstWeight;
    }

    public void setFirstWeight(BigDecimal firstWeight) {
        this.firstWeight = firstWeight;
    }

    public BigDecimal getFirstFee() {
        return firstFee;
    }

    public void setFirstFee(BigDecimal firstFee) {
        this.firstFee = firstFee;
    }

    public BigDecimal getContinueWeight() {
        return continueWeight;
    }

    public void setContinueWeight(BigDecimal continueWeight) {
        this.continueWeight = continueWeight;
    }

    public BigDecimal getContinmeFee() {
        return continmeFee;
    }

    public void setContinmeFee(BigDecimal continmeFee) {
        this.continmeFee = continmeFee;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", chargeType=").append(chargeType);
        sb.append(", firstWeight=").append(firstWeight);
        sb.append(", firstFee=").append(firstFee);
        sb.append(", continueWeight=").append(continueWeight);
        sb.append(", continmeFee=").append(continmeFee);
        sb.append(", dest=").append(dest);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}