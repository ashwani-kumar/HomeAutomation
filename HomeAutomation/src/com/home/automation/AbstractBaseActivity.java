package com.home.automation;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

public class AbstractBaseActivity extends FragmentActivity {
	
	@Override
	protected void onCreate(Bundle onSavedInstanceState){
		super.onCreate(onSavedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.slidingdrawer);
	}

}
