package com.hbjy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hbjy.adapter.ConditionAdapter;
import com.hbjy.adapter.PlansTransportAdapter;
import com.hbjy.beans.Goods;
import com.hbjy.beans.PlansTransport;
import com.hbjy.carlocation.PlanDetailActivity;
import com.hbjy.carlocation.R;
import com.hbjy.nets.HttpUtil;
import com.hbjy.nets.ToastUtil;
import com.hbjy.utils.Constant;
import com.hbjy.view.XListView;
import com.hbjy.view.XListView.IXListViewListener;
import com.loopj.android.http.JsonHttpResponseHandler;

public class CheckTaskFragment extends Fragment implements IXListViewListener,OnClickListener{
	
	/**
	 * 查找数据的筛选条件：
	 * 	0表示全部数据
	 * 	1表示正在进行的任务数据
	 * 	2表示已经完成的任务数据
	 */
	private int[] plan_list_status = {0,1,2};
	
	private View view; 
	// 进度环kl
	private ProgressDialog progresscircle;
	private List<PlansTransport> plansList = new ArrayList<PlansTransport>();
	
	private TextView plan_status_selected;
	private RelativeLayout plan_condition;// 头部窗口
	private PopupWindow mPopupWindow;// 泡泡窗口
	private View contentView;// 弹出来的窗口
	private ListView listView;// 窗口中的listview
	private ArrayList<String> conditions;// 筛选条件内容
	private ConditionAdapter conditionAdapter;// 适配器
	
	private XListView mListView ;
	
	//任务单数据适配
	private PlansTransportAdapter plansTransportAdapter;
	
	//加载更多的物品列表
	private List<PlansTransport> morePlansList = new ArrayList<PlansTransport>();
	
	private String requestUrl = Constant.HOST + "/plansTransport/listPlansTransport?page=" + 
			Constant.PAGE + "&&number=" + Constant.NUMBER;
//	private String requestUrlWithStaus;
	//当前加载页数
	private int pageNumber = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_plans_transport, null);
		mListView = (XListView)view.findViewById(R.id.plansListView); 
		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(this);
		
		showProgressCircle();
		
		initPlansList(requestUrl);
		
		plansTransportAdapter = new PlansTransportAdapter(getActivity(), R.layout.plan_transport_item_info, plansList);
		mListView.setAdapter(plansTransportAdapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				// TODO Auto-generated method stub
				onListItemClick(index);
			}
		});
		
		//标题点击事件注册
		plan_condition = (RelativeLayout) view.findViewById(R.id.relativelayout_top);
		plan_status_selected = (TextView) view.findViewById(R.id.plan_status_selected);
		plan_condition.setOnClickListener(this);
		
