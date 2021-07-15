package com.example.mall.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.mall.model.BesUser;
import com.example.mall.model.PmsProduct;
import com.example.mall.model.PmsSkuStock;
import com.example.mall.model.SmsGroupBuyAssemble;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import sun.nio.cs.ext.Big5;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

//单品明细信息+促销信息
@Data
public class PmsProductRelaPormotionVO extends PmsProduct {

    @ApiModelProperty("促销短语信息")
    private List<String> promotiondescs;

//    //促销价格
//    private BigDecimal promPrice;

    private PmsSkuStock sku;

    private BesUserPro besUser;

    @ApiModelProperty("共有多少人在拼单")
    private int groupPerTotal;

    private int isPromotion;

    @ApiModelProperty("拼团活动ID")
    private String groupbuyproId;

    @ApiModelProperty("成团列表")
    private List<SmsGroupBuyAssemble> assembleList;

    @ApiModelProperty("如果是拼团活动  则是截止时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @ApiModelProperty("拼团商品的限购数量")
    private Integer limitCount;

    @ApiModelProperty("积分")
    private Integer credit;

}
