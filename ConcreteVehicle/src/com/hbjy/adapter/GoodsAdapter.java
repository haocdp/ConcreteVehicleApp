package com.hbjy.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hbjy.CarInfoApplication;
import com.hbjy.beans.Goods;
import com.hbjy.carlocation.R;

/**
 *@author: hao
 *@CreateTime:2016年7月17日上午11:06:40
 *@description: 物品溯源模块的物品适配器，暂时数据是静态的
 */
public class GoodsAdapter extends BaseAdapter {
	private List<Goods> goodsList;
	
	public GoodsAdapter(){
		super();
//		initGoodsList();
	}
	
	//初始化静态数据
	private void initGoodsList(){
		List<Goods.Position> goodsList1 = new ArrayList<Goods.Position>();
		List<Goods.Position> goodsList2 = new ArrayList<Goods.Position>();
		List<Goods.Position> goodsList3 = new ArrayList<Goods.Position>();
		List<Goods.Position> goodsList4 = new ArrayList<Goods.Position>();
		List<Goods.Position> goodsList5 = new ArrayList<Goods.Position>();
		
		Goods.Position good1_position1 = new Goods.Position(39.903322,116.328000);
		Goods.Position good1_position2 = new Goods.Position(39.904000,116.328432);
		Goods.Position good1_position3 = new Goods.Position(39.903333,116.325222);
		Goods.Position good1_position4 = new Goods.Position(39.904653,116.327434);
		Goods.Position good1_position5 = new Goods.Position(39.903232,116.326323);
		goodsList1.add(good1_position1);
		goodsList1.add(good1_position2);
		goodsList1.add(good1_position3);
		goodsList1.add(good1_position4);
		goodsList1.add(good1_position5);
		
		Goods.Position good2_position1 = new Goods.Position(39.903322,116.328000);
		Goods.Position good2_position2 = new Goods.Position(39.904000,116.328432);
		Goods.Position good2_position3 = new Goods.Position(39.903333,116.325222);
		Goods.Position good2_position4 = new Goods.Position(39.904653,116.327434);
		Goods.Position good2_position5 = new Goods.Position(39.903232,116.326323);
		goodsList2.add(good2_position1);
		goodsList2.add(good2_position2);
		goodsList2.add(good2_position3);
		goodsList2.add(good2_position4);
		goodsList2.add(good2_position5);
		
		Goods goods1 = new Goods("物品1", "该信息是对物品的详细描述",goodsList1);
		Goods goods2 = new Goods("物品2", "该信息是对物品的详细描述",goodsList2);
		Goods goods3 = new Goods("物品3", "该信息是对物品的详细描述",goodsList3);
		Goods goods4 = new Goods("物品4", "该信息是对物品的详细描述",goodsList4);
		Goods goods5 = new Goods("物品5", "该信息是对物品的详细描述",goodsList5);
		
		goodsList.add(goods1);
		goodsList.add(goods2);
		goodsList.add(goods3);
		goodsList.add(goods4);
		goodsList.add(goods5);
	}
	
	/**
	 * 静态数据
	 * @return
	 */
	public List<Goods> setGoodsList(){
		initGoodsList();
		return goodsList;
	}
	
	public void setGoodsList(List<Goods> goodsList){
		this.goodsList = goodsList;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.goodsList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return this.goodsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Goods goods = (Goods)getItem(position);
		View view;
		ViewHolder viewHolder;
		
		if(convertView == null){
			view = View.inflate(CarInfoApplication.getInstance(), R.layout.goods_info_item, null);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView)view.findViewById(R.id.goods_name);
			viewHolder.desc = (TextView)view.findViewById(R.id.goods_desc);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder)view.getTag();
		}
		
		viewHolder.name.setText(goods.getName());
		viewHolder.desc.setText(goods.getDesc());
		
		return view;
	}
	
	
	/**
	 * 对TextView控件的实例进行缓存
	 * @author hao
	 *
	 */
	class ViewHolder{
		TextView name;
		TextView desc;
	}

}
