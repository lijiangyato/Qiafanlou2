package com.ings.gogo.entity;

import java.util.List;

public class DetailScoreEntity {
	private Boolean success;
	private String msg;

	private List<DetailScoreDatas> data;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<DetailScoreDatas> getData() {
		return data;
	}

	public void setData(List<DetailScoreDatas> data) {
		this.data = data;
	}

	public static class DetailScoreDatas {

		// "scoreid":"00a64f7e-3946-4045-ab31-60eda487b49b",
		// "phone":"15185045789",
		// "recordnum":-4050,
		// "score":65850,
		// "remark":"订单G20161214103029_02使用4050.00积分抵扣40.50元",
		// "createdate":"2016-12-14 10:30:29",
		// "orderno":null
		private String scoreid;
		private String phone;
		private int recordnum;
		private int score;
		private String remark;
		private String createdate;
		private String orderno;

		public String getScoreid() {
			return scoreid;
		}

		public void setScoreid(String scoreid) {
			this.scoreid = scoreid;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public int getRecordnum() {
			return recordnum;
		}

		public void setRecordnum(int recordnum) {
			this.recordnum = recordnum;
		}

		public int getScore() {
			return score;
		}

		public void setScore(int score) {
			this.score = score;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public String getCreatedate() {
			return createdate;
		}

		public void setCreatedate(String createdate) {
			this.createdate = createdate;
		}

		public String getOrderno() {
			return orderno;
		}

		public void setOrderno(String orderno) {
			this.orderno = orderno;
		}

	}

}
