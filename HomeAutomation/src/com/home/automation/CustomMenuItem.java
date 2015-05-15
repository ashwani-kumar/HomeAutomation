package com.home.automation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomMenuItem extends LinearLayout {
	private ToggleImageButton menuImage;
	private TextView menuText;
	private Context context;
	int resId;
	int resId_Pressed;
	int light = 0;
	private String name;
	
	public CustomMenuItem(Context context, int resId, int resId_Pressed,int light){
		this(context, null);
		this.context = context;
		this.resId = resId;
		this.resId_Pressed = resId_Pressed;
		this.light = light;
	}
	
	public CustomMenuItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomMenuItem(Context context2, int resId, int resId_Pressed,String areaName) {
		this(context2, null);
		this.context = context2;
		this.resId = resId;
		this.resId_Pressed = resId_Pressed;
		this.name = areaName;
	}

	public LinearLayout getCustomView(){
		LayoutInflater menuItemInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout menuLayout = (LinearLayout) menuItemInflater.inflate(R.layout.menuitem, null); 
		menuImage = (ToggleImageButton) menuLayout.findViewById(R.id.menuimage);
		menuImage.setStateChecked(getResources().getDrawable(resId_Pressed));
		menuImage.setStateUnChecked(getResources().getDrawable(resId));
		menuImage.setChecked(false);
		menuText = (TextView) menuLayout.findViewById(R.id.menutext);
		if(light == 0 && name != null)
			menuText.setText(name);
		else
			menuText.setText(light);
		return menuLayout;
	}
}
