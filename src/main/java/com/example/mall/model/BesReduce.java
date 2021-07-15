package com.example.mall.model;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@TableName("bes_promotion")
public class BesReduce implements Serializable {//店铺优惠

    @TableId("id")
    private String id;
    //商家ID
    @TableField("bes_id")
    private Long besId;

    @TableField("reduce_amount")
    private BigDecimal reduceAmount;

    @TableField("limit_amount")
    private BigDecimal limitAmount;

    @TableField("status")
    private Integer status;

    @TableField("remark")
    private String remark;



}
