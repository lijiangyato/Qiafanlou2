package com.ings.gogo.entity;

import java.util.List;

public class OrderTimeEntity {

	// success":false,
	// "msg":null,
	// "data":[
	private Boolean success;
	private String msg;
	private List<TimeDatas> data;

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

	public List<TimeDatas> getData() {
		return data;
	}

	public void setData(List<TimeDatas> data) {
		this.data = data;
	}

	public static class TimeDatas {
		private String timestr;

		public String getTimestr() {
			return timestr;
		}

		public void setTimestr(String timestr) {
			this.timestr = timestr;
		}

	}

}
