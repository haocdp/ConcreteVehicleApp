package com.hbjy.beans;

public class PlanConcreteTran {

	private String fplanId;//任务单
	private String concreteName;
	private String startPoint;
	private String endPoint;
	private double transCap;//预计方
	private double transDistance;//距离
	private String startTime;
	private String endTime;
	private int count;//趟次
	private double transedCap;//交付方
	private String site;//施工单位
	
	
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public double getTransedCap() {
		return transedCap;
	}
	public void setTransedCap(double transedCap) {
		this.transedCap = transedCap;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getFplanId() {
		return fplanId;
	}
	public void setFplanId(String fplanId) {
		this.fplanId = fplanId;
	}
	public String getConcreteName() {
		return concreteName;
	}
	public void setConcreteName(String concreteName) {
		this.concreteName = concreteName;
	}
	public String getStartPoint() {
		return startPoint;
	}
	public void setStartPoint(String startPoint) {
		this.startPoint = startPoint;
	}
	public double getTransCap() {
		return transCap;
	}
	public void setTransCap(double transCap) {
		this.transCap = transCap;
	}
	public double getTransDistance() {
		return transDistance;
	}
	public void setTransDistance(double transDistance) {
		this.transDistance = transDistance;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getEndPoint() {
		return endPoint;
	}
	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}
	
}
