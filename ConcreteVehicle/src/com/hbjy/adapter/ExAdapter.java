package com.hbjy.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.hbjy.carlocation.R;

public class ExAdapter extends BaseExpandableListAdapter {

	Context context;
	List<String> groupDatas;
	List<List<String>> childDatas;
	public ExAdapter() {
		// TODO Auto-generated constructor stub
		super();
	}
	public ExAdapter(Context context,List<String> groupData,List<List<String>> childData)
	{
		super();
		this.context = context;
		this.groupDatas = groupData;
		this.childDatas = childData;
	}
	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return groupDatas.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return childDatas.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return groupDatas.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childDatas.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = convertView;
		if(view==null)
		{
			LayoutInflater inflater = LayoutInflater.from(context);
			view = inflater.inflate(R.layout.exlistview_member_listview, null);
		}
		TextView title  = (TextView) view.findViewById(R.id.content_001);
		title.setText(getGroup(groupPosition).toString());

		return view;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = convertView;
		if(view==null)
		{
			LayoutInflater inflater = LayoutInflater.from(context);
			view = inflater.inflate(R.layout.exlistview_member_childitem, null);
			
		}
		TextView title = (TextView) view.findViewById(R.id.child_text);
		title.setText(childDatas.get(groupPosition).get(childPosition).toString());
		
		
		return view;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

}
