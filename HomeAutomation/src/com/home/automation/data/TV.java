package com.home.automation.data;

public class TV extends Controls{
	private int state;
	private String name;
	private int muteState;
	public static final String TYPE = "TV";
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

	/**
	 * @return the muteState
	 */
	public int getMuteState() {
		return muteState;
	}

	/**
	 * @param muteState the muteState to set
	 */
	public void setMuteState(int muteState) {
		this.muteState = muteState;
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
