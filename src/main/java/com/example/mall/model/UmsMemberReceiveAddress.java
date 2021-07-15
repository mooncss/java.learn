package com.example.mall.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;


@TableName("ums_member_receive_address")
public class UmsMemberReceiveAddress implements Serializable {

    @TableId("id")
    private String id;
    //会员ID  U家用户的手机号
    @TableField("member_id")
    private String memberId;

    @TableField("name")
    @ApiModelProperty(value = "收货人名称")
    private String name;

    @ApiModelProperty(value = "收货人手机号")
    @TableField("phone_number")
    private String phoneNumber;
    @TableField("default_status")
    @ApiModelProperty(value = "是否为默认  0 为默认  1 为非默认")
    private Integer defaultStatus;
    @TableField("post_code")
    @ApiModelProperty(value = "邮政编码")
    private String postCode;

    @TableField("province")
    @ApiModelProperty(value = "省份/直辖市")
    private String province;
    @TableField("city")
    @ApiModelProperty(value = "城市")
    private String city;
    @TableField("region")
    @ApiModelProperty(value = "区")
    private String region;

    @TableField("detail_address")
    @ApiModelProperty(value = "详细地址(街道)")
    private String detailAddress;

    //经度
    @TableField("lat")
    private String lat;
    //纬度
    @TableField("lon")
    private String lon;

    //备注
    @TableField("remark")
    private String remark;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(Integer defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", memberId=").append(memberId);
        sb.append(", name=").append(name);
        sb.append(", phoneNumber=").append(phoneNumber);
        sb.append(", defaultStatus=").append(defaultStatus);
        sb.append(", postCode=").append(postCode);
        sb.append(", province=").append(province);
        sb.append(", city=").append(city);
        sb.append(", region=").append(region);
        sb.append(", detailAddress=").append(detailAddress);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}