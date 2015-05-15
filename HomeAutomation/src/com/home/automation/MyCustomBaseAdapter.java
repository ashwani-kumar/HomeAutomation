package com.home.automation;

import java.util.ArrayList;

import android.content.Context;
import android.opengl.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.home.automation.SlidingMenuUtility.Control_Type;
import com.home.automation.data.Controls;
import com.home.automation.data.DummyContent;
import com.home.automation.data.HomeArea;

public class MyCustomBaseAdapter extends BaseAdapter {
	private static ArrayList<HomeArea> homeDetails;
	private Control_Type currentControlType;

	private LayoutInflater mInflater;

	@SuppressWarnings("unchecked")
	public MyCustomBaseAdapter(Context context, ArrayList<?> homeDetailList, Control_Type controltype) {
		if(DummyContent.MENU_MODE == DummyContent.MODE_CONTROL){
		homeDetails = (ArrayList<HomeArea>) homeDetailList.clone();
		this.currentControlType=controltype;
		filterNoControlarea((ArrayList<HomeArea>) homeDetailList);
		mInflater = LayoutInflater.from(context);
		}else{
//			controlDetails = (ArrayList<DummyControl>) homeDetailList;
//			mInflater = LayoutInflater.from(context);
		}
//		else{
//			controlDetails = (ArrayList<Controls>) homeDetailList;
//			mInflater = LayoutInflater.from(context);
//		}
	}
	


	private void filterNoControlarea(ArrayList<HomeArea> homeDetailList) {
		
		for(HomeArea item: homeDetailList){
			Controls control = item.getControls();
			if (currentControlType.equals(Control_Type.Fan)) {
				if(control.getFanCount()==0){
					homeDetails.remove(item);
				}
			} else if (currentControlType.equals(Control_Type.AC)) {
				if(control.getAcCount()==0){
					homeDetails.remove(item);
				}
			} else if (currentControlType.equals(Control_Type.TV)) {
				if(control.getTvCount()==0){
					homeDetails.remove(item);
				}
			} else if (currentControlType.equals(Control_Type.Light)){
				if(control.getLightsCount()==0){
					homeDetails.remove(item);
				}
			} else if (currentControlType.equals(Control_Type.SETTOP)){
				if(control.getSTBCount()==0){
					homeDetails.remove(item);
				}
			}
		}
		
		
	
	}


	public int getCount() {
		return homeDetails.size();
	}

	public Object getItem(int position) {
		return homeDetails.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.main_item_two_line_row, null);
			holder = new ViewHolder();
			holder.txtAreaName = (TextView) convertView.findViewById(R.id.text1);
			holder.txtControlState = (TextView) convertView
					.findViewById(R.id.twext2);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(DummyContent.MENU_MODE == DummyContent.MODE_CONTROL){
			holder.txtAreaName.setText(homeDetails.get(position).getAreaName());
		holder.txtControlState.setText(homeDetails.get(position)
				.getControlState());}
		else if(DummyContent.MENU_MODE == DummyContent.MODE_AREA){
			//holder.txtAreaName.setText(controlDetails.get(position).mControl.);
		holder.txtControlState.setText(homeDetails.get(position)
				.getControlState());
		}

		return convertView;
	}

	static class ViewHolder {
		TextView txtAreaName;
		TextView txtControlState;
	}

	public void remove(int index) {
		// TODO Auto-generated method stub
		homeDetails.remove(index);
	}
}
