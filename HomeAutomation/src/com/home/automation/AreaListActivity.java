 package com.home.automation;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import org.restlet.Client;
import org.restlet.data.Protocol;
import org.restlet.engine.Engine;
import org.restlet.engine.connector.HttpClientHelper;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.home.automation.SlidingMenuUtility.Control_Type;
import com.home.automation.data.DummyContent;
import com.home.automation.server.WebServerService;
import com.home.temperature.TemperatureActivity;

/**
 * An activity representing a list of Controls. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link AreaDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link AreaListFragment} and the item details (if present) is a
 * {@link AreaDetailFragment}.
 * <p>
 * This activity also implements the required {@link AreaListFragment.Callbacks}
 * interface to listen for item selections.
 */
@SuppressWarnings("deprecation")
public class AreaListActivity extends FragmentActivity implements
		AreaListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	
    private SlidingMenuUtility utility;

	private boolean mTickerStopped;

	private Handler mHandler;

	private Runnable mTicker;
	private TextView dateText;

	private ImageView handle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_area_list);
		utility = SlidingMenuUtility.getInstance(this);
		handle = (ImageView)findViewById(R.id.handle);
		
		dateText= (TextView) findViewById(R.id.date);
		
		dateTimeUpdater();
		dateText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), TemperatureActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getApplicationContext().startActivity(i);
			    finish();
			}
		});
		
//		if(getIntent()!=null){
//			Bundle bundle = getIntent().getExtras();
//			int typeOrdinal = (Integer) bundle.get("name");
//			Control_Type type = Control_Type.values()[typeOrdinal];
//			//By default we will display light controls
//			if(type!=null){
//				utility.setControlType(type);
//			}
//		}else{
//			utility.setControlType(Control_Type.Light);
//		}
		handle.performClick();
		utility.Init(this);
		utility.redrawMenu(DummyContent.MENU_MODE, this);
		utility.setSlidingDrawer();
		
		Intent webServerService = new Intent(this, WebServerService.class);
		this.startService(webServerService);
		
		if (findViewById(R.id.area_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((AreaListFragment) getSupportFragmentManager().findFragmentById(
					R.id.area_list)).setActivateOnItemClick(true);
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}
	
	/**
	 * 
	 */
	
	protected void showServerResponse() {
		// TODO Auto-generated method stub
		Engine.getInstance().getRegisteredClients().clear();
        Engine.getInstance().getRegisteredClients()
                .add(new HttpClientHelper(new Client(Protocol.HTTP)));

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
        	String response = "";
            @Override
            protected Void doInBackground(Void... params) {
            	ClientResource clientResource = new ClientResource(
                      "http://127.0.0.1:1234/home.html");
            	Representation str = clientResource.get();
				try {
					response = str.getText();
					Log.d("MobileMailClientMainActivity", "server response is "+response);
					runOnUiThread(new Runnable() {
	                    public void run() {
	                    	Toast.makeText(AreaListActivity.this,  "Server response: "+response, Toast.LENGTH_SHORT).show();
	                    }
	                });
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                return null;
            }
        };

        task.execute(null, null, null);
    }

	@Override
	public void onResume(){
		super.onResume();
	}
	
	private void dateTimeUpdater() {
		// TODO Auto-generated method stub
		mTickerStopped = false;
        mHandler = new Handler();

        /**
         * requests a tick on the next hard-second boundary
         */
        mTicker = new Runnable() {
				public void run() {
                    if (mTickerStopped) return;
                    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            		// textView is the TextView view that should display it
            		dateText.setText(currentDateTimeString);
                    long now = SystemClock.uptimeMillis();
                    long next = now + (1000 - now % 1000);
                    mHandler.postAtTime(mTicker, next);
                }
            };
        mTicker.run();
	}

	/**
	 * Callback method from {@link AreaListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(AreaDetailFragment.ARG_ITEM_ID, id);
			arguments.putString(AreaDetailFragment.CONTROL_TYPE, utility.getControlType().toString());
			AreaDetailFragment fragment = new AreaDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.area_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, AreaDetailActivity.class);
			detailIntent.putExtra(AreaDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
}
