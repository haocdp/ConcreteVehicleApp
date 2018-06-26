package com.hbjy.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hbjy.carlocation.R;

public class ConditionAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<String> conditions;
	private LayoutInflater mLayoutInflater;
	
	public ConditionAdapter() {
		// TODO Auto-generated constructor stub
	}
	
	
	public ConditionAdapter(Context mContext, ArrayList<String> groups) {
		super();
		this.mContext = mContext;
		this.conditions = groups;
		mLayoutInflater = LayoutInflater.from(this.mContext);
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return conditions.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return conditions.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if(convertView==null)
		{
			viewHolder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.group_item,null);
			convertView.setTag(viewHolder);
			viewHolder.conditionItemTextView = (TextView) convertView.findViewById(R.id.tv_group_item);
		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.conditionItemTextView.setText(conditions.get(position));
		return convertView;
	}
	
	static class ViewHolder
	{
		TextView conditionItemTextView;
	}

}
