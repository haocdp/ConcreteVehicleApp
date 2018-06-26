package com.hbjy.device;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hbjy.carlocation.R;
import com.hbjy.view.CustomDialog;


public class ReportFragment extends Fragment {
	private CustomDialog dialog;
	
	private LinearLayout ll_device_mileage,ll_device_oil,ll_month_performance,ll_day_performance;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_device_report, null);
		ll_device_mileage = (LinearLayout) view.findViewById(R.id.ll_device_mileage);
		ll_device_oil = (LinearLayout) view.findViewById(R.id.ll_device_oil);
		ll_month_performance = (LinearLayout) view.findViewById(R.id.ll_month_performance);
		ll_day_performance = (LinearLayout) view.findViewById(R.id.ll_day_performance);
		
		LinearLayoutOnclickListener linearLayoutOnclickListener = new LinearLayoutOnclickListener();
		ll_device_mileage.setOnClickListener(linearLayoutOnclickListener);
		ll_device_oil.setOnClickListener(linearLayoutOnclickListener);
		ll_month_performance.setOnClickListener(linearLayoutOnclickListener);
		ll_day_performance.setOnClickListener(linearLayoutOnclickListener);
		
		int screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
		dialog = new CustomDialog(getActivity(),R.layout.dialog_custom,R.style.customDialog,screenWidth,screenHeight);
		
		return view;
	}
	
	public class LinearLayoutOnclickListener implements OnClickListener
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			dialog.Load("http://app001.u12580.com/4s/ObjectReport/MileageSumReport.aspx?objectId=0");
			dialog.show();
		}
		
	}
}
