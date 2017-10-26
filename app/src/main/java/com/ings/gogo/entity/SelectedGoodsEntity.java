package com.ings.gogo.entity;

import java.io.Serializable;

public class SelectedGoodsEntity implements Serializable {
	private int id;
	private String goodsName;
	private String goodsPrice;
	private String goodsNum;
	private String goodsImage;
	private String totalPrice;
	private String goodsProid;
	private String isToday;
	private boolean checked;

	public SelectedGoodsEntity() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

	public SelectedGoodsEntity(String goodsName, String goodsPrice,
			String goodsNum, String totalPrice, String goodsProid,
			String isToday) {
		super();
		this.goodsName = goodsName;
		this.goodsPrice = goodsPrice;
		this.goodsNum = goodsNum;
		this.totalPrice = totalPrice;
		this.goodsProid = goodsProid;
		this.isToday = isToday;
	}



	public SelectedGoodsEntity(String goodsName, String goodsPrice,
			String goodsNum, String totalPrice, String goodsProid) {
		super();
		this.goodsName = goodsName;
		this.goodsPrice = goodsPrice;
		this.goodsNum = goodsNum;
		this.totalPrice = totalPrice;
		this.goodsProid = goodsProid;
	}

	public String getIsToday() {
		return isToday;
	}

	public void setIsToday(String isToday) {
		this.isToday = isToday;
	}

	public String getGoodsProid() {
		return goodsProid;
	}

	public void setGoodsProid(String goodsProid) {
		this.goodsProid = goodsProid;
	}

	public String getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getGoodsPrice() {
		return goodsPrice;
	}

	public void setGoodsPrice(String goodsPrice) {
		this.goodsPrice = goodsPrice;
	}

	public String getGoodsNum() {
		return goodsNum;
	}

	public void setGoodsNum(String goodsNum) {
		this.goodsNum = goodsNum;
	}

	public String getGoodsImage() {
		return goodsImage;
	}

	public void setGoodsImage(String goodsImage) {
		this.goodsImage = goodsImage;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

}
