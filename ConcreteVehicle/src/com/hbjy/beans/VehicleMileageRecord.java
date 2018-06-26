package com.hbjy.beans;

import org.json.JSONException;
import org.json.JSONObject;

public class VehicleMileageRecord {

	private String vehiclenum;// 车辆编号
	private String vehicleId;// 车牌号

	private String e_acc;// acc状态
	private double e_gpsmileage;// 里程
	private double e_latitude;// 纬度
	private double e_longitude;// 经度
	private double mileage;// 里程
	private double oilVolumn;// 油量
	private String endtime;// 结束时间
	private String periodtime;// 持续时间
	private String s_acc;// 开始状态
	private double s_gpsmileage;//
	private double s_latitude;//
	private double s_longitude;//

	private String starttime;//

	private String startPoint;
	private String endPoint;

	public VehicleMileageRecord() {

	}

	public VehicleMileageRecord(JSONObject jsonObject) throws JSONException {
		this.vehiclenum = jsonObject.getString("Vehiclenum");
		this.vehicleId = jsonObject.getString("VehicleId");
		this.e_acc = jsonObject.getString("e_acc");
		this.e_gpsmileage = jsonObject.getDouble("e_gpsmileage");
		this.e_latitude = jsonObject.getDouble("e_latitude");
		this.e_longitude = jsonObject.getDouble("e_longitude");
		this.mileage = jsonObject.getDouble("e_mileage")
				- jsonObject.getDouble("s_mileage");
		this.oilVolumn = jsonObject.getDouble("e_oilVolumn")
				- jsonObject.getDouble("s_oilVolumn");
		this.endtime = jsonObject.getString("endtime");
		this.periodtime = jsonObject.getString("periodtime");
		this.s_acc = jsonObject.getString("s_acc");
		this.s_gpsmileage = jsonObject.getDouble("s_gpsmileage");
		this.s_latitude = jsonObject.getDouble("s_latitude");
		this.s_longitude = jsonObject.getDouble("s_longitude");
		this.starttime = jsonObject.getString("starttime");
	}

	public String getVehiclenum() {
		return vehiclenum;
	}

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getE_acc() {
		return e_acc;
	}

	public void setE_acc(String e_acc) {
		this.e_acc = e_acc;
	}

	public double getE_gpsmileage() {
		return e_gpsmileage;
	}

	public void setE_gpsmileage(double e_gpsmileage) {
		this.e_gpsmileage = e_gpsmileage;
	}

	public double getE_latitude() {
		return e_latitude;
	}

	public void setE_latitude(double e_latitude) {
		this.e_latitude = e_latitude;
	}

	public double getE_longitude() {
		return e_longitude;
	}

	public void setE_longitude(double e_longitude) {
		this.e_longitude = e_longitude;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getPeriodtime() {
		return periodtime;
	}

	public void setPeriodtime(String periodtime) {
		this.periodtime = periodtime;
	}

	public String getS_acc() {
		return s_acc;
	}

	public void setS_acc(String s_acc) {
		this.s_acc = s_acc;
	}

	public double getS_gpsmileage() {
		return s_gpsmileage;
	}

	public void setS_gpsmileage(double s_gpsmileage) {
		this.s_gpsmileage = s_gpsmileage;
	}

	public double getS_latitude() {
		return s_latitude;
	}

	public void setS_latitude(double s_latitude) {
		this.s_latitude = s_latitude;
	}

	public double getS_longitude() {
		return s_longitude;
	}

	public void setS_longitude(double s_longitude) {
		this.s_longitude = s_longitude;
	}

	public double getMileage() {
		return mileage;
	}

	public void setMileage(double mileage) {
		this.mileage = mileage;
	}

	public double getOilVolumn() {
		return oilVolumn;
	}

	public void setOilVolumn(double oilVolumn) {
		this.oilVolumn = oilVolumn;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(String startPoint) {
		this.startPoint = startPoint;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	public void setVehiclenum(String vehiclenum) {
		this.vehiclenum = vehiclenum;
	}

}
