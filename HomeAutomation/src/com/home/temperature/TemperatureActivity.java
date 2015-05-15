/*
 * Copyright (C) 2013 Surviving with Android (http://www.survivingwithandroid.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.home.temperature;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.home.automation.AreaListActivity;
import com.home.automation.R;
import com.home.automation.SlidingMenuUtility;
import com.home.automation.SlidingMenuUtility.Control_Type;
import com.home.automation.data.AC;
import com.home.automation.data.Controls;
import com.home.automation.data.DummyContent;
import com.home.automation.data.Fan;
import com.home.automation.data.HomeArea;
import com.home.automation.data.Lights;
import com.home.automation.data.RGBLight;
import com.home.automation.data.SetTopBox;
import com.home.automation.data.TV;
import com.home.automation.db.DataBaseHelper;
import com.home.temperature.JSONWeatherParser;
import com.home.temperature.WeatherHttpClient;
import com.home.weatherAdapter.DailyForecastPageAdapter;
import com.home.weatherModel.Weather;
import com.home.weatherModel.WeatherForecast;

public class TemperatureActivity extends FragmentActivity {

	private static final String PREF_IS_DB_AVAILABLE = "pref_is_db_available";

	private TextView cityText;
	private TextView condDescr;
	private TextView temp;
	//private TextView press;
	//private TextView windSpeed;
	//private TextView windDeg;
	private TextView unitTemp;
	
	//private TextView hum;
	private ImageView imgView;
	private TextView dateText;
	
	private static String forecastDaysNum = "7";
	private ViewPager pager;
	//private AreaListActivity ac;
	
	private SharedPreferences pref;

	private HashMap<String, String> locationInfo;
	private DailyForecastPageAdapter adapter;

	private boolean mTickerStopped;

	private Handler mHandler;

	private Runnable mTicker;

	private TextView ambientTemp;

	private TextView unitambientTemp;

	//private SeekBar ambientTempSeekBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_temp);
		SlidingMenuUtility utility = SlidingMenuUtility.getInstance(this);
		pref = getApplicationContext().getSharedPreferences("TempPref", MODE_PRIVATE);
		new  AsyncDBTask().execute("");
		dateText= (TextView) findViewById(R.id.date);
		dateTimeUpdater();
		if(!isNetworkAvailable()){
			utility.setControlType(Control_Type.Light);
			AlertDialog.Builder alert= new AlertDialog.Builder(this);
			alert.setTitle(Html.fromHtml("<font color='#FF7F27'>Network Not available</font>"));
			alert.setMessage("Do you want to continue?\nClick yes to move to controls screen and No to Exit");
			
				alert.setPositiveButton("Yes", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(getApplicationContext(), AreaListActivity.class);
						Control_Type.Light.attachTo(i);
						startActivity(i);
						finish();
					}
				});
				alert.setNegativeButton("NO", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
			alert.show();
		}else {
			// slidingMenu = (SlidingDrawer)findViewById(R.id.slidingDrawer1);
			// TableRow iconRow = (TableRow) findViewById(R.id.tableRow1);
			// ac = new AreaListActivity();
			// drawMenuPermItems();
			// By default we will display light controls
			utility.setControlType(Control_Type.Light);
			utility.Init(this);
			utility.redrawMenu(DummyContent.MENU_MODE, this);

			ambientTemp = (TextView) findViewById(R.id.temp1);
			//ambientTempSeekBar = (SeekBar) findViewById(R.id.frequency_slider);
			//Hard coding this value for time being but in future it should be obtained from hardware and stored in sharedprefs.
			ambientTemp.setText("25");
			//Hard coding this value for time being but in future it should be obtained from hardware and stored in sharedprefs.
			//ambientTempSeekBar.setProgress(25);
			unitambientTemp = (TextView) findViewById(R.id.unittemp1);
			unitambientTemp.setText("°C");
			
			cityText = (TextView) findViewById(R.id.cityText);
			temp = (TextView) findViewById(R.id.temp);
			unitTemp = (TextView) findViewById(R.id.unittemp);
			unitTemp.setText("°C");
			condDescr = (TextView) findViewById(R.id.skydesc);

			pager = (ViewPager) findViewById(R.id.pager);
			imgView = (ImageView) findViewById(R.id.condIcon);
			cityText.setText(pref.getString("Location", "-- , --"));
			temp.setText("" + pref.getFloat("Temp", 0));
			condDescr.setText(pref.getString("Condition", "--"));
			temp.setText("" + pref.getFloat("Temp2", 0));
			// hum.setText("" + weather.currentCondition.getHumidity() + "%");
			// press.setText("" + weather.currentCondition.getPressure() +
			// " hPa");
			// windSpeed.setText("" + weather.wind.getSpeed() + " mps");
			// WINDDEG.SETTEXT("" + WEATHER.WIND.GETDEG() + "°");

			// adapter = new
			// DailyForecastPageAdapter(Integer.parseInt(forecastDaysNum),
			// getSupportFragmentManager(), new WeatherForecast());
			//
			// pager.setAdapter(adapter);
			/*
			 * condDescr = (TextView) findViewById(R.id.condDescr);
			 * 
			 * hum = (TextView) findViewById(R.id.hum); press = (TextView)
			 * findViewById(R.id.press); windSpeed = (TextView)
			 * findViewById(R.id.windSpeed); windDeg = (TextView)
			 * findViewById(R.id.windDeg);
			 */

			// JSONWeatherTask task = new JSONWeatherTask();
			// task.execute(new String[]{"New Delhi, India","en"});
			//
			// JSONForecastWeatherTask task1 = new JSONForecastWeatherTask();
			// task1.execute(new String[]{"New Delhi, India","en",
			// forecastDaysNum});

			// uncomment while executing on device, emulator has limitations
			boolean processGpsOn = false;
			if (!isGpsEnabled()) {
				processGpsOn = turnGPSOn();
				if (!processGpsOn) {
					Toast.makeText(this, "Unable to turn on GPS",
							Toast.LENGTH_SHORT).show();
				}
			}
			locationInfo = getMyCurrentLocation();
			String stateName = locationInfo.get("State");
			String cityName = locationInfo.get("City");
			String countryName = locationInfo.get("Country");
			String location = " New Delhi, India";

			// Though i am able to get location data, but the provider is
			// returning city name as null due to which this code is commented
			// if you have a clear solution then implement here.
			if (cityName != null) {
				location = cityName + ", ";
			}

			if (stateName != null && cityName != null) {
				location += stateName + ", ";
			} else if (stateName != null && null == cityName) {
				location = stateName + ", ";
			}

			if (countryName != null && stateName != null && cityName != null) {
				location += countryName;
			} else if (countryName != null && stateName != null
					&& null == cityName) {
				location += countryName;
			} else if (countryName != null && null == stateName
					&& null == cityName) {
				location = countryName;
			}
		
			Log.i("Temp", ">>" + location + "<<");

			JSONWeatherTask task = new JSONWeatherTask();
			task.execute(new String[] { location, "en" });

			JSONForecastWeatherTask task1 = new JSONForecastWeatherTask();
			task1.execute(new String[]{location.replaceAll(" ", ""),"en", forecastDaysNum});
		
		}
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

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager 
		= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	private boolean isGpsEnabled() {
		LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		return  locManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		turnGPSOff();
		mTickerStopped = true;
	}

	
	private class AsyncDBTask extends AsyncTask<String, Void, Void> {
		//ProgressDialog progressDialog = null;

		@Override
		protected void onPreExecute() {
			// Called before doInBackground.
			// Initialize your progressDialog here.
//			progressDialog = ProgressDialog.show(TemperatureActivity.this,
//					"Loading ", "Please Wait ...copying DB ", false, false);
		}
		
		@Override
		protected Void doInBackground(String... params) {
			DataBaseHelper myDbHelper =  DataBaseHelper.getInstance(getApplicationContext());
	        try {
	        	if(!getDBAvailable()){
	        		myDbHelper.createDataBase();
	        	}
	        	List<HomeArea> homerArealistWithoutControls = myDbHelper.extractHomeArea();
	        	Controls controlsList = myDbHelper.extractControls();
	        	List<HomeArea> homerArealistWithControls = extractControlsAreaWise( homerArealistWithoutControls,	controlsList);
	        	DummyContent content = DummyContent.getInstance();
	        	content.clearAreaItem();
	        	for (HomeArea homeArea : homerArealistWithControls) {
					content.addAreaItem(homeArea);
				}
	        	myDbHelper.extractControlStrings();
	 
	 	} catch (IOException ioe) {
	 
	 		throw new Error("Unable to create database");
	 
	 	} catch (java.sql.SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 
	 	try {
	 
	 		myDbHelper.openDataBase();
	 
	 	}catch(SQLException sqle){
	 
	 		throw sqle;
	 
	 	} catch (java.sql.SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 
			return null;
		}

		private List<HomeArea> extractControlsAreaWise(
				List<HomeArea> homerArealist, Controls controlsList) {
			ArrayList<TV> tvconrtrols = controlsList.getmTv();
			ArrayList<AC> acconrtrols = controlsList.getmAC();
			ArrayList<Fan> fanconrtrols = controlsList.getmFan();
			ArrayList<Lights> lightconrtrols = controlsList.getmLights();
			ArrayList<RGBLight> rgbconrtrols = controlsList.getmRGB();
			ArrayList<SetTopBox> stbconrtrols = controlsList.getmSTB();
			for (HomeArea homeArea : homerArealist) {
				Controls homeAreaControl = new Controls();
				int tvindex=0;
				for (TV tvcontrol : tvconrtrols) {
					if(tvcontrol.getAreaName().equalsIgnoreCase(homeArea.getAreaName())){
						homeAreaControl.setmTv(tvcontrol, tvindex);			
						tvindex++;
					}
				}
				int acindex=0;
				for (AC acControl : acconrtrols) {
					if(acControl.getAreaName().equalsIgnoreCase(homeArea.getAreaName())){
						homeAreaControl.setmAC(acControl, acindex);		
						acindex++;
						}
				}
				int fanindex=0;
				for (Fan fanControl : fanconrtrols) {
					if(fanControl.getAreaName().equalsIgnoreCase(homeArea.getAreaName())){
						homeAreaControl.setmFan(fanControl, fanindex);		
						fanindex++;
						}
				}
				
				int lightIndex=0;
				for (Lights lightControl : lightconrtrols) {
					if(lightControl.getAreaName().equalsIgnoreCase(homeArea.getAreaName())){
						homeAreaControl.setmLights(lightControl, lightIndex);		
						lightIndex++;
						}
				}
				int stbindex=0;
				for (SetTopBox stbControl : stbconrtrols) {
					if(stbControl.getAreaName().equalsIgnoreCase(homeArea.getAreaName())){
						homeAreaControl.setmSTB(stbControl, stbindex);		
						stbindex++;
						}
				}
				
				int rgbindex=0;
				for (RGBLight rgbControl : rgbconrtrols) {
					if(rgbControl.getAreaName().equalsIgnoreCase(homeArea.getAreaName())){
						homeAreaControl.setmRGB(rgbControl, rgbindex);		
						rgbindex++;
						}
				}
				homeArea.setControls(homeAreaControl);
			}
			return homerArealist;
		}
		
		@Override
		protected void onPostExecute(Void v) {
			//Dismiss the progessDialog here.
			setDBAvailable(true);
			//progressDialog.dismiss();
		}
		
	}

	private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {
		
		@Override
		protected Weather doInBackground(String... params) {
			Weather weather = new Weather();
			

			
			String data = ( (new WeatherHttpClient()).getWeatherData(params[0], params[1]));

			try {
				weather = JSONWeatherParser.getWeather(data);
				System.out.println("Weather ["+weather+"]");
				// Let's retrieve the icon
				weather.iconData = ( (new WeatherHttpClient()).getImage(weather.currentCondition.getIcon()));
				
			} catch (JSONException e) {				
				e.printStackTrace();
			}
			return weather;
		
	}

				
		
	@Override
	protected void onPostExecute(Weather weather) {			
			super.onPostExecute(weather);
			if (weather.iconData != null && weather.iconData.length > 0) {
				Bitmap img = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length); 
				imgView.setImageBitmap(img);
			}
			if(weather.location!= null && weather.temperature!=null){
			if(pref!=null){
				Editor editor = pref.edit();
				editor.putString("Location", weather.location.getCity() + "," + weather.location.getCountry());
				editor.putFloat("Temp", Math.round((weather.temperature.getTemp() - 275.15)));
				editor.putString("Condition", weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
				editor.putFloat("Temp2", Math.round((weather.temperature.getTemp() - 275.15)));
				editor.commit();
			}
			cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
			temp.setText("" + Math.round((weather.temperature.getTemp() - 275.15)));
			condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
			temp.setText("" + Math.round((weather.temperature.getTemp() - 275.15)) );
		//	hum.setText("" + weather.currentCondition.getHumidity() + "%");
//			press.setText("" + weather.currentCondition.getPressure() + " hPa");
//			windSpeed.setText("" + weather.wind.getSpeed() + " mps");
//			WINDDEG.SETTEXT("" + WEATHER.WIND.GETDEG() + "°");
			}else{
				Editor editor = pref.edit();
				editor.putString("Location", "-- , --");
				editor.putFloat("Temp", 0);
				editor.putString("Condition", "--");
				editor.putFloat("Temp2", 0);
				editor.commit();
				Toast.makeText(getApplicationContext(), "Failed to fetch weather data", Toast.LENGTH_SHORT).show();
			}
			
		}



  }
	
	
	private class JSONForecastWeatherTask extends AsyncTask<String, Void, WeatherForecast> {
		
		@Override
		protected WeatherForecast doInBackground(String... params) {
			
			String data = ( (new WeatherHttpClient()).getForecastWeatherData(params[0], params[1], params[2]));
			WeatherForecast forecast = new WeatherForecast();
			try {
				forecast = JSONWeatherParser.getForecastWeather(data);
				System.out.println("Weather ["+forecast+"]");
				// Let's retrieve the icon
			//	weather.iconData = ( (new WeatherHttpClient()).getImage(weather.currentCondition.getIcon()));
				
			} catch (JSONException e) {				
				e.printStackTrace();
			}
			return forecast;
		
	}
		
		
		
		
	@Override
		protected void onPostExecute(WeatherForecast forecastWeather) {			
			super.onPostExecute(forecastWeather);
			if(forecastWeather != null){
			adapter = new DailyForecastPageAdapter(Integer.parseInt(forecastDaysNum), getSupportFragmentManager(), forecastWeather);
			
			pager.setAdapter(adapter);
			}else{
				Toast.makeText(getApplicationContext(), "Failed to fetch forcast data", Toast.LENGTH_SHORT).show();
			}
		}



  }


	private void setDBAvailable(boolean available){
		PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(PREF_IS_DB_AVAILABLE, available).commit();
	}
	private boolean getDBAvailable(){
		return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PREF_IS_DB_AVAILABLE, false);
	}
	
	/** Method to turn on GPS **/
	public boolean turnGPSOn() {
		try {
			String provider = Settings.Secure.getString(
					getContentResolver(),
					Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
			if (!provider.contains("gps")) { // if gps is disabled
				final Intent poke = new Intent();
				poke.setClassName("com.android.settings",
						"com.android.settings.widget.SettingsAppWidgetProvider");
				poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
				poke.setData(Uri.parse("3"));
				sendBroadcast(poke);
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	// Method to turn off the GPS
	public void turnGPSOff() {
		String provider = Settings.Secure.getString(
				getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

		if (provider.contains("gps")) { // if gps is enabled
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings",
					"com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			sendBroadcast(poke);
		}
	}

	// turning off the GPS if its in on state. to avoid the battery drain.
	protected void cleanUp() {
		// TODO Auto-generated method stub
		turnGPSOff();
	}

	/**
	 * Check the type of GPS Provider available at that instance and collect the
	 * location informations
	 * 
	 * @Output Latitude and Longitude
	 */
	public HashMap<String, String> getMyCurrentLocation() {

		LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener locListener = new MyLocationListener();
		HashMap<String, String> locationInfo = new HashMap<String, String>();


		// don't start listeners if no provider is enabled
		// if(!gps_enabled && !network_enabled)
		// return false;

		if (isGpsEnabled()) {
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
					0, locListener);
			location = locManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}

//		if (network_enabled && location == null) {
//			locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
//					0, 0, locListener);
//
//		}

		if (isNetworkAvailable() && location == null) {
			location = locManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		}

		if (location != null) {

			MyLat = location.getLatitude();
			MyLong = location.getLongitude();

		} else {
			Location loc = getLastKnownLocation(this);
			if (loc != null) {

				MyLat = loc.getLatitude();
				MyLong = loc.getLongitude();

			}
		}
		locManager.removeUpdates(locListener); // removes the periodic updates
		// from location listener to
		// //avoid battery drainage. If
		// you want to get location at
		// the periodic intervals call
		// this method using //pending
		// intent.

		try {
			// Getting address from found locations.
			Geocoder geocoder;

			List<Address> addresses;
			geocoder = new Geocoder(this, Locale.getDefault());
			addresses = geocoder.getFromLocation(MyLat, MyLong, 1);
			StateName = addresses.get(0).getAdminArea();
			CityName = addresses.get(0).getLocality();
			CountryName = addresses.get(0).getCountryName();
			// you can get more details other than this . like country code,
			// state code, etc.
			System.out.println(" StateName " + StateName);
			System.out.println(" CityName " + CityName);
			System.out.println(" CountryName " + CountryName);

			locationInfo.put("State", StateName);
			locationInfo.put("City", CityName);
			locationInfo.put("Country", CountryName);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return locationInfo;

	}

	// Location listener class. to get location.
	public class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location location) {
			if (location != null) {
			}
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}
	}

	//private boolean gps_enabled = false;
	//private boolean network_enabled = false;
	Location location;

	Double MyLat, MyLong;
	String CityName = "";
	String StateName = "";
	String CountryName = "";

	// below method to get the last remembered location. because we don't get
	// locations all the times .At some instances we are unable to get the
	// location from GPS. so at that moment it will show us the last stored
	// location.

	private static Location getLastKnownLocation(Context context) {
		Location location = null;
		LocationManager locationmanager = (LocationManager) context
				.getSystemService("location");
		List<String> list = locationmanager.getAllProviders();
		boolean i = false;
		Iterator<String> iterator = list.iterator();
		do {
			if (!iterator.hasNext())
				break;
			String s = (String) iterator.next();
			if (i != false && !locationmanager.isProviderEnabled(s))
				continue;
			Location location1 = locationmanager.getLastKnownLocation(s);
			if (location1 == null)
				continue;
			if (location != null) {
				float f = location.getAccuracy();
				float f1 = location1.getAccuracy();
				if (f >= f1) {
					long l = location1.getTime();
					long l1 = location.getTime();
					if (l - l1 <= 600000L)
						continue;
				}
			}
			location = location1;
			i = locationmanager.isProviderEnabled(s);
		} while (true);
		return location;
	}
}