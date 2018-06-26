package com.hbjy.beans;

import org.json.JSONException;
import org.json.JSONObject;

public class VehicleGps {

	private double latitude;
	private double longitude;
	private String vehicleId;
	private String vehiclenum;
	private String recordtime;
	private double speed;
	private int direction;
	private double gpsmileage;
	private double mileage;
	private double oilVolumn;
	private String status;
	private String accstatus;

	public VehicleGps() {
	}

	public VehicleGps(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated constructor stub
		this.vehicleId = jsonObject.getString("VehicleId");
		this.vehiclenum = jsonObject.getString("Vehiclenum");
		this.oilVolumn = jsonObject.getDouble("oilVolumn");
		this.status = jsonObject.getString("status");
		this.direction = jsonObject.getInt("direction");
		this.gpsmileage = jsonObject.getDouble("gpsmileage");
		this.gpsmileage = jsonObject.getDouble("mileage");
		this.recordtime = jsonObject.getString("recordtime");
		this.speed = jsonObject.getInt("speed");
		this.longitude = jsonObject.getDouble("longitude");
		this.latitude = jsonObject.getDouble("latitude");
		this.accstatus = jsonObject.getString("accstatus");

	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public double getMileage() {
		return mileage;
	}

	public void setMileage(double mileage) {
		this.mileage = mileage;
	}

	public double getGpsmileage() {
		return gpsmileage;
	}

	public void setGpsmileage(double gpsmileage) {
		this.gpsmileage = gpsmileage;
	}

	public double getOilVolumn() {
		return oilVolumn;
	}

	public void setOilVolumn(double oilVolumn) {
		this.oilVolumn = oilVolumn;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRecordtime() {
		return recordtime;
	}

	public void setRecordtime(String recordtime) {
		this.recordtime = recordtime;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
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

	public String getAccstatus() {
		return accstatus;
	}

	public void setAccstatus(String accstatus) {
		this.accstatus = accstatus;
	}

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getVehiclenum() {
		return vehiclenum;
	}

	public void setVehiclenum(String vehiclenum) {
		this.vehiclenum = vehiclenum;
	}

}
