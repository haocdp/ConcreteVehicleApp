package com.hbjy.carlocation;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.hbjy.beans.UserProfile;
import com.hbjy.nets.HttpUtil;
import com.hbjy.nets.ToastUtil;
import com.hbjy.utils.Constant;
import com.loopj.android.http.JsonHttpResponseHandler;

public class ShowUserProfileActivity extends Activity implements OnClickListener{

	private ImageView back;
	private TextView user_nickName;
	private TextView user_gender;
	private TextView user_birthday;
	private TextView user_introduction;
	
	private Activity activity = this;
	
	private String requestUrl = Constant.HOST + "/user/getUserProfile";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_user_profile);
		
		back = (ImageView) findViewById(R.id.user_profile_return);
		user_nickName = (TextView) findViewById(R.id.user_nickName);
		user_gender = (TextView) findViewById(R.id.user_gender);
		user_birthday = (TextView) findViewById(R.id.user_birthday);
		user_introduction = (TextView) findViewById(R.id.user_introduction);
		
		back.setOnClickListener(this);
		
		fillData();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.user_profile_return){
			finish();
		}
	}
	
	/**
	 * 填充数据
	 */
	private void fillData(){
		HttpUtil.get2(requestUrl, null, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, response);
				if(response != null){
					try {
						UserProfile userProfile = new UserProfile(response);
						
						user_nickName.setText(userProfile.getNickName());
						user_gender.setText(userProfile.getGender());
						user_birthday.setText(userProfile.getBirthday());
						user_introduction.setText(userProfile.getIntroduction());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			@Override
			public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
				// TODO Auto-generated method stub
				super.onFailure(statusCode, e, errorResponse);
				ToastUtil.showLongToast(activity, "数据请求失败，请稍后重试");
			}
			
//			@Override
//			public void onFailure(Throwable e, JSONObject errorResponse) {
//				// TODO Auto-generated method stub
//				super.onFailure(e, errorResponse);
//				ToastUtil.showLongToast(activity, "数据请求失败，请稍后重试");
//			}
		});
	}
}
