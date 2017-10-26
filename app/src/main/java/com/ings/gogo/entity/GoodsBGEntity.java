package com.ings.gogo.entity;

import java.util.List;

public class GoodsBGEntity {
	private List<GoodsData> proimages;

	private static class GoodsData {
		private String imgurl;

		public String getImgurl() {
			return imgurl;
		}

		public void setImgurl(String imgurl) {
			this.imgurl = imgurl;
		}
	}

}
