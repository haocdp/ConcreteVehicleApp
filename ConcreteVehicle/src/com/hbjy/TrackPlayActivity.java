package com.hbjy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.Symbol;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.hbjy.beans.VehicleGps;
import com.hbjy.carlocation.R;
import com.hbjy.nets.HttpUtil;
import com.hbjy.nets.ToastUtil;
import com.hbjy.utils.Constant;
import com.loopj.android.http.JsonHttpResponseHandler;

public class TrackPlayActivity extends Activity implements OnClickListener {

	private String LOG_TAG = "TrackPlayActivity";
	private ImageView iv_rechoicetrack;
	private ImageView iv_record;
	private MapView mMapView;
	private MapController mMapController = null;
	private SeekBar seekbar;
	private Button btn_sudu;
	private Button btn_startorstop;
	private ImageView iv_return;
	private TextView tv_startinfo;
	private TextView tv_endinfo;

	private String vehicleId;// 车辆编号

	// 屏幕高度和宽度
	private int height, width;

	private Intent intent;

	// 弹出框的控件
	private View view;
	private AlertDialog dialog;
	private Button btn_inputTimeOk;
	private Button btn_inputTimeCancel;
	private EditText et_inputEndDate;
	private EditText et_inputEndTime;
	private Spinner s_selectTime;
	private Spinner s_selectSpeed;
	private int totalPoints = 0;

	// 放大缩小按钮
	private Button btn_zoomIn;
	private Button btn_zoomOut;
	private int maxZoomLevel;
	private int minZoomLevel;

	// 轨迹回放
	private List<GeoPoint> points;
	private List<VehicleGps> vehicleTracks;
	private PlayTrackThread playtrackThread;
	OverlayTrack itemOverlay;
	OverlayItem item;
	private int startPoint;// 经度
	private String endDateTime;// 结束时间
	private String startDateTime;// 开始时间
	double maxSpeed = -1;// 速度
	private View popView;
	// popView里面的控件
	private TextView tv_time = null;
	private TextView tv_speed = null;
	private TextView tv_mileage = null;
	private TextView tv_position = null;

	private TextView tv_vehicleId;
	private LinearLayout ll__buttom_menu;

	private ProgressDialog progresscircle;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:// 播放轨迹
				if (itemOverlay != null) {
					itemOverlay.removeItem(item);
					Log.d("移除", "移除");
					if (startPoint < points.size()) {
						Log.d("画点", "画点");
						item = new OverlayItem(points.get(startPoint), "停留", "");
						item.setMarker(rotateDrawable(
								R.drawable.location_arrows,
								vehicleTracks.get(startPoint).getDirection()));
						itemOverlay.addItem(item);
					}
				}

				initPopview(startPoint, true);// 默认显示
				mMapView.refresh();

				break;
			case 3:// 获取数据成功
				Log.d("开始播放线程", "开始播放线程");
				ll__buttom_menu.setVisibility(LinearLayout.VISIBLE);// seekbar可见

