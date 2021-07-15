package com.example.mall.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


/**
 *按商家分组 存放商家运费信息
 */
@Data
public class UserCartTotalFeight implements Serializable {
   private  BigDecimal totalFeight;
    List<UserCartFeightCalc> flist;
}
