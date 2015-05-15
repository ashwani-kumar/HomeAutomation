package com.home.automation;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;

import com.home.automation.data.DummyContent;
import com.home.automation.data.HomeArea;

@SuppressWarnings("deprecation")
public class SlidingMenuUtility extends ViewGroup{
	
	private Control_Type controlType;
	private Area_Type areaType;
	
	private static int image;

	private SlidingDrawer menuDrawer;
	private ImageButton menuImageBar;
	private ToggleImageButton menuImageSense;
	
	private LinearLayout menuScrollView;

	private ToggleImageButton lastSelectedMenuItem = null;
	private int lastSelectedIndex;
	
	/**
	 * Store the control type information
	 * @author Administrator
	 *
	 */
	public enum Control_Type{
		Light,
		Fan,
		AC,
		TV, SETTOP;
		private static final String name = Control_Type.class.getName();
		public void attachTo(Intent intent) {
			intent.putExtra("name", ordinal());
		}
		public static Control_Type detachFrom(Intent intent) {
			if(!intent.hasExtra(name)) throw new IllegalStateException();
			return values()[intent.getIntExtra(name, -1)];
		}
	}

	enum Area_Type{
		Bed_Room,
		Kitchen,
		Bath_Room,
		Lobby,
		Hall,
		Garage;
		private static final String name = Area_Type.class.getName();
		public void attachTo(Intent intent) {
			intent.putExtra(name, ordinal());
		}
		public static Area_Type detachFrom(Intent intent) {
			if(!intent.hasExtra(name)) throw new IllegalStateException();
			return values()[intent.getIntExtra(name, -1)];
		}
	}

	private static SlidingMenuUtility myInstance = null;
	private SlidingMenuUtility(FragmentActivity context) {
		super(context);
		Init(context);
	}

	/**
	 * @param context
	 */
	public void Init(FragmentActivity context) {
		menuDrawer = (SlidingDrawer)context.findViewById(R.id.slidingDrawer1);
		menuScrollView = (LinearLayout) context.findViewById(R.id.menuLayoutView);
		drawMenuPermItems(context);
	}
	
	public static SlidingMenuUtility getInstance(FragmentActivity context){
		if(myInstance==null){
			myInstance = new SlidingMenuUtility(context);
		}
		return myInstance;
	}
	
//	@Override
//	public Parcelable onSaveInstanceState(){
//		Parcelable bundle = super.onSaveInstanceState();
//		Bundle state = new Bundle();
//		state.putParcelable("scrollView", menuDrawer);
//		return null;
//		
//	}
//	
//	@Override
//	public void onRestoreInstanceState(Parcelable state){
//		
//	}
	
	//immediatly open the drawer, patch preserve open state over multiple activities
	public void openDrawer(){
		menuDrawer.open();
	}
		
	// might be used in future
	public void closeDrawer(){
		menuDrawer.close();
	}
		

	/**
	 * @return the controlType
	 */
	public Control_Type getControlType() {
		return controlType;
	}

	/**
	 * @param controlType the controlType to set
	 */
	public void setControlType(Control_Type controlType) {
		this.controlType = controlType;
	}

	
	/**
	 * @return the controlType
	 */
	public Area_Type getAreaType() {
		return areaType;
	}

	/**
	 * @param controlType the controlType to set
	 */
	public void setAreaType(Area_Type areaType) {
		this.areaType = areaType;
	}