				playtrackThread = new PlayTrackThread();
				new Thread(playtrackThread).start();
				progresscircle.dismiss();
				break;
			}
		}

	};

	private Date currentDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_play);
		intent = getIntent();
		points = new ArrayList<GeoPoint>();
		vehicleTracks = new ArrayList<VehicleGps>();
		WindowManager wm = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth();
		height = wm.getDefaultDisplay().getHeight();
		vehicleId = intent.getStringExtra("vehicleId");
		findView();
		setupView();

	}

	private void setupView() {
		// TODO Auto-generated method stub
		iv_rechoicetrack.setOnClickListener(this);
		iv_record.setOnClickListener(this);
		btn_startorstop.setOnClickListener(this);
		btn_sudu.setOnClickListener(this);
		iv_return.setOnClickListener(this);

		// 放大缩小事件
		btn_zoomIn.setOnClickListener(this);
		btn_zoomOut.setOnClickListener(this);

		DialogOnclickListener dialogOnclickListener = new DialogOnclickListener();
		btn_inputTimeCancel.setOnClickListener(dialogOnclickListener);
		btn_inputTimeOk.setOnClickListener(dialogOnclickListener);
		et_inputEndDate.setOnClickListener(dialogOnclickListener);
		et_inputEndTime.setOnClickListener(dialogOnclickListener);

		seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				TrackPlayActivity.this.startPoint = progress;
				mHandler.sendEmptyMessage(0);

			}
		});
	}

	private void findView() {
		// TODO Auto-generated method stub
		iv_rechoicetrack = (ImageView) findViewById(R.id.iv_rechoicetrack);
		iv_record = (ImageView) findViewById(R.id.iv_record);
		iv_return = (ImageView) findViewById(R.id.iv_return);
		tv_startinfo = (TextView) findViewById(R.id.tv_startinfo);// 开始点信息
		tv_endinfo = (TextView) findViewById(R.id.tv_endinfo);// 结束点信息
		seekbar = (SeekBar) findViewById(R.id.seekbar);
		btn_startorstop = (Button) findViewById(R.id.btn_startorstop);
		btn_sudu = (Button) findViewById(R.id.btn_sudu);
		mMapView = (MapView) findViewById(R.id.locationMapView);
		// 放大缩小按钮
		btn_zoomIn = (Button) findViewById(R.id.btn_zoomin);
		btn_zoomOut = (Button) findViewById(R.id.btn_zoomout);
		ll__buttom_menu = (LinearLayout) findViewById(R.id.ll__buttom_menu);
		view = LayoutInflater.from(this).inflate(
				R.layout.dialog_track_condition, null);
		btn_inputTimeCancel = (Button) view.findViewById(R.id.inputTimeCancel);
		btn_inputTimeOk = (Button) view.findViewById(R.id.inputTimeOk);

		et_inputEndDate = (EditText) view.findViewById(R.id.inputEndDate);
		et_inputEndTime = (EditText) view.findViewById(R.id.inputEndTime);
		s_selectTime = (Spinner) view.findViewById(R.id.s_selectTime);// 时间
		s_selectSpeed = (Spinner) view.findViewById(R.id.s_selectSpeed);// 速度

		popView = getLayoutInflater().inflate(R.layout.popview_tracklocation,
				null);// 详细信息泡泡窗口

		mMapController = mMapView.getController();
		mMapController.enableClick(true);
		mMapController.setZoom(14);
		double longitude = 0, latitude = 0;
		longitude = intent.getExtras().getDouble("longitude");
		latitude = intent.getExtras().getDouble("latitude");
		// 地图的放大级数
		maxZoomLevel = mMapView.getMaxZoomLevel();
		minZoomLevel = mMapView.getMinZoomLevel();

		mMapController.setCenter(new GeoPoint((int) (latitude * 1E6),
				(int) (longitude * 1E6)));

		dialog = new AlertDialog.Builder(this).create();
		Log.d(LOG_TAG, "屏幕宽度：" + this.width + ",高度：" + this.height);

		dialog.setView(view, 0, 0, 0, 0);

		InitCircleDialog();
		showDialog();

	}

	void InitCircleDialog() {
		progresscircle = new ProgressDialog(this);
		progresscircle.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progresscircle.setMessage("正在加载车辆轨迹中，请稍等");
		progresscircle.setIndeterminate(false);
		progresscircle.setCancelable(true);
	}

	void showDialog() {
		currentDate = Calendar.getInstance().getTime();
		et_inputEndDate.setText((new SimpleDateFormat("yyyy-MM-dd"))
				.format(currentDate));
		// 24小时格式
		et_inputEndTime.setText((new SimpleDateFormat("kk:mm:ss"))
				.format(currentDate));
		dialog.show();

		// 居中显示
		Window localWindow = this.dialog.getWindow();
		WindowManager.LayoutParams localLayoutParams = localWindow
				.getAttributes();
		// WindowManager.LayoutParams localLayoutParams = dialog.
		localLayoutParams.x = 0;
		localLayoutParams.y = 0;
		localLayoutParams.height = LayoutParams.WRAP_CONTENT;
		localLayoutParams.width = LayoutParams.WRAP_CONTENT;
		localWindow.setAttributes(localLayoutParams);

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

	private void initPopview(int position, boolean paramBoolean) {
		// if(this.mMapView.)
		VehicleGps v = vehicleTracks.get(position);
		this.mMapView.removeView(popView);

		this.tv_time = ((TextView) this.popView.findViewById(R.id.tv_time));
		this.tv_mileage = ((TextView) this.popView
				.findViewById(R.id.tv_mileage));
		this.tv_vehicleId = ((TextView) this.popView
				.findViewById(R.id.tv_vehicleId));
		this.tv_position = ((TextView) this.popView
				.findViewById(R.id.tv_position));

		this.tv_speed = ((TextView) this.popView.findViewById(R.id.tv_speed));

		tv_vehicleId.setText(vehicleId);
		tv_time.setText(v.getRecordtime());
		tv_speed.setText(v.getSpeed() + " Km/h");
		tv_mileage.setText(v.getGpsmileage() + " Km");
		tv_position.setText("[" + (startPoint + 1) + "/" + totalPoints + "]");

		((TextView) this.popView.findViewById(R.id.tv_oilVolumn)).setText(v
				.getOilVolumn() + " L");
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		((TextView) this.popView.findViewById(R.id.tv_latitude)).setText(df
				.format(v.getLatitude()));
		((TextView) this.popView.findViewById(R.id.tv_longitude)).setText(df
				.format(v.getLongitude()));
		if (v.getAccstatus().equals("null")) {
			((TextView) this.popView.findViewById(R.id.tv_status))
					.setText("暂无数据");
		} else {
			((TextView) this.popView.findViewById(R.id.tv_status)).setText(v
					.getAccstatus());
		}

		this.mMapView.addView(this.popView,
				new MapView.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT, points.get(position), 0,
						-30, MapView.LayoutParams.BOTTOM_CENTER));
		if (paramBoolean)
			this.popView.setVisibility(View.VISIBLE);
		else
			this.popView.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int j;
		switch (v.getId()) {
		case R.id.iv_rechoicetrack:
			showDialog();
			break;
		case R.id.iv_record:
			Intent it = new Intent(TrackPlayActivity.this, MileRecordList.class);
			it.putExtra("vehiclenum", intent.getStringExtra("vehiclenum"));
			it.putExtra("vehicleId", intent.getStringExtra("vehicleId"));
			TrackPlayActivity.this.startActivity(it);
			break;
		case R.id.btn_startorstop:
			if (!playtrackThread.suspendFlag) {// 线程正在执行
				this.btn_startorstop.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.btn_start_xml_bg));
				System.out.println("暂停线程");
				playtrackThread.setSuspendFlag();
			} else {
				System.out.println("线程是否完成：" + playtrackThread.mFinished);
				if (playtrackThread.mFinished && points.size() > 0) {
					TrackPlayActivity.this.startPoint = 0;
					itemOverlay.removeItem(item);
					new Thread(playtrackThread).start();
				}
				playtrackThread.setResume();
				this.btn_startorstop.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.btn_stop_xml_bg));
			}

			break;
		case R.id.btn_sudu:
			if (Constant.NORMALSPEED == playtrackThread.getSpeed()) {
				btn_sudu.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.two_speed_xml_bg));
				playtrackThread.setSpeed(Constant.NORMALSPEED / 2);
			} else {
				btn_sudu.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.one_speed_xml_bg));
				playtrackThread.setSpeed(Constant.NORMALSPEED);
			}

			break;
		case R.id.iv_return:
			finish();
			break;
		case R.id.btn_zoomin:// 放大
			mMapView.getController().zoomIn();
			j = (int) mMapView.getZoomLevel();
			TrackPlayActivity.this.refreshZoomButtonStatus(j);
			break;
		case R.id.btn_zoomout:// 缩小
			mMapView.getController().zoomOut();
			j = (int) mMapView.getZoomLevel();
			TrackPlayActivity.this.refreshZoomButtonStatus(j);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		mMapView.destroy();
		InterruptPlayThread();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		mMapView.onPause();

		super.onPause();
		if (vehicleTracks != null && vehicleTracks.size() > 0) {
			InterruptPlayThread();
			finish();
		}
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();

	}

	class DialogOnclickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.inputTimeCancel:

				dialog.dismiss();
				break;
			case R.id.inputTimeOk:
				TrackPlayActivity.this.btn_startorstop
						.setBackgroundDrawable(getResources().getDrawable(
								R.drawable.btn_stop_xml_bg));

				endDateTime = et_inputEndDate.getText().toString() + " "
						+ et_inputEndTime.getText().toString();
				// String startTime =
				int mTime = (s_selectTime.getSelectedItemPosition() + 1);
				if (mTime == 5)// 第5项
					mTime = 6;
				if (mTime == 6)// 第6项
					mTime = 12;
				int speed = s_selectSpeed.getSelectedItemPosition();

				switch (speed) {
				case 0:
					maxSpeed = -1;
					break;
				case 1:
					maxSpeed = 5;
					break;
				case 2:
					maxSpeed = 30;
					break;
				case 3:
					maxSpeed = 60;
					break;
				}
				Date dt = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				try {
					dt = sdf.parse(endDateTime);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				endDateTime = sdf.format(dt);// 结束时间
				dt.setHours(dt.getHours() - mTime);
				startDateTime = sdf.format(dt);// 开始时间

				InterruptPlayThread();// 中断线程
				mMapView.getOverlays().clear();// 清除所有图层
				mMapView.removeView(popView);
				dialog.dismiss();
				progresscircle.show();
				// 开始结束位置地理位置清除
				tv_startinfo.setText("");
				tv_endinfo.setText("");
				InitTrackData();
				break;
			case R.id.inputEndDate:
				et_inputEndDate.setFocusable(false);
				// et_timebegin.requestFocusFromTouch();
				et_inputEndDate.requestFocus();
				// et_inputEndDate.requestFocusFromTouch();
				// et_inputEndDate.requestFocus();

				final String[] oldDate = et_inputEndDate.getText().toString()
						.split("-");
				DatePickerDialog datePickDialog = new DatePickerDialog(
						TrackPlayActivity.this,
						new DatePickerDialog.OnDateSetListener() {

							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								// TODO Auto-generated method stub
								String month = (monthOfYear >= 9) ? String
										.valueOf(monthOfYear + 1)
										: ("0" + (monthOfYear + 1));
								String day = (dayOfMonth >= 10) ? String
										.valueOf(dayOfMonth)
										: ("0" + dayOfMonth);
								et_inputEndDate.setText(year + "-" + month
										+ "-" + day);
							}
						}, Integer.parseInt(oldDate[0]),
						Integer.parseInt(oldDate[1]) - 1,
						Integer.parseInt(oldDate[2]));
				datePickDialog.show();
				break;
			case R.id.inputEndTime:
				et_inputEndTime.setFocusable(false);
				// et_timebegin.requestFocusFromTouch();
				et_inputEndTime.requestFocus();
				// et_inputEndTime.requestFocusFromTouch();
				// et_inputEndTime.requestFocus();
				System.out.println("弹出时间窗口");
				final String[] oldtime = et_inputEndTime.getText().toString()
						.split(":");
				TimePickerDialog timePickDialog = new TimePickerDialog(
						TrackPlayActivity.this,
						new TimePickerDialog.OnTimeSetListener() {

							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute) {
								// TODO Auto-generated method stub
								String hour = (hourOfDay >= 10) ? String
										.valueOf(hourOfDay) : ("0" + hourOfDay);
								String min = (minute >= 10) ? String
										.valueOf(minute) : ("0" + minute);
								et_inputEndTime.setText(hour + ":" + min + ":"
										+ oldtime[2]);
							}
						}, Integer.parseInt(oldtime[0]), Integer
								.parseInt(oldtime[1]), true);
				timePickDialog.show();
				break;
			default:
				break;
			}

		}

	}

	public void InterruptPlayThread() {
		// TODO Auto-generated method stub
		if (playtrackThread != null) {
			playtrackThread.setmStop(true);
			if (playtrackThread.suspendFlag)// 阻塞了就唤醒
			{
				playtrackThread.setResume();
			}
			playtrackThread = null;
		}
	}

	class OverlayTrack extends ItemizedOverlay<OverlayItem> {

		public OverlayTrack(Drawable mark, MapView mapView) {
			super(mark, mapView);
			// TODO Auto-generated constructor stub
		}

		/**
		 * 单击图标事件
		 */
		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			// TODO Auto-generated method stub
			// System.out.println("onTap(GeoPoint arg0, MapView arg1)被点击");
			return super.onTap(arg0, arg1);
		}

		@Override
		protected boolean onTap(int arg0) {
			// TODO Auto-generated method stub
			// if(getItem(arg0)==item)
			// {
			// System.out.println("被点击");
			// }
			System.out.println("onTap(int arg0)被点击");
			return true;// 已经处理
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
						.setBackgroundResource(R.drawable.btn_map_szoomin_bg);
			}
			if (!this.btn_zoomOut.isEnabled()) {
				this.btn_zoomOut.setEnabled(true);
				this.btn_zoomOut
						.setBackgroundResource(R.drawable.btn_map_szoomout_bg);
			}
		}
	}

	class PlayTrackThread implements Runnable {

		private int speed;

		public int getSpeed() {
			return speed;
		}

		public void setSpeed(int speed) {
			this.speed = speed;
		}

		public boolean mFinished;// 用于判断一个线程是否执行完成
		public boolean suspendFlag = false;

		public PlayTrackThread() {
			// TODO Auto-generated constructor stub
			speed = Constant.NORMALSPEED;
			mFinished = false;
			startPoint = 0;
			mStop = false;
		}

		private boolean mStop;

		public void setmStop(boolean mStop) {
			this.mStop = mStop;
		}

		public PlayTrackThread(boolean IsReplay) {

			speed = Constant.NORMALSPEED;
			mFinished = false;
			startPoint = 0;
		}

		/*
		 * 暂停线程
		 */
		public void setSuspendFlag() {
			this.suspendFlag = true;
			mFinished = false;
		}

		/*
		 */
		public synchronized void setResume() {
			this.suspendFlag = false;
			notify();
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (startPoint < points.size() && !mStop) {
				// Drawable mark = getResources().getDrawable(
				// R.drawable.location_arrows);
				// itemOverlay = new OverlayTrack(mark, mMapView);
				final GeoPoint currentPoint = points.get(startPoint);
				// item = new OverlayItem(currentPoint, "停留", "停留");
				// 使用setMarker()方法设置overlay图片,如果不设置则使用构建ItemizedOverlay时的默认设置
				// item1.setMarker(mark);
				// 将IteminizedOverlay添加到MapView中
				// itemOverlay.addItem(item);
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						// 现在所有准备工作已准备好，使用以下方法管理overlay.
						// 添加overlay,
						// 当批量添加Overlay时使用addItem(List<OverlayItem>)效率更高
						mMapController.animateTo(currentPoint);
						mMapView.refresh();
						seekbar.setProgress(startPoint);
						if (startPoint == points.size() - 1) {
							TrackPlayActivity.this.btn_startorstop
									.setBackgroundDrawable(getResources()
											.getDrawable(
													R.drawable.btn_start_xml_bg));

						}
					}
				});

				try {
					Thread.sleep(speed);

					synchronized (this) {// 暂停线程
						if (suspendFlag) {
							wait();
						}
						// if (startPoint < points.size() - 1) {
						//
						// mMapView.getOverlays().remove(itemOverlay);
						// }
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					mStop = true;// 出现异常就置为true
					System.out.println("中断被唤醒");
					e.printStackTrace();
				}
				startPoint++;
				System.out.println("startPoint=" + startPoint);
			}
			suspendFlag = true;
			System.out.println("mFinishee=" + mFinished);
			mFinished = true;

		}

	}

	public void InitTrackData() {
		// TODO Auto-generated method stub

		// String url =
		// "http://61.183.9.107:4015/InfoVehicleService.svc/InfoVehicle/getVehicleTrace/VEHI20140624072433287949/20140902193028/20140902233028/-1.0";
		HttpUtil.get(
				HttpUtil.INFOVEHICLE_URL + "getVehicleTrace/"
						+ intent.getStringExtra("vehiclenum") + "/"
						+ startDateTime + "/" + endDateTime + "/" + maxSpeed,
				null, new JsonHttpResponseHandler() {
					@Override
					public void onFailure(Throwable error) {
						// TODO Auto-generated method stub
						ll__buttom_menu.setVisibility(LinearLayout.GONE);// seekbar隐藏
						ToastUtil.showShortToast(TrackPlayActivity.this,
								"访问车辆轨迹超时");
						progresscircle.dismiss();
					}

					@Override
					public void onSuccess(int statusCode, JSONArray response) {
						// TODO Auto-generated method stub
						if (response == null || response.length() == 0) {
							ll__buttom_menu.setVisibility(LinearLayout.GONE);// seekbar隐藏
							ToastUtil.showShortToast(TrackPlayActivity.this,
									"此时间段没有数据");
							progresscircle.dismiss();
							return;
						}
						for (int i = 0; i < response.length(); i++) {
							try {
								vehicleTracks.add(new VehicleGps(response
										.getJSONObject(i)));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								ll__buttom_menu
										.setVisibility(LinearLayout.GONE);// seekbar隐藏
								ToastUtil.showShortToast(
										TrackPlayActivity.this, "访问数据出错");
								progresscircle.dismiss();
								return;
							}
						}
						points.clear();
						for (VehicleGps v : vehicleTracks) {
							points.add(new GeoPoint(
									(int) (v.getLatitude() * 1E6), (int) (v
											.getLongitude() * 1E6)));
						}
						VehicleGps startGP = vehicleTracks.get(0);// 开始点
						VehicleGps endGP = vehicleTracks.get(vehicleTracks
								.size() - 1);// 最后一个点
						setStartEndText(startGP, endGP);
						totalPoints = vehicleTracks.size();
						seekbar.setMax(points.size() - 1);// 设置seekbar的最大值
						Log.d("Count", "VehicleTracks数量：" + totalPoints
								+ ",points数量：" + points.size());
						DrawLine();// 划线

					}
				});

	}

	public void DrawLine() {
		// TODO Auto-generated method stub
		Log.d("开始划线", "开始划线");
		itemOverlay = new OverlayTrack(getResources().getDrawable(
				R.drawable.location_arrows), mMapView);
		mMapView.getOverlays().add(itemOverlay);
		OverlayItem itemStart = new OverlayItem(points.get(0), "起点", "起点");
		itemStart.setMarker(getResources().getDrawable(R.drawable.start_point));
		itemOverlay.addItem(itemStart);
		OverlayItem itemEnd = new OverlayItem(points.get(points.size() - 1),
				"终点", "终点");
		itemEnd.setMarker(getResources().getDrawable(R.drawable.end_point));
		itemOverlay.addItem(itemEnd);

		GraphicsOverlay graphicsOverlay = new GraphicsOverlay(mMapView);
		// 构建线
		Geometry lineGeometry = new Geometry();
		GeoPoint[] geopoints = new GeoPoint[points.size()];
		geopoints = (GeoPoint[]) points.toArray(geopoints);
		lineGeometry.setPolyLine(geopoints);
		// 设定样式
		Symbol lineSymbol = new Symbol();
		Symbol.Color lineColor = lineSymbol.new Color();
		lineColor.red = 255;
		lineColor.green = 0;
		lineColor.blue = 0;
		lineColor.alpha = 255;
		lineSymbol.setLineSymbol(lineColor, 5);
		// 生成Graphic对象
		Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);
		graphicsOverlay.setData(lineGraphic);
		mMapView.getOverlays().add(graphicsOverlay);
		Log.d("划线成功", "划线成功");
		mHandler.sendEmptyMessage(3);// 更新列表

	}

	/*
	 * 设置起点和终点地理位置
	 */
	void setStartEndText(final VehicleGps startGP, final VehicleGps endGP) {
		Log.d(LOG_TAG, "解析地理位置");
		HttpUtil.get2(
				HttpUtil.getAddUriByLatLng(startGP.getLatitude() + "",
						startGP.getLongitude() + ""), null,
				new JsonHttpResponseHandler() {
					@Override
					public void onFailure(Throwable error) {
						// TODO Auto-generated method stub
						Spanned startPError = Html
								.fromHtml("<p><font size=\"4\" color=#118ee9>开始:<font size=\"2\" color=#108215>解析地理位置失败</font></p>");
						tv_startinfo.setText(startPError);
					}

					@Override
					public void onSuccess(int statusCode, JSONObject response) {
						// TODO Auto-generated method stub
						try {
							String location = response.getJSONObject("result")
									.getString("formatted_address");
							Spanned startP = Html
									.fromHtml("<p><font size=\"4\" color=#118ee9>开始:</font><font size=\"3\" color=#6d6d6d>"
											+ startGP.getRecordtime()
											+ " </font><font size=\"2\" color=#108215>[0]"
											+ location + "</font></p>");
							tv_startinfo.setText(startP);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							Spanned startPError = Html
									.fromHtml("<p><font size=\"4\" color=#118ee9>开始:<font size=\"2\" color=#108215>解析地理位置失败</font></p>");
							tv_startinfo.setText(startPError);
							e.printStackTrace();
						}

					}
				});

		HttpUtil.get2(
				HttpUtil.getAddUriByLatLng(endGP.getLatitude() + "",
						endGP.getLongitude() + ""), null,
				new JsonHttpResponseHandler() {

					@Override
					public void onFailure(Throwable error) {
						// TODO Auto-generated method stub
						Spanned endPError = Html
								.fromHtml("<p><font size=\"4\" color=#118ee9>结束:<font size=\"2\" color=#108215>解析地理位置失败</font></p>");
						tv_endinfo.setText(endPError);
					}

					@Override
					public void onSuccess(JSONObject response) {
						// TODO Auto-generated method stub
						try {
							String location = response.getJSONObject("result")
									.getString("formatted_address");
							Spanned endP = Html
									.fromHtml("<p><font size=\"4\" color=#118ee9>结束:</font><font size=\"3\" color=#6d6d6d>"
											+ endGP.getRecordtime()
											+ " </font><font size=\"2\" color=#108215>[0]"
											+ location + "</font></p>");
							tv_endinfo.setText(endP);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							Spanned endPError = Html
									.fromHtml("<p><font size=\"4\" color=#118ee9>结束:<font size=\"2\" color=#108215>解析地理位置失败</font></p>");
							tv_endinfo.setText(endPError);
							e.printStackTrace();
						}
					}

				});

	}

}
