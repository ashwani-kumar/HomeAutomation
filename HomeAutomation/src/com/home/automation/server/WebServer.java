package com.home.automation.server;

import java.io.IOException;

import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;

public class WebServer extends ServerResource{

	public static boolean RUNNING = false;
	public static int serverPort = 1234;
	public static String serverIp = "127.0.0.1";

	//private static final String ALL_PATTERN = "*";
	//private static final String EXCEL_PATTERN = "/*.xls";
	private static final String HOME_PATTERN = "/home.html";

	private Component serverComponent;

	public void runServer() {
		serverComponent = new Component();
		serverComponent.getServers().add(Protocol.HTTP, serverIp, serverPort); 
		final Router router = new Router(serverComponent.getContext().createChildContext());  
		router.attach(HOME_PATTERN, WebServer.class);  
		serverComponent.getDefaultHost().attach(router);  
		 
			while (RUNNING) {
				try {
					serverComponent.start();
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		RUNNING = false;
	}
	
	@Get
	public String present(){
		return "Home_automation";
	}

	public synchronized void startServer() {
		RUNNING = true;
		runServer();
	}

	public synchronized void stopServer() {
		RUNNING = false;
		if (serverComponent != null) {
			try {
				serverComponent.stop();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
