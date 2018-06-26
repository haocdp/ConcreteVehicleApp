package com.hbjy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.hbjy.carlocation.R;
import com.hbjy.utils.Constant;

public class PreLoginActivity extends Activity {

	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg) {
			Log.d("网络状态","打开");
			if (getIsSave()&&!getUserName().equals("") && !getPassWd().equals("")&& getLastTime()!=0) {
				PreLoginActivity.this.startActivity(new Intent(PreLoginActivity.this, MainActivity.class));
				finish();
			} else {
				PreLoginActivity.this.startActivity(new Intent(PreLoginActivity.this, LoginActivity.class));
				finish();
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prelogin);
		if (!networkStatus()) {
			Log.d("网络状态","没有打开");
			openNetworkSettings();
		}
		else
		{
			this.mHandler.sendEmptyMessageDelayed(0, 2000L);//延迟2s之后在跳转
		}
			
	}

	private boolean networkStatus() {
		boolean bisConnFlag = false;
		ConnectivityManager localConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = localConnectivityManager.getActiveNetworkInfo();
		if (network != null) {
			bisConnFlag = network.isAvailable();
		}
		
		return bisConnFlag;
	}

	private void openNetworkSettings() {
		new AlertDialog.Builder(this)
				.setTitle("网络服务")
				.setMessage("是否开启网络服务")
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface paramDialogInterface,
							int paramInt) {
						Intent intent = null;
						// 判断手机系统的版本 即API大于10 就是3.0或以上版本
						if (android.os.Build.VERSION.SDK_INT > 10) {
							intent = new Intent(
									android.provider.Settings.ACTION_SETTINGS);
						} else {
							intent = new Intent(
									android.provider.Settings.ACTION_WIRELESS_SETTINGS);
						}
						PreLoginActivity.this
								.startActivityForResult(intent, 10);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface paramDialogInterface,
							int paramInt) {
						paramDialogInterface.dismiss();
						PreLoginActivity.this.finish();
					}
				}).show();
	}

	public String getUserName() {
		return CarInfoApplication.mInstance.getSharedPreferences(
				Constant.FILENAME, 0).getString(Constant.USERNAME, "");
	}

	public String getPassWd() {
		return CarInfoApplication.mInstance.getSharedPreferences(
				Constant.FILENAME, 0).getString(Constant.PASSWORD, "");
	}
	
	public Boolean getIsSave() {
		return CarInfoApplication.mInstance.getSharedPreferences(
				Constant.FILENAME, 0).getBoolean(Constant.ISSAVE, false);
	}
	
	public Long getLastTime() {
		return CarInfoApplication.mInstance.getSharedPreferences(
				Constant.FILENAME, 0).getLong(Constant.LASTTIME, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if ((!getUserName().equals("")) && (!getPassWd().equals(""))&&(getLastTime()!=0)) {
			startActivity(new Intent(this, MainActivity.class));
			finish();
		} else {
			startActivity(new Intent(this, LoginActivity.class));
			finish();
		}
	}

}
