package com.hbjy.beans;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 物流车辆任务单
 * @author hao
 *
 */
public class PlansTransport implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String vehicleNo;//车辆编号/车牌号
	private String goods; //运输货物
	private String startPosition; //发车地点
	private String endPosition; //目标地点
	private double distance; //距离
	private String createTime; //创建时间
	private double currentLatitude; //当前车辆位置维度
	private double currentLongitude; //当前车辆位置经度
	private String status; //任务状态：正在进行；已经完成
	private double progress; //任务进度
	
	public PlansTransport(){}
	
	public PlansTransport(JSONObject jsonObject) throws JSONException{
		this.vehicleNo = "".equals(jsonObject.getString("vehicleNo")) ||
				jsonObject.getString("vehicleNo") == null ? "" : jsonObject.getString("vehicleNo");
		
		this.goods = "".equals(jsonObject.getString("goods")) ||
				jsonObject.getString("goods") == null ? "" : jsonObject.getString("goods");
		
		this.startPosition = "".equals(jsonObject.getString("startPosition")) ||
				jsonObject.getString("startPosition") == null  ? "" : jsonObject.getString("startPosition");
		
		this.endPosition = "".equals(jsonObject.getString("endPosition")) ||
				jsonObject.getString("endPosition") == null  ? "" : jsonObject.getString("endPosition");
		
		this.distance = jsonObject.getDouble("distance");
		
		this.currentLatitude = jsonObject.getDouble("currentLatitude");
		
		this.currentLongitude = jsonObject.getDouble("currentLongitude");
		
		this.progress = jsonObject.getDouble("progress");
		
		this.status = "".equals(jsonObject.getString("status")) ||
				jsonObject.getString("status") == null  ? "" : jsonObject.getString("status");
		
		this.createTime = "".equals(jsonObject.getString("createTime")) ||
				jsonObject.getString("createTime") == null  ? "" : jsonObject.getString("createTime");
	}
	
	public String getVehicleNo() {
		return vehicleNo;
	}
	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}
	public String getGoods() {
		return goods;
	}
	public void setGoods(String goods) {
		this.goods = goods;
	}
	public String getStartPosition() {
		return startPosition;
	}
	public void setStartPosition(String startPosition) {
		this.startPosition = startPosition;
	}
	public String getEndPosition() {
		return endPosition;
	}
	public void setEndPosition(String endPosition) {
		this.endPosition = endPosition;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public double getCurrentLatitude() {
		return currentLatitude;
	}
	public void setCurrentLatitude(double currentLatitude) {
		this.currentLatitude = currentLatitude;
	}
	public double getCurrentLongitude() {
		return currentLongitude;
	}
	public void setCurrentLongitude(double currentLongitude) {
		this.currentLongitude = currentLongitude;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public double getProgress() {
		return progress;
	}
	public void setProgress(double progress) {
		this.progress = progress;
	}
	
	
}
