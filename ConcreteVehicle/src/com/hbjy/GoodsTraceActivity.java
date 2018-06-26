package com.hbjy;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.hbjy.beans.Goods;
import com.hbjy.carlocation.R;

/**
 *@author: hao
 *@CreateTime:2016年7月17日下午1:46:03
 *@description:物品溯源轨迹追踪
 */
public class GoodsTraceActivity extends Activity implements OnClickListener{
	/**
	 *  MapView 是地图主控件
	 */
	private MapView mMapView = null;
	/**
	 *  用MapController完成地图控制 
	 */
	private MapController mMapController = null;
	Toast toast;
	
	// 地图图层显示类别kl
	private ImageView back;
	private ImageView trace_map_layer;
	private ImageView trace_map_lukuang;
	private ImageView trace_map_replay;
	private PopupWindow popWindow;
	// 图层路况kl
	private View viewMapMode;
	private TextView tv_mapmode_3d;
	private TextView tv_mapmode_satellite;
	private TextView tv_mapmode_plain;
	boolean islukuangopen = false;
	
	// 屏幕高度和宽度
	private int height, width;
	
	//轨迹点集合
	private List<GeoPoint> points = new ArrayList<GeoPoint>();
	private Goods goods;
	
	//地图覆盖物
	ItemizedOverlay<OverlayItem> itemOverlay;
	//轨迹标注点
	OverlayItem markerItem;
	
	//计数器
	Timer timer;
	//计数次数
	int time;
	//轨迹点个数
	int number;
	
