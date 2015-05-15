package com.home.automation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AreaLogo extends RelativeLayout {
	
	private LayoutInflater areaLogoInflater;
	private RelativeLayout areaLogoLayout;
	
	public AreaLogo(Context context, AttributeSet attr) {
		super(context, attr);
	}
	
	public AreaLogo(Context context) {
		this(context, null);
		areaLogoInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		areaLogoLayout = (RelativeLayout) areaLogoInflater.inflate(R.layout.area_logo, null); 
	}

	public void setAreaLogo(int i){
		ImageView areaLogo = (ImageView) areaLogoLayout.findViewById(R.id.areaLogo);
		areaLogo.setBackgroundResource(i);
	}
	
	public void setAreaName(String areaName){
		TextView areaLogo = (TextView) areaLogoLayout.findViewById(R.id.areaName);
		areaLogo.setText(areaName);
	}

}
