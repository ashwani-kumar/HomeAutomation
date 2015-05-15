package com.home.automation;

import java.util.ArrayList;
import java.util.HashMap;

import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.home.automation.SlidingMenuUtility.Area_Type;
import com.home.automation.SlidingMenuUtility.Control_Type;
import com.home.automation.data.Controls;
import com.home.automation.data.DummyContent;
import com.home.automation.data.HomeArea;

/**
 * A list fragment representing a list of Controls. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link AreaDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class AreaListFragment extends ListFragment {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(String id);
	}
	
	ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();

	private MyCustomBaseAdapter customAdapter;
	
	private LinearLayout headerLayout;

	private Object lastHeaderLayout;

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String id) {
			
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public AreaListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 // TODO: replace with a real list adapter.
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
			getListView().setDivider(null);
			SlidingMenuUtility slidingutility = SlidingMenuUtility.getInstance(getActivity());
		if (DummyContent.MENU_MODE == DummyContent.MODE_CONTROL) {
			generateListHeader(slidingutility.getControlType());
			refereshDataSet(slidingutility.getControlType());
			customAdapter = new MyCustomBaseAdapter(getActivity()
					.getBaseContext(),
					(ArrayList<HomeArea>) DummyContent.areaItems,slidingutility.getControlType());
			setListAdapter(customAdapter);
			customAdapter.notifyDataSetChanged();
			getListView().invalidate();
			Bundle arguments = new Bundle();
			arguments.putString(AreaDetailFragment.ARG_ITEM_ID, "1");
			arguments.putString(AreaDetailFragment.CONTROL_TYPE, slidingutility.getControlType().toString());
			AreaDetailFragment fragment = new AreaDetailFragment();
			fragment.setArguments(arguments);
			getActivity().getSupportFragmentManager().beginTransaction()
					.replace(R.id.area_detail_container, fragment).commit();
			if(getListView().getChildCount()>1)
			((TextView) getListView().getChildAt(1).findViewById(R.id.text1)).setTextColor(Color.parseColor("#FFFFFF"));
			getListView().invalidate();
		} else {
			generateListHeader(Area_Type.Bed_Room);
			customAdapter = new MyCustomBaseAdapter(getActivity()
					.getBaseContext(),
					(ArrayList<Controls>) DummyContent.controlItems,slidingutility.getControlType());
			setListAdapter(customAdapter);
			refereshDataSet(Area_Type.Bed_Room);
		}
		getListView().setSelectionAfterHeaderView();
		getListView().setItemChecked(1, true);
		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(getListView().getChildCount() > 1){
			getListView().performItemClick(getListView().getChildAt(1), 1, customAdapter.getItemId(1));
			//refereshDataSet(Control_Type.Light);
		} 	
	}

	/**
	 * @param light 
	 * 
	 */
	private void generateListHeader(Control_Type controlType) {
		ListView homeAreaList = getListView();
		if(lastHeaderLayout != null){
			homeAreaList.removeHeaderView(headerLayout);
		}
		LayoutInflater inflator = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		headerLayout = (LinearLayout) inflator.inflate(R.layout.list_header, null);
		ImageView controlIcon = (ImageView) headerLayout.findViewById(R.id.controlImage);
		TextView controlText = (TextView) headerLayout.findViewById(R.id.control_display_title);
		if(controlType.equals(Control_Type.Fan)){
			controlIcon.setImageResource(R.drawable.fan2_01);
			controlText.setText("Fan Controls");
		}else if(controlType.equals(Control_Type.AC)){
			controlIcon.setImageResource(R.drawable.ac2_01);
			controlText.setText("AC Controls");
		}else if(controlType.equals(Control_Type.TV)){
			controlIcon.setImageResource(R.drawable.tv2_01);
			controlText.setText("TV Controls");
		}else{
			controlIcon.setImageResource(R.drawable.light1_01);
			controlText.setText("Lighting Controls");
		}
		
		headerLayout.setClickable(false);
		lastHeaderLayout = headerLayout;
		homeAreaList.addHeaderView(headerLayout);
	}
	/**
	 * @param light 
	 * 
	 */
	private void generateListHeader(Area_Type areaType) {
		ListView controlList = getListView();
		if(lastHeaderLayout != null){
			controlList.removeHeaderView(headerLayout);
		}
		LayoutInflater inflator = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		headerLayout = (LinearLayout) inflator.inflate(R.layout.list_header, null);
		ImageView areaIcon = (ImageView) headerLayout.findViewById(R.id.controlImage);
		TextView areaText = (TextView) headerLayout.findViewById(R.id.control_display_title);
		if(areaType.equals(Area_Type.Bed_Room)){
			areaIcon.setImageResource(R.drawable.bedroom);
			areaText.setText("BedRoom");
		}else if(areaType.equals(Area_Type.Kitchen)){
			areaIcon.setImageResource(R.drawable.kitchen);
			areaText.setText("Kitchen");
		}else if(areaType.equals(Area_Type.Hall)){
			areaIcon.setImageResource(R.drawable.kitchen);
			areaText.setText("Hall");
		}else if(areaType.equals(Area_Type.Lobby)){
			areaIcon.setImageResource(R.drawable.lobby);
			areaText.setText("Lobby");
		}else{
			areaIcon.setImageResource(R.drawable.bathroom);
			areaText.setText("Bathroom");
		}		
		headerLayout.setClickable(false);
		lastHeaderLayout = headerLayout;
		controlList.addHeaderView(headerLayout);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}
		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);
		int childCount = listView.getChildCount();
		for (int i = 1; i < childCount; i++) {
			((TextView) getListView().getChildAt(i).findViewById(R.id.text1)).setTextColor(Color.parseColor("#FFFFFF"));
		}
		TextView textarea=(TextView) view.findViewById(R.id.text1);
		textarea.setTextColor(Color.parseColor("#f59131"));
		
		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		if(position!=0) 
