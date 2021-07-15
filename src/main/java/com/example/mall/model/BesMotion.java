package com.example.mall.model;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("bes_motion_map")
public class BesMotion  implements Serializable {

    @TableId("id")
    private String id;

    private int ind;
    //经度
    private String lon ;
    //纬度
    private String lat;

    private String address;

}
