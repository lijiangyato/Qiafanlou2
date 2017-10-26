package com.ings.gogo.entity;

import java.util.List;

public class GoodsCategaryEntity {
	private List<GoodsCategaryDatas> data;

	public List<GoodsCategaryDatas> getData() {
		return data;
	}

	public void setData(List<GoodsCategaryDatas> data) {
		this.data = data;
	}

	public static class GoodsCategaryDatas {
		// "categoryid": "8792CBB4-B0CA-4276-AC5A-1F1B08562563",
		// "categoryname": "¿ì²Í"

		private String categoryid;
		private String categoryname;

		public String getCategoryid() {
			return categoryid;
		}

		public void setCategoryid(String categoryid) {
			this.categoryid = categoryid;
		}

		public String getCategoryname() {
			return categoryname;
		}

		public void setCategoryname(String categoryname) {
			this.categoryname = categoryname;
		}

	}

}
