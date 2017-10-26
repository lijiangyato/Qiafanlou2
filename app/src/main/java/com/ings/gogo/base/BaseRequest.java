package com.ings.gogo.base;

import com.google.gson.Gson;

/**
 * http �������
 */
public class BaseRequest {
	
	/** �������� */
	private String cmd;
	 
	/** �û�Id */
	private int userId;
//	/** �û�Id */
//	private int userId;
	
	/** ����tocken */
	private String tocken;
	
	/** ����ʱ�� */
	private long requestTime;
	
	/** 0=�����ͣ�1=���� */
	private int markId;

	/** �������� */
	public String getCommand() {
		return cmd;
	}

	/** �������� */
	public void setCommand(String cmd) {
		this.cmd = cmd;
	}

	/** �û�Id */
	public int getUserId() {
		return userId;
	}

	/** �û�Id */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/** ����tocken */
	public String getTocken() {
		return tocken;
	}

	/** ����tocken */
	public void setTocken(String tocken) {
		this.tocken = tocken;
	}

	/** ����ʱ�� */
	public long getRequestTime() {
		return requestTime;
	}

	/** ����ʱ�� */
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
