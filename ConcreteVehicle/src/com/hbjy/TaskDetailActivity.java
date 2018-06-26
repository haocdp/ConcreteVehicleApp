package com.hbjy;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hbjy.beans.PlanVehicle;
import com.hbjy.carlocation.R;
import com.hbjy.nets.NetWork;
import com.hbjy.utils.Constant;

public class TaskDetailActivity extends Activity implements OnClickListener{

	private ListView lv_vehicle_task;
	private ImageView iv_return;
	private Intent it;
	private TaskDetailListAdapter taskdetailListAdapter;
	private List<PlanVehicle> taskdetailsList = new ArrayList<PlanVehicle>();
	List<PlanVehicle> list = new ArrayList<PlanVehicle>();//生成的数据
	private TextView tv_planId;
	private TextView tv_euserName;
	private TextView tv_startpoint;
	private TextView tv_endpiont;
	private TextView tv_site;
	private TextView tv_transDistance;
	private TextView tv_count;
	private TextView tv_transCap;
	private TextView tv_transedCap;
	private ProgressBar pb_schedule;
	private String BaseUrl;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 3:
				if(list.size()>=0)
				{
					taskdetailsList.addAll(list);		
					taskdetailListAdapter.notifyDataSetChanged();
					//onLoad();
				}
				
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_detail);
		it = getIntent();
		SharedPreferences sp = getSharedPreferences(Constant.FILENAME, 0);
		String userName = sp.getString(Constant.USERNAME, "");
		if(userName.equals(""))
			return;
		else	
			BaseUrl = NetWork.USERCLIENT_URL + "getPlanVehicleDetailByPlanId/"+userName+"/"
			+ it.getStringExtra("fplanId");
		
//		BaseUrl = NetWork.USERCLIENT_URL + "getPlanVehicleDetailByPlanId/"+userName+"/"
//				+ it.getStringExtra("fplanId");
		geneItems();
		findView();
		setupEvent();
	}

	private void setupEvent() {
		// TODO Auto-generated method stub
		//初始化值
		tv_planId.setText(it.getStringExtra("fplanId"));
		tv_euserName.setText(it.getStringExtra("euserName"));
		tv_startpoint.setText(it.getStringExtra("startPoint"));
		tv_endpiont.setText(it.getStringExtra("endPoint"));
		tv_site.setText(it.getStringExtra("site"));//施工单位
		tv_transDistance.setText(it.getDoubleExtra("transDistance",0.0)+" Km");
		tv_count.setText(it.getIntExtra("count", 0)+"");
		tv_transCap.setText(it.getDoubleExtra("transCap", 0.0)+"");
		tv_transedCap.setText(it.getDoubleExtra("transedCap", 0.0)+"");
		pb_schedule.setMax((int)it.getDoubleExtra("transCap", 0.0));
		pb_schedule.setProgress((int)it.getDoubleExtra("transedCap", 0.0));
		
		iv_return.setOnClickListener(this);
		taskdetailListAdapter = new TaskDetailListAdapter(this,taskdetailsList);
		lv_vehicle_task.setAdapter(taskdetailListAdapter);
		lv_vehicle_task.setEnabled(false);
	}

	private void findView() {
		// TODO Auto-generated method stub
		iv_return = (ImageView) findViewById(R.id.iv_return);
		lv_vehicle_task = (ListView) findViewById(R.id.lv_vehicle_tasks);
		tv_planId = (TextView) findViewById(R.id.tv_planId);
		tv_euserName = (TextView) findViewById(R.id.tv_euserName);
		tv_startpoint = (TextView) findViewById(R.id.tv_startpoint);
		tv_endpiont = (TextView) findViewById(R.id.tv_endpiont);
		tv_site = (TextView) findViewById(R.id.tv_site);
		tv_transDistance = (TextView) findViewById(R.id.tv_transDistance);
		tv_count = (TextView) findViewById(R.id.tv_count);
		tv_transCap = (TextView) findViewById(R.id.tv_transCap);
		tv_transedCap = (TextView) findViewById(R.id.tv_transedCap);
		pb_schedule = (ProgressBar) findViewById(R.id.pb_schedule);
		
	}

	private void geneItems() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				String url = BaseUrl;
				try {

					String result = NetWork.getResponseString(url);
					Log.d("Result", result);
					list.clear();
					list = JSON.parseArray(result,PlanVehicle.class);
					Log.d("任务单",list.get(0).getVehicleID()+","+list.get(0).getRealTransAmount()+
							","+list.get(0).getTransedDistance()+","+list.get(0).getVehiclenum());
					Log.d("Count",list.size()+"");
					

				} catch (Exception e) {
					e.printStackTrace();
				} finally {

					mHandler.sendEmptyMessage(3);// 更新列表
				}

			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.iv_return:
			finish();
			break;
		}
	}
	
	public class TaskDetailListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private List<PlanVehicle> contents;

		public TaskDetailListAdapter(Context context) {
			super();
			// TODO Auto-generated constructor stub
			mInflater = LayoutInflater.from(context);
		}

		public TaskDetailListAdapter(Context context, List<PlanVehicle> contents) {
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
				convertView = mInflater.inflate(R.layout.task_detail_list_item,
						null);
				holder = new ViewHolder();
				holder.tv_vehicleId = (TextView) convertView.findViewById(R.id.tv_vehicleId);
				holder.tv_cube = (TextView) convertView.findViewById(R.id.tv_cube);
				holder.tv_accStatus = (TextView) convertView.findViewById(R.id.tv_accStatus);
				holder.tv_totalmile = (TextView) convertView.findViewById(R.id.tv_totalmile);
				holder.tv_distance = (TextView) convertView.findViewById(R.id.tv_distance);
				holder.tv_site2send = (TextView) convertView.findViewById(R.id.tv_site2send);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			PlanVehicle pv = contents.get(position);
			holder.tv_accStatus.setText(pv.accstatus);
			holder.tv_vehicleId.setText(pv.getVehicleID());
			holder.tv_cube.setText(pv.getRealTransAmount()+"");
			holder.tv_site2send.setText(it.getStringExtra("endPoint"));
			holder.tv_distance.setText(pv.getTransedDistance());
			
			//holder.tv_endpiont.setText(contents.get(position).getEndPoint());
			return convertView;
		}

	}

	public class ViewHolder {
		public TextView tv_vehicleId;
		public TextView tv_accStatus;
		public TextView tv_totalmile;
		public TextView tv_cube;
		public TextView tv_distance;
		public TextView tv_site2send;
	}
}
