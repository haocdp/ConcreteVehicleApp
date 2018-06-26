package com.hbjy;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;

import com.hbjy.adapter.GoodsAdapter;
import com.hbjy.beans.Goods;
import com.hbjy.carlocation.R;
import com.hbjy.nets.HttpUtil;
import com.hbjy.nets.ToastUtil;
import com.hbjy.utils.Constant;
import com.hbjy.view.XListView;
import com.hbjy.view.XListView.IXListViewListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class MyNewsFragment extends Fragment implements IXListViewListener{
	private View view; 
	// 进度环kl
	private ProgressDialog progresscircle;
	//页面显示的物品列表
	private List<Goods> goodsList = new ArrayList<Goods>();
	//加载更多的物品列表
	private List<Goods> moreGoodsList = new ArrayList<Goods>();
	//当前加载页数
	private int pageNumber = 0;
	
	private String requestUrl = Constant.HOST + "/goods/listGoods?page=" + 
			Constant.PAGE + "&&number=" + Constant.NUMBER;
	
	//物品适配器
	private GoodsAdapter goodsAdapter;
	
	private XListView mListView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_empty, null);
		mListView = (XListView)view.findViewById(R.id.goodsListView); 
		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(this);
		showProgressCircle();
		
		initGoodsList();
		
		// 添加ListItem，设置事件响应
		goodsAdapter = new GoodsAdapter();
		goodsAdapter.setGoodsList(goodsList);
		
        mListView.setAdapter(goodsAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {  
            public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {  
            	onListItemClick(index);
            }  
        });  
		return view;
	}
	
	void showProgressCircle() {
		progresscircle = new ProgressDialog(getActivity());
		progresscircle.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progresscircle.setMessage("正在加载物品信息");
		progresscircle.setIndeterminate(false);
		progresscircle.setCancelable(true);
		progresscircle.show();
	}
	
	void onListItemClick(int index) {
		Intent intent = null;
		intent = new Intent(getActivity(), GoodsTraceActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("goods", goodsList.get(index-1));
		intent.putExtras(bundle);
		this.startActivity(intent);
    }
	/**
	 * 请求服务器数据
	 */
	private void initGoodsList(){
		Log.d("",requestUrl);
		
		HttpUtil.get2(requestUrl, null, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONArray response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, response);
				
				goodsList.clear();
				try{
				
					for(int i = 0; i < response.length(); i++){
						goodsList.add(new Goods(response.getJSONObject(i)));
					}
					
					if(goodsList.size() > 0){
						goodsAdapter.notifyDataSetChanged();
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
		
//		HttpIPv6Test.get2(requestUrl, new HttpResponseHandler(){
//			@Override
//			public void onSuccess(int statusCode, String response) {
//				// TODO Auto-generated method stub
//				super.onSuccess(statusCode, response);
//				goodsList.clear();
//				ToastUtil.showLongToast(getActivity(), response);
//			}
//			
//			@Override
//			public void onFailure(int statusCode) {
//				// TODO Auto-generated method stub
//				super.onFailure(statusCode);
//			}
//		});
	}
	
	/**
	 * 加载更多时的请求url
	 * @return
	 */
	public String urlLoadMore(){
		return Constant.HOST + "/goods/listGoods?page=" + 
				(pageNumber+1) + "&&number=" + Constant.NUMBER;
	}
	
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		Log.d("", "刷新数据");
		mListView.setPullLoadEnable(true);
		initGoodsList();
	}
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		Log.d("", "加载更多");
		Log.d("",urlLoadMore());
		HttpUtil.get2(urlLoadMore(), null, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONArray response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, response);
				
				try{
					moreGoodsList.clear();
					for(int i = 0; i < response.length(); i++){
						moreGoodsList.add(new Goods(response.getJSONObject(i)));
					}
					
					if(moreGoodsList.size() > 0){
						goodsList.addAll(moreGoodsList);
						goodsAdapter.notifyDataSetChanged();
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
	
	/**
	 * 加载数据以后的操作
	 */
	private void onLoad() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
		String date = format.format(new Date());
		mListView.setRefreshTime(date);
	}
	
}
