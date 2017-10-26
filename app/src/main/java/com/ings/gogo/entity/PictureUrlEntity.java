package com.ings.gogo.entity;

import java.util.List;

public class PictureUrlEntity {
	private List<PictureDatas> data;

	public List<PictureDatas> getData() {
		return data;
	}

	public void setData(List<PictureDatas> data) {
		this.data = data;
	}

	public static class PictureDatas {
		// "adslink":"# ",
		// "imgurl":"http://112.74.25.40:8086/upfile/gogo/2016-01/ad1.jpg"
		private String adslink;
		private String imgurl;

		public String getAdslink() {
			return adslink;
		}

		public void setAdslink(String adslink) {
			this.adslink = adslink;
		}

		public String getImgurl() {
			return imgurl;
		}

		public void setImgurl(String imgurl) {
			this.imgurl = imgurl;
		}

	}

}
