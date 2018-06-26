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

public class TargetFragment extends Fragment {

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
		groupData.add("������Ϣ");
		groupData.add("������Ϣ");
		//groupData.add("����/����/�����Ϣ");
		groupData.add("һ������");
		
		List<String> child1 = new ArrayList<String>();
		child1.add("�豸����:");
		child1.add("�豸�ͺ�:");
		child1.add("IMEI/ID��:");
		child1.add("��������:");
		child1.add("��ͨ����:");
		child1.add("������:");
		child1.add("��ϵ��:");
		child1.add("��ϵ�绰:");
		child1.add("��ע:");
		childData.add(child1);
		
		List<String> child2 = new ArrayList<String>();
		child2.add("�豸���:");
		child2.add("��װ����:");
//		child2.add("�豸��¼:");
		child2.add("����Ʒ��:");
		child2.add("�����ͺ�:");
		child2.add("��������:");
		child2.add("���ܺ�:");
//		child2.add("������������:");
		child2.add("SIM������:");
		childData.add(child2);
		
//		List<String> child3 = new ArrayList<String>();
//		child3.add("�����������:");
//		child3.add("����ʱ������:");
//		child3.add("�´α������:");
//		child3.add("�´α�������:");
//		child3.add("���չ�˾:");
//		child3.add("��������:");
//		child3.add("������:");
//		child3.add("������Ч����:");
//		child3.add("�´���������:");
//		child3.add("�������:");
//		child3.add("�´��������:");
//		childData.add(child3);
		
		List<String> child4 = new ArrayList<String>();
		child4.add("���պ���:");
		child4.add("��������:");
		child4.add("4S��:");
		child4.add("������Ԯ:");
		child4.add("����:");
		childData.add(child4);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_device_target, null);
		exList = (ExpandableListView) view.findViewById(R.id.expandableListView1);
		exList.setAdapter(adapter);
		exList.setDivider(null);
		exList.setGroupIndicator(null);
		return view;
	}

}
