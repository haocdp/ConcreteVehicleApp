package com.hbjy;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.hbjy.nets.HttpUtil;
import com.hbjy.utils.Constant;

public class CarInfoApplication extends Application {

	public static CarInfoApplication mInstance = null;
	public boolean m_bKeyRight = true;
	public BMapManager mBMapManager = null;

	public static final String strKey = "3IDGyxax6XNcQ8APLD1Gdah3B5koZLr9";

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("初始化", "CarInfoApplication正在初始化");
		mInstance = this;
		initEngineManager(this);
		initIpAndPort();

	}

	/**
	 * 初始化Ip地址和端口号
	 */
	private void initIpAndPort() {
		String ip = Constant.getSpValue(Constant.IP);
		String port = Constant.getSpValue(Constant.PORT);
		if ("".equals(ip) || "".equals(port)) {// 没有初始化
			// 将Ip地址和端口号保存
			Constant.SaveUserDate(Constant.IP, Constant.DEFAULTIP);
			Constant.SaveUserDate(Constant.PORT, Constant.DEFAULTPORT);
			ip = Constant.DEFAULTIP;
			port = Constant.DEFAULTPORT;
		} 
		// 更新Url地址
		HttpUtil.BASE_URL = "http://" + ip + ":" + port + "/";// 实际地址
	}

	public void initEngineManager(Context context) {
		if (mBMapManager == null) {
			mBMapManager = new BMapManager(context);
		}

		if (!mBMapManager.init(strKey, new MyGeneralListener())) {
			Toast.makeText(
					CarInfoApplication.getInstance().getApplicationContext(),
					"BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
		}
	}

	public static CarInfoApplication getInstance() {
		return mInstance;
	}

	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	public static class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				Toast.makeText(
						CarInfoApplication.getInstance()
								.getApplicationContext(), "您的网络出错啦！",
						Toast.LENGTH_LONG).show();
			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
				Toast.makeText(
						CarInfoApplication.getInstance()
								.getApplicationContext(), "输入正确的检索条件！",
						Toast.LENGTH_LONG).show();
			}
			// ...
		}

		@Override
		public void onGetPermissionState(int iError) {
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				// 授权Key错误：
				Toast.makeText(
						CarInfoApplication.getInstance()
								.getApplicationContext(),
						"请在 DemoApplication.java文件输入正确的授权Key！",
						Toast.LENGTH_LONG).show();
				CarInfoApplication.getInstance().m_bKeyRight = false;
			}
		}
	}
}