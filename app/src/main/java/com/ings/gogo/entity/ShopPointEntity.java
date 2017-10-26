package com.ings.gogo.entity;

import java.io.Serializable;
import java.util.List;

public class ShopPointEntity implements Serializable {
	private List<ShopPointDatas> data;

	public List<ShopPointDatas> getData() {
		return data;
	}

	public void setData(List<ShopPointDatas> data) {
		this.data = data;
	}

	public static class ShopPointDatas {
		// "tips":"����*�������*֧���ŵ�����",
		// "gpsy":"26.576424",
		// "paylater":"1",
		// "maplocation":"1",
		// "phone":"18685030908",
		// "gpsx":"106.718137",
		// "ranges":500,
		// "pointid":"17fb70c8-beea-46b8-b53b-e2e0c1682425",
		// "pointaddress":"����ʡ����������������·50��",
		// "groupman":"��Ůʿ",
		// "pointname":"����·��"
		private String tips;
		private String paylater;
		private String maplocation;
		private String phone;
		private String groupman;
		private String pointid;
		// ����
		private String gpsx;
		private int ranges;
		private String pointaddress;
		private String pointname;
		// ����
		private String gpsy;

		public String getPointid() {
			return pointid;
		}

		public void setPointid(String pointid) {
			this.pointid = pointid;
		}

		public String getGpsx() {
			return gpsx;
		}

		public void setGpsx(String gpsx) {
			this.gpsx = gpsx;
		}

		public int getRanges() {
			return ranges;
		}

		public void setRanges(int ranges) {
			this.ranges = ranges;
		}

		public String getPointaddress() {
			return pointaddress;
		}

		public void setPointaddress(String pointaddress) {
			this.pointaddress = pointaddress;
		}

		public String getPointname() {
			return pointname;
		}

		public void setPointname(String pointname) {
			this.pointname = pointname;
		}

		public String getGpsy() {
			return gpsy;
		}

		public void setGpsy(String gpsy) {
			this.gpsy = gpsy;
		}

		public String getTips() {
			return tips;
		}

		public void setTips(String tips) {
			this.tips = tips;
		}

		public String getPaylater() {
			return paylater;
		}

		public void setPaylater(String paylater) {
			this.paylater = paylater;
		}

		public String getMaplocation() {
			return maplocation;
		}

		public void setMaplocation(String maplocation) {
			this.maplocation = maplocation;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getGroupman() {
			return groupman;
		}

		public void setGroupman(String groupman) {
			this.groupman = groupman;
		}

	}

}