//		showProgressCircle();
		return view;
	}
	
	/**
	 * 每条数据点击事件
	 */
	void onListItemClick(int index){
		Intent intent = null;
		intent = new Intent(getActivity(), PlanDetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("planDetail", plansList.get(index-1));
		intent.putExtras(bundle);
		this.startActivity(intent);
	}
	
	void showProgressCircle() {
		progresscircle = new ProgressDialog(getActivity());
		progresscircle.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progresscircle.setMessage("正在加载信息");
		progresscircle.setIndeterminate(false);
		progresscircle.setCancelable(true);
		progresscircle.show();
	}
	
	/**
	 * 初始化任务数据
	 */
	void initData(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		plansList = new ArrayList<PlansTransport>();
		
		PlansTransport plansTransport1 = new PlansTransport();
		PlansTransport plansTransport2 = new PlansTransport();
		PlansTransport plansTransport3 = new PlansTransport();
		PlansTransport plansTransport4 = new PlansTransport();
		PlansTransport plansTransport5 = new PlansTransport();
		PlansTransport plansTransport6 = new PlansTransport();
		PlansTransport plansTransport7 = new PlansTransport();
		
		plansTransport1.setVehicleNo("沪B19877");
		plansTransport1.setStatus("正在进行");
		plansTransport1.setGoods("苹果");
		plansTransport1.setProgress(0.4);
		plansTransport1.setStartPosition("华新赤壁站");
		plansTransport1.setEndPosition("华琛灭火剂产业园");
		plansTransport1.setDistance(14.23);
		plansTransport1.setCreateTime(simpleDateFormat.format(new Date()));
		plansTransport1.setCurrentLatitude(39.903322);
		plansTransport1.setCurrentLongitude(116.328000);
		
		plansTransport2.setVehicleNo("沪B19877");
		plansTransport2.setStatus("正在进行");
		plansTransport2.setGoods("苹果");
		plansTransport2.setProgress(0.4);
		plansTransport2.setStartPosition("华新赤壁站发动机开始放假打算dddddddddddddddddddddddddddddddddddddddddddddddddddd");
		plansTransport2.setEndPosition("华琛灭火剂产业园dddddddddddddddddddddddddddddddddddddd");
		plansTransport2.setDistance(14.23);
		plansTransport2.setCreateTime(simpleDateFormat.format(new Date()));
		plansTransport2.setCurrentLatitude(39.903322);
		plansTransport2.setCurrentLongitude(116.328000);
		
		plansTransport3.setVehicleNo("沪B19877");
		plansTransport3.setStatus("正在进行");
		plansTransport3.setGoods("苹果");
		plansTransport3.setProgress(0.4);
		plansTransport3.setStartPosition("华新赤壁站");
		plansTransport3.setEndPosition("华琛灭火剂产业园");
		plansTransport3.setDistance(14.23);
		plansTransport3.setCreateTime(simpleDateFormat.format(new Date()));
		plansTransport3.setCurrentLatitude(39.903322);
		plansTransport3.setCurrentLongitude(116.328000);
		
		plansTransport4.setVehicleNo("沪B19877");
		plansTransport4.setStatus("正在进行");
		plansTransport4.setGoods("苹果");
		plansTransport4.setProgress(0.4);
		plansTransport4.setStartPosition("华新赤壁站");
		plansTransport4.setEndPosition("华琛灭火剂产业园");
		plansTransport4.setDistance(14.23);
		plansTransport4.setCreateTime(simpleDateFormat.format(new Date()));
		plansTransport4.setCurrentLatitude(39.903322);
		plansTransport4.setCurrentLongitude(116.328000);
		
		plansTransport5.setVehicleNo("沪B19877");
		plansTransport5.setStatus("正在进行");
		plansTransport5.setGoods("苹果");
		plansTransport5.setProgress(0.4);
		plansTransport5.setStartPosition("华新赤壁站");
		plansTransport5.setEndPosition("华琛灭火剂产业园");
		plansTransport5.setDistance(14.23);
		plansTransport5.setCreateTime(simpleDateFormat.format(new Date()));
		plansTransport5.setCurrentLatitude(39.903322);
		plansTransport5.setCurrentLongitude(116.328000);
		
		plansTransport6.setVehicleNo("沪B19877");
		plansTransport6.setStatus("正在进行");
		plansTransport6.setGoods("苹果");
		plansTransport6.setProgress(0.4);
		plansTransport6.setStartPosition("华新赤壁站");
		plansTransport6.setEndPosition("华琛灭火剂产业园");
		plansTransport6.setDistance(14.23);
		plansTransport6.setCreateTime(simpleDateFormat.format(new Date()));
		plansTransport6.setCurrentLatitude(39.903322);
		plansTransport6.setCurrentLongitude(116.328000);
		
		plansTransport7.setVehicleNo("沪B19877");
		plansTransport7.setStatus("正在进行");
		plansTransport7.setGoods("苹果");
		plansTransport7.setProgress(0.4);
		plansTransport7.setStartPosition("华新赤壁站");
		plansTransport7.setEndPosition("华琛灭火剂产业园");
		plansTransport7.setDistance(14.23);
		plansTransport7.setCreateTime(simpleDateFormat.format(new Date()));
		plansTransport7.setCurrentLatitude(39.903322);
		plansTransport7.setCurrentLongitude(116.328000);
		
		plansList.add(plansTransport1);
		plansList.add(plansTransport2);
		plansList.add(plansTransport3);
		plansList.add(plansTransport4);
		plansList.add(plansTransport5);
		plansList.add(plansTransport6);
		plansList.add(plansTransport7);
	}
	
	private void initPlansList(String url){
		Log.d("",url);
		
		HttpUtil.get2(url, null, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONArray response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, response);
//				Log.d("",response.toString());
				plansList.clear();
				try{
				
					for(int i = 0; i < response.length(); i++){
						plansList.add(new PlansTransport(response.getJSONObject(i)));
					}
					
					if(plansList.size() > 0){
						plansTransportAdapter.notifyDataSetChanged();
						pageNumber = 1;
						onLoad();
					}else{
//						ToastUtil.showLongToast(getActivity(), "暂无数据");
						mListView.setPullLoadEnable(false);
					}
					
					progresscircle.dismiss();
				}catch(Exception e){
					e.printStackTrace();
				}
//				Log.d("",response.toString());
			}
			
			@Override
			public void onFailure(String responseBody, Throwable error) {
				// TODO Auto-generated method stub
				super.onFailure(responseBody, error);
				ToastUtil.showLongToast(getActivity(), "服务器发生错误，请稍后重试！");
			}
		});
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
			conditions.add("正在进行");
			conditions.add("已经完成");			

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
					/*mPopupWindow.dismiss();
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
					onRefresh();*/

					mPopupWindow.dismiss();
					plan_status_selected.setText(conditions.get(position));
					
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
	
	/**
	 * 加载更多时的请求url
	 * @return
	 */
	public String urlLoadMore(){
		int index = 0;
		if("正在进行".equals(plan_status_selected.getText()))
			index = 1;
		else if("已经完成".equals(plan_status_selected.getText()))
			index = 2;
		
		return Constant.HOST + "/plansTransport/listPlansTransport?page=" + 
				(pageNumber+1) + "&&number=" + Constant.NUMBER +
				"&&status=" + plan_list_status[index];
	}
	
	/**
	 * 刷新时的请求url
	 * @return
	 */
	public String urlRefresh(){
		int index = 0;
		if("正在进行".equals(plan_status_selected.getText()))
			index = 1;
		else if("已经完成".equals(plan_status_selected.getText()))
			index = 2;
		
		return Constant.HOST + "/plansTransport/listPlansTransport?page=1" + 
				"&&number=" + Constant.NUMBER +
				"&&status=" + plan_list_status[index];
	}
	
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		mListView.setPullLoadEnable(true);
		initPlansList(urlRefresh());
	}
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		Log.d("",urlLoadMore());
		HttpUtil.get2(urlLoadMore(), null, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONArray response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, response);
				
				try{
					morePlansList.clear();
					for(int i = 0; i < response.length(); i++){
						morePlansList.add(new PlansTransport(response.getJSONObject(i)));
					}
					
					if(morePlansList.size() > 0){
						plansList.addAll(morePlansList);
						plansTransportAdapter.notifyDataSetChanged();
						pageNumber++;
						onLoad();
					}else{
//						ToastUtil.showLongToast(getActivity(), "暂无更多数据");
						mListView.setPullLoadEnable(false);
					}
					
				}catch(Exception e){
					e.printStackTrace();
				}
//				Log.d("",response.toString());
			}
			
			@Override
			public void onFailure(String responseBody, Throwable error) {
				// TODO Auto-generated method stub
				super.onFailure(responseBody, error);
				ToastUtil.showLongToast(getActivity(), "服务器发生错误，请稍后重试！");
			}
		});
	}
	
	private void onLoad() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
		String date = format.format(new Date());
		mListView.setRefreshTime(date);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.relativelayout_top:
			showPopupwindow(v);
			break;
		default:
			break;
				
		}
	}
}
