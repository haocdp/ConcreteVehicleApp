package com.hbjy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hbjy.beans.VehicleMileageRecord;
import com.hbjy.carlocation.R;
import com.hbjy.nets.HttpUtil;
import com.hbjy.nets.ToastUtil;
import com.hbjy.utils.Constant;

public class MileRecordList extends Activity implements OnClickListener {

	private String LOG_TAG = "MileRecordList";
	private ImageView iv_back;
	private ImageView iv_choose_time;
	// private ImageButton ib_detailrecord;
	private ListView lv_milerecord;
	private TextView tv_vehicleId;// 车牌号
	private Calendar currentDate;
	private TextView tv_curdate;
	private RelativeLayout rl_content;
	private ProgressBar pb;
	private String vehicleNum;
	private String vehicleId;
	private TextView tv_avgOilfee;// 平均油耗
	private TextView tv_totaloils;// 总油耗
	private TextView tv_totalmiles;// 总里程
	private TextView tv_oilfee;// 参考油费

	private ListAdapter contentAdapter;// 显示列表的适配器
	private List<VehicleMileageRecord> contents = new ArrayList<VehicleMileageRecord>();// 车牌号
	private List<VehicleMileageRecord> vehicleRecords;// 存放临时数据
	private String startTime;
	private String endTime;
	private String BaseUrl;// url地址
	double totalmiles = 0;// 总里程
	double totaloils = 0;// 总油耗
	protected MyGestureListener myGestureListener;
	private java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 3:// 获取数据成功
				Log.d("数据加载完毕", "更新");
				pb.setVisibility(ProgressBar.GONE);
				lv_milerecord.setVisibility(View.VISIBLE);
				tv_totalmiles.setText(df.format(totalmiles) + " Km");
				tv_totaloils.setText(df.format(totaloils) + " L");
				if (totalmiles == 0)
					tv_avgOilfee.setText("0.0  元/Km");
				else {
					tv_avgOilfee.setText(totalmiles * Constant.OILFEES
							/ totalmiles + " 元/Km");
				}
				contents.clear();
				if (vehicleRecords.size() >= 0) {
					contents.addAll(vehicleRecords);
				}
				contentAdapter.notifyDataSetChanged();
				break;
			case 4:// 获取数据失败
				pb.setVisibility(ProgressBar.GONE);
				ToastUtil.showShortToast(MileRecordList.this, "此时间段没有数据");
				break;
			}
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mileage_record_list);
		vehicleRecords = new ArrayList<VehicleMileageRecord>();
		Intent it = getIntent();
		vehicleNum = it.getStringExtra("vehiclenum");// 车辆编号
		vehicleId = it.getStringExtra("vehicleId");
		BaseUrl = HttpUtil.getAbsoluteUrl(HttpUtil.INFOVEHICLE_URL)
				+ "getVehicleDriveRecord/" + vehicleNum + "/";
		findView();
		setupEvent();
		Init();
	}

	private void setupEvent() {
		// TODO Auto-generated method stub
		currentDate = Calendar.getInstance();
		iv_back.setOnClickListener(this);
		iv_choose_time.setOnClickListener(this);
		// ib_detailrecord.setOnClickListener(this);

		// 列表
		contentAdapter = new ListAdapter(this, contents);
		lv_milerecord.setAdapter(contentAdapter);
		tv_curdate.setText(String.valueOf(currentDate
				.get(Calendar.DAY_OF_MONTH)));

		// 监听手势事件
		myGestureListener = new MyGestureListener(this);
		// rl_content.setLongClickable(true);
		// rl_content.setOnTouchListener(myGestureListener);

	}

	private void findView() {
		// TODO Auto-generated method stub
		iv_back = (ImageView) findViewById(R.id.iv_return);
		iv_choose_time = (ImageView) findViewById(R.id.iv_choose_time);
		tv_curdate = (TextView) findViewById(R.id.tv_curdate);
		// ib_detailrecord=(ImageButton)findViewById(R.id.ib_mileDetailRecord);
		lv_milerecord = (ListView) findViewById(R.id.lv_mileage_record);
		tv_vehicleId = (TextView) findViewById(R.id.tv_vehicleId);
		tv_vehicleId.setText(vehicleId);
		pb = (ProgressBar) findViewById(R.id.progressBar1);
		tv_avgOilfee = (TextView) findViewById(R.id.tv_avgOilfee);
		tv_totaloils = (TextView) findViewById(R.id.tv_totaloils);
		tv_totalmiles = (TextView) findViewById(R.id.tv_totalmiles);
		tv_oilfee = (TextView) findViewById(R.id.tv_oilfee);
		tv_oilfee.setText(Constant.OILFEES + " 元");// 默认油价
		rl_content = (RelativeLayout) findViewById(R.id.rl_content);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_return:
			finish();
			break;
		case R.id.iv_choose_time:
			DatePickerDialog datePickDialog = new DatePickerDialog(
					MileRecordList.this,
					new DatePickerDialog.OnDateSetListener() {
						private boolean mFired = true;// 是否第一次执行

						@Override
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							// TODO Auto-generated method stub
							if (mFired) {
								currentDate.set(Calendar.YEAR, year);
								currentDate.set(Calendar.MONTH, monthOfYear);
								currentDate.set(Calendar.DAY_OF_MONTH,
										dayOfMonth);
								tv_curdate.setText(String.valueOf(dayOfMonth));
								Init();
								mFired = false;
							} else
								mFired = true;

						}
					}, currentDate.get(Calendar.YEAR),
					currentDate.get(Calendar.MONTH),
					currentDate.get(Calendar.DAY_OF_MONTH));
			datePickDialog.show();
			break;
		default:
			break;
		}
	}

	private void Init() {
		// String selectDate = tv_selectDate.getText().toString().replace("-",
		// "");
		pb.setVisibility(View.VISIBLE);
		lv_milerecord.setVisibility(View.GONE);

		InitRecordData();
	}

	// 获得行车记录数据
	public void InitRecordData() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String date = df.format(currentDate.getTime());
		startTime = date + "000000";
		endTime = date + "235959";
		if (vehicleRecords != null)
			vehicleRecords = new ArrayList<VehicleMileageRecord>();
		else
			vehicleRecords.clear();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String url = BaseUrl + startTime + "/" + endTime;
				String result;
				try {
					result = HttpUtil.getResponseString(url);

					if ("[]".equals(result)) {
						Log.d("Result", "没有数据");
						mHandler.sendEmptyMessage(4);// 没有数据
						return;
					}
					JSONArray response = new JSONArray(result);
					totalmiles = 0;
					totaloils = 0;
					for (int i = 0; i < response.length(); i++) {
						try {
							VehicleMileageRecord vmr = new VehicleMileageRecord(
									response.getJSONObject(i));
							vmr.setStartPoint(HttpUtil.getAddressByLatLng(
									vmr.getS_latitude() + "",
									vmr.getS_longitude() + ""));
							vmr.setEndPoint(HttpUtil.getAddressByLatLng(
									vmr.getE_latitude() + "",
									vmr.getE_longitude() + ""));
							vehicleRecords.add(vmr);
							totalmiles += vmr.getMileage();
							totaloils += vmr.getOilVolumn();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							mHandler.sendEmptyMessage(4);// 更新列表
							return;
						}
					}
					mHandler.sendEmptyMessage(3);// 更新列表
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					mHandler.sendEmptyMessage(4);// 没有数据
				}
			}

		}).start();

	}

	public class ListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private List<VehicleMileageRecord> contents;

		public ListAdapter(Context context) {
			super();
			// TODO Auto-generated constructor stub
			mInflater = LayoutInflater.from(context);
		}

		public ListAdapter(Context context, List<VehicleMileageRecord> contents) {
			super();
			// TODO Auto-generated constructor stub
			mInflater = LayoutInflater.from(context);
			this.contents = contents;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return contents.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return contents.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;

			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.mileage_record_list_item, null);
				holder = new ViewHolder();
				holder.tv_startTime = (TextView) convertView
						.findViewById(R.id.tv_startTime);
				holder.tv_startPoint = (TextView) convertView
						.findViewById(R.id.tv_startPoint);
				holder.tv_endTime = (TextView) convertView
						.findViewById(R.id.tv_endTime);
				holder.tv_endPoint = (TextView) convertView
						.findViewById(R.id.tv_endPoint);
				holder.tv_distance = (TextView) convertView
						.findViewById(R.id.tv_distance);
				holder.tv_parkTime = (TextView) convertView
						.findViewById(R.id.tv_parkTime);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			VehicleMileageRecord vmr = contents.get(position);
			holder.tv_startTime.setText(vmr.getStarttime().replace("/", "-")
					+ " 起点");
			holder.tv_startPoint.setText(vmr.getStartPoint());
			holder.tv_endTime.setText(vmr.getEndtime().replace("/", "-")
					+ " 终点");
			holder.tv_endPoint.setText(vmr.getEndPoint());
			holder.tv_distance.setText(df.format(vmr.getMileage()) + " Km");

			holder.tv_parkTime.setText(vmr.getPeriodtime());//

			return convertView;
		}

		public class ViewHolder {
			public TextView tv_startTime;
			public TextView tv_startPoint;
			public TextView tv_endTime;
			public TextView tv_endPoint;
			public TextView tv_distance;
			public TextView tv_parkTime;

		}

	}

	public class MyGestureListener extends SimpleOnGestureListener implements
			OnTouchListener// GDetector 名字是随便起的
	{
		Context context;
		GestureDetector gDetector;

		public MyGestureListener() {
			super();
		}

		public MyGestureListener(Context context) {
			this(context, null);
		}

		public MyGestureListener(Context context, GestureDetector gDetector) {

			if (gDetector == null)
				this.gDetector = new GestureDetector(context, this);
			else
				this.gDetector = gDetector;
			this.context = context;
		}

		// MotionEvent e1 按下时的状态,位置
		// MotionEvent e2, //松手时的状态，位置
		// float vx,//x坐标的移动速度，单位: px/秒
		// float y坐标的移动速度
		@Override
		public synchronized boolean onFling(MotionEvent e1, MotionEvent e2,
				float vx, float vy) {
			Log.d(LOG_TAG, "滑动事件...");
			if (e1 == null || e2 == null) {
				return false;
			}
			Log.e(LOG_TAG, "e1(x,y):" + e1.getX() + "," + e1.getY());
			Log.e(LOG_TAG, "e2(x,y):" + e2.getX() + "," + e2.getY());
			// 滑动速度足够快至少50点/秒，手指起落点减起点是正值且>200 判断属于向左滑动
			// 修改为10点/秒，长度修改为80 --wbl
			if ((e1.getX() - e2.getX() > 150) && (Math.abs(vx) > 50)) {
				currentDate.add(Calendar.DAY_OF_YEAR, -1);
				tv_curdate.setText(String.valueOf(currentDate
						.get(Calendar.DAY_OF_MONTH)));
				Init();

			} else if ((e2.getX() - e1.getX() > 150) && (Math.abs(vx) > 50)) // 同理判断是向右滑动
			{
				Calendar now = Calendar.getInstance();
				if (currentDate.get(Calendar.YEAR) == now.get(Calendar.YEAR)
						&& currentDate.get(Calendar.MONTH) == now
								.get(Calendar.MONTH)
						&& currentDate.get(Calendar.DAY_OF_MONTH) == now
								.get(Calendar.DAY_OF_MONTH)) {// 已经是当前天
					ToastUtil.showShortToast(MileRecordList.this,
							"选择的日期不能超过当前日期");
				} else {
					currentDate.add(Calendar.DAY_OF_YEAR, 1);
					tv_curdate.setText(String.valueOf(currentDate
							.get(Calendar.DAY_OF_MONTH)));
					Init();
				}

			}
			return false;

		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {

			return super.onSingleTapConfirmed(e);
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			return gDetector.onTouchEvent(event);
		}

		public GestureDetector getDetector() {
			return gDetector;
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		myGestureListener.getDetector().onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

}