	/**
	 * 地图级别以及对应的比例尺
	 */
	double[] level = {2000, 2000, 2000, 2000, 1000, 
			500, 200, 100, 50, 25, 
			20, 10, 5, 2, 1, 
			0.5, 0.2, 0.1, 0.05, 0.02,
			0.01, 0.005};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 使用地图sdk前需先初始化BMapManager.
         * BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
         * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
         */
        CarInfoApplication app = (CarInfoApplication)this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(this);
            /**
             * 如果BMapManager没有初始化则初始化BMapManager
             */
            app.mBMapManager.init(CarInfoApplication.strKey,new CarInfoApplication.MyGeneralListener());
        }
        /**
          * 由于MapView在setContentView()中初始化,所以它需要在BMapManager初始化之后
          */
        setContentView(R.layout.activity_goods_trace_map);
        mMapView = (MapView)findViewById(R.id.trace_bmapView);
        
        initTraceData();
		
		if(points.size() > 0){
	        /**
	         * 获取地图控制器
	         */
	        mMapController = mMapView.getController();
	        /**
	         *  设置地图是否响应点击事件  .
	         */
	        mMapController.enableClick(true);
	        /**
	         * 设置地图缩放级别
	         */
	//        mMapController.setZoom(12);
	        /**
	         * 显示内置缩放控件
	         */
	        mMapView.setBuiltInZoomControls(true);
	       
	        /**
	         * 将地图移动至天安门
	         * 使用百度经纬度坐标，可以通过http://api.map.baidu.com/lbsapi/getpoint/index.html查询地理坐标
	         * 如果需要在百度地图上显示使用其他坐标系统的位置，请发邮件至mapapi@baidu.com申请坐标转换接口
	         */
	//        double cLat = 39.945 ;
	//        double cLon = 116.404 ;
	//        GeoPoint p = new GeoPoint((int)(cLat * 1E6), (int)(cLon * 1E6));
	//        mMapController.setCenter(p);
	        
	        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			viewMapMode = layoutInflater.inflate(R.layout.popview_map_layer, null);
			back = (ImageView) findViewById(R.id.trace_return);
			trace_map_layer = (ImageView) findViewById(R.id.trace_map_layer);
			trace_map_lukuang = (ImageView) findViewById(R.id.trace_map_lukuang);
			trace_map_replay = (ImageView) findViewById(R.id.trace_map_play);
			tv_mapmode_3d = (TextView) viewMapMode.findViewById(R.id.tv_mapmode_3d);
			tv_mapmode_satellite = (TextView) viewMapMode
					.findViewById(R.id.tv_mapmode_satellite);
			tv_mapmode_plain = (TextView) viewMapMode
					.findViewById(R.id.tv_mapmode_plain);
			
			WindowManager localWindowManager = (WindowManager) getSystemService("window");
			this.width = localWindowManager.getDefaultDisplay().getWidth();
			this.height = localWindowManager.getDefaultDisplay().getHeight();
			
			back.setOnClickListener(this);
			trace_map_layer.setOnClickListener(this);
			trace_map_lukuang.setOnClickListener(this);
			trace_map_replay.setOnClickListener(this);
			tv_mapmode_3d.setOnClickListener(map_Layerclick);
			tv_mapmode_plain.setOnClickListener(map_Layerclick);
			tv_mapmode_satellite.setOnClickListener(map_Layerclick);
		
			initTraceOverlay();
			setZoomLevelAndCenter();
			tracePlay();
		}else{
			new AlertDialog.Builder(GoodsTraceActivity.this)
			.setTitle("警告").setMessage("暂无该商品的物流信息")
			.setPositiveButton("确定", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					finish();
				}
				
			}).show();
		}
    } 
    
    /**
     * 设置底图显示模式
     * @param view
     */
    /*public void setMapMode(View view){
    	 boolean checked = ((RadioButton) view).isChecked();
         switch(view.getId()) {
             case R.id.normal:
                 if (checked)
                	 mMapView.setSatellite(false);
                 break;
             case R.id.statellite:
                 if (checked)
                	 mMapView.setSatellite(true);
                 break;
         }	
    }*/
    /**
     * 设置是否显示交通图
     * @param view
     */
    /*public void setTraffic(View view){
    	mMapView.setTraffic(((CheckBox) view).isChecked());
    }*/
    @Override
    protected void onPause() {
    	/**
    	 *  MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
    	 */
        mMapView.onPause();
        super.onPause();
    }
    
    @Override
    protected void onResume() {
    	/**
    	 *  MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
    	 */
        mMapView.onResume();
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
    	/**
    	 *  MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
    	 */
        mMapView.destroy();
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.trace_return:
			finish();
			break;
		case R.id.trace_map_layer:
			showPopWindow(v);
			break;
		case R.id.trace_map_lukuang:
			if (islukuangopen == false) {
				trace_map_lukuang.setImageResource(R.drawable.map_lukuang_open);
				mMapView.setTraffic(true);
				islukuangopen = true;
			} else {
				trace_map_lukuang.setImageResource(R.drawable.map_lukuang_close);
				mMapView.setTraffic(false);
				islukuangopen = false;
			}

			break;
		case R.id.trace_map_play:
			tracePlay();
			break;
		default:
			break;
		}
	}
	
	
	/**
	 * 地图模式监听
	 */
	View.OnClickListener map_Layerclick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.tv_mapmode_3d:
				tv_mapmode_3d.setSelected(true);
				tv_mapmode_plain.setSelected(false);
				tv_mapmode_satellite.setSelected(false);
				mMapController.setRotation(90);
				mMapController.setOverlooking(-30);
				break;
			case R.id.tv_mapmode_satellite:
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
	
	/**
	 * 显示地图模式弹框
	 * @param parent
	 */
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
//				restorePlusBtn();
			}
		});
	}
	
	/**
	 * 初始化轨迹数据，目前是静态的
	 */
	private void initTraceData(){
		Intent intent = this.getIntent();
		goods = (Goods)intent.getSerializableExtra("goods");
		for(Goods.Position position : goods.getTrace())
			points.add(new GeoPoint((int)(position.getLatitude() * 1E6),
					(int)(position.getLongitude() * 1E6)));
//		points.add(new GeoPoint((int)(39.904000 * 1E6),(int)(116.328432 * 1E6)));
//		points.add(new GeoPoint((int)(39.903333 * 1E6),(int)(116.325222 * 1E6)));
//		points.add(new GeoPoint((int)(39.904653 * 1E6),(int)(116.327434 * 1E6)));
//		points.add(new GeoPoint((int)(39.903232 * 1E6),(int)(116.326323 * 1E6)));
	}
	
	/**
	 * 将轨迹路线显示在地图上
	 */
	private void initTraceOverlay(){
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
		
		itemOverlay = new ItemizedOverlay<OverlayItem>(
				getResources().getDrawable(R.drawable.location_arrows), mMapView);
		OverlayItem itemStart = new OverlayItem(points.get(0), "起点", "起点"); 
		OverlayItem itemEnd = new OverlayItem(points.get(points.size()-1), "终点", "终点"); 
		itemStart.setMarker(getResources().getDrawable(R.drawable.start_point));
		itemEnd.setMarker(getResources().getDrawable(R.drawable.end_point));
		itemOverlay.addItem(itemStart);
		itemOverlay.addItem(itemEnd);
		mMapView.getOverlays().add(itemOverlay);
		mMapView.refresh();
	}
	
	/**
	 * 根据轨迹路线，确定地图缩放级别和中心位置
	 */
	private void setZoomLevelAndCenter(){
		//计算折线点之间最大的距离
		double distance  = 0;
		double d =0;
		for(int i=0;i<points.size(); i++){
			for(int j=i+1;j<points.size();j++){
				d = DistanceUtil.getDistance(points.get(i), points.get(j));
				if( d > distance)
					distance = d;
			}
		}
		
		int zoomLevel = 0;//地图缩放级别
		distance = distance / 1000;//将单位米转化为千米
		for(int i=0; i<level.length;i++ ){
			if(distance /3 < level[i] && distance/3 > level[i+1]){
				zoomLevel = i;
				break;
			}
		}
		
		mMapController.setZoom(zoomLevel+2);
		mMapController.setCenter(
				new GeoPoint(
						(points.get(0).getLatitudeE6()+points.get(points.size()-1).getLatitudeE6())/2,
						(points.get(0).getLongitudeE6()+points.get(points.size()-1).getLongitudeE6())/2
				));
	}
	
	/**
	 * 根据轨迹，设置标注在轨迹上移动
	 */
	private void tracePlay(){
//		if(markerItem != null){
//			itemOverlay.removeItem(markerItem);
//		}
//		
//		markerItem = new OverlayItem(
//				new GeoPoint(points.get(0).getLatitudeE6()-230,points.get(0).getLongitudeE6()),"停留","");
//		markerItem.setMarker(rotateDrawable(
//				R.drawable.location_arrows,
////				200));
//				(float)calculateDirection(points.get(0), points.get(1))));
//		itemOverlay.addItem(markerItem);
		
		
		try{
			
			number = points.size();
			time = 0;
			timer = new Timer();
			timer.schedule(new TimerTask(){
	
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					if(time <= number-1){
						if(markerItem != null){
							itemOverlay.removeItem(markerItem);
						}
						
						markerItem = new OverlayItem(
								new GeoPoint(points.get(time).getLatitudeE6()-230,points.get(time).getLongitudeE6()),"停留","");
						if(time != number-1){
							markerItem.setMarker(rotateDrawable(
									R.drawable.location_arrows,
									(float)calculateDirection(points.get(time), points.get(time+1))));
						}else{
							markerItem.setMarker(rotateDrawable(
									R.drawable.location_arrows,
									(float)calculateDirection(points.get(number-2), points.get(number-1))));
						}
						itemOverlay.addItem(markerItem);
						mMapView.refresh();
						mMapController.setCenter(points.get(time));
						time++;
					}else{
						this.cancel();
						timer.cancel();
					}
				}}, 2000, 2000);
		}catch(Exception e){
			e.printStackTrace();
			toast = Toast.makeText(getApplicationContext(),"系统出错",Toast.LENGTH_LONG);
	    	toast.show();
		}
		
	}
	
	/**
	 * 旋转图片
	 * @param r id
	 * @param degree 角度
	 * @return
	 */
	Drawable rotateDrawable(int r, float degree) {
		// 加载需要操作的图片，这里是eoeAndroid的logo图片
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), r);
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		return new BitmapDrawable(Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true));
	}
	
	/**
	 * 计算角度
	 * @param p1
	 * @param p2
	 * @return
	 */
	public double calculateDirection(GeoPoint p1, GeoPoint p2){
		int latitude1 = p1.getLatitudeE6();
		int longitude1 = p1.getLongitudeE6();
		int latitude2 = p2.getLatitudeE6();
		int longitude2 = p2.getLongitudeE6();
		if(latitude1 == latitude2){
			if(longitude1 > longitude2)
				return 270;
			else
				return 90;
		}else if(longitude1 == longitude2){
			if(latitude1 > latitude2)
				return 180;
			else
				return 0;
		}else if(latitude1 < latitude2 && longitude1 < longitude2){
//			System.out.println((180 * (Math.atan(
//					((double)(latitude2-latitude1))
//					/(longitude2-longitude1)
//					)))/Math.PI);
			return (90-180 * (Math.atan(
					((double)(latitude2-latitude1))
					/(longitude2-longitude1)
					))/Math.PI);
		}else if(latitude1 > latitude2 && longitude1 < longitude2){
			return (90+180 * (Math.atan(
					((double)(latitude1-latitude2))
					/(longitude2-longitude1)
					))/Math.PI);
		}else if(latitude1 > latitude2 && longitude1 > longitude2){
			return (180+180 * (Math.atan(
					((double)(longitude1 - longitude2))
					/(latitude1 - latitude2)
					))/Math.PI);
		}else{
			return (360-180 * (Math.atan(
					((double)(longitude1 - longitude2))
					/(latitude2 - latitude1)
					))/Math.PI);
		}
	}
	
}
