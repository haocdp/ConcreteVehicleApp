package com.hbjy.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *@author: hao
 *@CreateTime:2016年7月17日上午11:09:06
 *@description: 物品描述
 */
public class Goods implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String desc;
	private List<Position> trace = new ArrayList<Position>();
	
	
	public Goods(){}
	public Goods(String name,String desc,List<Position> trace){
		this.name = name;
		this.desc = desc;
		this.trace =trace;
	}
	
	public Goods(JSONObject jsonObject)throws JSONException{
		if(!"".equals(jsonObject.getString("name")) || 
				jsonObject.getString("name")== null){
			this.name = jsonObject.getString("name");
		}
		if(!"".equals(jsonObject.getString("description")) ||
				jsonObject.getString("description") == null){
			this.desc = jsonObject.getString("description");
		}
		
		if(jsonObject.getJSONArray("positionVOList")!= null){
			JSONArray jsonArray = jsonObject.getJSONArray("positionVOList");
			for(int index = 0; index<jsonArray.length();index++){
				this.trace.add(new Position(jsonArray.getJSONObject(index)));
			}
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public List<Position> getTrace(){
		return this.trace;
	}
	
	public static class Position implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private double latitude;
		private double longitude;
		
		public Position(){}
		
		public Position(double latitude, double longitude){
			this.setLatitude(latitude);
			this.setLongitude(longitude);
		}
		
		public Position(JSONObject jsonObject)throws JSONException{
			if(jsonObject.getDouble("latitude") != 0){
				this.latitude = jsonObject.getDouble("latitude");
			}
			if(jsonObject.getDouble("longitude") != 0){
				this.longitude = jsonObject.getDouble("longitude");
			}
		}

		public double getLatitude() {
			return latitude;
		}

		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}

		public double getLongitude() {
			return longitude;
		}

		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}
		
		
	}

	
}
