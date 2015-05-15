package com.home.automation.data;

public class RGBLight extends Controls{
	private int state;
	private String name;
	public static final String TYPE = "RGBLight";
	private String areaName;
	private String areaType;


	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	
	public String getAreaName(){
		return this.areaName;
	}

	public void setAreaType(String areaType) {
		this.areaType = areaType;
	}
	
	public String getAreaType(){
		return this.areaType;
	}

}
