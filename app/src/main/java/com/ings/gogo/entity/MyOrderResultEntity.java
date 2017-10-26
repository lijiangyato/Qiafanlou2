package com.ings.gogo.entity;

public class MyOrderResultEntity {
	// "success":true,
	// "msg":"*",
	// "data":null
	private Boolean success;
	private String msg;
	private String data;

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

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
