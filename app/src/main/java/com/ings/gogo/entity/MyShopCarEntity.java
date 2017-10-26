package com.ings.gogo.entity;

public class MyShopCarEntity {
	private int id;
	private String goodsName;
	private String goodsPrice;
	private String goodsNum;
	private String goodsImage;
	private String goodsProid;
	private String isToday;
	private boolean checked;

	public MyShopCarEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MyShopCarEntity(String isToday) {
		super();
		this.isToday = isToday;
	}
	

	public MyShopCarEntity(String goodsName, String goodsPrice,
			String goodsNum, String goodsImage, String goodsProid,
			String isToday) {
		super();
		this.goodsName = goodsName;
		this.goodsPrice = goodsPrice;
		this.goodsNum = goodsNum;
		this.goodsImage = goodsImage;
		this.goodsProid = goodsProid;
		this.isToday = isToday;
	}

	public MyShopCarEntity(String goodsName, String goodsPrice,
			String goodsNum, String goodsImage, String goodsProid) {
		super();
		this.goodsName = goodsName;
		this.goodsPrice = goodsPrice;
		this.goodsNum = goodsNum;
		this.goodsImage = goodsImage;
		this.goodsProid = goodsProid;
	}

	public String getGoodsProid() {
		return goodsProid;
	}

	public void setGoodsProid(String goodsProid) {
		this.goodsProid = goodsProid;
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

	public String getIsToday() {
		return isToday;
	}

	public void setIsToday(String isToday) {
		this.isToday = isToday;
	}

}
