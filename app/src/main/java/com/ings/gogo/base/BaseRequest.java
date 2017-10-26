package com.ings.gogo.base;

import com.google.gson.Gson;

/**
 * http 请求基类
 */
public class BaseRequest {
	
	/** 请求命令 */
	private String cmd;
	 
	/** 用户Id */
	private int userId;
//	/** 用户Id */
//	private int userId;
	
	/** 请求tocken */
	private String tocken;
	
	/** 请求时间 */
	private long requestTime;
	
	/** 0=不推送，1=推送 */
	private int markId;

	/** 请求命令 */
	public String getCommand() {
		return cmd;
	}

	/** 请求命令 */
	public void setCommand(String cmd) {
		this.cmd = cmd;
	}

	/** 用户Id */
	public int getUserId() {
		return userId;
	}

	/** 用户Id */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/** 请求tocken */
	public String getTocken() {
		return tocken;
	}

	/** 请求tocken */
	public void setTocken(String tocken) {
		this.tocken = tocken;
	}

	/** 请求时间 */
	public long getRequestTime() {
		return requestTime;
	}

	/** 请求时间 */
	public void setRequestTime(long requestTime) {
		this.requestTime = requestTime;
	}
	
	
	
	public int getMarkId() {
		return markId;
	}

	public void setMarkId(int markId) {
		this.markId = markId;
	}

	public String toJson(){
		return new Gson().toJson(this);
	}
}
