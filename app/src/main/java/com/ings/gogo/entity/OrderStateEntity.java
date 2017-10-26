package com.ings.gogo.entity;

import java.util.List;

public class OrderStateEntity {
	private Boolean success;
	private String msg;

	private List<StateDatas> data;

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

	public List<StateDatas> getData() {
		return data;
	}

	public void setData(List<StateDatas> data) {
		this.data = data;
	}

	public static class StateDatas {
		// "ysc":0,
		// "dpl":0,
		// "dps":6,
		// "dfk":6
		private String ysc;
		private String dpl;
		private String dps;
		private String dfk;

		public String getYsc() {
			return ysc;
		}

		public void setYsc(String ysc) {
			this.ysc = ysc;
		}

		public String getDpl() {
			return dpl;
		}

		public void setDpl(String dpl) {
			this.dpl = dpl;
		}

		public String getDps() {
			return dps;
		}

		public void setDps(String dps) {
			this.dps = dps;
		}

		public String getDfk() {
			return dfk;
		}

		public void setDfk(String dfk) {
			this.dfk = dfk;
		}

	}

}
