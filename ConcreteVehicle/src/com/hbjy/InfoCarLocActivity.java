package com.hbjy;

import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.hbjy.beans.VehicleGps;
import com.hbjy.carlocation.R;
import com.hbjy.nets.HttpUtil;
import com.hbjy.nets.ToastUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class InfoCarLocActivity extends Activity implements OnClickListener {
	private final String LOG_TAG = "InfoCarLocActivity";
	private CarInfoApplication app;
	private Intent it;

	private ImageView back;
	private MapView mMapView = null;
	private MapController mMapController = null;
	private ImageView iv_myloaction = null;
	private ImageView iv_trackplay = null;
	private ImageView iv_mycar_location = null;
	private LinearLayout ll_jishi;

	private Toast mToast;

	// 车辆定位图层
	private LocationData mCarLocData;
	private LocationOverlay myCarLocationOverlay = null;

	// 定位相关
	private LocationClient mLocClient;
	private LocationData mPosLocData;
	private MyLocationOverlay myLocationOverlay = null;
	private PopupOverlay mPosPopupOverlay = null;// 弹出泡泡图层，浏览节点时使用
	private String mCity = "";// 定位城市
	// private BDLocation location;

	private boolean isRequest = false;// 是否手动触发请求定位
	private boolean isFirstLoc = true;// 是否首次定位

	private PopupOverlay mPopupOverlay = null;// 弹出泡泡图层，浏览节点时使用
	private View viewCache;
	private View viewCache_Pos;

	private boolean IsShowPopupOverlay = true;// 默认显示,车辆位置
	private boolean IsShowPosPopupOverlay = true;// 默认显示,我的位置
	// 地图图层显示类别kl
	private ImageView iv_map_layer;
	private ImageView iv_map_lukuang;
	private PopupWindow popWindow;
	// 图层路况kl
	private View viewMapMode;
	private TextView tv_mapmode_3d;
	private TextView tv_mapmode_satellite;
	private TextView tv_mapmode_plain;
	boolean islukuangopen = false;
	// 进度环kl
	private ProgressDialog progresscircle;
	// 定义提示对话框

	// 计时器
	private TextView tv_shuaxintime = null;
	private int clocktt = 31;
	private final int update = 1;
	private Timer mTimer;
	private TimerTask mTimerTask;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case update:
				if (clocktt < 0) {
					// requestLocation();
					mMapView.getOverlays().clear();
					tv_location_tips.setText("");
					stopTimer();
					getDataById();
				}
				tv_shuaxintime.setText(clocktt + "");
				break;

			case 5:// 更新获得的地理位置信息
					// 将地址信息、兴趣点信息显示在TextView上
				String sb = (String) msg.obj;
				Log.d("结果", sb.toString());
				pb.setVisibility(ProgressBar.GONE);
				tv_location_tips.setText(sb.toString());
				mPopupOverlay.hidePop();
				mPopupOverlay.showPopup(
						viewCache,
						new GeoPoint((int) (vg.getLatitude() * 1e6), (int) (vg
								.getLongitude() * 1e6)), 10);// 重新加载
				tv_shuaxintime.setText(clocktt + "");

				// showPopupOverlay(result.geoPt.getLatitudeE6() / 1e6,
				// result.geoPt.getLongitudeE6() / 1e6/* ,sb.toString() */);
				startTimer();
				ll_jishi.setVisibility(LinearLayout.VISIBLE);
				break;
			default:
				break;
			}
		}
	};
	// 放大缩小按钮
	private Button btn_zoomIn;
	private Button btn_zoomOut;
	private int maxZoomLevel;
	private int minZoomLevel;

	private String url;
	private VehicleGps vg = null;// 一辆车的信息
	private TextView tv_location_tips;// 地理位置信息
	ProgressBar pb;// 进度条
	private MKSearch mkSearch;

	// 线路规划
	// private MKSearch mNavMkSearch;
	private Button btn_pop_nav;
	private Dialog bundler;
	private View dialogview;
	private Button btn_drive;
	private Button btn_walk;
	private Button btn_bus;
	private Button btn_baidunav;
	private Button btn_cancel;

	View.OnClickListener targetnavclick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			MKPlanNode startMKPlanNode = new MKPlanNode();
			startMKPlanNode.pt = new GeoPoint(
					(int) (InfoCarLocActivity.this.mPosLocData.latitude * 1e6),
					(int) (InfoCarLocActivity.this.mPosLocData.longitude * 1e6));
			MKPlanNode endMKPlanNode = new MKPlanNode();
			endMKPlanNode.pt = new GeoPoint(
					(int) (InfoCarLocActivity.this.mCarLocData.latitude * 1e6),
					(int) (InfoCarLocActivity.this.mCarLocData.longitude * 1e6));
			;
			switch (v.getId()) {
			case R.id.btn_cancel:
				if (InfoCarLocActivity.this.bundler != null)
					InfoCarLocActivity.this.bundler.dismiss();
				break;
			case R.id.btn_baidunav:// 百度地图导航
				if (InfoCarLocActivity.this.bundler != null)
					InfoCarLocActivity.this.bundler.dismiss();
				// 使用URI API导航
				String uri = "intent://map/direction?origin=latlng:"
						+ InfoCarLocActivity.this.mPosLocData.latitude
						+ ","
						+ InfoCarLocActivity.this.mPosLocData.longitude
						+ "|name:我的位置&destination=latlng:"
						+ InfoCarLocActivity.this.mCarLocData.latitude
						+ ","
						+ InfoCarLocActivity.this.mCarLocData.longitude
						+ "|name:车辆位置&mode=driving&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end";
				try {
					startActivityForResult(Intent.getIntent(uri), 0);
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case R.id.btn_drive:
				if (InfoCarLocActivity.this.bundler != null)
					InfoCarLocActivity.this.bundler.dismiss();
				InfoCarLocActivity.this.mkSearch
						.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
				InfoCarLocActivity.this.mkSearch.drivingSearch(null,
						startMKPlanNode, null, endMKPlanNode);
				Log.d("起点", "经度：" + startMKPlanNode.pt.getLatitudeE6() + ",纬度:"
						+ startMKPlanNode.pt.getLongitudeE6());
				Log.d("终点", "经度：" + endMKPlanNode.pt.getLatitudeE6() + ",纬度:"
						+ endMKPlanNode.pt.getLongitudeE6());
				break;
			case R.id.btn_walk:// 步行
				if (InfoCarLocActivity.this.bundler != null)
					InfoCarLocActivity.this.bundler.dismiss();
				InfoCarLocActivity.this.mkSearch.walkingSearch(null,
						startMKPlanNode, null, endMKPlanNode);
				break;
			case R.id.btn_bus:
				if (InfoCarLocActivity.this.bundler != null)
					InfoCarLocActivity.this.bundler.dismiss();
				Log.d("城市", "城市：" + InfoCarLocActivity.this.mCity);
				if (mCity != null && !"".equals(InfoCarLocActivity.this.mCity)) {
					Log.d("城市", "城市不为空");
					InfoCarLocActivity.this.mkSearch
							.setTransitPolicy(MKSearch.EBUS_TRANSFER_FIRST);
					InfoCarLocActivity.this.mkSearch.transitSearch(mCity,
							startMKPlanNode, endMKPlanNode); // 必须设置城市名
				} else {
					InfoCarLocActivity.this.mkSearch
							.setTransitPolicy(MKSearch.EBUS_TRANSFER_FIRST);
					InfoCarLocActivity.this.mkSearch.transitSearch("武汉",
							startMKPlanNode, endMKPlanNode); // 必须设置城市名
					Log.d("城市", "城市为空");
					ToastUtil.showShortToast(InfoCarLocActivity.this,"定位失败,不能获取城市名称");
				}
				break;

			}
		}

	};
	View.OnClickListener map_Layerclick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			;
			switch (v.getId()) {
			case R.id.tv_mapmode_3d:
				// iv_mapmode_3d.setSelected(true);
				// iv_mapmode_plain.setSelected(false);
				// iv_mapmode_satellite.setSelected(false);
				tv_mapmode_3d.setSelected(true);
				tv_mapmode_plain.setSelected(false);
				tv_mapmode_satellite.setSelected(false);
				mMapController.setRotation(90);
				mMapController.setOverlooking(-30);
				break;
			case R.id.tv_mapmode_satellite:
				// iv_mapmode_satellite.setSelected(true);
				// iv_mapmode_3d.setSelected(false);
				// iv_mapmode_plain.setSelected(false);
				tv_mapmode_satellite.setSelected(true);
				tv_mapmode_3d.setSelected(false);
				tv_mapmode_plain.setSelected(false);
				mMapView.setSatellite(true);
				mMapController.setRotation(0);
				mMapController.setOverlooking(90);
				break;
			case R.id.tv_mapmode_plain:
				// iv_mapmode_plain.setSelected(true);
				// iv_mapmode_3d.setSelected(false);
				// iv_mapmode_satellite.setSelected(false);
				tv_mapmode_plain.setSelected(true);
				tv_mapmode_3d.setSelected(false);
				tv_mapmode_satellite.setSelected(false);
				mMapView.setSatellite(false);
				mMapController.setRotation(0);
				mMapController.setOverlooking(90);

				break;
			}
		}

	};
	// 屏幕高度和宽度
	private int height, width;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		app = (CarInfoApplication) this.getApplication();
		app.initEngineManager(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_info_carloc);
		showProgressCircle();
		// 获得传过来的参数值
		it = getIntent();
		getDataById();

		setUpView();

	}

	void showProgressCircle() {
		progresscircle = new ProgressDialog(InfoCarLocActivity.this);
		progresscircle.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progresscircle.setMessage("正在加载车辆实时信息，请稍等");
		progresscircle.setIndeterminate(false);
		progresscircle.setCancelable(true);
		progresscircle.show();
	}

	void setUpView() {
		tv_shuaxintime = (TextView) findViewById(R.id.tv_shuaxintime);
		back = (ImageView) super.findViewById(R.id.iv_return);
		iv_myloaction = (ImageView) findViewById(R.id.my_location);
		iv_trackplay = (ImageView) findViewById(R.id.track_play);
		iv_mycar_location = (ImageView) findViewById(R.id.iv_mycar_location);
		ll_jishi = (LinearLayout) findViewById(R.id.ll_jishi);

		btn_zoomIn = (Button) findViewById(R.id.btn_zoomin);// 放大
		btn_zoomOut = (Button) findViewById(R.id.btn_zoomout);// 缩小

		// 地图模式kl
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		viewMapMode = layoutInflater.inflate(R.layout.popview_map_layer, null);
		iv_map_layer = (ImageView) findViewById(R.id.iv_map_layer);
		iv_map_lukuang = (ImageView) findViewById(R.id.iv_map_lukuang);
		tv_mapmode_3d = (TextView) viewMapMode.findViewById(R.id.tv_mapmode_3d);
		tv_mapmode_satellite = (TextView) viewMapMode
				.findViewById(R.id.tv_mapmode_satellite);
		tv_mapmode_plain = (TextView) viewMapMode
				.findViewById(R.id.tv_mapmode_plain);

		// 线路规划
		// this.mNavMkSearch = new MKSearch();
		// initmNavSearch();//初始化监听
		dialogview = LayoutInflater.from(this).inflate(
				R.layout.dialog_target_nav, null);
		btn_drive = (Button) dialogview.findViewById(R.id.btn_drive);
		btn_walk = (Button) dialogview.findViewById(R.id.btn_walk);
		btn_bus = (Button) dialogview.findViewById(R.id.btn_bus);
		btn_baidunav = (Button) dialogview.findViewById(R.id.btn_baidunav);
		btn_cancel = (Button) dialogview.findViewById(R.id.btn_cancel);

		viewCache = LayoutInflater.from(this).inflate(
				R.layout.popview_mycarlocation, null);
		viewCache.setOnClickListener(new OnClickListener() {
			Intent intent;

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("跳转", "跳转到详细页面");
			}
		});
		viewCache_Pos = LayoutInflater.from(this).inflate(
				R.layout.popview_mylocation, null);
		btn_pop_nav = (Button) viewCache_Pos.findViewById(R.id.btn_pop_nav);
		btn_pop_nav.setOnClickListener(this);
		WindowManager localWindowManager = (WindowManager) getSystemService("window");
		this.width = localWindowManager.getDefaultDisplay().getWidth();
		this.height = localWindowManager.getDefaultDisplay().getHeight();

		// init baidumap
		mMapView = (MapView) findViewById(R.id.bmapView);

		mkSearch = new MKSearch();
		mkSearch.init(app.mBMapManager, new MySearchListener());// 地址解析
		mMapController = mMapView.getController();
		mMapController.enableClick(true);
		mMapController.setZoom(18);
		maxZoomLevel = mMapView.getMaxZoomLevel();
		minZoomLevel = mMapView.getMinZoomLevel();
		Log.d(LOG_TAG, "latitude:" + it.getDoubleExtra("latitude", 0)
				+ ",longitude:" + it.getDoubleExtra("longitude", 0));
		mMapController.setCenter(new GeoPoint((int) (it.getDoubleExtra(
				"latitude", 0) * 1E6),
				(int) (it.getDoubleExtra("longitude", 0) * 1E6)));

		back.setOnClickListener(this);
		// 定位
		iv_myloaction.setOnClickListener(this);
		// 轨迹回放
		iv_trackplay.setOnClickListener(this);
		iv_mycar_location.setOnClickListener(this);

		// kl地图模式
		iv_map_layer.setOnClickListener(this);
		iv_map_lukuang.setOnClickListener(this);

		// 目标导航事件
		btn_drive.setOnClickListener(targetnavclick);
		btn_walk.setOnClickListener(targetnavclick);
		btn_bus.setOnClickListener(targetnavclick);
		btn_baidunav.setOnClickListener(targetnavclick);
		btn_cancel.setOnClickListener(targetnavclick);
		// 地图模式 目标导航事件kl
		// iv_mapmode_3d.setOnClickListener(map_Layerclick);
		// iv_mapmode_satellite.setOnClickListener(map_Layerclick);
		// iv_mapmode_plain.setOnClickListener(map_Layerclick);
		tv_mapmode_3d.setOnClickListener(map_Layerclick);
		tv_mapmode_satellite.setOnClickListener(map_Layerclick);
		tv_mapmode_plain.setOnClickListener(map_Layerclick);

		mPopupOverlay = new PopupOverlay(mMapView, null);

		mPosPopupOverlay = new PopupOverlay(mMapView, null);

		ZoomOnClickListener zoomOnclickListener = new ZoomOnClickListener();
		btn_zoomIn.setOnClickListener(zoomOnclickListener);
		btn_zoomOut.setOnClickListener(zoomOnclickListener);
	}

	void getDataById() {
		HttpUtil.get(
				HttpUtil.INFOVEHICLE_URL + "getVehicleGps/"
						+ it.getStringExtra("vehiclenum"), null,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(String content) {
						// TODO Auto-generated method stub
						if (content != null && !content.equals("")) {

							try {
								JSONObject jo = new JSONObject(content);
								vg = new VehicleGps(jo);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							if (vg != null) {
								initCarLocationOverlay();
							}
						} else {
							Log.d(LOG_TAG, "没有数据");
							new AlertDialog.Builder(InfoCarLocActivity.this)
									.setTitle("警告").setMessage("暂无GPS数据")
									.setPositiveButton("确定", null).show();

							stopTimer();
						}
						progresscircle.dismiss();

					}

					@Override
					public void onFailure(Throwable error) {
						// TODO Auto-generated method stub
						ToastUtil.showShortToast(InfoCarLocActivity.this,
								"刷新失败");
						progresscircle.dismiss();
						stopTimer();
					}
				});
	}

	void initCarLocationOverlay() {
		mCarLocData = new LocationData();
		double lat = vg.getLatitude();
		double longitude = vg.getLongitude();
		float direction = vg.getDirection();

		mCarLocData.latitude = lat;
		mCarLocData.longitude = longitude;
		// 如果不显示定位精度圈，将accuracy赋值为0即可
		mCarLocData.accuracy = 0;// 不显示精度圈
		// mLocData.direction = direction;

		// 定位图层初始化
		myCarLocationOverlay = new LocationOverlay(mMapView);
		// 设置定位数据
		myCarLocationOverlay.setData(mCarLocData);

		myCarLocationOverlay.setMarker(rotateDrawable(
				R.drawable.location_arrows, direction));
		mMapView.getOverlays().add(myCarLocationOverlay);
		myCarLocationOverlay.enableCompass();
		// 修改定位数据后刷新图层生效
		mMapView.refresh();
		GeoPoint geoPoint = new GeoPoint((int) (lat * 1e6),
				(int) (longitude * 1e6));
		mMapController.setCenter(geoPoint);
		mkSearch.reverseGeocode(geoPoint);// 地理位置反编译
		showPopupOverlay(lat, longitude);
	}

	// 旋转图片
	Drawable rotateDrawable(int r, float degree) {
		// 加载需要操作的图片，这里是eoeAndroid的logo图片
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), r);
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		return new BitmapDrawable(Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true));
	}

	private void initMyLocation() {
		mLocClient = new LocationClient(getApplicationContext());
		mPosLocData = new LocationData();
		mLocClient.registerLocationListener(new BDLocationListenerImpl());
		LocationClientOption localLocationClientOption = new LocationClientOption();
		localLocationClientOption.setOpenGps(true);
		localLocationClientOption.setAddrType("all");
		localLocationClientOption.setCoorType("bd09ll");
		localLocationClientOption.setScanSpan(10000);// 设置定时定位的时间间隔。单位ms
		this.mLocClient.setLocOption(localLocationClientOption);
		this.mLocClient.start();
		myLocationOverlay = new myLocationOverlay(mMapView);

		this.myLocationOverlay.setData(this.mPosLocData);
		this.mMapView.getOverlays().add(this.myLocationOverlay);
		this.myLocationOverlay.enableCompass();
		this.myLocationOverlay.isCompassEnable();
		this.mMapView.refresh();
	}

	/**
	 * 定位接口，需要实现两个方法
	 * 
	 * 
	 * 
	 */
	public class BDLocationListenerImpl implements BDLocationListener {

		/**
		 * 接收异步返回的定位结果，参数是BDLocation类型参数
		 */
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}
			mPosLocData.latitude = location.getLatitude();
			mPosLocData.longitude = location.getLongitude();
			// 如果不显示定位精度圈，将accuracy赋值为0即可
			mPosLocData.accuracy = location.getRadius();
			// mPosLocData.accuracy = 100;// 不显示精度圈
			mPosLocData.direction = location.getDerect();
			mCity = location.getCity();
			// 将定位数据设置到定位图层里
			myLocationOverlay.setData(mPosLocData);

			if (isFirstLoc || isRequest) {
				mMapController.animateTo(new GeoPoint((int) (location
						.getLatitude() * 1e6),
						(int) (location.getLongitude() * 1e6)));
				((TextView) viewCache_Pos.findViewById(R.id.tv_pop_content))
						.setText("我的位置\n精确到" + (int) location.getRadius() + "米");
				mPosPopupOverlay.showPopup(viewCache_Pos,
						new GeoPoint((int) (location.getLatitude() * 1e6),
								(int) (location.getLongitude() * 1e6)), 10);
				isRequest = false;
			}
			// 更新图层数据执行刷新后生效
			mMapView.refresh();
			isFirstLoc = false;
		}

		/**
		 * 接收异步返回的POI查询结果，参数是BDLocation类型参数
		 */
		@Override
		public void onReceivePoi(BDLocation poiLocation) {

		}

	}

	private void showPopupOverlay(double latitude, double longitude/*
																	 * ,String
																	 * location_tips
																	 */) {

		tv_location_tips = ((TextView) viewCache
				.findViewById(R.id.tv_location_tips));
		pb = (ProgressBar) viewCache.findViewById(R.id.progressBar1);
		pb.setVisibility(ProgressBar.VISIBLE);
		TextView timeText = (TextView) viewCache.findViewById(R.id.tv_time);
		TextView tv_speed = (TextView) viewCache.findViewById(R.id.tv_speed);
		TextView tv_mileage = (TextView) viewCache
				.findViewById(R.id.tv_mileage);
		TextView tv_oilVolumn = (TextView) viewCache
				.findViewById(R.id.tv_oilVolumn);
		TextView tv_status = (TextView) viewCache.findViewById(R.id.tv_status);
		// tv_location_tips.setText(location_tips);
		((TextView) viewCache.findViewById(R.id.tv_vehicleId)).setText(vg
				.getVehicleId());
		timeText.setText(vg.getRecordtime());
		tv_speed.setText(vg.getSpeed() + " Km/h");
		tv_mileage.setText(vg.getGpsmileage() + "Km");
		tv_oilVolumn.setText(vg.getOilVolumn() + "L");
		tv_status.setText(vg.getAccstatus());

		mPopupOverlay.showPopup(viewCache, new GeoPoint((int) (latitude * 1e6),
				(int) (longitude * 1e6)), 10);
	}

	/**
	 * 手动请求定位的方法
	 */
	public void requestLocation() {
		isRequest = true;

		if (mLocClient != null && mLocClient.isStarted()) {
			ToastUtil.showShortToast(InfoCarLocActivity.this,"正在定位......");
			mLocClient.requestLocation();
		} else {
			Log.d("LocSDK3", "locClient is null or not started");
		}
	}

	@Override
	protected void onPause() {
		/**
		 * MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		 */
		mMapView.onPause();
		stopTimer();
		tv_shuaxintime.setText("30");
		Log.d("onPause", "onPause");
		super.onPause();
	}

	@Override
	protected void onResume() {
		/**
		 * MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		 */
		mMapView.onResume();
		Log.d("onResume", "onResume");
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		/**
		 * MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		 */
		// 退出时销毁定位
		if (mLocClient != null)
			mLocClient.stop();
		mMapView.destroy();
		stopTimer();
		Log.d("onDestroy", "onDestroy");
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}


	/**
	 * 讲XML布局文件转化成Bitmap图像
	 * 
	 * @param view
	 * @return
	 */
	public static Bitmap getBitmapFromView(View view) {
		view.setDrawingCacheEnabled(true);
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}

	private void startTimer() {
		if (mTimer == null) {
			mTimer = new Timer();
		}

		if (mTimerTask == null) {
			mTimerTask = new TimerTask() {
				@Override
				public void run() {
					clocktt--;
					mHandler.sendEmptyMessage(update);
				}
			};
		}

		if (mTimer != null && mTimerTask != null)
			mTimer.schedule(mTimerTask, 1000, 1000);

	}

	private void stopTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}
		clocktt = 30;// 初始化为30秒
	}

	// 车辆位置
	private class LocationOverlay extends MyLocationOverlay {

		public LocationOverlay(MapView arg0) {
			super(arg0);
		}

		@Override
		protected boolean dispatchTap() {
			if (mPopupOverlay != null) {
				if (IsShowPopupOverlay)// 已经显示就隐藏
				{
					mPopupOverlay.hidePop();
					IsShowPopupOverlay = false;// 设置为隐藏
				} else {
					mPopupOverlay.showPopup(viewCache,
							new GeoPoint((int) (vg.getLatitude() * 1e6),
									(int) (vg.getLongitude() * 1e6)), 10);
					IsShowPopupOverlay = true;// 设置为显示
				}
			}

			return super.dispatchTap();
		}

		@Override
		public void setMarker(Drawable arg0) {
			super.setMarker(arg0);
		}
	}

	// 我的位置
	private class myLocationOverlay extends MyLocationOverlay {

		public myLocationOverlay(MapView arg0) {
			super(arg0);
		}

		@Override
		protected boolean dispatchTap() {
			if (mPosPopupOverlay != null) {
				if (IsShowPosPopupOverlay)// 已经显示就隐藏
				{
					mPosPopupOverlay.hidePop();
					IsShowPosPopupOverlay = false;// 设置为隐藏
				} else {
					mPosPopupOverlay.showPopup(viewCache_Pos, new GeoPoint(
							(int) (mPosLocData.latitude * 1e6),
							(int) (mPosLocData.longitude * 1e6)), 10);
					IsShowPosPopupOverlay = true;// 设置为显示
				}
			}
			return super.dispatchTap();
		}

	}

	private class ZoomOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int j = 0;
			switch (v.getId()) {
			case R.id.btn_zoomin:

				mMapView.getController().zoomIn();
				j = (int) mMapView.getZoomLevel();
				InfoCarLocActivity.this.refreshZoomButtonStatus(j);
				break;
			case R.id.btn_zoomout:

				mMapView.getController().zoomOut();
				j = (int) mMapView.getZoomLevel();
				InfoCarLocActivity.this.refreshZoomButtonStatus(j);
				break;
			default:
				break;
			}

		}

	}

	public void refreshZoomButtonStatus(int paramInt) {
		// TODO Auto-generated method stub
		if (this.mMapView == null)
			throw new NullPointerException(
					"you can call setMapView(MapView mapView) at first");
		if ((paramInt > this.minZoomLevel) && (paramInt < this.maxZoomLevel)) {
			if (!this.btn_zoomIn.isEnabled()) {
				this.btn_zoomIn.setEnabled(true);
				this.btn_zoomIn
						.setBackgroundResource(R.drawable.btn_map_zoomin_bg);
			}
			if (!this.btn_zoomOut.isEnabled()) {
				this.btn_zoomOut.setEnabled(true);
				this.btn_zoomOut
						.setBackgroundResource(R.drawable.btn_map_zoomout_bg);
			}
		}

		if (paramInt == this.maxZoomLevel) {
			this.btn_zoomIn.setEnabled(false);
			this.btn_zoomIn
					.setBackgroundResource(R.drawable.map_zoomin_disable);

		}
		if (paramInt == this.minZoomLevel) {
			this.btn_zoomOut.setEnabled(false);
			this.btn_zoomOut
					.setBackgroundResource(R.drawable.map_zoomout_disable);

		}

	}

	private void restorePlusBtn() {
		// TODO Auto-generated method stub

	}

	private void showPopWindow(View parent) {
		if (popWindow == null) {

			popWindow = new PopupWindow(viewMapMode, width,
					R.layout.popview_map_layer);
		}
		popWindow.setFocusable(true);
		popWindow.setOutsideTouchable(true);
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		popWindow.showAsDropDown(parent, Gravity.LEFT, 0);
		popWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				restorePlusBtn();
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.track_play:

			if (mCarLocData == null) {
				new AlertDialog.Builder(InfoCarLocActivity.this).setTitle("警告")
						.setMessage("没有轨迹信息").setPositiveButton("确定", null)
						.show();
			} else {
				Intent intent = new Intent(InfoCarLocActivity.this,
						TrackPlayActivity.class);
				intent.putExtra("longitude", mCarLocData.longitude);
				intent.putExtra("latitude", mCarLocData.latitude);
				intent.putExtra("vehiclenum", this.vg.getVehiclenum());
				intent.putExtra("vehicleId", this.vg.getVehicleId());
				startActivityForResult(intent, 1);
			}

			break;
		case R.id.my_location:
			mMapView.getOverlays().clear();
			ll_jishi.setVisibility(LinearLayout.INVISIBLE);
			stopTimer();
			if (mLocClient == null) {
				Log.d("第一次", "第一次启动");
				initMyLocation();
			} else {
				this.mMapView.getOverlays().remove(this.myLocationOverlay);
				this.myLocationOverlay = new LocationOverlay(this.mMapView);
				this.myLocationOverlay.setData(this.mPosLocData);
				this.mMapView.getOverlays().add(this.myLocationOverlay);
				this.myLocationOverlay.enableCompass();
				requestLocation();
			}

			break;
		case R.id.iv_return:
			finish();
			break;
		case R.id.iv_mycar_location:
			mMapView.getOverlays().clear();
			if (tv_location_tips != null)
				tv_location_tips.setText("");
			stopTimer();// 停止定时器

			getDataById();
			break;
		case R.id.btn_pop_nav:// 目标导航
			if (mCarLocData == null) {
				new AlertDialog.Builder(InfoCarLocActivity.this).setTitle("警告")
						.setMessage("无车辆位置信息,无法规划线路")
						.setPositiveButton("确定", null).show();
			} else {
				if (this.bundler == null) {
					this.bundler = new Dialog(this, R.style.dialog);
					this.bundler.setContentView(this.dialogview);
					Window localWindow = this.bundler.getWindow();
					WindowManager.LayoutParams localLayoutParams = localWindow
							.getAttributes();
					localLayoutParams.width = (-60 + this.width);
					localLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
					localWindow.setAttributes(localLayoutParams);
				}
				this.bundler.show();
			}

			break;
		case R.id.iv_map_layer:
			showPopWindow(v);
			break;
		case R.id.iv_map_lukuang:
			if (islukuangopen == false) {
				iv_map_lukuang.setImageResource(R.drawable.map_lukuang_open);
				mMapView.setTraffic(true);
				islukuangopen = true;
			} else {
				iv_map_lukuang.setImageResource(R.drawable.map_lukuang_close);
				mMapView.setTraffic(false);
				islukuangopen = false;
			}

			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		mMapView.getOverlays().clear();
		tv_location_tips.setText("");
		pb.setVisibility(ProgressBar.VISIBLE);
		stopTimer();// 停止定时器
		getDataById();
		super.onActivityResult(requestCode, resultCode, data);
	}

	// 地理位置解析
	class MySearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo result, int iError) {
			// TODO Auto-generated method stub
			// TODO Auto-generated method stub
			if (result == null) {
				Log.d("结果", "返回失败");
				return;
			}
			StringBuffer sb = new StringBuffer();
			// 经纬度所对应的位置
			sb.append(result.strAddr);
			Message msg = mHandler.obtainMessage();
			msg.what = 5;
			msg.obj = sb.toString();
			mHandler.sendMessage(msg);

		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult result,
				int iError) {
			// TODO Auto-generated method stub
			if (result == null || iError != 0) {
				ToastUtil.showShortToast(InfoCarLocActivity.this,"没有可以规划的驾车线路");
				return;
			}
			RouteOverlay routeOverlay = new RouteOverlay(
					InfoCarLocActivity.this, mMapView); // 此处仅展示一个方案作为示例
			routeOverlay.setData(result.getPlan(0).getRoute(0));
			mMapView.getOverlays().clear();
			mMapView.getOverlays().add(routeOverlay);
			InfoCarLocActivity.this.mMapController
					.animateTo(result.getStart().pt);
			mMapView.refresh();
		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
				int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult result,
				int iError) {
			// TODO Auto-generated method stub
			if (result == null || iError != 0) {
				ToastUtil.showShortToast(InfoCarLocActivity.this,"没有可以规划的公交线路");
				return;
			}
			TransitOverlay transitOverlay = new TransitOverlay(
					InfoCarLocActivity.this, mMapView);
			// 此处仅展示一个方案作为示例
			transitOverlay.setData(result.getPlan(0));
			mMapView.getOverlays().clear();
			mMapView.getOverlays().add(transitOverlay);
			InfoCarLocActivity.this.mMapController
					.animateTo(result.getStart().pt);
		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult result,
				int iError) {
			// TODO Auto-generated method stub
			if (result == null || iError != 0) {
				ToastUtil.showShortToast(InfoCarLocActivity.this,"没有可以规划的步行路径");
				return;
			}
			RouteOverlay routeOverlay = new RouteOverlay(
					InfoCarLocActivity.this, mMapView); // 此处仅展示一个方案作为示例
			routeOverlay.setData(result.getPlan(0).getRoute(0));
			mMapView.getOverlays().clear();
			mMapView.getOverlays().add(routeOverlay);
			InfoCarLocActivity.this.mMapController
					.animateTo(result.getStart().pt);
		}

	}
}
