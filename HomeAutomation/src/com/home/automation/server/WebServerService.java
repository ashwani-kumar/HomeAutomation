package com.home.automation.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class WebServerService extends Service {

	private WebServer server = null;

	@Override
	public void onCreate() {
		Log.i("HTTPSERVICE", "Creating and starting httpService");
		super.onCreate();
		new WebServerTask();
	}

	@Override
	public void onDestroy() {
		Log.i("HTTPSERVICE", "Destroying httpService");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	class WebServerTask extends Thread {
		
		public WebServerTask(){
			start();
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//if(!WebServer.RUNNING){
				server = new WebServer();
				server.startServer();
			//}else{
			//	server.stopServer();
			//}
		}

	}
}