	/**
	 * 
	 */
	public void drawMenuPermItems(final FragmentActivity context) {
		//add scence button on menubar
		menuImageSense = (ToggleImageButton) context.findViewById(R.id.sensemenuimage);
		
		menuImageSense.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(lastSelectedMenuItem!=null){
					lastSelectedMenuItem.setChecked(false);
				}
				lastSelectedMenuItem = menuImageSense;
			}
		});
		
		//add switch button on menubar
		menuImageBar = (ImageButton) context.findViewById(R.id.permmenuimage);
		menuImageBar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(DummyContent.MENU_MODE == DummyContent.MODE_AREA)
					DummyContent.MENU_MODE = DummyContent.MODE_CONTROL;
				else
					DummyContent.MENU_MODE = DummyContent.MODE_AREA;
				redrawMenu(DummyContent.MENU_MODE,context);
			}
		});
	}

	public void redrawMenu(int menuMode, FragmentActivity context) {
		// TODO Auto-generated method stub
		if(menuMode == 1){
			drawControlMenu(context);
		}else{
			drawAreaMenu(context);
		}
	}

	private void drawAreaMenu(final FragmentActivity context) {
		clearMenuItems();
		CustomMenuItem menuItem = null;
		final String actName=getTopActivityStackName(context);
		int areaCount = DummyContent.areaItems.size();
		for(int i = 0; i < areaCount; i++){
			HomeArea area = DummyContent.areaItems.get(i);
			Log.d("Area Type", area.getAreaType());
			if(area.getAreaType().equalsIgnoreCase("BedRoom")){
				menuItem = new CustomMenuItem(context, R.drawable.bedroom01, R.drawable.ic_66,area.getAreaName());
				LinearLayout customMenuItem = menuItem.getCustomView();
				final ToggleImageButton menuImage = (ToggleImageButton) customMenuItem.findViewById(R.id.menuimage);
				menuScrollView.addView(customMenuItem,i);
				final int index = i;
				restoreLastViewState(actName, menuImage, R.drawable.ic_66, index );
				menuImage.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						setAreaType(Area_Type.Bed_Room);
						((AreaListFragment) context.getSupportFragmentManager().findFragmentById(
								R.id.area_list)).refereshList(getAreaType());
					
						saveLastItemMenu(menuImage, index);
					}
				});
			}else if(area.getAreaType().equalsIgnoreCase("Kitchen")){
				menuItem = new CustomMenuItem(context, R.drawable.ic_7, R.drawable.ic_77,area.getAreaName());
				LinearLayout customMenuItem = menuItem.getCustomView();
				final ToggleImageButton menuImage = (ToggleImageButton) customMenuItem.findViewById(R.id.menuimage);
				menuScrollView.addView(customMenuItem,i);
				final int index = i;
				restoreLastViewState(actName, menuImage, R.drawable.ic_77, index );
				menuImage.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						saveLastItemMenu(menuImage, index);
					}
				});
			}else if(area.getAreaType().equalsIgnoreCase("Lobby")){
				menuItem = new CustomMenuItem(context, R.drawable.ic_13_13, R.drawable.ic_13_13_13,area.getAreaName());
				LinearLayout customMenuItem = menuItem.getCustomView();
				final ToggleImageButton menuImage = (ToggleImageButton) customMenuItem.findViewById(R.id.menuimage);
				menuScrollView.addView(customMenuItem,i);
				final int index = i;
				restoreLastViewState(actName, menuImage, R.drawable.ic_13_13_13, index );
				menuImage.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						saveLastItemMenu(menuImage, index);
					}
				});
			}else if(area.getAreaType().equalsIgnoreCase("Hall")){
				menuItem = new CustomMenuItem(context, R.drawable.ic_12_12, R.drawable.ic_12_12_12,area.getAreaName());
				LinearLayout customMenuItem = menuItem.getCustomView();
				final ToggleImageButton menuImage = (ToggleImageButton) customMenuItem.findViewById(R.id.menuimage);
				menuScrollView.addView(customMenuItem,i);
				final int index = i;
				restoreLastViewState(actName, menuImage, R.drawable.ic_12_12_12, index );
				menuImage.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						saveLastItemMenu(menuImage, index);
					}
				});
			}else if(area.getAreaType().equalsIgnoreCase("Bathroom")){
				menuItem = new CustomMenuItem(context, R.drawable.ic_9, R.drawable.ic_99,area.getAreaName());
				LinearLayout customMenuItem = menuItem.getCustomView();
				final ToggleImageButton menuImage = (ToggleImageButton) customMenuItem.findViewById(R.id.menuimage);
				menuScrollView.addView(customMenuItem,i);
				final int index = i;
				restoreLastViewState(actName, menuImage, R.drawable.ic_99, index );
				menuImage.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						saveLastItemMenu(menuImage, index);
					}
				});
			}
		}
	}
	
	private void drawControlMenu(final FragmentActivity context) {
		clearMenuItems();
		final String actName=getTopActivityStackName(context);
		for(int i = 0; i < 5; i++){
			if(i == 0){
				CustomMenuItem menuItem = new CustomMenuItem(context, R.drawable.ic_1, R.drawable.ic_11,R.string.light);
				LinearLayout customMenuItem = menuItem.getCustomView();
				final ToggleImageButton menuImage = (ToggleImageButton) customMenuItem.findViewById(R.id.menuimage);
				menuScrollView.addView(customMenuItem, 0);
				final int index = i;
				restoreLastViewState(actName, menuImage, R.drawable.ic_11, index );
				menuImage.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						if(!actName.contains("AreaListActivity")){
							setControlType(Control_Type.Light);
							Intent i = new Intent(context, AreaListActivity.class);
							Control_Type.Light.attachTo(i);
							context.startActivity(i);
							context.finish();
						}else{
							setControlType(Control_Type.Light);
							((AreaListFragment) context.getSupportFragmentManager().findFragmentById(
									R.id.area_list)).refereshList(getControlType());
							callFirstItem(context);

						}
							saveLastItemMenu(menuImage, index);
						//showServerResponse();
						}
					
				});
			}
			if(i == 2){
				CustomMenuItem menuItem = new CustomMenuItem(context, R.drawable.ic_4, R.drawable.ic_44,R.string.tv);
				LinearLayout customMenuItem = menuItem.getCustomView();
				final ToggleImageButton menuImage = (ToggleImageButton) customMenuItem.findViewById(R.id.menuimage);
				menuScrollView.addView(customMenuItem,2);
				final int index = i;
				restoreLastViewState(actName, menuImage, R.drawable.ic_44, index );
				menuImage.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						setControlType(Control_Type.TV);
						if(!actName.contains("AreaListActivity")){
							Intent i = new Intent(context, AreaListActivity.class);
							Control_Type.TV.attachTo(i);
							context.startActivity(i);
						}else{
							((AreaListFragment) ((FragmentActivity) context).getSupportFragmentManager().findFragmentById(
									R.id.area_list)).refereshList(getControlType());
							callFirstItem(context);

						}
							saveLastItemMenu(menuImage, index);
					}
					
				});
			}
			if(i == 1){
				CustomMenuItem menuItem = new CustomMenuItem(context, R.drawable.ic_2, R.drawable.ic_22, R.string.fan);
				LinearLayout customMenuItem = menuItem.getCustomView();
				final ToggleImageButton menuImage = (ToggleImageButton) customMenuItem.findViewById(R.id.menuimage);
				menuScrollView.addView(customMenuItem,1);
				final int index = i;
				restoreLastViewState(actName, menuImage, R.drawable.ic_22, index );
				menuImage.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						setControlType(Control_Type.Fan);
						if(!actName.contains("AreaListActivity")){
							Intent i = new Intent(context, AreaListActivity.class);
							Control_Type.Fan.attachTo(i);
							context.startActivity(i);
						}else{
							((AreaListFragment) ((FragmentActivity) context).getSupportFragmentManager().findFragmentById(
									R.id.area_list)).refereshList(getControlType());
							callFirstItem(context);

						}
							saveLastItemMenu(menuImage, index);
					}
					
				});
			}
			if(i == 3){
				CustomMenuItem menuItem = new CustomMenuItem(context, R.drawable.ic_3, R.drawable.ic_33,R.string.ac);
				LinearLayout customMenuItem = menuItem.getCustomView();
				final ToggleImageButton menuImage = (ToggleImageButton) customMenuItem.findViewById(R.id.menuimage);
				menuScrollView.addView(customMenuItem,3);
				final int index = i;
				restoreLastViewState(actName, menuImage, R.drawable.ic_33, index );
				menuImage.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						setControlType(Control_Type.AC);
						if(!actName.contains("AreaListActivity")){
							Intent i = new Intent(context, AreaListActivity.class);
							Control_Type.AC.attachTo(i);
							context.startActivity(i);
						}else{
							((AreaListFragment) ((FragmentActivity) context).getSupportFragmentManager().findFragmentById(
									R.id.area_list)).refereshList(getControlType());
							callFirstItem(context);

							
						}
							saveLastItemMenu(menuImage, index);
					}
							});
			}
