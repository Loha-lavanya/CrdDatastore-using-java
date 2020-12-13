package com.datastore.crd.model;


import java.util.Date;

public class Value {
	private int timeToLive;
	private String createdTime;
	public Value()
	{
		
	}
	public Value(int timeToLive,String createdTime) {
		super();
		this.timeToLive = timeToLive;
		this.createdTime = createdTime;
	}
	public int getTimeToLive() {
		return timeToLive;
	}
	public void setTimeToLive(int timeToLive) {
		this.timeToLive = timeToLive;
	}
	public String getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	
	

}


