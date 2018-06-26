package com.hbjy.carlocation;

import org.apache.http.Header;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hbjy.nets.ToastUtil;
import com.hbjy.utils.Constant;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class FeedbackActivity extends Activity implements OnClickListener{

	private EditText feedbackText; //意见反馈文本框
	private Button feedbackButton; //意见反馈提交按钮
	private TextView feedbackTextNumber; //意见反馈提交字数
	
	private ImageView feedbackReturn; //返回按钮
	
	private Activity activity = this;
	
	private AsyncHttpClient client = new AsyncHttpClient();
	
	private String requestUrl = Constant.HOST + "/user/addFeedback";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		
		feedbackText = (EditText) findViewById(R.id.feedback_edit);
		feedbackButton = (Button) findViewById(R.id.feedback_submit);
		feedbackTextNumber = (TextView) findViewById(R.id.feedback_text_number);
		feedbackReturn = (ImageView) findViewById(R.id.feedback_return);
		
		feedbackButton.setOnClickListener(this);
		feedbackReturn.setOnClickListener(this);
		
		feedbackButton.setEnabled(false);
		
		feedbackText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				feedbackTextNumber.setText(""+s.toString().length());
				if(s.toString().length() > 0)
					feedbackButton.setEnabled(true);
				else
					feedbackButton.setEnabled(false);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.feedback_return: //返回键
			finish();
			break;
		case R.id.feedback_submit: //提交
			submitFeedback(feedbackText.getText().toString());
			break;
		default:
			break;
		}
	}

	/**
	 * 提交反馈意见
	 * @param feedback
	 */
	private void submitFeedback(String feedback){
		Log.d("反馈信息", feedback);
		RequestParams requestParams = new RequestParams();
		requestParams.put("content", feedback);
		requestParams.put("userId", 1);
		
		client.post(requestUrl, requestParams, new AsyncHttpResponseHandler(){
			
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0, arg1, arg2);
//				Log.d("success","" + arg0 + arg1.toString() + arg2.toString());
				ToastUtil.showLongToast(getApplicationContext(), "反馈成功");
				activity.finish();
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1, arg2, arg3);
//				Log.d("failure","" + arg0 + arg1.toString() + arg2.toString());
				ToastUtil.showLongToast(getApplicationContext(), "反馈失败，请稍后重试");
			}
		});
	}
	
	
	/*public class ResponseHandler extends AsyncHttpResponseHandler{
		private Activity activity;
		
		public ResponseHandler(){
			super();
		}
		
		public ResponseHandler(Activity activity){
			this.setActivity(activity);
		}

		public Activity getActivity() {
			return activity;
		}

		public void setActivity(Activity activity) {
			this.activity = activity;
		}
	}*/
}