//			if(i == 4){
//				CustomMenuItem menuItem = new CustomMenuItem(context, R.drawable.ic_5, R.drawable.ic_55,R.string.settop);
//				LinearLayout customMenuItem = menuItem.getCustomView();
//				final ToggleImageButton menuImage = (ToggleImageButton) customMenuItem.findViewById(R.id.menuimage);
//				menuScrollView.addView(customMenuItem,4);
//				final int index = i;
//				restoreLastViewState(actName, menuImage, R.drawable.ic_55, index );
//				menuImage.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View arg0) {
//						setControlType(Control_Type.SETTOP);
//						if(!actName.contains("AreaListActivity")){
//							Intent i = new Intent(context, AreaListActivity.class);
//							Control_Type.AC.attachTo(i);
//							context.startActivity(i);
//						}else{
//							((AreaListFragment) ((FragmentActivity) context).getSupportFragmentManager().findFragmentById(
//									R.id.area_list)).refereshList(getControlType());
//										callFirstItem(context);
//						}
//							saveLastItemMenu(menuImage, index);
//					}
//
//				});
//			}
		}
	}
	
	/**
	 * @param menuImage
	 * @param index
	 */
	public void saveLastItemMenu(
			final ToggleImageButton menuImage, final int index) {
		if(lastSelectedMenuItem!=null){
			if(lastSelectedMenuItem.isChecked()){
			lastSelectedMenuItem.setChecked(false);
			}else{
				lastSelectedMenuItem.setChecked(true);
			}
		}
		lastSelectedMenuItem = menuImage;
		lastSelectedIndex = index;
	}

	/**
	 * @param actName
	 * @param menuImage
	 * @param resId 
	 */
	public void restoreLastViewState(String actName,
			final ToggleImageButton menuImage, int resId, int index) {
		if(lastSelectedMenuItem!=null){
		Log.d("SlidingDrawer", "is AreaListActi "+actName.contains("AreaListActivity") +" is last Checked"+ lastSelectedMenuItem.isChecked() +" at position"+ index +"and was at"+lastSelectedIndex);
		if(actName.contains("AreaListActivity") && lastSelectedMenuItem.isChecked() && lastSelectedIndex == index){
			menuImage.setBackgroundResource(resId);
			menuImage.setChecked(true);
			menuImage.invalidate();
			lastSelectedMenuItem.setChecked(false);
			lastSelectedMenuItem = menuImage;
		}
		}
	}

	public String getTopActivityStackName(Activity activity)
	{
		ActivityManager mActivityManager = (ActivityManager)
				activity.getSystemService(Activity.ACTIVITY_SERVICE);
		//PackageManager mPackageManager = activity.getPackageManager();
		String activityname = mActivityManager.getRunningTasks(1).get(0).topActivity.getClassName();
		Log.d("TopActivity", activityname);
		//        ApplicationInfo mApplicationInfo;
		//        try 
		//        {
		//            mApplicationInfo = mPackageManager.getApplicationInfo( packageName, 0);
		//        } catch (NameNotFoundException e) {
		//            mApplicationInfo = null;
		//        }
		//       String appName = (String) (mApplicationInfo != null ? 
		//               mPackageManager.getApplicationLabel(mApplicationInfo) : "(unknown)");

		return activityname;
	}
	
	private void clearMenuItems() {
		int childCount = menuScrollView.getChildCount();
		if(menuScrollView != null && childCount!=0)
			menuScrollView.removeAllViews();
	}

	public void setSlidingDrawer() {
		// TODO Auto-generated method stub
		menuDrawer = myInstance.menuDrawer;
		menuScrollView = myInstance.menuScrollView;
		lastSelectedMenuItem = myInstance.lastSelectedMenuItem;
	}

	private void callFirstItem(final FragmentActivity context) {
		Bundle arguments = new Bundle();
		arguments.putString(AreaDetailFragment.ARG_ITEM_ID, "1");
		arguments.putString(AreaDetailFragment.CONTROL_TYPE, getControlType().toString());
		AreaDetailFragment fragment = new AreaDetailFragment();
		fragment.setArguments(arguments);
		context.getSupportFragmentManager().beginTransaction()
				.replace(R.id.area_detail_container, fragment).commit();
	}
	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return the lastSelectedMenuItem
	 */
	public ToggleImageButton getLastSelectedMenuItem() {
		return lastSelectedMenuItem;
	}

	/**
	 * @param lastSelectedMenuItem the lastSelectedMenuItem to set
	 */
	public void setLastSelectedMenuItem(ToggleImageButton lastSelectedMenuItem) {
		this.lastSelectedMenuItem = lastSelectedMenuItem;
	}

	/**
	 * @return the image
	 */
	public static int getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public static void setImage(int image) {
		SlidingMenuUtility.image = image;
	}

}
