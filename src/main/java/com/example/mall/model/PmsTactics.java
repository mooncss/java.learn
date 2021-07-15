package com.example.mall.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

/*
* 商城策略临时表
*
* */
@Data
@TableName("pms_tactics")
public class PmsTactics implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("type")
    private  String  type;
    @TableField("product_id")
    private Long productId;
    @TableField("remark")
    private String remark;
}
