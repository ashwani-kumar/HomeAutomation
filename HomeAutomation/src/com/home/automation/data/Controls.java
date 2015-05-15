package com.home.automation.data;

import java.util.ArrayList;

public class Controls {
	
	private ArrayList<Fan> mFan;
	private ArrayList<AC> mAC;
	private ArrayList<Lights> mLights;
	private ArrayList<TV> mTv;
	private ArrayList<RGBLight> mRGB;
	private ArrayList<SetTopBox> mSTB;
	private HomeArea area;
	private int fanOnCount;
	private int acOnCount;
	private int lightsOnCount;
	private int tvOnCount;
	private int rgbOnCount;
	private int stbOnCount;

	private String areaType;
	private String areaName;
	private String controlState="";
	
	public Controls(){
		mFan = new ArrayList<Fan>();
		mAC = new ArrayList<AC>();
		mLights = new ArrayList<Lights>();
		mTv = new ArrayList<TV>();
		mRGB= new ArrayList<RGBLight>();
		mSTB= new ArrayList<SetTopBox>();
	}
	
	public ArrayList<Fan> getmFan() {
		return mFan;
	}

	public ArrayList<AC> getmAC() {
		return mAC;
	}

	public ArrayList<Lights> getmLights() {
		return mLights;
	}

	public ArrayList<TV> getmTv() {
		return mTv;
	}

	public ArrayList<RGBLight> getmRGB() {
		return mRGB;
	}

	public ArrayList<SetTopBox> getmSTB() {
		return mSTB;
	}

	public int getRgbOnCount() {
		return rgbOnCount;
	}

	public void setRgbOnCount(int rgbOnCount) {
		this.rgbOnCount = rgbOnCount;
	}

	public int getStbOnCount() {
		return stbOnCount;
	}

	public void setStbOnCount(int stbOnCount) {
		this.stbOnCount = stbOnCount;
	}

	public void setFanOnCount(int fanOnCount) {
		this.fanOnCount = fanOnCount;
	}

	public void setAcOnCount(int acOnCount) {
		this.acOnCount = acOnCount;
	}

	public void setTvOnCount(int tvOnCount) {
		this.tvOnCount = tvOnCount;
	}

	public RGBLight getmRGB(int index) {
		return mRGB.get(index);
	}

	public void setmRGB(RGBLight mRGB, int index) {
		if(mRGB != null && mRGB.getState() == 1)
			rgbOnCount++;
		this.mRGB.add(index, mRGB);
	}

	public SetTopBox getmSTB(int index) {
		return mSTB.get(index);
	}

	public void setmSTB(SetTopBox mSTB, int index) {
		if(mSTB != null && mSTB.getState() == 1)
			stbOnCount++;
		this.mSTB.add(index, mSTB);
	}

	public Fan getmFan(int index) {
		return mFan.get(index);
	}
	public void setmFan(Fan mFan, int index) {
		if(mFan != null && mFan.getState() == 1)
			fanOnCount++;
		this.mFan.add(index, mFan);
	}
	public AC getmAC(int index) {
		return mAC.get(index);
	}
	public void setmAC(AC mAC, int index) {
		if(mAC != null && mAC.getState() == 1)
			acOnCount++;
		this.mAC.add(index, mAC);
	}
	public Lights getmLights(int index) {
		return mLights.get(index);
	}
	public void setmLights(Lights mLights, int index) {
		if(mLights != null && mLights.getState() == 1)
			setLightsOnCount(getLightsOnCount() + 1);
		this.mLights.add(index, mLights);
	}
	public TV getmTv(int index) {
		return mTv.get(index);
	}
	public void setmTv(TV mTv, int index) {
		if(mTv != null && mTv.getState() == 1){			
			tvOnCount++;
		}
		this.mTv.add(index, mTv);
	}
	
	public int getFanOnCount() {
		return fanOnCount;
	}
	public int getAcOnCount() {
		return acOnCount;
	}
	public int getLightsOnCount() {
		return lightsOnCount;
	}
	public int getTvOnCount() {
		return tvOnCount;
	}
	public int getRGBOnCount(){
		return rgbOnCount;
	}
	public int getSTBOnCount(){
		return stbOnCount;
	}

	public int getFanCount() {
		return mFan.size();
	}
	public int getAcCount() {
		return mAC.size();
	}
	public int getTvCount() {
		return mTv.size();
	}
	public int getLightsCount() {
		return mLights.size();
	}
	public int getRGBCount() {
		return mRGB.size();
	}
	public int getSTBCount() {
		return mSTB.size();
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

	public void setLightsOnCount(int lightsOnCount) {
		this.lightsOnCount = lightsOnCount;
	}

	public HomeArea getarea() {
		// TODO Auto-generated method stub
		return area;
	}
	public void  setArea(HomeArea area) {
		// TODO Auto-generated method stub
		this.area= area;
	}

	public void setControlState(String controlstate) {
		this.controlState=controlstate;
	}
	public String getControlState() {
		return controlState;
	}
	

}
