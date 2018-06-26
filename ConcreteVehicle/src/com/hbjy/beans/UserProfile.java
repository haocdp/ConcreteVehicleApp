package com.hbjy.beans;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *@author:hao
 *@createTime:下午1:11:34
 *@desc: 用户资料
 */
public class UserProfile {
	private String nickName;
	private String gender;
	private String birthday;
	private String introduction;
	
	public UserProfile(JSONObject jsonObject) throws JSONException{
		this.nickName = "".equals(jsonObject.getString("nickName")) || 
				jsonObject.getString("nickName") == null ? "" :jsonObject.getString("nickName");
		this.gender = "".equals(jsonObject.getString("gender")) || 
				jsonObject.getString("gender") == null ? "" :jsonObject.getString("gender");
		this.birthday = "".equals(jsonObject.getString("birthday")) || 
				jsonObject.getString("birthday") == null ? "" :jsonObject.getString("birthday");
		this.introduction = "".equals(jsonObject.getString("introduction")) || 
				jsonObject.getString("introduction") == null ? "" :jsonObject.getString("introduction");
	}
	
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getIntroduction() {
		return introduction;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	
	
}
