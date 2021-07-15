package com.example.mall.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/*
* 商家实体类
* */
@TableName("bes_user")
@Data
public class BesUser implements Serializable {

    @TableId(value = "bes_id")
    private Long besId;

    @TableField("nick_name")
    private String  nickName;

    private String account;
    private String password; //密码
    @TableField("real_name")
    private String realName; //真实姓名
    private String phone;//手机号
    private String province;
    private String city;
    private String area;
    private String detailaddr;
    private String status;  //  1.审核通过，2.审核失败 3.关闭店铺
    private String checked;  //审核状态  1.待审核 2.已审核
    private String license;
    private String urole;
    private String contactman;
    private String contactphone;

    @TableField("shopname")
    private String shopname;
    private String  shoptype;
    //店铺头像
    @TableField("shopico")
    private String shopico;
    private String  idno;  //身份证号
    private String idfront;  //身份证人像面
    private String  idback;//身份证国徽面
    private String remark;  //备注信息
    private String isautarky; //是否自营  0 自营 1非自营

    private Date applytime;
    private Date checktime;
    private String checkuser; //审核人

    //经纬度
    private String lon;
    private String lat;

    @TableField(exist =  false)
    private String code;

}
