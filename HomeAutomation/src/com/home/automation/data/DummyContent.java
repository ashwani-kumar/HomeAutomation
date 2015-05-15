package com.home.automation.data;

import android.annotation.SuppressLint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

	 private static DummyContent instance = null;
	   protected DummyContent() {
	      // Exists only to defeat instantiation.
	   }
	   public static DummyContent getInstance() {
	      if(instance == null) {
	         instance = new DummyContent();
	      }
	      return instance;
	   }
	
	
	/**
	 * An array of sample (dummy) items.
	 */
	public static List<HomeArea > areaItems = new ArrayList<HomeArea>();
	public static Map<String, AppllianceCntrl> cntrlStrings= new HashMap<String, AppllianceCntrl>();
	
	public static Map<String, AppllianceCntrl> getCntrlStrings() {
		return cntrlStrings;
	}
	public static void setCntrlStrings(Map<String, AppllianceCntrl> cntrlStrings) {
		DummyContent.cntrlStrings = cntrlStrings;
	}
	public static void setAreaItems(List<HomeArea> areaItems) {
		DummyContent.areaItems = areaItems;
	}
	public static void setAreaItemMap(Map<Integer, HomeArea> areaItemMap) {
		DummyContent.areaItemMap = areaItemMap;
	}
	public static void setControlItems(List<Controls> controlItems) {
		DummyContent.controlItems = controlItems;
	}
	public static void setControlItemMap(Map<String, Controls> controlItemMap) {
		DummyContent.controlItemMap = controlItemMap;
	}


	/**
	 * A map of sample (dummy) items, by ID.
	 */
	@SuppressLint("UseSparseArrays")
	public static Map<Integer, HomeArea> areaItemMap = new HashMap<Integer, HomeArea>();
	
	/**
	 * An array of sample (dummy) items.
	 */
	public static List<Controls> controlItems = new ArrayList<Controls>();

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public static Map<String, Controls> controlItemMap = new HashMap<String, Controls>();
	
	public static final int MODE_CONTROL = 1;
	public static final int MODE_AREA = 2;
	public static int MENU_MODE = MODE_CONTROL;

//	static {
	
	///for control in area screen
			//1)get list of all controls with associated with associated Home area and corresponding control type
			//2)for each control type
			//3)get all control and create a map having control type and controls as entries
			//4) show it on UI
			
	
//		//1)get listof objects of all Homeareas(HOME_UNIT) with associated areatype(HOME_MASTER_ID)
//		//2) for each home area 
//				//get all controls(Appliances) object with with their associated home areas
//				//populates their control objects based on control name associated in APPLIANCES_MASTER(if else structures)
//				//associate each home area with its controls list
//		//3)add area item to show on UI
//		//4)add control item to show on UI
//		
//		// My room
//		HomeArea myBedRoom = new HomeArea();
//		myBedRoom.setAreaName("My Bedroom");
//		myBedRoom.setAreaType("Bed_Room");
//		
//		Controls myBedRoomCtrls = myBedRoomControls();
//		myBedRoomCtrls.setAreaName("My BedRoom");
//		myBedRoomCtrls.setAreaType("BedRoom");
//		
//		myBedRoom.setControls(myBedRoomCtrls);
//		
//		//Papa's room
//		HomeArea papasRoom = new HomeArea();
//		papasRoom.setAreaName("Papa's Bedroom");
//		papasRoom.setAreaType("Bed_Room");
//		
//		Controls papasRoomCtrls = papaRoomControls();
//		papasRoomCtrls.setAreaName("Papa's BedRoom");
//		papasRoomCtrls.setAreaType("BedRoom");
//		
//		papasRoom.setControls(papasRoomCtrls);
//		
//		HomeArea kitchen = new HomeArea();
//		kitchen.setAreaName("Kitchen");
//		kitchen.setAreaType("Kitchen");
//		
//		Controls myKitchenCtrls = myKitchenControls();
//		myKitchenCtrls.setAreaName("Kitchen");
//		myKitchenCtrls.setAreaType("Kitchen");
//		
//		kitchen.setControls(myKitchenCtrls);
//		
//		HomeArea lobby = new HomeArea();
//		lobby.setAreaName("Lobby");
//		lobby.setAreaType("Lobby");
//		
//		Controls myLobbyCtrls = myLobbyControls();
//		myLobbyCtrls.setAreaName("Lobby");
//		myLobbyCtrls.setAreaType("Lobby");
//		
//		lobby.setControls(myLobbyCtrls);
//		
//		
//	//addAreaItem(new DummyArea("1", myBedRoom, myBedRoomCtrls.getLightsOnCount()+" on/ "+(myBedRoomCtrls.getLightsCount()-myBedRoomCtrls.getLightsOnCount())+" off"));
//	//	addAreaItem(new DummyArea("2", papasRoom, papasRoomCtrls.getLightsOnCount()+" on/ "+(papasRoomCtrls.getLightsCount()-papasRoomCtrls.getLightsOnCount())+" off"));
//	//	addAreaItem(new DummyArea("3", kitchen, myKitchenCtrls.getLightsOnCount()+" on/ "+(myKitchenCtrls.getLightsCount()-myKitchenCtrls.getLightsOnCount())+" off"));
//	//	addAreaItem(new DummyArea("4", lobby, myLobbyCtrls.getLightsOnCount()+" on/ "+(myLobbyCtrls.getLightsCount()-myLobbyCtrls.getLightsOnCount())+" off"));
//		
////		addControlItem(new DummyControl("1", myBedRoomCtrls, myBedRoomCtrls.getLightsOnCount()+" on/ "+(myBedRoomCtrls.getLightsCount()-myBedRoomCtrls.getLightsOnCount())+" off"));
////		addControlItem(new DummyControl("2", papasRoomCtrls, papasRoomCtrls.getLightsOnCount()+" on/ "+(papasRoomCtrls.getLightsCount()-papasRoomCtrls.getLightsOnCount())+" off"));
////		addControlItem(new DummyControl("3", myKitchenCtrls, myKitchenCtrls.getLightsOnCount()+" on/ "+(myKitchenCtrls.getLightsCount()-myKitchenCtrls.getLightsOnCount())+" off"));
////		addControlItem(new DummyControl("4", myLobbyCtrls, myLobbyCtrls.getLightsOnCount()+" on/ "+(myLobbyCtrls.getLightsCount()-myLobbyCtrls.getLightsOnCount())+" off"));
//	
//	}

