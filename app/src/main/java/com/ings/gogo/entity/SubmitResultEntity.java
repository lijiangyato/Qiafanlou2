package com.ings.gogo.entity;

import java.util.List;

public class SubmitResultEntity {

	// "success":true,
	// "msg":"提交成功",
	// "data":
	private Boolean success;
	private String msg;
	private ResultDatas data;

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

	public ResultDatas getData() {
		return data;
	}

	public void setData(ResultDatas data) {
		this.data = data;
	}

	public static class ResultDatas {
		// "orderno":"G20161212045212_0631",
		// "postage":0,
		// "prototal":18,
		// "cheap":0,
		// "paytotal":18
		private String orderno;
		private Double postage;
		private Double prototal;
		private String cheapinfo;
		private Double paytotal;
		private int lasttime;

		public String getOrderno() {
			return orderno;
		}

		public void setOrderno(String orderno) {
			this.orderno = orderno;
		}

		public Double getPostage() {
			return postage;
		}

		public void setPostage(Double postage) {
			this.postage = postage;
		}

		public Double getPrototal() {
			return prototal;
		}

		public void setPrototal(Double prototal) {
			this.prototal = prototal;
		}

		public String getCheapinfo() {
			return cheapinfo;
		}

		public void setCheapinfo(String cheapinfo) {
			this.cheapinfo = cheapinfo;
		}

		public Double getPaytotal() {
			return paytotal;
		}

		public void setPaytotal(Double paytotal) {
			this.paytotal = paytotal;
		}

		public int getLasttime() {
			return lasttime;
		}

		public void setLasttime(int lasttime) {
			this.lasttime = lasttime;
		}

	}

}
