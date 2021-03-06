package com.hbjy.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class LeftPanel extends LinearLayout{

	public int MAX_WIDTH = 0;
	private Context mContext;
	private OnPanelStatusChangedListener onPanelStatusChangedListener;
	public LeftPanel(Context context,int width,int height) {
		super(context);
		this.mContext = context;
		// TODO Auto-generated constructor stub
		LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(width, height);
		localLayoutParams.leftMargin = -localLayoutParams.width;
		this.MAX_WIDTH = Math.abs(localLayoutParams.leftMargin);
		setLayoutParams(localLayoutParams);
	}
	
	//单击的图片
	public void setBindView(ImageView paramIamgeView)
	{
		paramIamgeView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LinearLayout.LayoutParams localLayoutParams = (LayoutParams) LeftPanel.this.getLayoutParams();
				if(localLayoutParams.leftMargin<0)//展开动画
				{
//					localLayoutParams.leftMargin = Math.max(localLayoutParams.leftMargin +
//							20, -LeftPanel.this.MAX_WIDTH);
					onPanelStatusChangedListener.onPanelOpened(LeftPanel.this);
				}
				else//收缩动画
				{
					//localLayoutParams.leftMargin = Math.min(localLayoutParams.leftMargin-20, LeftPanel.this.MAX_WIDTH);
					onPanelStatusChangedListener.onPanelClosed(LeftPanel.this);
				}
				
			}
		});
	}
	
	//向控件中添加view
	public void setContentView(View paramView)
	{
		addView(paramView);
	}

	public void setOnPanelStatusChangedListener(
			OnPanelStatusChangedListener paramOnPanelStatusChangedListener) {
		this.onPanelStatusChangedListener = paramOnPanelStatusChangedListener;
	}
	
	//状态改变接口
	public static abstract interface OnPanelStatusChangedListener {
		public abstract void onPanelClosed(LeftPanel paramLeftPanel);

		public abstract void onPanelOpened(LeftPanel paramLeftPanel);
	}
	
	

}
