package com.ings.gogo.entity;

import java.util.List;

public class UpdateAppEntity {
	private Boolean success;
	private String msg;

	private UpdateDatas data;

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

	public UpdateDatas getData() {
		return data;
	}

	public void setData(UpdateDatas data) {
		this.data = data;
	}

	public static class UpdateDatas {

		// "versionno":"1.0.0",
		// "download":"http://localhost:14147/app/getversion",
		// "mark":"更新UI，优化下单流程",
		// "updateflag":1
		private int versiondigit;
		private String versionno;
		private String download;
		private String mark;
		private String updateflag;

		public int getVersiondigit() {
			return versiondigit;
		}

		public void setVersiondigit(int versiondigit) {
			this.versiondigit = versiondigit;
		}

		public String getVersionno() {
			return versionno;
		}

		public void setVersionno(String versionno) {
			this.versionno = versionno;
		}

		public String getDownload() {
			return download;
		}

		public void setDownload(String download) {
			this.download = download;
		}

		public String getMark() {
			return mark;
		}

		public void setMark(String mark) {
			this.mark = mark;
		}

		public String getUpdateflag() {
			return updateflag;
		}

		public void setUpdateflag(String updateflag) {
			this.updateflag = updateflag;
		}

	}

}
