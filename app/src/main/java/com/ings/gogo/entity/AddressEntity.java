package com.ings.gogo.entity;

import java.util.List;

public class AddressEntity {
	private List<PlaceDatas> data;

	public List<PlaceDatas> getData() {
		return data;
	}

	public void setData(List<PlaceDatas> data) {
		this.data = data;
	}

	public static class PlaceDatas {
		// "gpsx":null,
		// "phone":"15185045789",
		// "consigneeid":"20160727223822",
		// "consignee_no":null,
		// "sex":"先生 ",
		// "consignee_add":"白云南路2",
		// "consignee_name":"赵飞翔",
		// "consignee_phone":"15185045789",
		// "gpsy":null
		private String gpsx;
		private String phone;
		private String consigneeid;
		private String sex;
		private String consignee_no;
		private String consignee_add;
		private String consignee_name;
		private String consignee_phone;
		private String gpsy;

		public String getConsignee_no() {
			return consignee_no;
		}

		public void setConsignee_no(String consignee_no) {
			this.consignee_no = consignee_no;
		}

		public String getGpsx() {
			return gpsx;
		}

		public void setGpsx(String gpsx) {
			this.gpsx = gpsx;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getConsigneeid() {
			return consigneeid;
		}

		public void setConsigneeid(String consigneeid) {
			this.consigneeid = consigneeid;
		}

		public String getSex() {
			return sex;
		}

		public void setSex(String sex) {
			this.sex = sex;
		}

		public String getConsignee_add() {
			return consignee_add;
		}

		public void setConsignee_add(String consignee_add) {
			this.consignee_add = consignee_add;
		}

		public String getConsignee_name() {
			return consignee_name;
		}

		public void setConsignee_name(String consignee_name) {
			this.consignee_name = consignee_name;
		}

		public String getConsignee_phone() {
			return consignee_phone;
		}

		public void setConsignee_phone(String consignee_phone) {
			this.consignee_phone = consignee_phone;
		}

		public String getGpsy() {
			return gpsy;
		}

		public void setGpsy(String gpsy) {
			this.gpsy = gpsy;
		}

	}

}
