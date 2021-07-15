package com.example.mall.dto;

import com.example.mall.model.SmsGroupBuyProduct;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class SmsGroupBuyBesPronameDto extends SmsGroupBuyProduct {
    private String productName;
    private String propic;
    private String shopname;
    private String shopico;

    private BigDecimal price;
    private Long productCategoryId;
    private int stock;
    private String productSn;
    private String albumPics;

    private String detailMobileHtml;

    //分类名称
    private String productCategoryName;

}
