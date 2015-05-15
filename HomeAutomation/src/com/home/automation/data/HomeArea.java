package com.home.automation.data;

public class HomeArea {
	private int id;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private String areaName;
	private Controls controls;
	private String areaType;
	private boolean IS_ACTIVE;	
	private String controlState="";

	public void setControlState(String controlState) {
		this.controlState = controlState;
	}

	public String getControlState() {
		return controlState;
	}

	public boolean isIS_ACTIVE() {
		return IS_ACTIVE;
	}

	public void setIS_ACTIVE(boolean iS_ACTIVE) {
		IS_ACTIVE = iS_ACTIVE;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public Controls getControls() {
		return controls;
	}
	

	public void setControls(Controls controls) {
		this.controls = controls;
	}
	
	/**
	 * @return the areaType
	 */
	public String getAreaType() {
		return areaType.trim();
	}

	/**
	 * @param areaType the areaType to set
	 */
	public void setAreaType(String areaType) {
		this.areaType = areaType;
	}

}
