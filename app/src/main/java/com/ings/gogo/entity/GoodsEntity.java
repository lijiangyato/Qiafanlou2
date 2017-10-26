package com.ings.gogo.entity;

public class GoodsEntity {
	private String goodsImageUrl;
	private String goodsDescrib;
	private String goodsPrice;

	public GoodsEntity(String goodsImageUrl, String goodsDescrib,
			String goodsPrice) {
		super();
		this.goodsImageUrl = goodsImageUrl;
		this.goodsDescrib = goodsDescrib;
		this.goodsPrice = goodsPrice;
	}

	public String getGoodsImageUrl() {
		return goodsImageUrl;
	}

	public void setGoodsImageUrl(String goodsImageUrl) {
		this.goodsImageUrl = goodsImageUrl;
	}

	public String getGoodsDescrib() {
		return goodsDescrib;
	}

	public void setGoodsDescrib(String goodsDescrib) {
		this.goodsDescrib = goodsDescrib;
	}

	public String getGoodsPrice() {
		return goodsPrice;
	}

	public void setGoodsPrice(String goodsPrice) {
		this.goodsPrice = goodsPrice;
	}

}