//	private static Controls myBedRoomControls() {
//		Controls myBedRoomCtrls = new Controls();
//		
//		AC mBedAC = new AC();
//		mBedAC.setState(0);	
//		mBedAC.setName("My AC");
//		mBedAC.setAreaName("My BedRoom");
//		mBedAC.setAreaType("BedRoom");
//		myBedRoomCtrls.setmAC(mBedAC, 0);	
//		
//		Fan mBedFan = new Fan();
//		mBedFan.setState(0);
//		mBedFan.setName("My Fan");
//		mBedFan.setAreaName("My BedRoom");
//		mBedFan.setAreaType("BedRoom");
//		myBedRoomCtrls.setmFan(mBedFan, 0);
//		
//		Lights mBedLight = new Lights("Main Light");
//		mBedLight.setState(1);
//		mBedLight.setAreaName("My BedRoom");
//		mBedLight.setAreaType("BedRoom");
//		myBedRoomCtrls.setmLights(mBedLight, 0);
//		
//		Lights mBedLight1 = new Lights("Study Lamp");
//		mBedLight1.setState(0);
//		mBedLight1.setAreaName("My BedRoom");
//		mBedLight1.setAreaType("BedRoom");
//		myBedRoomCtrls.setmLights(mBedLight1, 1);
//		
//		Lights mBedLight2 = new Lights("Night Bulb");
//		mBedLight2.setState(1);
//		mBedLight2.setAreaName("My BedRoom");
//		mBedLight2.setAreaType("BedRoom");
//		myBedRoomCtrls.setmLights(mBedLight2, 2);
//		
//		Lights mBedLight3 = new Lights("CFL Light");
//		mBedLight3.setState(0);
//		mBedLight3.setAreaName("My BedRoom");
//		mBedLight3.setAreaType("BedRoom");
//		myBedRoomCtrls.setmLights(mBedLight3, 3);
//		
//		Lights mBedLight4 = new Lights("Tube Light");		
//		mBedLight4.setState(0);
//		mBedLight4.setAreaName("My BedRoom");
//		mBedLight4.setAreaType("BedRoom");
//		myBedRoomCtrls.setmLights(mBedLight4, 4);
//		
//		TV myTV = new TV();
//		myTV.setName("My TV");
//		myTV.setState(0);
//		myTV.setMuteState(0);
//		myTV.setAreaName("My BedRoom");
//		myTV.setAreaType("BedRoom");
//		myBedRoomCtrls.setmTv(myTV, 0);
//		return myBedRoomCtrls;
//	}

//	private static void addControlItem(Controls dummyControl) {
//		// TODO Auto-generated method stub
//		controlItems.add(dummyControl);
////		controlItemMap.put(dummyControl.id, dummyControl);
//	}

	public void addAreaItem(HomeArea item) {
		areaItems.add(item);
		areaItemMap.put(item.getId(), item);
	}

	public void clearAreaItem() {
		areaItems.clear();
		areaItemMap.clear();
	}

}
