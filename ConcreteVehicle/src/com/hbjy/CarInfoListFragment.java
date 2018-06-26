package com.hbjy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.hbjy.adapter.ConditionAdapter;
import com.hbjy.beans.Vehicle;
import com.hbjy.carlocation.R;
import com.hbjy.nets.HttpUtil;
import com.hbjy.nets.ToastUtil;
import com.hbjy.utils.Constant;
import com.hbjy.view.XListView;
import com.hbjy.view.XListView.IXListViewListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.special.ResideMenu.ResideMenu;

public class CarInfoListFragment extends Fragment implements
		IXListViewListener, OnClickListener {

	private String Log_TAG = "CarInfoListFragment";
	private String BaseUrl;
	private int index = 1;
	// 顶层部分
	private RelativeLayout relativelayout_top;
	private ImageView iv_openMenu;
	private XListView carList;
	private RelativeLayout rl_condition_choices;
	// 查询
	private AlertDialog dialog;
	private View dialogView;
	// 查询框
	private AutoCompleteTextView act_keyword;// 关键字
	private ImageView iv_search2;
	private Button btn_cancel;// 取消按钮

	private TextView tv_condition;// 头部窗口
	private PopupWindow mPopupWindow;// 泡泡窗口
	private View contentView;// 弹出来的窗口
	private ListView listView;// 窗口中的listview
	private ArrayList<String> conditions;// 筛选条件内容
	private ConditionAdapter conditionAdapter;// 适配器

	private ListAdapter contentAdapter;
	private List<Vehicle> contents = new ArrayList<Vehicle>();// 车牌号
	List<Vehicle> list = new ArrayList<Vehicle>();// 生成的数据

	private ProgressDialog progresscircle;
	private SimpleAdapter adapter;
	private List<Map<String, Object>> search_vehicles;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 进度环显示
		progresscircle = new ProgressDialog(getActivity());
		progresscircle.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progresscircle.setMessage("正在加载车辆信息中，请稍等");
		progresscircle.setIndeterminate(false);
		progresscircle.setCancelable(true);
		progresscircle.show();

		String userName = Constant.getUserId();
		if (userName.equals("")) {
			progresscircle.dismiss();
			return;
		} else {
			BaseUrl = HttpUtil.INFOVEHICLE_URL + "getVehicleList/" + userName
					+ "/";
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_car_info_list, null);
		index = 1;

		relativelayout_top = (RelativeLayout) view
				.findViewById(R.id.relativelayout_top);
		iv_openMenu = (ImageView) view.findViewById(R.id.iv_openMenu);
		carList = (XListView) view.findViewById(R.id.carlist);
		carList.setPullLoadEnable(true);
		carList.setXListViewListener(this);
		tv_condition = (TextView) view.findViewById(R.id.tv_condition);
		tv_condition.setOnClickListener(this);
		geneItems(BaseUrl + index);
		iv_openMenu.setOnClickListener(this);

		// 条件框
		rl_condition_choices = (RelativeLayout) view
				.findViewById(R.id.rl_condition_choices);
		rl_condition_choices.setOnClickListener(this);
		// 输入搜索框
		dialog = new AlertDialog.Builder(getActivity()).create();
		// dialog.setCanceledOnTouchOutside(false);
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {// 窗口消失事件
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				relativelayout_top.setVisibility(View.VISIBLE);// 重新显示
			}
		});
		dialogView = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_search, null);
		// 关键字
		act_keyword = (AutoCompleteTextView) dialogView
				.findViewById(R.id.act_keyword);
		iv_search2 = (ImageView) dialogView.findViewById(R.id.iv_search2);
		btn_cancel = (Button) dialogView.findViewById(R.id.btn_cancel);

		dialog.setView(dialogView, 0, 0, 0, 0);

		iv_search2.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		// dialog.setInverseBackgroundForced(false);
		contentAdapter = new ListAdapter(getActivity(), contents);
		carList.setAdapter(contentAdapter);
		viewController();

		act_keyword.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String vehicleId = ((TextView) view
						.findViewById(R.id.tv_carnumber)).getText().toString();
				double longitude = Double.parseDouble(((TextView) view
						.findViewById(R.id.tv_longitude)).getText().toString());
				double latitude = Double.parseDouble(((TextView) view
						.findViewById(R.id.tv_latitude)).getText().toString());
				Log.d(Log_TAG, "经度：" + longitude + ",纬度：" + latitude);
				String vehiclenum = ((TextView) view
						.findViewById(R.id.tv_vehiclenum)).getText().toString();
				act_keyword.setText(vehicleId);
				dialog.cancel();
				// if (longitude != 0 && latitude != 0) {
				Intent it = new Intent(getActivity(), InfoCarLocActivity.class);
				it.putExtra("vehicleId", vehicleId);
				it.putExtra("longitude", longitude);
				it.putExtra("latitude", latitude);
				it.putExtra("vehiclenum", vehiclenum);
				getActivity().startActivity(it);

			}

		});
		act_keyword.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				AutoCompleteTextView view = (AutoCompleteTextView) v;
				if (hasFocus) {
					view.showDropDown();
				}
			}

		});

		return view;
	}

	void geneItems(final String url) {
		HttpUtil.get(url, null, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, JSONArray response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, response);
				list.clear();
				for (int i = 0; i < response.length(); i++) {
					try {
						list.add(new Vehicle(response.getJSONObject(i)));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (list.size() >= 0) {
					contents.clear();
					contents.addAll(list);
					contentAdapter.notifyDataSetChanged();
					onLoad();
				} else {// 到达最后一页了
					carList.setPullLoadEnable(false);
				}
				progresscircle.dismiss();
			}

			@Override
			public void onFailure(Throwable error) {
				// TODO Auto-generated method stub
				progresscircle.dismiss();
				ToastUtil.showShortToast(getActivity(), "网络连接失败");
			}
		});

	}

	void setMoreCarList(final String url) {
		HttpUtil.get(url, null, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, JSONArray response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, response);
				list.clear();
				for (int i = 0; i < response.length(); i++) {
					try {
						list.add(new Vehicle(response.getJSONObject(i)));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (list.size() >= 0) {
					contents.addAll(list);
					contentAdapter.notifyDataSetChanged();
					onLoad();
				} else {// 到达最后一页了
					carList.setPullLoadEnable(false);
				}
				progresscircle.dismiss();
			}

			@Override
			public void onFailure(Throwable error) {
				// TODO Auto-generated method stub
				progresscircle.dismiss();
			}
		});

	}

	public void viewController() {

		carList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Vehicle v = contents.get(arg2 - 1);
				Intent it = new Intent(getActivity(), InfoCarLocActivity.class);

				it.putExtra("vehicleId", v.getVehicleId());
				it.putExtra("longitude", v.getLongitude());
				it.putExtra("latitude", v.getLatitude());
				it.putExtra("vehiclenum", v.getVehiclenum());
				getActivity().startActivity(it);

			}
		});

	}

	public class ListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private List<Vehicle> contents;

		public ListAdapter(Context context) {
			super();
			// TODO Auto-generated constructor stub
			mInflater = LayoutInflater.from(context);
		}

		public ListAdapter(Context context, List<Vehicle> contents) {
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
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return contents.get(arg0);
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
				convertView = mInflater.inflate(R.layout.car_info_list_item,
						null);
				holder = new ViewHolder();
				holder.tv_carnumber = (TextView) convertView
						.findViewById(R.id.tv_carnumber);
				holder.tv_accstate = (TextView) convertView
						.findViewById(R.id.tv_accstate);
				holder.tv_speed = (TextView) convertView
						.findViewById(R.id.tv_speed);
				holder.tv_inserttime = (TextView) convertView
						.findViewById(R.id.tv_inserttime);
				holder.tv_gpsgeoposition = (TextView) convertView
						.findViewById(R.id.tv_gpsgeoposition);
				holder.iv_car_status = (ImageView) convertView
						.findViewById(R.id.iv_car_status);
				holder.iv_cartype = (ImageView) convertView
						.findViewById(R.id.iv_cartype);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Vehicle v = contents.get(position);
			holder.tv_carnumber.setText(v.getVehicleId());
			if (v.getAccstatus().equals("null")) {
				holder.tv_accstate.setText("暂无数据");
			} else {
				holder.tv_accstate.setText(v.getAccstatus());
			}

			if (v.getRecordtime().equals("null")) {
				holder.tv_inserttime.setText("暂无数据");
			} else {
				holder.tv_inserttime.setText(v.getRecordtime());
			}

			holder.tv_speed.setText(v.getSpeed() + " Km/h");

			String locmode = "非定位";
			holder.tv_gpsgeoposition.setText(locmode);
			holder.iv_cartype.setBackgroundDrawable(getCarType(v
					.getVehicleType()));
			holder.iv_car_status.setBackgroundDrawable(getCarStatus(v.getStatus()));
			return convertView;
		}

		/**
		 * 车辆状态
		 * 
		 * @param status
		 * @return
		 */
		private Drawable getCarType(String carType) {
			Resources r = getResources();
//			if (Constant.VehicleType.type1.equals(carType)) {// 卡车
//				return r.getDrawable(R.drawable.vehicle_type_normal_truck);//
//			}
//			if (Constant.VehicleType.type2.equals(carType)) {// 客车
//				return r.getDrawable(R.drawable.vehicle_type_coach);//
//			}
//			if (Constant.VehicleType.type3.equals(carType)) {// 塔车
//				return r.getDrawable(R.drawable.vehicle_type_normal_truck);//
//			}
//			if (Constant.VehicleType.type4.equals(carType)) {// 挖掘机
//				return r.getDrawable(R.drawable.vehicle_type_grab_excavator);//
//			}
//			if (Constant.VehicleType.type5.equals(carType)) {// 拖拉机
//				return r.getDrawable(R.drawable.vehicle_type_tractor);//
//			}
//			if (Constant.VehicleType.type6.equals(carType)) {// 重卡
//				return r.getDrawable(R.drawable.vehicle_type_heavy_truck);//
//			}
//			if (Constant.VehicleType.type7.equals(carType)) {// 小汽车
//				return r.getDrawable(R.drawable.vehicle_type_car);//
//			}
//			if (Constant.VehicleType.type8.equals(carType)) {// 混凝土车
//				return r.getDrawable(R.drawable.vehicle_type_concrete_truck);//
//			}

//			return r.getDrawable(R.drawable.c40);// 离线，默认
			
			return r.getDrawable(R.drawable.icon_transport_vehicle);

		}

		/**
		 * 车辆状态
		 * 
		 * @param status
		 * @return
		 */
		private Drawable getCarStatus(int status) {
			Resources r = getResources();
			Drawable drabwable = null;
			switch (status) {
			case 0:
				drabwable = r.getDrawable(R.drawable.c40);// 离线
				break;
			case 1:
				drabwable = r.getDrawable(R.drawable.c41);// 在线停车
				break;
			case 2:
				drabwable = r.getDrawable(R.drawable.c42);// 在线
				break;
			default:
				drabwable = r.getDrawable(R.drawable.c40);// 离线，默认
				break;
			}
			return drabwable;

		}

	}

	public class ViewHolder {
		public TextView tv_carnumber;
		public TextView tv_accstate;
		public TextView tv_speed;
		public TextView tv_inserttime;
		public TextView tv_gpsgeoposition;
		public ImageView iv_car_status;
		public ImageView iv_cartype;

	}

	public void showPopupwindow(View parent) {
		// TODO Auto-generated method stub
		if (mPopupWindow == null) {
			Log.d("mPopupWindow", "为空");
			LayoutInflater mLayoutInflater = LayoutInflater.from(getActivity());
			contentView = mLayoutInflater.inflate(R.layout.group_list, null);
			listView = (ListView) contentView.findViewById(R.id.lv_group);
			conditions = new ArrayList<String>();
			conditions.add("全部");
			conditions.add(Constant.vehicleStatesOffOnlineBlue);// 离线
			conditions.add(Constant.vehicleStatesPaseGreen);// 在线停车
			conditions.add(Constant.vehicleStatesOnlineRun);// 行驶中
			
			

			conditionAdapter = new ConditionAdapter(getActivity(), conditions);
			listView.setAdapter(conditionAdapter);
			mPopupWindow = new PopupWindow(contentView,
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			// 监听选择事件
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					mPopupWindow.dismiss();
					tv_condition.setText(conditions.get(position));
					SharedPreferences sp = getActivity().getSharedPreferences(
							Constant.FILENAME, 0);
					String userName = sp.getString(Constant.USERNAME, "");
					index = 1;
					if (position == 0)
						BaseUrl = HttpUtil.INFOVEHICLE_URL + "getVehicleList/"
								+ userName + "/";
					else
						BaseUrl = HttpUtil.INFOVEHICLE_URL + "getVehicleListByStatus/"
								+ userName + "/"+(position-1)+"/";
					progresscircle.show();
					onRefresh();

				}

			});

		}
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setFocusable(true);

		// 显示的位置为:屏幕的宽度的1/16
		int xPos = getActivity().getWindowManager().getDefaultDisplay()
				.getWidth() / 16;

		mPopupWindow.showAsDropDown(parent, xPos, 0);

	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		index = 1;// 初始化

		carList.setPullLoadEnable(true);// 刷新后允许加载更多
		geneItems(BaseUrl + index);

	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		// conditionAdapter.notifyDataSetChanged();
		index++;
		setMoreCarList(BaseUrl + index);

	}

	private void onLoad() {
		carList.stopRefresh();
		carList.stopLoadMore();
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
		String date = format.format(new Date());
		carList.setRefreshTime(date);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_condition_choices:
			act_keyword.setText("");// 清空
			initAutoComplete();// 初始化自动填充框
			relativelayout_top.setVisibility(View.GONE);// 隐藏最上面的
			dialog.show();
			dialog.getWindow().setGravity(Gravity.TOP | Gravity.LEFT);
			dialog.getWindow().setLayout(
					WindowManager.LayoutParams.FILL_PARENT,
					WindowManager.LayoutParams.WRAP_CONTENT);
			break;
		case R.id.iv_search2:
			progresscircle.show();
			String keyword = act_keyword.getText().toString();
			index = 1;
			if (!keyword.equals(""))
				getVehiclesById(keyword);

			Log.d("保存成功", "保存成功");
			break;
		case R.id.iv_openMenu:
			MainActivity main = (MainActivity) getActivity();
			main.resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
			break;
		case R.id.btn_cancel:
			dialog.cancel();
			break;
		case R.id.tv_condition:
			showPopupwindow(v);
			break;
		default:
			break;
		}

	}

	void getVehiclesById(String keyword) {
		HttpUtil.get(
				HttpUtil.INFOVEHICLE_URL + "getVehicleById/"
						+ Constant.getUserId() + "/" + keyword, null,
				new JsonHttpResponseHandler() {
					@Override
					public void onFailure(Throwable error) {
						// TODO Auto-generated method stub
						ToastUtil.showShortToast(getActivity(), "查询失败");
						progresscircle.dismiss();
					}

					@Override
					public void onSuccess(int statusCode, JSONArray response) {
						// TODO Auto-generated method stub
						progresscircle.dismiss();
						search_vehicles.clear();
						for (int i = 0; i < response.length(); i++) {
							Map<String, Object> data = new HashMap<String, Object>();
							JSONObject jo;
							try {
								jo = response.getJSONObject(i);
								data.put("vehicleId", jo.getString("VehicleId"));
								data.put("vehiclenum",
										jo.getString("Vehiclenum"));
								data.put("longitude", String.valueOf(jo
										.getDouble("longitude")));
								data.put("latitude", String.valueOf(jo
										.getDouble("longitude")));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								ToastUtil.showShortToast(getActivity(), "查询错误");
								return;
							}
							search_vehicles.add(data);

						}
						adapter = new SimpleAdapter(getActivity(),
								search_vehicles, R.layout.car_search_list_item,
								new String[] { "vehicleId", "longitude",
										"latitude", "vehiclenum" }, new int[] {
										R.id.tv_carnumber, R.id.tv_longitude,
										R.id.tv_latitude, R.id.tv_vehiclenum });
						act_keyword.setAdapter(adapter);
						act_keyword.showDropDown();
					}
				});
	}

	void getVehicles() {
		if (search_vehicles == null)
			search_vehicles = new ArrayList<Map<String, Object>>();
		else
			search_vehicles.clear();
		for (int i = 0; i < contents.size(); i++) {
			Map<String, Object> data = new HashMap<String, Object>();
			Vehicle v = contents.get(i);
			data.put("vehicleId", v.getVehicleId());
			data.put("vehiclenum", v.getVehiclenum());
			data.put("longitude", String.valueOf(v.getLongitude()));
			data.put("latitude", String.valueOf(v.getLatitude()));
			search_vehicles.add(data);
		}
	}

	private void initAutoComplete() {
		getVehicles();
		adapter = new SimpleAdapter(getActivity(), search_vehicles,
				R.layout.car_search_list_item, new String[] { "vehicleId",
						"longitude", "latitude", "vehiclenum" }, new int[] {
						R.id.tv_carnumber, R.id.tv_longitude, R.id.tv_latitude,
						R.id.tv_vehiclenum });

		act_keyword.setAdapter(adapter);
		act_keyword.setDropDownHeight(600);
		act_keyword.setThreshold(2);// 设置输入1个字符串后就出现提示
		act_keyword.setFocusable(true);

	}
}
