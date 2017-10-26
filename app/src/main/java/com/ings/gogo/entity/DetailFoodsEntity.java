package com.ings.gogo.entity;

import java.util.List;

public class DetailFoodsEntity {

	private String proid;
	private String categoryid;
	private String proname;
	private float markprice;
	private float price;
	private int stock;
	private String proattri;
	private String imgurl;
	private List<DetailFoosdPic> proimages;

	public String getProid() {
		return proid;
	}

	public void setProid(String proid) {
		this.proid = proid;
	}

	public String getCategoryid() {
		return categoryid;
	}

	public void setCategoryid(String categoryid) {
		this.categoryid = categoryid;
	}

	public String getProname() {
		return proname;
	}

	public void setProname(String proname) {
		this.proname = proname;
	}

	public float getMarkprice() {
		return markprice;
	}

	public void setMarkprice(float markprice) {
		this.markprice = markprice;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public String getProattri() {
		return proattri;
	}

	public void setProattri(String proattri) {
		this.proattri = proattri;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public List<DetailFoosdPic> getProimages() {
		return proimages;
	}

	public void setProimages(List<DetailFoosdPic> proimages) {
		this.proimages = proimages;
	}

	public static class DetailFoosdPic {
		// "imgurl":"http://112.74.25.40:8086/upfile/gogo/2016-11/thumb_f9d50d3e-efee-4268-bdae-cfe4347c80cd_480x480.png",
		// "imgclassify":"1"
		private String imgurl;
		private String imgclassify;

		public String getImgurl() {
			return imgurl;
		}

		public void setImgurl(String imgurl) {
			this.imgurl = imgurl;
		}

		public String getImgclassify() {
			return imgclassify;
		}

		public void setImgclassify(String imgclassify) {
			this.imgclassify = imgclassify;
		}

	}

}
