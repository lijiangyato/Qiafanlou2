package com.ings.gogo.entity;

import java.util.List;

public class TotalScoreEntity {
	private Boolean success;
	private String msg;

	private TotalScoreDatas data;

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

	public TotalScoreDatas getData() {
		return data;
	}

	public void setData(TotalScoreDatas data) {
		this.data = data;
	}

	public static class TotalScoreDatas {

		// "phone":"15185045789",
		// "recordnum":200,
		// "score":200,
		// "remark":"123",
		// "orderno":null,
		// "createdate":"2016-12-12 00:00:00"

		private String phone;
		private int recordnum;
		private int score;
		private String remark;
		private String orderno;
		private String createdate;

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

		public String getOrderno() {
			return orderno;
		}

		public void setOrderno(String orderno) {
			this.orderno = orderno;
		}

		public String getCreatedate() {
			return createdate;
		}

		public void setCreatedate(String createdate) {
			this.createdate = createdate;
		}

	}

}
