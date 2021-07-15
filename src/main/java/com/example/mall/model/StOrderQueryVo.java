package com.example.mall.model;


/**
 * 驿站存放快件订单
 * 驿站存放订单
 * */

public class StOrderQueryVo {
    private String orderId;
    private String userId;
    private String commitMobile;
    private String stageName;
    private String stageId;
    private String stageMan;
    private String stagePhone;
    private String state; //0待支付 1 已支付 2 已取消
    private String siglePrice;
    private String payAmount;
    private String qunatity;
    private String createTime;
    private String dName;
    private String dPhone;
    private String dShipperCode;
    private String dShipperName;

	public String getStageId() {
		return stageId;
	}

	public void setStageId(String stageId) {
		this.stageId = stageId;
	}

	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getCommitMobile() {
		return commitMobile;
	}
	public void setCommitMobile(String commitMobile) {
		this.commitMobile = commitMobile;
	}
	public String getStageName() {
		return stageName;
	}
	public void setStageName(String stageName) {
		this.stageName = stageName;
	}
	public String getStageMan() {
		return stageMan;
	}
	public void setStageMan(String stageMan) {
		this.stageMan = stageMan;
	}
	public String getStagePhone() {
		return stagePhone;
	}
	public void setStagePhone(String stagePhone) {
		this.stagePhone = stagePhone;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getSiglePrice() {
		return siglePrice;
	}
	public void setSiglePrice(String siglePrice) {
		this.siglePrice = siglePrice;
	}
	public String getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(String payAmount) {
		this.payAmount = payAmount;
	}
	public String getQunatity() {
		return qunatity;
	}
	public void setQunatity(String qunatity) {
		this.qunatity = qunatity;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
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
}
