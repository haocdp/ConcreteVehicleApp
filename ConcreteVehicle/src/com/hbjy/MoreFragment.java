package com.hbjy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.hbjy.carlocation.R;
import com.hbjy.utils.Constant;

public class MoreFragment extends Fragment implements OnClickListener{

	private View view ;
	private Button btn_exit;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_more, null);
		findview();
		setUpEvent();
		return view;
	}
	
	void findview()
	{
		btn_exit = (Button) view.findViewById(R.id.btn_exit);
		
	}
	
	void setUpEvent()
	{
		btn_exit.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.btn_exit:
			exits();
			break;
		}
		
	}
	
	private void exits()
	{
		new AlertDialog.Builder(getActivity()).setTitle("�˳�").setMessage("��ȷ��Ҫ�˳���").setPositiveButton("ȷ��", new DialogInterface.OnClickListener()
	    {
	      public void onClick(DialogInterface paramDialogInterface, int paramInt)
	      {
	        SharedPreferences.Editor localEditor = CarInfoApplication.mInstance.getSharedPreferences(Constant.FILENAME, 0).edit();
	        localEditor.putLong(Constant.LASTTIME, 0);
	        localEditor.commit();
	        Intent localIntent = new Intent();
	        localIntent.setClass(getActivity(), LoginActivity.class);
	        getActivity().startActivity(localIntent);
	        getActivity().finish();
	      }
	    }).setNegativeButton("ȡ��", new DialogInterface.OnClickListener()
	    {
	      public void onClick(DialogInterface paramDialogInterface, int paramInt)
	      {
	        paramDialogInterface.dismiss();
	      }
	    }).show();
	}

}
