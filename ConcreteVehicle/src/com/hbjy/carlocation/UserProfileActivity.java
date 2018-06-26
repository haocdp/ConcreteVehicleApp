package com.hbjy.carlocation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hbjy.CarInfoApplication;
import com.hbjy.LoginActivity;
import com.hbjy.utils.Constant;
import com.hbjy.view.InputPasswordDialog;

public class UserProfileActivity extends Activity implements OnClickListener{
	private ImageView wg_userPicture; //用户头像
	private TextView wg_userName;	//用户姓名
	private RelativeLayout wg_userInfo; //用户信息
	private RelativeLayout wg_userPass; //修改密码
	private RelativeLayout wg_userFeedback; //反馈意见
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);
		
		wg_userPicture = (ImageView) findViewById(R.id.user_placeholder);
		wg_userName = (TextView) findViewById(R.id.user_name);
		wg_userInfo = (RelativeLayout) findViewById(R.id.layout_user_info);
		wg_userPass = (RelativeLayout) findViewById(R.id.layout_user_password);
		wg_userFeedback = (RelativeLayout) findViewById(R.id.layout_user_feedback);
		
		fillData();
	}

	private void fillData(){
		String userName = CarInfoApplication.mInstance.getSharedPreferences(
				Constant.FILENAME, 0).getString(Constant.USERNAME, "");
		
		if(!"".equals(userName)){
			wg_userName.setText(userName);
		}else{
			startActivity(new Intent(this, LoginActivity.class));
			finish();
		}
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){
		case R.id.layout_user_info:
			break;
		case R.id.layout_user_password:
			break;
		case R.id.layout_user_feedback:
			break;
		default:
			break;
		}
	}

	private void validateDialog(){
		InputPasswordDialog inputPasswordDialog = new InputPasswordDialog(this);
		inputPasswordDialog.setContentView(R.layout.dialog_enter_password);
	}
}
