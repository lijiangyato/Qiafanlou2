package com.ings.gogo.entity;

import java.util.List;

public class LoginEntity {

	private boolean success;
	private String msg;
	private List<LoginEntitys> data;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<LoginEntitys> getData() {
		return data;
	}

	public void setData(List<LoginEntitys> data) {
		this.data = data;
	}

	public static class LoginEntitys {
		private String UserID;
		private String UnitID;
		private String LoginName;
		private String Password;
		private String DispName;
		private String Phone;
		private String Sex;
		private String LockFlg;
		private String LastLoginTime;
		private String Recuser;
		private String Recdate;
		private String UserType;
		private String Duty;
		private String CurrentUserID;

		public String getUserID() {
			return UserID;
		}

		public void setUserID(String userID) {
			UserID = userID;
		}

		public String getUnitID() {
			return UnitID;
		}

		public void setUnitID(String unitID) {
			UnitID = unitID;
		}

		public String getLoginName() {
			return LoginName;
		}

		public void setLoginName(String loginName) {
			LoginName = loginName;
		}

		public String getPassword() {
			return Password;
		}

		public void setPassword(String password) {
			Password = password;
		}

		public String getDispName() {
			return DispName;
		}

		public void setDispName(String dispName) {
			DispName = dispName;
		}

		public String getPhone() {
			return Phone;
		}

		public void setPhone(String phone) {
			Phone = phone;
		}

		public String getSex() {
			return Sex;
		}

		public void setSex(String sex) {
			Sex = sex;
		}

		public String getLockFlg() {
			return LockFlg;
		}

		public void setLockFlg(String lockFlg) {
			LockFlg = lockFlg;
		}

		public String getLastLoginTime() {
			return LastLoginTime;
		}

		public void setLastLoginTime(String lastLoginTime) {
			LastLoginTime = lastLoginTime;
		}

		public String getRecuser() {
			return Recuser;
		}

		public void setRecuser(String recuser) {
			Recuser = recuser;
		}

		public String getRecdate() {
			return Recdate;
		}

		public void setRecdate(String recdate) {
			Recdate = recdate;
		}

		public String getUserType() {
			return UserType;
		}

		public void setUserType(String userType) {
			UserType = userType;
		}

		public String getDuty() {
			return Duty;
		}

		public void setDuty(String duty) {
			Duty = duty;
		}

		public String getCurrentUserID() {
			return CurrentUserID;
		}

		public void setCurrentUserID(String currentUserID) {
			CurrentUserID = currentUserID;
		}

	}

}
