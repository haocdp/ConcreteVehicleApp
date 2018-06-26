package com.hbjy;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hbjy.carlocation.FeedbackActivity;
import com.hbjy.carlocation.R;
import com.hbjy.carlocation.ShowUserProfileActivity;
import com.hbjy.nets.ToastUtil;
import com.hbjy.utils.Constant;
import com.hbjy.view.InputPasswordDialog;

public class MotorInfoListFragment extends Fragment implements OnClickListener{
	private View view;
	private ImageView wg_userPicture; //用户头像
	private TextView wg_userName;	//用户姓名
	private RelativeLayout wg_userInfo; //用户信息
	private RelativeLayout wg_userPass; //修改密码
	private RelativeLayout wg_userFeedback; //反馈意见
	
	private InputPasswordDialog inputPasswordDialog; //验证密码弹出框
	private View validateDialog;//验证密码
	private EditText inputPassword;//验证密码输入框
	private Button inputPasswordOk;//验证密码确定键
	private Button inputPasswordCancel;//验证密码取消键
	
	private Dialog updatePasswordDialog; //更新密码弹出框
	private View updateDialog; //更新密码
	private EditText inputNewPassword1;//更新密码输入框1
	private EditText inputNewPassword2;//更新密码输入框2
	private Button updatePasswordOk;//更新密码确认键
	private Button updatePasswordCancel;//更新密码取消键
	
	private boolean isBlank1 = true; //更新密码界面判断是否已经填了密码
	private boolean isBlank2 = true; //更新密码界面判断是否已经填了密码
	
	private Toast toast;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = LayoutInflater.from(getActivity()).inflate(
				R.layout.activity_user_profile, null);
		
		wg_userPicture = (ImageView) view.findViewById(R.id.user_placeholder);
		wg_userName = (TextView) view.findViewById(R.id.user_name);
		wg_userInfo = (RelativeLayout) view.findViewById(R.id.layout_user_info);
		wg_userPass = (RelativeLayout) view.findViewById(R.id.layout_user_password);
		wg_userFeedback = (RelativeLayout) view.findViewById(R.id.layout_user_feedback);
		
		wg_userInfo.setOnClickListener(this);
		wg_userPass.setOnClickListener(this);
		wg_userFeedback.setOnClickListener(this);
		
		fillData();
		
