package com.hbjy;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hbjy.carlocation.R;
import com.hbjy.nets.HttpUtil;
import com.hbjy.nets.ToastUtil;
import com.hbjy.utils.Constant;
import com.loopj.android.http.JsonHttpResponseHandler;

public class LoginActivity extends Activity implements OnClickListener {
	private final String LOG_TAG = "LoginActivity";
	private int screenW;
	private ViewPager vp_login;
	private RelativeLayout pager_address, pager_login;
	private ImageView iv_cursor;// 头标
	private View view1, view2;// 各个选项卡
	private int offset = 0;// 动画图片偏移量
	private int bmpW;// 动画图片宽度
	private List<View> viewList;// 把需要滑动的页卡添加到这个list中
	private int currIndex;

	private Button btn_login;
	private CheckBox cb_remberpassword;
	private EditText et_passwd;
	private EditText et_username;
	private EditText et_ip, et_port;
	private Button btn_save;

	private ProgressDialog progresscircle;// 经度框

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		// set the soft input gone when we first get in the activity
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenW = dm.widthPixels;// 获取分辨率宽度
		findView();
		viewController();

	}

	void findView() {
		vp_login = (ViewPager) this.findViewById(R.id.vp_login);
		pager_login = (RelativeLayout) this.findViewById(R.id.pager_login);
		pager_address = (RelativeLayout) this.findViewById(R.id.pager_address);

		iv_cursor = (ImageView) this.findViewById(R.id.iv_cursor);
		// 设置宽度
		LayoutParams para;
		para = iv_cursor.getLayoutParams();
		// 设置
		para.height = 4;
		para.width = screenW / 2;// 为屏幕的一半
		iv_cursor.setLayoutParams(para);

		InitImageView();
		InitTextView();
		InitViewPager();
		// 初始化每个页面中的控件
		setUpChildView();

	}

	/**
	 * 初始化动画，这个就是页卡滑动时，下面的横线也滑动的效果，在这里需要计算一些数据
	 */
	private void InitImageView() {
		offset = (screenW / 2 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		iv_cursor.setImageMatrix(matrix);// 设置动画初始位置
	}

	/**
	 * 初始化头标
	 */
	private void InitTextView() {
		pager_login.setOnClickListener(new TitleOnClickListener(0));
		pager_address.setOnClickListener(new TitleOnClickListener(1));
	}

	/**
	 * 初始化viewPager
	 */
	private void InitViewPager() {
		viewList = new ArrayList<View>();
		LayoutInflater inflater = this.getLayoutInflater();
		view1 = inflater.inflate(R.layout.viewpager_fragment_login, null);
		view2 = inflater.inflate(R.layout.viewpager_fragment_address, null);
		viewList.add(view1);
		viewList.add(view2);

		vp_login.setAdapter(new LoginViewPagerAdapter(viewList));// 适配器
		vp_login.setOnPageChangeListener(new TitleOnPageChangeListener());// 页面切换事件
		vp_login.setCurrentItem(0);

	}

	private void setUpChildView() {
		// 页面1
		btn_login = (Button) view1.findViewById(R.id.btn_login);
		cb_remberpassword = (CheckBox) view1
				.findViewById(R.id.cb_remberpassword);
		et_username = (EditText) view1.findViewById(R.id.et_username);
		et_passwd = (EditText) view1.findViewById(R.id.et_passwd);

		// 每一項被點擊事件

		// 页面2
		et_ip = (EditText) view2.findViewById(R.id.et_ip);
		et_port = (EditText) view2.findViewById(R.id.et_port);
		btn_save = (Button) view2.findViewById(R.id.btn_save);

		// 初始化进度框
		progresscircle = new ProgressDialog(this);
		progresscircle.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progresscircle.setMessage("正在登录");
		progresscircle.setIndeterminate(false);
		progresscircle.setCancelable(true);

	}

	public void viewController() {
		LoadUserData();
		LoadIpData();
		btn_login.setOnClickListener(this);
		btn_save.setOnClickListener(this);
	}
	
	/**
	 * 获取Ip和端口信息
	 */
	private void LoadIpData(){
		et_ip.setText(Constant.getSpValue(Constant.IP));
		et_port.setText(Constant.getSpValue(Constant.PORT));
	}

	/**
	 * 保存用户信息
	 */
	private void SaveUserDate() {
		// 载入配置文件
		SharedPreferences sp = CarInfoApplication.mInstance
				.getSharedPreferences(Constant.FILENAME, 0);
		// 写入配置文件
		Editor spEd = sp.edit();
		spEd.putBoolean(Constant.ISSAVE, cb_remberpassword.isChecked());
		spEd.putString(Constant.USERNAME, et_username.getText().toString());
		spEd.putString(Constant.PASSWORD, et_passwd.getText().toString());
		spEd.putLong(Constant.LASTTIME, System.currentTimeMillis());
		spEd.commit();
	}

	/**
	 * 载入已记住的用户信息
	 */
	private void LoadUserData() {
		if (CarInfoApplication.mInstance == null) {
			Log.d("初始化", "CarInfoApplication初始化失败");
			return;
		}
		SharedPreferences sp = CarInfoApplication.mInstance
				.getSharedPreferences(Constant.FILENAME, 0);

		// boolean flag=sp.getBoolean(Constant.ISSAVE, true);
		if (sp.getBoolean(Constant.ISSAVE, true)) {
			String username = sp.getString(Constant.USERNAME, "");
			String userpassword = sp.getString(Constant.PASSWORD, "");
			if (!("".equals(username) && "".equals(userpassword))) {
				et_username.setText(username);
				et_passwd.setText(userpassword);
				cb_remberpassword.setChecked(true);
			}
		}
	}

	/**
	 * 标题单击事件
	 * 
	 * @author wen
	 * 
	 */
	class TitleOnClickListener implements OnClickListener {

		private int index;

		public TitleOnClickListener(int i) {
			// TODO Auto-generated constructor stub
			this.index = i;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			vp_login.setCurrentItem(index);
		}

	}

	/**
	 * viewPager的适配器
	 * 
	 * @author wen
	 * 
	 */
	class LoginViewPagerAdapter extends PagerAdapter {

		private List<View> mListViews;

		public LoginViewPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mListViews.get(position), 0);

			return mListViews.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mListViews.get(position));
		}

	}

	/**
	 * 标题被切换事件
	 * 
	 * @author wen
	 * 
	 */
	class TitleOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int index) {
			// TODO Auto-generated method stub
			Animation animation = new TranslateAnimation(one * currIndex, one
					* index, 0, 0);// 显然这个比较简洁，只有一行代码。
			currIndex = index;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(50);
			iv_cursor.startAnimation(animation);
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_save:
			SaveIp();
			break;
		case R.id.btn_login:
			progresscircle.show();
			if ("".equals(Constant.getSpValue(Constant.IP))
					|| "".equals(Constant.getSpValue(Constant.PORT))) {
				ToastUtil.showShortToast(LoginActivity.this, "请先初始化Ip地址和端口");
				progresscircle.dismiss();
				return;
			}
			String username = et_username.getText().toString();
			String passwd = et_passwd.getText().toString();
			if ("".equals(username)) {
				ToastUtil.showShortToast(LoginActivity.this, "请输入账号");
				progresscircle.dismiss();
				return;
			}
			if ("".equals(passwd)) {
				ToastUtil.showShortToast(LoginActivity.this, "请输入密码");
				progresscircle.dismiss();
				return;
			}
			Log.d(LOG_TAG, "密码：" + Constant.SHA1(passwd));
			validateUser(username, Constant.SHA1(passwd));
			break;

		default:
			break;
		}
	}

	void validateUser(String userName, String passwd) {
		HttpUtil.get(HttpUtil.INFOVEHICLE_URL + "userLogin/" + userName + "/"
				+ passwd, null, new JsonHttpResponseHandler() {
			@Override
			public void onFailure(Throwable error) {
				// TODO Auto-generated method stub
				ToastUtil.showShortToast(LoginActivity.this, "登录失败，请重试");
				progresscircle.dismiss();
			}

			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, response);
				try {
					if (response.getInt("status") == 1) {
						Log.d("Result", "登陆成功");
						SaveUserDate();
						Intent main = new Intent(LoginActivity.this,
								MainActivity.class);
						startActivity(main);
						progresscircle.dismiss();
						LoginActivity.this.finish();
						progresscircle.dismiss();
					} else {
						ToastUtil.showShortToast(LoginActivity.this,
								"账号或密码不正确，请重新输入");
						progresscircle.dismiss();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		/*Log.d("Result", "登陆成功");
		SaveUserDate();
		Intent main = new Intent(LoginActivity.this,
				MainActivity.class);
		startActivity(main);
		progresscircle.dismiss();
		LoginActivity.this.finish();
		progresscircle.dismiss();*/
	}

	/**
	 * 保存Ip地址和端口
	 */
	void SaveIp() {
		String ip = et_ip.getText().toString();
		String port = et_port.getText().toString();
		// ip地址的正则表达
		Pattern pattern = Pattern
				.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\."
						+ "((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
		if ("".equals(ip)) {
			ToastUtil.showShortToast(LoginActivity.this, "请输入Ip地址");
			progresscircle.dismiss();
			return;
		} else if (!pattern.matcher(ip).find()) {// 不符合Ip地址的规范
			ToastUtil.showShortToast(LoginActivity.this, "Ip地址格式不正确");
			progresscircle.dismiss();
			return;
		}
		if ("".equals(port)) {
			ToastUtil.showShortToast(LoginActivity.this, "请输入端口号");
			progresscircle.dismiss();
			return;
		}
		//将Ip地址和端口号保存
		Constant.SaveUserDate(Constant.IP, ip);
		Constant.SaveUserDate(Constant.PORT, port);
		//更新Url地址
		HttpUtil.BASE_URL = "http://"+ip+":"+port+"/";// 实际地址
	}
}
