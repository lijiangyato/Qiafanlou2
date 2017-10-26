package com.ings.gogo.entity;

import java.util.List;

public class MyCheckOrderEntity {
	private List<OrderDatas> data;

	public List<OrderDatas> getData() {
		return data;
	}

	public void setData(List<OrderDatas> data) {
		this.data = data;
	}

	public static class OrderDatas {

		// "orderno":"20160730180126",
		// "payno":null,
		// "payway":"在线支付",
		// "phone":null,
		// "orderclassify":"20160727223856",
		// "total":20,
		// "postage":0,
		// "createdate":"2016-07-30 18:01:26",
		// "mark":null,
		// "state":"待付款",
		// "orderPro":{
		// "orderPros":null
		// },
		private String orderno;
		private String payno;
		private String payway;
		private String phone;
		private String orderclassify;
		private Double total;
		private Double postage;
		private String state;
		private String createdate;
		private String mark;
		private MyorderPro orderPro;
		private String orderPros;

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getOrderno() {
			return orderno;
		}

		public void setOrderno(String orderno) {
			this.orderno = orderno;
		}

		public String getPayno() {
			return payno;
		}

		public void setPayno(String payno) {
			this.payno = payno;
		}

		public String getPayway() {
			return payway;
		}

		public void setPayway(String payway) {
			this.payway = payway;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getOrderclassify() {
			return orderclassify;
		}

		public void setOrderclassify(String orderclassify) {
			this.orderclassify = orderclassify;
		}

		public Double getTotal() {
			return total;
		}

		public void setTotal(Double total) {
			this.total = total;
		}

		public Double getPostage() {
			return postage;
		}

		public void setPostage(Double postage) {
			this.postage = postage;
		}

		public String getCreatedate() {
			return createdate;
		}

		public void setCreatedate(String createdate) {
			this.createdate = createdate;
		}

		public String getMark() {
			return mark;
		}

		public void setMark(String mark) {
			this.mark = mark;
		}

		public MyorderPro getOrderPro() {
			return orderPro;
		}

		public void setOrderPro(MyorderPro orderPro) {
			this.orderPro = orderPro;
		}

		public String getOrderPros() {
			return orderPros;
		}

		public void setOrderPros(String orderPros) {
			this.orderPros = orderPros;
		}

		public static class MyorderPro {
			// "proid":"f09ec7cb-ba68-43c9-b255-5ac05a1fdaaa",
			// "amount":1,
			// "price":20,
			// "star":0,
			// "pronum":1,
			// "proname":"回锅肉",
			// "imgurl":"http://112.74.25.40:8086/upfile/gogo/2016-11/d5918930-365c-4e59-967f-4597b1bc2229.png"
			private String proid;
			private Double amount;
			private Double price;
			private Double star;
			private int pronum;
			private String proname;
			private String imgurl;

			public String getProid() {
				return proid;
			}

			public void setProid(String proid) {
				this.proid = proid;
			}

			public Double getAmount() {
				return amount;
			}

			public void setAmount(Double amount) {
				this.amount = amount;
			}

			public Double getPrice() {
				return price;
			}

			public void setPrice(Double price) {
				this.price = price;
			}

			public Double getStar() {
				return star;
			}

			public void setStar(Double star) {
				this.star = star;
			}

			public int getPronum() {
				return pronum;
			}

			public void setPronum(int pronum) {
				this.pronum = pronum;
			}

			public String getProname() {
				return proname;
			}

			public void setProname(String proname) {
				this.proname = proname;
			}

			public String getImgurl() {
				return imgurl;
			}

			public void setImgurl(String imgurl) {
				this.imgurl = imgurl;
			}

		}

	}

}
