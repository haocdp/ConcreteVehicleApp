package com.hbjy.device;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.hbjy.adapter.ExAdapter;
import com.hbjy.carlocation.R;

public class RunFragment extends Fragment {
	
	List<String> groupData = new ArrayList<String>();
	List<List<String>> childData = new ArrayList<List<String>>();

	ExAdapter adapter;
	ExpandableListView exList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		InitData();
		adapter = new ExAdapter(getActivity(),groupData,childData);
	}
	private void InitData() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				groupData.add("������Ϣ");
				
				
				List<String> child1 = new ArrayList<String>();
				child1.add("״̬:");
				child1.add("λ��:");
				child1.add("��̣�0.0 km(�ۻ�)    0.0 km(����)");
				child1.add("�ٶ�:");
				child1.add("��λ:");
				child1.add("����:");
				child1.add("��λʱ��:");
				child1.add("����ʱ��:");
				
				childData.add(child1);
				
				
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_device_run, null);
		exList = (ExpandableListView) view.findViewById(R.id.expandableListView1);
		exList.setAdapter(adapter);
		exList.setDivider(null);
		exList.setGroupIndicator(null);
		
		return view;
	}
}
