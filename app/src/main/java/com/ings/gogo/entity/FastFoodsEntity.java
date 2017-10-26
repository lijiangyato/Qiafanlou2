package com.ings.gogo.entity;

import java.util.List;

public class FastFoodsEntity {
	private List<FastFoodsDatas> data;

	public List<FastFoodsDatas> getData() {
		return data;
	}

	public void setData(List<FastFoodsDatas> data) {
		this.data = data;
	}

	public static class FastFoodsDatas {

		private String proid;
		private String categoryid;
		private String proname;
		private float markprice;
		private float price;
		private int stock;
		private String proattri;
		private String imgurl;
		private String proimages;
		private String shortdesc;

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

		public String getProimages() {
			return proimages;
		}

		public void setProimages(String proimages) {
			this.proimages = proimages;
		}

		public String getShortdesc() {
			return shortdesc;
		}

		public void setShortdesc(String shortdesc) {
			this.shortdesc = shortdesc;
		}

	}

}
