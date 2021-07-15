package com.example.mall.model;


/**
 * 驿站存放快件订单
 * 驿站存放订单
 * */

public class StageDeliverQueryVo {
	private String dId;  //快递员ID
    private String dName;
    private String dPhone;
    private String dShipperCode;
    private String dShipperName;
    private String price;
    private String state;
    private String createTime;

	public String getdId() {
		return dId;
	}

	public void setdId(String dId) {
		this.dId = dId;
	}

	public String getdName() {
		return dName;
	}
	public void setdName(String dName) {
		this.dName = dName;
	}
	public String getdPhone() {
		return dPhone;
	}
	public void setdPhone(String dPhone) {
		this.dPhone = dPhone;
	}
	public String getdShipperCode() {
		return dShipperCode;
	}
	public void setdShipperCode(String dShipperCode) {
		this.dShipperCode = dShipperCode;
	}
	public String getdShipperName() {
		return dShipperName;
	}
	public void setdShipperName(String dShipperName) {
		this.dShipperName = dShipperName;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}
