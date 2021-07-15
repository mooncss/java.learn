package com.example.mall.model;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

/**
 * 快递公司图标信息
 */
@Data
@TableName("st_logstic_ico")
public class StLogsticIco {

    @TableId("id")
    private String id;

    @TableField("shipper_code")
    private String  shipperCode;

    @TableField("shipper_name")
    private String shipperName;

    @TableField("ico")
    private String ico;


}
