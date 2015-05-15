package com.home.automation;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.home.automation.SlidingMenuUtility.Control_Type;
import com.home.automation.R.drawable;
import com.home.automation.data.AC;
import com.home.automation.data.Controls;
import com.home.automation.data.DummyContent;
import com.home.automation.data.Fan;
import com.home.automation.data.HomeArea;
import com.home.automation.data.Lights;
import com.home.automation.data.TV;
import com.home.automation.db.DataBaseHelper;
import com.home.automation.ftdi.FtdiManager;

/**
 * A fragment representing a single Area detail screen. This fragment is either
 * contained in a {@link AreaListActivity} in two-pane mode (on tablets) or a
 * {@link AreaDetailActivity} on handsets.
 */
public class AreaDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String CONTROL_TYPE = "control_type";

	private String controlType;

	private int muteControlState;

	private ArrayList<ToggleButton> lightControlList;

	private ArrayList<ToggleButton> fanControlList;

	private ToggleButton masterControlButton;

	private HomeArea mItem;
	
	//private static final int VID = 1027;//05c6
	//private static final int PID = 24577;//904cI believe it is 0x0000 for the Arduino Megas
	private FtdiManager fdtiMgr;
	//private static UsbController sUsbController;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public AreaDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if ( getArguments().containsKey(ARG_ITEM_ID) && getArguments().containsKey(CONTROL_TYPE)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			controlType = getArguments().getString(CONTROL_TYPE);
			mItem = DummyContent.areaItemMap.get(Integer.parseInt(getArguments().getString(
					ARG_ITEM_ID)));
		}
	}

	/**
	 * @param rootView 
	 * 
	 */
	@SuppressLint("NewApi")
	private void createTableLayout(View rootView) {
		TableLayout layout = new TableLayout(getActivity());  
		layout.setPadding(8, 8, 8, 8);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(  
				TableRow.LayoutParams.MATCH_PARENT,  
				TableRow.LayoutParams.MATCH_PARENT);  

		layout.setLayoutParams(lp);  
		layout.setStretchAllColumns(true);

		// int index = 0; 
		int controlCount = 0;
		Controls control = mItem.getControls();
		if(controlType != null && controlType.equalsIgnoreCase(Control_Type.Fan.toString())){
			controlCount = control.getFanCount();
			double tempControlCount = Double.valueOf(controlCount);
			int rowCount = (int) Math.round((tempControlCount/2));
			//TextView masterFanSwitch = (TextView)rootView.findViewById(R.id.master_switch_text);
			//masterFanSwitch.setText("All Fans");
			layout = drawControls(layout, controlCount, control, rowCount, Control_Type.Fan);
		}else if(controlType != null && controlType.equalsIgnoreCase(Control_Type.AC.toString())){
			controlCount = control.getAcCount();
			double tempControlCount = Double.valueOf(controlCount);
			int rowCount = (int) Math.round((tempControlCount/2));
			RelativeLayout masterControlSwitch = (RelativeLayout)rootView.findViewById(R.id.master_switch_layout);
			masterControlSwitch.setVisibility(View.GONE);
			layout = drawControls(layout, controlCount, control, rowCount, Control_Type.AC);
		}else if(controlType != null && controlType.equalsIgnoreCase(Control_Type.TV.toString())){
			controlCount = control.getTvCount();
			double tempControlCount = Double.valueOf(controlCount);
			int rowCount = (int) Math.round((tempControlCount/2));
			RelativeLayout masterControlSwitch = (RelativeLayout)rootView.findViewById(R.id.master_switch_layout);
			masterControlSwitch.setVisibility(View.GONE);
			layout = drawControls(layout, controlCount, control, rowCount, Control_Type.TV);
		}else if(controlType != null && controlType.equalsIgnoreCase(Control_Type.Light.toString())){
			controlCount = control.getLightsCount();
			double tempControlCount = Double.valueOf(controlCount);
			int rowCount = (int) Math.round((tempControlCount/2));

			layout = drawControls(layout, controlCount, control, rowCount, Control_Type.Light);
		}

		LinearLayout controlLayout = (LinearLayout) rootView.findViewById(R.id.control_layout) ; 
		controlLayout.addView(layout);
	}

	/**
	 * @param layout
	 * @param controlCount
	 * @param control
	 * @param rowCount
	 * @param controlChildType 
	 * @return
	 */
	private TableLayout drawControls(TableLayout layout, int controlCount,
			final Controls control, int rowCount, final Control_Type controlChildType) {
		LayoutInflater inflator = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//ImageView  areaImage=(ImageView) getView().findViewById(R.id.imageView1);
		if (controlChildType.equals(Control_Type.Light)) {
			lightControlList = new ArrayList<ToggleButton>();
		}else if (controlChildType.equals(Control_Type.Fan)) {
			fanControlList = new ArrayList<ToggleButton>();
		}
		for (int index = 0; index < rowCount; index++) {
			TableRow tr = new TableRow(getActivity());
			for (int k = 0; k < 2; k++) {
				if (controlCount != 0) {
					int controlState = 0;
					LinearLayout customRow = null;
					RelativeLayout customRow1=null;
					if (controlChildType.equals(Control_Type.Fan)) {
						final Fan fan = control.getmFan(controlCount - 1);
						controlState = fan.getState();
						final String controlName = fan.getName();
						final String areaName = fan.getAreaName(); 
						boolean activated = controlState == 0 ? false : true;
						customRow = (LinearLayout) inflator.inflate(R.layout.fan_layout, null);
						TextView tv = (TextView)customRow.findViewById(R.id.fan_name);
						final ToggleButton btn = (ToggleButton)customRow.findViewById(R.id.fan_switch);
						final Button spdbutton1=(Button)customRow.findViewById(R.id.fan_speed1);
						final Button spdbutton2=(Button)customRow.findViewById(R.id.fan_speed2);
						final Button spdbutton3=(Button)customRow.findViewById(R.id.fan_speed3);
						final Button spdbutton4=(Button)customRow.findViewById(R.id.fan_speed4);
						spdbutton1.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								if(fdtiMgr != null ){
									Log.d("Home_automation", "ftdi manager is not null");
									String cntrlStr=DataBaseHelper.getInstance(getActivity().getApplicationContext()).getControlString(controlChildType,controlName,spdbutton1.getText().toString());
									//it should originally it should come from appliances database
									//String str = "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
									//									byte[] array = str.getBytes();
									Log.d("Home_automation", "sending data str"+cntrlStr);
									sendToFTDIDevice(cntrlStr);
								}
							}
						});
						spdbutton2.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								if(fdtiMgr != null ){
									Log.d("Home_automation", "ftdi manager is not null");
									String cntrlStr=DataBaseHelper.getInstance(getActivity().getApplicationContext()).getControlString(controlChildType,controlName,spdbutton2.getText().toString());
									//it should originally it should come from appliances database
									//String str = "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
									//									byte[] array = str.getBytes();
									Log.d("Home_automation", "sending data str"+cntrlStr);
									sendToFTDIDevice(cntrlStr);
								}
							}
						});
						spdbutton3.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								if(fdtiMgr != null ){
									Log.d("Home_automation", "ftdi manager is not null");
									String cntrlStr=DataBaseHelper.getInstance(getActivity().getApplicationContext()).getControlString(controlChildType,controlName,spdbutton3.getText().toString());
									//it should originally it should come from appliances database
									//String str = "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
									//									byte[] array = str.getBytes();
									Log.d("Home_automation", "sending data str"+cntrlStr);
									sendToFTDIDevice(cntrlStr);
								}
							}
						});
						spdbutton4.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								if(fdtiMgr != null ){
									Log.d("Home_automation", "ftdi manager is not null");
									String cntrlStr=DataBaseHelper.getInstance(getActivity().getApplicationContext()).getControlString(controlChildType,controlName,spdbutton4.getText().toString());
									//it should originally it should come from appliances database
									//String str = "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
									//									byte[] array = str.getBytes();
									Log.d("Home_automation", "sending data str"+cntrlStr);
									sendToFTDIDevice(cntrlStr);
								}
							}
						});
						tv.setText(controlName);
						btn.setChecked(activated);
						fanControlList.add(btn);
						LayoutParams params = new LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT);
						params.setMargins(8, 8, 8, 8);
						tv.setLayoutParams(params);
						btn.setLayoutParams(params);
						btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
								Log.d("Home_automation", "on check changed for fan is called");
								DataBaseHelper dbHelper = new DataBaseHelper(getActivity().getApplicationContext());
								dbHelper.updateDBControlState(isChecked,controlName,areaName);
								int switchVal=(isChecked)?1:0;

								fan.setState(switchVal);

								if(isChecked){
									control.setFanOnCount(control
											.getFanOnCount() + 1);
								}else{
									control.setFanOnCount(control
											.getFanOnCount() - 1);
								}
								((AreaListFragment) getFragmentManager().findFragmentById(
										R.id.area_list)).refereshList(Control_Type.Fan);
								Log.d("Home_automation", "refreshing  of fan list called");
								if(fdtiMgr != null ){
									Log.d("Home_automation", "ftdi manager is not null");
									String cntrlStr=dbHelper.getControlString(controlChildType,controlName,isChecked,btn.getText().toString());
									//it should originally it should come from appliances database
									//String str = "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
									//									byte[] array = str.getBytes();
									Log.d("Home_automation", "sending data str"+cntrlStr);
									sendToFTDIDevice(cntrlStr);
								}
							}
						});
					} else if (controlChildType.equals(Control_Type.AC)) {
						customRow1 = (RelativeLayout) inflator.inflate(R.layout.ac_layout, null);
						TextView tv = (TextView)customRow1.findViewById(R.id.ac_name);
						final ToggleButton btn = (ToggleButton)customRow1.findViewById(R.id.ac_switch);
						final Button btn_16 = (Button)customRow1.findViewById(R.id.ac_temp_16);
						final Button btn_20 = (Button)customRow1.findViewById(R.id.ac_temp_20);
						final Button btn_24 = (Button)customRow1.findViewById(R.id.ac_temp_24);
						final Button btn_28 = (Button)customRow1.findViewById(R.id.ac_temp_28);
						final Button btn_fanspeed1 = (Button)customRow1.findViewById(R.id.ac_FN1);
						final Button btn_fanspeed2 = (Button)customRow1.findViewById(R.id.ac_FN2);
						final Button btn_fanspeed3 = (Button)customRow1.findViewById(R.id.ac_FN3);
						
						final AC ac = control.getmAC(controlCount - 1);
						final String controlName = ac.getName(); 
						final String areaName = ac.getAreaName();
						controlState = ac.getState();
						boolean activated = controlState == 0 ? false : true;
						tv.setText(controlName);
						btn.setChecked(activated);
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
								RelativeLayout.LayoutParams.WRAP_CONTENT,
								RelativeLayout.LayoutParams.WRAP_CONTENT);
						params.setMargins(8, 8, 8, 8);
						tv.setLayoutParams(params);
						btn.setLayoutParams(params);
						btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {	

							@Override
							public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
								DataBaseHelper dbHelper = new DataBaseHelper(getActivity().getApplicationContext());
								dbHelper.updateDBControlState(isChecked,controlName,areaName);
								int switchVal=(isChecked)?1:0;
								ac.setState(switchVal);

								if(isChecked){
									control.setAcOnCount(control
											.getAcOnCount() + 1);
								}else{
									control.setAcOnCount(control
											.getAcOnCount() - 1);
								}
								((AreaListFragment) getFragmentManager().findFragmentById(
										R.id.area_list)).refereshList(Control_Type.AC);
								if(fdtiMgr != null ){
									Log.d("Home_automation", "ftdi manager is not null");
									String cntrlStr=dbHelper.getControlString(controlChildType,controlName,isChecked,btn.getText().toString());
									//it should originally it should come from appliances database
									//String str = "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
									//									byte[] array = str.getBytes();
									Log.d("Home_automation", "sending data str"+cntrlStr);
									sendToFTDIDevice(cntrlStr);
								}
							}
						});
						btn_16.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								if(fdtiMgr != null ){
									Log.d("Home_automation", "ftdi manager is not null");
									String cntrlStr=DataBaseHelper.getInstance(getActivity().getApplicationContext()).getControlString(controlChildType,controlName,btn_16.getText().toString());
									//it should originally it should come from appliances database
									//String str = "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
									//									byte[] array = str.getBytes();
									Log.d("Home_automation", "sending data str"+cntrlStr);
									sendToFTDIDevice(cntrlStr);
								}
							}
						

						});

						btn_20.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if(fdtiMgr != null ){
									Log.d("Home_automation", "ftdi manager is not null");
									String cntrlStr=DataBaseHelper.getInstance(getActivity().getApplicationContext()).getControlString(controlChildType,controlName,btn_20.getText().toString());
									//it should originally it should come from appliances database
									//String str = "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
									//									byte[] array = str.getBytes();
									Log.d("Home_automation", "sending data str"+cntrlStr);
									sendToFTDIDevice(cntrlStr);
								}
							}


						});
						btn_24.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if(fdtiMgr != null ){
									Log.d("Home_automation", "ftdi manager is not null");
									String cntrlStr=DataBaseHelper.getInstance(getActivity().getApplicationContext()).getControlString(controlChildType,controlName,btn_24.getText().toString());
									//it should originally it should come from appliances database
									//String str = "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
									//									byte[] array = str.getBytes();
									Log.d("Home_automation", "sending data str"+cntrlStr);
									sendToFTDIDevice(cntrlStr);
								}
							}
						});
						btn_28.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if(fdtiMgr != null ){
									Log.d("Home_automation", "ftdi manager is not null");
									String cntrlStr=DataBaseHelper.getInstance(getActivity().getApplicationContext()).getControlString(controlChildType,controlName,btn_28.getText().toString());
									//it should originally it should come from appliances database
									//String str = "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
									//									byte[] array = str.getBytes();
									Log.d("Home_automation", "sending data str"+cntrlStr);
									sendToFTDIDevice(cntrlStr);
								}
							}
						});
	btn_fanspeed1.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								if(fdtiMgr != null ){
									Log.d("Home_automation", "ftdi manager is not null");
									String cntrlStr=DataBaseHelper.getInstance(getActivity().getApplicationContext()).getControlString(controlChildType,controlName,btn_fanspeed1.getText().toString());
									//it should originally it should come from appliances database
									//String str = "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
									//									byte[] array = str.getBytes();
									Log.d("Home_automation", "sending data str"+cntrlStr);
									sendToFTDIDevice(cntrlStr);
								}
							}
						

						});
	btn_fanspeed2.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(fdtiMgr != null ){
				Log.d("Home_automation", "ftdi manager is not null");
				String cntrlStr=DataBaseHelper.getInstance(getActivity().getApplicationContext()).getControlString(controlChildType,controlName,btn_fanspeed2.getText().toString());
				//it should originally it should come from appliances database
				//String str = "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
				//									byte[] array = str.getBytes();
				Log.d("Home_automation", "sending data str"+cntrlStr);
				sendToFTDIDevice(cntrlStr);
			}
		}
	

	});
	btn_fanspeed3.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(fdtiMgr != null ){
				Log.d("Home_automation", "ftdi manager is not null");
				String cntrlStr=DataBaseHelper.getInstance(getActivity().getApplicationContext()).getControlString(controlChildType,controlName,btn_fanspeed3.getText().toString());
				//it should originally it should come from appliances database
				//String str = "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
				//									byte[] array = str.getBytes();
				Log.d("Home_automation", "sending data str"+cntrlStr);
				sendToFTDIDevice(cntrlStr);
			}
		}
	

	});


					} else if (controlChildType.equals(Control_Type.TV)) {
						customRow = (LinearLayout) inflator.inflate(R.layout.tv_layout, null);
						TextView tv = (TextView)customRow.findViewById(R.id.tv_name);
						final ToggleButton on_btn = (ToggleButton)customRow.findViewById(R.id.tv_power_switch);
						final ToggleButton mute_btn = (ToggleButton)customRow.findViewById(R.id.tv_mute_switch);
						final Button btn_9 = (Button)customRow.findViewById(R.id.nine);
						final Button btn_8 = (Button)customRow.findViewById(R.id.eight);
						final Button btn_7 = (Button)customRow.findViewById(R.id.seven);
						final Button btn_6 = (Button)customRow.findViewById(R.id.six);
						final Button btn_5 = (Button)customRow.findViewById(R.id.five);
						final Button btn_4 = (Button)customRow.findViewById(R.id.four);
						final Button btn_3 = (Button)customRow.findViewById(R.id.three);
						final Button btn_2 = (Button)customRow.findViewById(R.id.two);
						final Button btn_1 = (Button)customRow.findViewById(R.id.one);
						final Button btn_0 = (Button)customRow.findViewById(R.id.zero);
						final Button btn_vup = (Button)customRow.findViewById(R.id.vup);
						final Button btn_vdn = (Button)customRow.findViewById(R.id.vdn);
						final TV mTv = control.getmTv(controlCount -1);
						final String controlName = mTv.getName(); 
						final String areaName = mTv.getAreaName();
						controlState = mTv.getState();
						muteControlState = mTv.getMuteState();
						boolean activated = controlState == 0 ? false : true;
						boolean muteActivated = muteControlState == 0 ? false : true;
						tv.setText(controlName);
						on_btn.setChecked(activated);
						on_btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {		

							@Override
							public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
								DataBaseHelper dbHelper = new DataBaseHelper(getActivity().getApplicationContext());
								dbHelper.updateDBControlState(isChecked,controlName,areaName);
								int switchVal=(isChecked)?1:0;
								mTv.setState(switchVal);

								if(isChecked){
									control.setTvOnCount(control
											.getTvOnCount() + 1);
								}else{
									control.setTvOnCount(control
											.getTvOnCount() - 1);
								}
								((AreaListFragment) getFragmentManager().findFragmentById(
										R.id.area_list)).refereshList(Control_Type.TV);
								if(fdtiMgr != null ){
									Log.d("Home_automation", "ftdi manager is not null");
									String cntrlStr=dbHelper.getControlString(controlChildType,controlName,isChecked,on_btn.getText().toString());
									//it should originally it should come from appliances database
									//String str = "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
									//									byte[] array = str.getBytes();
									Log.d("Home_automation", "sending data str"+cntrlStr);
								sendToFTDIDevice(cntrlStr);
								}
							}
						});
						mute_btn.setChecked(muteActivated);
						mute_btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {		

							@Override
							public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
								DataBaseHelper dbHelper = new DataBaseHelper(getActivity().getApplicationContext());
								dbHelper.updateDBControlState(isChecked,controlName,areaName);
								int switchVal=(isChecked)?1:0;
								mTv.setState(switchVal);

								((AreaListFragment) getFragmentManager().findFragmentById(
										R.id.area_list)).refereshList(Control_Type.TV);
								if(fdtiMgr != null ){
									Log.d("Home_automation", "ftdi manager is not null");
									String cntrlStr=dbHelper.getControlString(controlChildType,controlName,isChecked,mute_btn.getText().toString());
									//it should originally it should come from appliances database
									//String str = "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
									//									byte[] array = str.getBytes();
									Log.d("Home_automation", "sending data str"+cntrlStr);
								sendToFTDIDevice(cntrlStr);
								}
							}
						});
						
						btn_9.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (fdtiMgr != null) {
									Log.d("Home_automation",
											"ftdi manager is not null");
									String cntrlStr = DataBaseHelper
											.getInstance(
													getActivity()
															.getApplicationContext())
											.getControlString(controlChildType,
													controlName,
													"0"+btn_9.getText().toString());
									// it should originally it should come from
									// appliances database
									// String str =
									// "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
									// byte[] array = str.getBytes();
									Log.d("Home_automation", "sending data str"
											+ cntrlStr);
									sendToFTDIDevice(cntrlStr);
								}
							}
						});
						btn_8.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (fdtiMgr != null) {
									Log.d("Home_automation",
											"ftdi manager is not null");
									String cntrlStr = DataBaseHelper
											.getInstance(
													getActivity()
															.getApplicationContext())
											.getControlString(controlChildType,
													controlName,
													"0"+btn_8.getText().toString());
									// it should originally it should come from
									// appliances database
									// String str =
									// "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
									// byte[] array = str.getBytes();
									Log.d("Home_automation", "sending data str"
											+ cntrlStr);
									sendToFTDIDevice(cntrlStr);
								}
							}
						});
						btn_7.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (fdtiMgr != null) {
									Log.d("Home_automation",
											"ftdi manager is not null");
									String cntrlStr = DataBaseHelper
											.getInstance(
													getActivity()
															.getApplicationContext())
											.getControlString(controlChildType,
													controlName,
													"0"+btn_7.getText().toString());
									// it should originally it should come from
									// appliances database
									// String str =
									// "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
									// byte[] array = str.getBytes();
									Log.d("Home_automation", "sending data str"
											+ cntrlStr);
									sendToFTDIDevice(cntrlStr);
								}
							}
						});
						btn_6.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (fdtiMgr != null) {
									Log.d("Home_automation",
											"ftdi manager is not null");
									String cntrlStr = DataBaseHelper
											.getInstance(
													getActivity()
															.getApplicationContext())
											.getControlString(controlChildType,
													controlName,
													"0"+btn_6.getText().toString());
									// it should originally it should come from
									// appliances database
									// String str =
									// "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
									// byte[] array = str.getBytes();
									Log.d("Home_automation", "sending data str"
											+ cntrlStr);
									sendToFTDIDevice(cntrlStr);
								}
							}
						});
						btn_5.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (fdtiMgr != null) {
									Log.d("Home_automation",
											"ftdi manager is not null");
									String cntrlStr = DataBaseHelper
											.getInstance(
													getActivity()
															.getApplicationContext())
											.getControlString(controlChildType,
													controlName,
													"0"+btn_5.getText().toString());
									// it should originally it should come from
									// appliances database
									// String str =
									// "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
									// byte[] array = str.getBytes();
									Log.d("Home_automation", "sending data str"
											+ cntrlStr);
									sendToFTDIDevice(cntrlStr);
								}
							}
						});
						btn_4.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (fdtiMgr != null) {
									Log.d("Home_automation",
											"ftdi manager is not null");
									String cntrlStr = DataBaseHelper
											.getInstance(
													getActivity()
															.getApplicationContext())
											.getControlString(controlChildType,
													controlName,
													"0"+btn_4.getText().toString());
									// it should originally it should come from
									// appliances database
									// String str =
									// "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
									// byte[] array = str.getBytes();
									Log.d("Home_automation", "sending data str"
											+ cntrlStr);
									sendToFTDIDevice(cntrlStr);
								}
							}
						});
						btn_3.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (fdtiMgr != null) {
									Log.d("Home_automation",
											"ftdi manager is not null");
									String cntrlStr = DataBaseHelper
											.getInstance(
													getActivity()
															.getApplicationContext())
											.getControlString(controlChildType,
													controlName,
													"0"+btn_3.getText().toString());
									// it should originally it should come from
									// appliances database
									// String str =
									// "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
									// byte[] array = str.getBytes();
									Log.d("Home_automation", "sending data str"
											+ cntrlStr);
									sendToFTDIDevice(cntrlStr);
								}
							}
						});
						btn_2.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (fdtiMgr != null) {
									Log.d("Home_automation",
											"ftdi manager is not null");
									String cntrlStr = DataBaseHelper
											.getInstance(
													getActivity()
															.getApplicationContext())
											.getControlString(controlChildType,
													controlName,
													"0"+btn_2.getText().toString());
									// it should originally it should come from
									// appliances database
									// String str =
									// "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
									// byte[] array = str.getBytes();
									Log.d("Home_automation", "sending data str"
											+ cntrlStr);
									sendToFTDIDevice(cntrlStr);
								}
							}
						});
						btn_1.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (fdtiMgr != null) {
									Log.d("Home_automation",
											"ftdi manager is not null");
									String cntrlStr = DataBaseHelper
											.getInstance(
													getActivity()
															.getApplicationContext())
											.getControlString(controlChildType,
													controlName,
													"0"+btn_1.getText().toString());
									// it should originally it should come from
									// appliances database
									// String str =
									// "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
									// byte[] array = str.getBytes();
									Log.d("Home_automation", "sending data str"
											+ cntrlStr);
									sendToFTDIDevice(cntrlStr);
								}
							}
						});
						btn_0.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (fdtiMgr != null) {
									Log.d("Home_automation",
											"ftdi manager is not null");
									String cntrlStr = DataBaseHelper
											.getInstance(
													getActivity()
															.getApplicationContext())
											.getControlString(controlChildType,
													controlName,
													"0"+btn_0.getText().toString());
									// it should originally it should come from
									// appliances database
									// String str =
									// "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
									// byte[] array = str.getBytes();
									Log.d("Home_automation", "sending data str"
											+ cntrlStr);
									sendToFTDIDevice(cntrlStr);
								}
							}
						});
						btn_vup.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (fdtiMgr != null) {
									Log.d("Home_automation",
											"ftdi manager is not null");
									String cntrlStr = DataBaseHelper
											.getInstance(
													getActivity()
															.getApplicationContext())
											.getControlString(
													controlChildType,
													controlName,
													btn_vup.getText()
															.toString());
									// it should originally it should come from
									// appliances database
									// String str =
									// "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
									// byte[] array = str.getBytes();
									Log.d("Home_automation", "sending data str"
											+ cntrlStr);
									sendToFTDIDevice(cntrlStr);
								}
							}
						});
						btn_vdn.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (fdtiMgr != null) {
									Log.d("Home_automation",
											"ftdi manager is not null");
									String cntrlStr = DataBaseHelper
											.getInstance(
													getActivity()
															.getApplicationContext())
											.getControlString(
													controlChildType,
													controlName,
													btn_vdn.getText()
															.toString());
									// it should originally it should come from
									// appliances database
									// String str =
									// "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
									// byte[] array = str.getBytes();
									Log.d("Home_automation", "sending data str"
											+ cntrlStr);
									sendToFTDIDevice(cntrlStr);
								}
							}
						});
						
					} else {
						customRow = (LinearLayout) inflator.inflate(R.layout.lights_layout, null);
						TableRow.LayoutParams ll = new TableRow.LayoutParams(
								TableRow.LayoutParams.WRAP_CONTENT,
								TableRow.LayoutParams.WRAP_CONTENT);
						customRow.setLayoutParams(ll);
						customRow.setPadding(8, 8, 8, 8);
						TextView tv = (TextView)customRow.findViewById(R.id.light_name);
						final ToggleButton btn = (ToggleButton)customRow.findViewById(R.id.light_switch);
						final Lights lights = control.getmLights(controlCount - 1);
						controlState = lights.getState();
						final String controlName = lights.getName();
						final String areaName = lights.getAreaName();
						boolean activated = controlState == 0 ? false : true;
						tv.setText(controlName);
						btn.setChecked(activated);
						lightControlList.add(btn);
						LayoutParams params = new LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT);
						params.setMargins(8, 8, 8, 8);
						tv.setLayoutParams(params);
						btn.setLayoutParams(params);
						btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {		

							@Override
							public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
								DataBaseHelper dbHelper = new DataBaseHelper(getActivity().getApplicationContext());
								dbHelper.updateDBControlState(isChecked,controlName,areaName);
								int switchVal=(isChecked)?1:0;
								lights.setState(switchVal);

								if(isChecked){
									control.setLightsOnCount(control
											.getLightsOnCount() + 1);
								}else{
									control.setLightsOnCount(control
											.getLightsOnCount() - 1);
								}
								((AreaListFragment) getFragmentManager().findFragmentById(
										R.id.area_list)).refereshList(Control_Type.Light);
								if(fdtiMgr != null ){
									Log.d("Home_automation", "ftdi manager is not null");
									String cntrlStr=dbHelper.getControlString(controlChildType,controlName,isChecked,btn.getText().toString());
									//it should originally it should come from appliances database
									//String str = "5"+"Fan 1"+"2"+"1"+"SCI"+"000"+"FN"+"X"+"10"+"4"+"10-June-2014"+"0"+"0"+"1";
									//									byte[] array = str.getBytes();
									Log.d("Home_automation", "sending data str"+cntrlStr);
								sendToFTDIDevice(cntrlStr);
								}
							}
						});

					}
					if(customRow1!=null){
						tr.addView(customRow1);
					}else{
					tr.addView(customRow);
					}
					controlCount--;
				}
			}
			layout.addView(tr);
		}
		return layout;
	}
	
	/**
	 * @param str
	 */
	public void sendToFTDIDevice(final String str) {
				fdtiMgr.writeTodevice(str);
	}

	@SuppressLint("NewApi")
	private void setAreaImage(Drawable drawable, ImageView areaImage) {
		areaImage.setBackground(null);
		areaImage.setBackground(drawable);
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_area_detail,
				container, false);

		//start serial port communication 
		if(fdtiMgr==null){
	        Log.d("FPGA_FIFO Activity", "ftDev fdtiMgr was null so again calling fdtimgr");

		fdtiMgr= new FtdiManager(getActivity());
		fdtiMgr.openDevice();
		}
		// Show the dummy content as text in a TextView.
		if (mItem != null) {
			((TextView) rootView.findViewById(R.id.areaName))
			.setText(mItem.getAreaType().toString());
			ImageView areaImage = ((ImageView) rootView.findViewById(R.id.areaLogo));
			setAreaImage(getdrawableViaAreaType(mItem.getAreaType()),areaImage);
			createTableLayout(rootView);
		}
		masterControlButton = (ToggleButton) rootView.findViewById(R.id.master_switch);
		masterControlButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(arg1){
					if (controlType.equals(Control_Type.Light.toString())){
						for(ToggleButton btn:lightControlList){
							btn.setChecked(arg1);
						}
					}else if(controlType.equals(Control_Type.Fan.toString())){
						for(ToggleButton btn:fanControlList){
							btn.setChecked(arg1);
						}
					}
				}else{
					if (controlType.equals(Control_Type.Light.toString())){
						for(ToggleButton btn:lightControlList){
							btn.setChecked(arg1);
						}
					}else if(controlType.equals(Control_Type.Fan.toString())){
						for(ToggleButton btn:fanControlList){
							btn.setChecked(arg1);
						}
					}
				}
			}
		});
		return rootView;
	}

	private Drawable getdrawableViaAreaType(String areaType) {
		int id=getResId(areaType.split(" ")[0].toLowerCase(), getActivity().getApplicationContext(), drawable.class);
		Log.d("Home_automation", ""+id +"  "+areaType.split(" ")[0].toLowerCase()+""+getActivity().getPackageName());

		return  getResources().getDrawable(id);

	}
	public static int getResId(String fieldName, Context context, Class<?> c) {

		try {
			Field field= c.getDeclaredField(fieldName);
			return field.getInt(field);
		} catch (Exception e) {
			return 0;
		} 
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
        Log.d("FPGA_FIFO Activity", "ft onDestroy called");

		if(fdtiMgr!=null){
			fdtiMgr.closeDevice();
			fdtiMgr.destroy();
		}
	}

}
