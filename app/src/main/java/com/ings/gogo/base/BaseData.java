package com.ings.gogo.base;

public class BaseData {

	public static String BASE_URL = "http://api.qiafanlou.com/app/";
	// static qiafanlou
	// http://api.jiangxin168.com/app/getcategorys?parentid=b4e19459-95b0-4e44-9066-91e512730240
	// http://api.jiangxin168.com/app/getads?adstype=2
	// http://api.jiangxin168.com/app/Logout
	// ��ȡ�ֲ�ͼ
	public static final String GET_ADS_PIC = BASE_URL + "getads?adstype=1";
	// ��ȡ����ҳ
	public static final String GET_WECOM_BG = BASE_URL + "getads?adstype=2";
	public static final String GET_PRODUCTS_LIST = BASE_URL
			+ "getproducts?pointid=";
	// ��ȡ���͵��
	public static final String GET_POINT = BASE_URL + "getpoints";
	public static String GET_DETAIL_FOODSURL = BASE_URL
			+ "getproductinfo?proid=";

	public static final String GET_GOODS_GETCATEGORY = BASE_URL
			+ "getcategorys?parentid=b4e19459-95b0-4e44-9066-91e512730240";
	public static final String GET_FAST_PRODUCTS = BASE_URL
			+ "getproducts?pointid=";
	// ��ȡ�����б�
	public static final String GET_CLEAN_LISt = BASE_URL
			+ "getproducts?pointid=";
	// ��ȡ��֤��
	public static final String GET_SMS_MSG = BASE_URL + "sendsms?phone=";
	// ע��
	public static final String REGIST_URL = BASE_URL + "register";
	// У����֤��
	public static final String CHECK_SMS_MSG = BASE_URL + "checksmscode?phone=";
	// ��¼
	public static final String LOGIN_URL = BASE_URL + "login";
	// ��������
	public static final String RESET_URL = BASE_URL + "resetpwd";
	// ��ȡ��ַ��Ϣ�б�
	public static final String GET_ADDRESS_DATA = BASE_URL + "getconsignees";
	// ��ӵ�ַ��Ϣ
	public static final String ADD_ADDRESS_DATA = BASE_URL + "subconsignees";
	// ɾ����ַ��Ϣ
	public static final String DELETE_ADDRESS_DATA = BASE_URL
			+ "deleconsignee?consigneeid=";
	// ��ȡ��ϸ��ַ��Ϣ
	public static final String GET_DETAIL_ADDRESS_DATA = BASE_URL
			+ "consigneeinfo?consigneeid=";
	// ��ȡʱ���
	public static final String GET_TIME = BASE_URL + "gettimespans?istoday=";
	// ��ȡ��������
	public static final String GET_TOTAL_SCORE = BASE_URL + "getscoreinfo";
	// �ύ����
	public static final String SUBMIT_ORDER = BASE_URL + "suborders";
	// ��ȡ������ϸ
	public static final String GET_SCORE_MSG = BASE_URL + "getscores";
	// �������
	public static final String CHECK_STOCK = BASE_URL + "checkstock";
	// ֧��֮ǰ�ļ��
	public static final String CHECK_PAY = BASE_URL + "suborderpay";
	// ��ȡ�����б�
	public static final String GET_ORDER_LIST = BASE_URL + "getorders";
	// ��ȡ��������
	public static final String GET_DETAIL_ORDER = BASE_URL
			+ "getorderinfo?orderno=";
	// ȡ�� ɾ�� ����
	public static final String CANCEL_ORDER = BASE_URL + "cancelorder";
	// ��ȡ����ת̨��ֵ
	public static final String GET_ORDER_STATE = BASE_URL + "getstatecount";
	// http://api.jiangxin168.com/app/getorders
	public static final String GET_DIFFERENT_STATE = BASE_URL
			+ "getorders?state=";
	// ��� �汾����
	public static final String APP_UPDATE = BASE_URL
			+ "getversion?plat=android";
	// �˳���¼Logout
	public static final String EXIT_APP = BASE_URL + "Logout";

}
