package com.hbjy.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hbjy.beans.PlansTransport;
import com.hbjy.carlocation.R;

public class PlansTransportAdapter extends ArrayAdapter<PlansTransport> {

	private int resourceId;
	
	public PlansTransportAdapter(Context context, int textViewResourceId,
			List<PlansTransport> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		resourceId = textViewResourceId;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		PlansTransport plansTransport = getItem(position);
		View view;
		ViewHolder viewHolder;
		
		if(convertView == null){
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.plan_vehicleNo = (TextView)view.findViewById(R.id.plan_vehicle_no);
			viewHolder.plan_startPosition = (TextView)view.findViewById(R.id.plan_startPosition);
			viewHolder.plan_endPosition = (TextView)view.findViewById(R.id.plan_endPosition);
			viewHolder.plan_createTime = (TextView)view.findViewById(R.id.plan_crate_time);
			viewHolder.plan_status = (TextView)view.findViewById(R.id.plan_status);
			viewHolder.plan_progressbar = (ProgressBar)view.findViewById(R.id.plan_progressBar);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder)view.getTag();
		}
		
		viewHolder.plan_vehicleNo.setText(plansTransport.getVehicleNo());
		viewHolder.plan_startPosition.setText(plansTransport.getStartPosition());
		viewHolder.plan_endPosition.setText(plansTransport.getEndPosition());
		viewHolder.plan_createTime.setText(plansTransport.getCreateTime());
		viewHolder.plan_status.setText(plansTransport.getStatus());
		viewHolder.plan_progressbar.setProgress((int)(plansTransport.getProgress()*1000));
		return view;
	}
	
	
	class ViewHolder{
		TextView plan_vehicleNo;
		TextView plan_startPosition;
		TextView plan_endPosition;
		TextView plan_createTime;
		TextView plan_status;
		ProgressBar plan_progressbar;
	}

}
