package com.hbjy.carlocation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.hbjy.CarInfoApplication;
import com.hbjy.beans.PlansTransport;

public class PlanDetailActivity extends Activity implements OnClickListener{

	private PlansTransport plansTransport;
	
	private TextView planVehicleNo;
	private TextView planStartPosition;
	private TextView planEndPosition;
	private TextView planDistance;
	private TextView planGoods;
	private TextView planCreateTime;
	private TextView planStatus;
	private ProgressBar planProgressBar;
	
	private ImageView back;//返回图标
	
	//地图控件
	private MapView mMapView;
	private MapController mMapController;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//地图初始化
		CarInfoApplication app = (CarInfoApplication)this.getApplication();
		if(app.mBMapManager == null){
			app.mBMapManager = new BMapManager(this);
			app.mBMapManager.init(CarInfoApplication.strKey,new CarInfoApplication.MyGeneralListener());
		}
		
		setContentView(R.layout.activity_plan_detail);
		
		mMapView = (MapView) findViewById(R.id.plan_detail_bmapView);
		mMapController = mMapView.getController();
		mMapView.setBuiltInZoomControls(true);
		
		initData();
		initMap();
		
		//获得相关的控件
		planVehicleNo = (TextView) findViewById(R.id.plan_detail_vehicle_no);
		planStartPosition = (TextView) findViewById(R.id.plan_detail_startPosition);
		planEndPosition = (TextView) findViewById(R.id.plan_detail_endPosition);
		planDistance = (TextView) findViewById(R.id.plan_detail_distance);
		planGoods = (TextView) findViewById(R.id.plan_detail_goods);
		planCreateTime = (TextView) findViewById(R.id.plan_detail_crate_time);
		planStatus = (TextView) findViewById(R.id.plan_detail_status);
		planProgressBar = (ProgressBar) findViewById(R.id.plan_detail_progressBar);
		
		back = (ImageView) findViewById(R.id.plan_detail_return);
		back.setOnClickListener(this);
		
		fillData();
	}
	
	/**
	 * 初始化数据，从intent获取数据
	 */
	private void initData(){
		Intent intent = this.getIntent();
		plansTransport = (PlansTransport) intent.getSerializableExtra("planDetail");
	}

	/**
	 * 将从intent得到的数据填充到控件中
	 */
	private void fillData(){
		planVehicleNo.setText(plansTransport.getVehicleNo());
		planStartPosition.setText(plansTransport.getStartPosition());
		planEndPosition.setText(plansTransport.getEndPosition());
		planDistance.setText(plansTransport.getDistance()+"km");
		planGoods.setText(plansTransport.getGoods());
		planCreateTime.setText(plansTransport.getCreateTime());
		planStatus.setText(plansTransport.getStatus());
		planProgressBar.setProgress((int)(plansTransport.getProgress()*1000));
	}
	
	private void initMap(){
		GeoPoint position = new GeoPoint(
				(int)(plansTransport.getCurrentLatitude() * 1E6),
				(int)(plansTransport.getCurrentLongitude() * 1E6));
		mMapController.setCenter(position);
		ItemizedOverlay<OverlayItem> itemOverlay = new ItemizedOverlay<OverlayItem>(
				getResources().getDrawable(R.drawable.location_arrows), mMapView);
		OverlayItem marker = new OverlayItem(position,"位置","位置");
		marker.setMarker(getResources().getDrawable(R.drawable.icon_gcoding));
		itemOverlay.addItem(marker);
		mMapView.getOverlays().add(itemOverlay);
		mMapView.refresh();
		mMapController.setZoom(20);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.plan_detail_return:
			finish();
			break;
		default:
				break;
		}
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mMapView.onPause();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mMapView.onResume();
		super.onResume();
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mMapView.destroy();
		super.onDestroy();
		this.finish();
	}
}