		return view;
	}

	/**
	 * 填充数据
	 */
	private void fillData(){
		String userName = CarInfoApplication.mInstance.getSharedPreferences(
				Constant.FILENAME, 0).getString(Constant.USERNAME, "");
		
		if(!"".equals(userName)){
			wg_userName.setText(userName);
		}else{
			getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
			getActivity().finish();
		}
	}
	
	@SuppressLint("ResourceAsColor") @Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){
		case R.id.layout_user_info:
//			toast = Toast.makeText(getActivity(), "暂无个人信息", Toast.LENGTH_LONG);
//			toast.show();
			Intent showUserProfileIntent = new Intent(this.getActivity(),ShowUserProfileActivity.class);
			startActivity(showUserProfileIntent);
			break;
		case R.id.layout_user_password: //点击修改密码选项
//			validatePasswordDialog();
			ToastUtil.showLongToast(getActivity(), "该服务暂不支持");
			break;
		case R.id.layout_user_feedback: //点击用户反馈选项
			Intent intent = new Intent(this.getActivity(),FeedbackActivity.class);
			startActivity(intent);
			break;
		case R.id.inputPasswordOk: //验证密码中的确定按钮
			boolean flag = validatePassword(inputPassword.getText().toString());
			if(flag){
				updatePasswordDialog();
				inputPasswordDialog.cancel();
			}else{
				toast = Toast.makeText(getActivity(), "密码错误，请重新输入！", Toast.LENGTH_LONG);
				toast.show();
				inputPassword.setText("");
			}
			break;
		case R.id.inputPasswordCancel: //验证密码中的取消按钮
			inputPasswordDialog.cancel();
			break;
		case R.id.updatePasswordOk: //更新密码弹窗中的确定按钮
			if(inputNewPassword1.getText().toString().equals(
					inputNewPassword2.getText().toString())){
				if(updatePassword(inputNewPassword1.getText().toString())){
					toast = Toast.makeText(getActivity(), "更新成功！", Toast.LENGTH_LONG);
					toast.show();
					updatePasswordDialog.cancel();
				}else{
					toast = Toast.makeText(getActivity(), "更新失败，请重试！", Toast.LENGTH_LONG);
					toast.show();
				}
			}else{
				toast = Toast.makeText(getActivity(), "密码不相同，请重新输入！", Toast.LENGTH_LONG);
				toast.show();
				inputNewPassword1.setText("");
				inputNewPassword2.setText("");
			}
			break;
		case R.id.updatePasswordCancel: //更新密码弹窗中的取消按钮
			updatePasswordDialog.cancel();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 修改密码验证窗口
	 */
	private void validatePasswordDialog(){
		inputPasswordDialog = new InputPasswordDialog(getActivity());
		
		validateDialog = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_enter_password, null);
		
		inputPassword = (EditText)validateDialog.findViewById(R.id.inputPassword);
		inputPasswordOk = (Button) validateDialog.findViewById(R.id.inputPasswordOk);
		inputPasswordCancel = (Button) validateDialog.findViewById(R.id.inputPasswordCancel);
		
		/**
		 * 初始状态 确定按钮是不可用
		 */
		inputPasswordOk.setEnabled(false);
//		inputPasswordOk.setPressed(true);
		/**
		 * 监听输入框，当输入框中输入字符，恢复按钮状态
		 */
		inputPassword.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(s.toString().contains(" ")){
					inputPassword.setText(inputPassword.getText().toString().trim());
					inputPassword.setSelection(inputPassword.getText().toString().length());
				}else{
					if(s.toString().length() > 0){
	//					inputPasswordOk.setPressed(false);
						inputPasswordOk.setEnabled(true);
					}else{
						inputPasswordOk.setEnabled(false);
	//					inputPasswordOk.setPressed(true);
					}
				}
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
		
		inputPasswordOk.setOnClickListener(this);
		inputPasswordCancel.setOnClickListener(this);
		
		inputPasswordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//没有标题
		inputPasswordDialog.setCanceledOnTouchOutside(false);//点击空白不消失
		inputPasswordDialog.addContentView(validateDialog,
				new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		inputPasswordDialog.dismiss();
		inputPasswordDialog.show();
	}

	/**
	 * 验证密码
	 */
	private boolean validatePassword(String password){
		/**
		 * @todo 验证用户密码正确，返回true or false
		 */
		return true;
	}
	
	/**
	 * 更新密码窗口
	 */
	private void updatePasswordDialog(){
		updatePasswordDialog = new Dialog(getActivity());
		
		updateDialog = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_update_password, null);
		
		inputNewPassword1 = (EditText) updateDialog.findViewById(R.id.inputNewPassword1);
		inputNewPassword2 = (EditText) updateDialog.findViewById(R.id.inputNewPassword2);
		updatePasswordOk = (Button) updateDialog.findViewById(R.id.updatePasswordOk);
		updatePasswordCancel = (Button) updateDialog.findViewById(R.id.updatePasswordCancel);
		
		updatePasswordOk.setEnabled(false);
		inputNewPassword1.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(s.toString().contains(" ")){
					inputNewPassword1.setText(inputNewPassword1.getText().toString().trim());
					inputNewPassword1.setSelection(inputNewPassword1.getText().toString().length());
				}else{
					if(s.toString().length() > 0){
						isBlank1 = false;
						if(!isBlank2)
							updatePasswordOk.setEnabled(true);
					}else{
						isBlank1 = true;
						updatePasswordOk.setEnabled(false);
					}
				}
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
		inputNewPassword2.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(s.toString().contains(" ")){
					inputNewPassword2.setText(inputNewPassword2.getText().toString().trim());
					inputNewPassword2.setSelection(inputNewPassword2.getText().toString().length());
				}else{
					if(s.toString().length() > 0){
						isBlank2 = false;
						if(!isBlank1)
							updatePasswordOk.setEnabled(true);
					}else{
						isBlank2 = true;
						updatePasswordOk.setEnabled(false);
					}
				}
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
		
		updatePasswordOk.setOnClickListener(this);
		updatePasswordCancel.setOnClickListener(this);
		
		updatePasswordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//没有标题
		updatePasswordDialog.setCanceledOnTouchOutside(false);//点击空白不消失
		updatePasswordDialog.addContentView(updateDialog,
				new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		inputPasswordDialog.dismiss();
		updatePasswordDialog.show();
	}
	
	/**
	 * 更新密码操作
	 */
	private boolean updatePassword(String password){
		/**
		 * @todo 更新用户密码，返回true or false
		 */
		return true;
	}
	
}
