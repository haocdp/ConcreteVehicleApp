package com.hbjy.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.hbjy.CarInfoApplication;

public class Constant {
	//默认的Ip地址和端口号
	public static final String DEFAULTIP = "61.183.9.107";
	public static final String DEFAULTPORT = "4015";
	
	//IP地址
//	public static final String HOST = "http://10.139.3.174:8080";
	public static final String HOST = "http://10.139.3.174:8080";
	
	//默认请求页数和每页的数据条数
	public static final int PAGE = 1;
	public static final int NUMBER = 10;
	
	public static final String MAPKEY = "3IDGyxax6XNcQ8APLD1Gdah3B5koZLr9";
	public static final String FILENAME = "CarLocation";
	public static final String USERNAME = "username";// 用户名
	public static final String PASSWORD = "passwd";// 密码
	public static final String ISSAVE = "isSave";// 记住密码
	public static final String LASTTIME = "lasttime";// 登录时间
	public static final String IP = "ip";// 登录时间
	public static final String PORT = "port";// 登录时间

	public static final int HISTORYNUM = 30;// 最多保存的历史记录
	public static final String CARHISTORY = "carHistory";// 车辆搜索历史记录

	public static final String SITEHISTORY = "siteHistory";// 站点搜索历史记录

	public static final String MOTORHISTORY = "motorHistory";// 站点搜索历史记录

	public static final String SUCCESSMSG = "OK";// 记住密码

	public static final int NORMALSPEED = 2000;// 平常速度

	//public static final String WEBVIE_WBASEURL = "http://59.69.105.73:13214/JQMBase/";// 设备信息url地址

	// 车辆的6种状态
	public static String vehicleStatesOnlineRun = "在线";
	public static String vehicleStatesPaseGreen = "在线停车";
	public static String vehicleStatesOffOnlineBlue = "离线";

	// 车辆的类型
	public class VehicleType {
		public static final String type1 = "卡车";
		public static final String type2 = "客车";
		public static final String type3 = "塔机";
		public static final String type4 = "挖掘机";
		public static final String type5 = "拖拉机";
		public static final String type6 = "重卡";
		public static final String type7 = "小汽车";
		public static final String type8 = "混凝土车";
	};

	public static double OILFEES = 10;// 默认油费

	/**
	 * SHA1加密
	 * 
	 * @param decript
	 * @return
	 */
	public static String SHA1(String decript) {
		try {
			MessageDigest digest = java.security.MessageDigest
					.getInstance("SHA-1");
			digest.update(decript.getBytes());
			byte messageDigest[] = digest.digest();
			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			// 字节数组转换为 十六进制 数
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获得用户名
	 * 
	 * @return
	 */
	public static String getUserId() {
		return getSpValue(USERNAME);
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public static String getSpValue(String key) {
		SharedPreferences sp = CarInfoApplication.getInstance()
				.getSharedPreferences(FILENAME, 0);
		return sp.getString(key, "");
	}

	public static void SaveUserDate(String Key, String Value) {
		SharedPreferences sp = CarInfoApplication.mInstance
				.getSharedPreferences(FILENAME, 0);
		Editor spEd = sp.edit();
		spEd.putString(Key, Value);
		spEd.commit();
	}

}
