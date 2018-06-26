package com.hbjy.beans;

import org.json.JSONException;
import org.json.JSONObject;

public class Vehicle {

	private String SIM;
	private String UserId;
	private String VehicleId;
	private int VehicleState;
	private String VehicleType;
	private int VehiclecurState;
	private String Vehiclenum;
	private String accstatus;
	private String recordtime;
	private float speed;
	private int status;
	private double latitude;
	private double longitude;

	public Vehicle() {
	}

	public Vehicle(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated constructor stub
		this.SIM = jsonObject.getString("SIM");
		this.UserId = jsonObject.getString("UserId");
		this.VehicleId = jsonObject.getString("VehicleId");
		this.VehicleState = jsonObject.getInt("VehicleState");
		this.VehicleType = jsonObject.getString("VehicleType");
		this.VehiclecurState = jsonObject.getInt("VehiclecurState");
		this.Vehiclenum = jsonObject.getString("Vehiclenum");
		this.accstatus = jsonObject.getString("accstatus");
		this.recordtime = jsonObject.getString("recordtime");
		this.speed = jsonObject.getInt("speed");
		this.longitude = jsonObject.getDouble("longitude");
		this.latitude = jsonObject.getDouble("latitude");
		this.status = jsonObject.getInt("status");

	}

	public int getStatus() {
		return status;
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

	public void setStatus(int status) {
		this.status = status;
	}

	public String getSIM() {
		return SIM;
	}

	public void setSIM(String sIM) {
		SIM = sIM;
	}

	public String getUserId() {
		return UserId;
	}

	public void setUserId(String userId) {
		UserId = userId;
	}

	public String getVehicleId() {
		return VehicleId;
	}

	public void setVehicleId(String vehicleId) {
		VehicleId = vehicleId;
	}

	public int getVehicleState() {
		return VehicleState;
	}

	public void setVehicleState(int vehicleState) {
		VehicleState = vehicleState;
	}

	public String getVehicleType() {
		return VehicleType;
	}

	public void setVehicleType(String vehicleType) {
		VehicleType = vehicleType;
	}

	public int getVehiclecurState() {
		return VehiclecurState;
	}

	public void setVehiclecurState(int vehiclecurState) {
		VehiclecurState = vehiclecurState;
	}

	public String getVehiclenum() {
		return Vehiclenum;
	}

	public void setVehiclenum(String vehiclenum) {
		Vehiclenum = vehiclenum;
	}

	public String getAccstatus() {
		return accstatus;
	}

	public void setAccstatus(String accstatus) {
		this.accstatus = accstatus;
	}

	public String getRecordtime() {
		return recordtime;
	}

	public void setRecordtime(String recordtime) {
		this.recordtime = recordtime;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

}
