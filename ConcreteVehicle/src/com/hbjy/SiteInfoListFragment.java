package com.hbjy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hbjy.carlocation.R;

public class SiteInfoListFragment extends Fragment {

	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_empty, null);
		
		return view;
	}

}
