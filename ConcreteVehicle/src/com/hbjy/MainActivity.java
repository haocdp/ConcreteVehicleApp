package com.hbjy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.hbjy.carlocation.R;
import com.hbjy.utils.Constant;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

public class MainActivity extends FragmentActivity implements OnClickListener {

	public ResideMenu resideMenu;// 侧滑
	private CarInfoListFragment carInfoListPage;
	private MyNewsFragment myNewsPage;
	private CheckTaskFragment checkTaskFragment;
	private MotorInfoListFragment motorInfoListPage;
	private FrameLayout checktask, mynews, carInfoList, motorInfoList;
	private long firstTime = 0;

	private ResideMenuItem itemHome;
	private ResideMenuItem itemLogout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		setupView();
		setUpMenu();
		clickCarinfoList();
	}

	/**
	 * 侧滑效果
	 */
	private void setUpMenu() {
		// attach to current activity;
		resideMenu = new ResideMenu(this);
		resideMenu.setBackground(R.drawable.menu_background);
		resideMenu.attachToActivity(this);
		resideMenu.setMenuListener(null);// 打开和关闭menu事件
		resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);// 不能右滑
		// valid scale factor is between 0.0f and 1.0f. leftmenu'width is
		// 150dip.
		resideMenu.setScaleValue(0.6f);

		// create menu items;
		itemHome = new ResideMenuItem(this);
		itemHome.setTitle("首页");
		itemLogout = new ResideMenuItem(this);
		itemLogout.setTitle("退出");

		itemHome.setOnClickListener(this);
		itemLogout.setOnClickListener(this);

		resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemLogout, ResideMenu.DIRECTION_LEFT);
	}

	private void setupView() {
		carInfoList = (FrameLayout) findViewById(R.id.carinfolist);
		mynews = (FrameLayout) findViewById(R.id.mynews);
		checktask = (FrameLayout) findViewById(R.id.checktask);
		// manageStorage = (FrameLayout) findViewById(R.id.managestorage);
		motorInfoList = (FrameLayout) findViewById(R.id.motorinfolist);

		carInfoList.setOnClickListener(this);
		mynews.setOnClickListener(this);
		checktask.setOnClickListener(this);
		motorInfoList.setOnClickListener(this);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return resideMenu.dispatchTouchEvent(ev);
	}

	@Override
	public void onClick(View v) {
		if (v == itemHome) {//首页点击

		} else if (v == itemLogout) {//登出点击
			exits();
		} else {
			switch (v.getId()) {
			case R.id.carinfolist:
				clickCarinfoList();
				break;
			case R.id.mynews:
				clickMyNews();
				break;
			case R.id.checktask:
				clickCheckTask();
				break;
			// case R.id.managestorage:
			// clickManageStorage();
			// break;
			case R.id.motorinfolist:
				clickReportsCenter();
				break;
			default:
				break;
			}
		}

	}

	private void clickCarinfoList() {
		if (carInfoListPage == null)
			carInfoListPage = new CarInfoListFragment();
		FragmentTransaction fragmentTransaction = this
				.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.frame_content, carInfoListPage);
		fragmentTransaction.commit();
		carInfoList.setSelected(true);
		mynews.setSelected(false);
		checktask.setSelected(false);
		motorInfoList.setSelected(false);
		// manageStorage.setSelected(false);

	}

	private void clickMyNews() {
		if (myNewsPage == null)
			myNewsPage = new MyNewsFragment();
		FragmentTransaction fragmentTransaction = this
				.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.frame_content, myNewsPage);
		fragmentTransaction.commit();
		carInfoList.setSelected(false);
		mynews.setSelected(true);
		checktask.setSelected(false);
		motorInfoList.setSelected(false);
		// manageStorage.setSelected(false);

	}

	private void clickCheckTask() {
		if (checkTaskFragment == null)
			checkTaskFragment = new CheckTaskFragment();
		FragmentTransaction fragmentTransaction = this
				.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.frame_content, checkTaskFragment);
		fragmentTransaction.commit();
		carInfoList.setSelected(false);
		mynews.setSelected(false);
		checktask.setSelected(true);
		// manageStorage.setSelected(false);
		motorInfoList.setSelected(false);

	}

	private void clickReportsCenter() {
		if (motorInfoListPage == null)
			motorInfoListPage = new MotorInfoListFragment();
		FragmentTransaction fragmentTransaction = this
				.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.frame_content, motorInfoListPage);
		fragmentTransaction.commit();
		carInfoList.setSelected(false);
		mynews.setSelected(false);
		checktask.setSelected(false);
		motorInfoList.setSelected(true);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long secondTime = System.currentTimeMillis();
			if ((secondTime - firstTime) > 2000) {
				Toast.makeText(MainActivity.this, "再按一次退出程序...",
						Toast.LENGTH_SHORT).show();
				firstTime = secondTime;
				return true;
			} else {
				System.exit(0);
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 登录退出
	 */
	private void exits()
	{
		new AlertDialog.Builder(this).setTitle("退出").setMessage("您确认要退出吗？").setPositiveButton("确认", new DialogInterface.OnClickListener()
	    {
	      public void onClick(DialogInterface paramDialogInterface, int paramInt)
	      {
	        SharedPreferences.Editor localEditor = CarInfoApplication.mInstance.getSharedPreferences(Constant.FILENAME, 0).edit();
	        localEditor.putLong(Constant.LASTTIME, 0);
	        localEditor.commit();
	        Intent localIntent = new Intent();
	        localIntent.setClass(MainActivity.this, LoginActivity.class);
	        MainActivity.this.startActivity(localIntent);
	        MainActivity.this.finish();
	      }
	    }).setNegativeButton("取消", new DialogInterface.OnClickListener()
	    {
	      public void onClick(DialogInterface paramDialogInterface, int paramInt)
	      {
	        paramDialogInterface.dismiss();
	      }
	    }).show();
	}

}