//			mCallbacks.onItemSelected(DummyContent.areaItemMap.get(""+(position)).id);
		mCallbacks.onItemSelected(""+position);
		//listView.invalidate();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	public void refereshList(Control_Type controlType) {
		// TODO Auto-generated method stub
		customAdapter = new MyCustomBaseAdapter(getActivity()
				.getBaseContext(),
				(ArrayList<HomeArea>) DummyContent.areaItems ,controlType);
		setListAdapter(customAdapter);
		generateListHeader(controlType);
		refereshDataSet(controlType);
		if(getListView().getChildCount() > 1)
			getListView().performItemClick(getListView().getChildAt(1), 1, customAdapter.getItemId(1));	
	}

	private void refereshDataSet(Control_Type controlType) {
		// TODO Auto-generated method stub
		for(HomeArea item: DummyContent.areaItems){
			//int index = 0;
			Controls control = item.getControls();
			if (controlType.equals(Control_Type.Fan)) {
				//if(control.getFanCount()>0)
					item.setControlState(control.getFanOnCount()+" on/ "+(control.getFanCount()-control.getFanOnCount())+" off");
//				else
//					customAdapter.remove(index);
			} else if (controlType.equals(Control_Type.AC)) {
				//if(control.getAcCount()>0)
				item.setControlState(control.getAcOnCount()+" on/ "+(control.getAcCount()-control.getAcOnCount())+" off");
//				else
//					customAdapter.remove(index);
			} else if (controlType.equals(Control_Type.TV)) {
				//if(control.getTvCount()>0)
				item.setControlState(control.getTvOnCount()+" on/ "+(control.getTvCount()-control.getTvOnCount())+" off");
//				else
//					customAdapter.remove(index);
			} else if (controlType.equals(Control_Type.Light)){
				//if(control.getLightsCount()>0)
				item.setControlState(control.getLightsOnCount()+" on/ "+(control.getLightsCount()-control.getLightsOnCount())+" off");
//				else
//					customAdapter.remove(index);
			} else if (controlType.equals(Control_Type.SETTOP)){
				//if(control.getSTBCount()>0)
					item.setControlState(control.getStbOnCount()+" on/ "+(control.getSTBCount()-control.getSTBOnCount())+" off");
//				else
//					customAdapter.remove(index);
			}
			//index++;
		}
		
		
	}
	
	private void refereshDataSet(Area_Type areaType) {
		// TODO Auto-generated method stub
		for(Controls item: DummyContent.controlItems){
			//int index = 0;
			//HomeArea area = item.getarea();
			if (item.equals(Control_Type.Fan)) {
				//if(control.getFanCount()>0)
					item.setControlState(item.getFanOnCount()+" on/ "+(item.getFanCount()-item.getFanOnCount())+" off");
//				else
//					customAdapter.remove(index);
			} else if (areaType.equals(Control_Type.AC)) {
				//if(control.getAcCount()>0)
				item.setControlState(item.getAcOnCount()+" on/ "+(item.getAcCount()-item.getFanOnCount())+" off");
//				else
//					customAdapter.remove(index);
			} else if (areaType.equals(Control_Type.TV)) {
				//if(control.getTvCount()>0)
				item.setControlState(item.getTvOnCount()+" on/ "+(item.getTvCount()-item.getTvOnCount())+" off");
//				else
//					customAdapter.remove(index);
			} else if (areaType.equals(Control_Type.Light)){
				//if(control.getLightsCount()>0)
				item.setControlState(item.getLightsOnCount()+" on/ "+(item.getLightsCount()-item.getLightsOnCount())+" off");
//				else
//					customAdapter.remove(index);
			} else if (areaType.equals(Control_Type.SETTOP)){
				//if(control.getSTBCount()>0)
					item.setControlState(item.getStbOnCount()+" on/ "+(item.getSTBCount()-item.getSTBOnCount())+" off");
//				else
//					customAdapter.remove(index);
			}
			//index++;
		}
		customAdapter.notifyDataSetChanged();
		getListView().invalidate();
		
	}

	public void refereshList(Area_Type areaType) {
		// TODO Auto-generated method stub
//		customAdapter = new MyCustomBaseAdapter(getActivity()
//				.getBaseContext(),
//				(ArrayList<HomeArea>) DummyContent.areaItems);
//		setListAdapter(customAdapter);
		generateListHeader(areaType);
		refereshDataSet(areaType);
//		if(getListView().getChildCount() > 1)
//			getListView().performItemClick(getListView().getChildAt(1), 1, customAdapter.getItemId(1));	
	}
}
