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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.util.Log;

import com.home.weatherAdapter.HttpUrlConnectionClient;

/**
 * @author Francesco
 *
 */
public class WeatherHttpClient {

	private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
	private static String IMG_URL = "http://openweathermap.org/img/w/";

	
	private static String BASE_FORECAST_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?mode=json&q=";
	private HttpUrlConnectionClient httpUrlConnectionClient;

	
	public String getWeatherData(String location, String lang) {
//		HttpURLConnection con = null ;
//		InputStream is = null;
		
		try {
			String url = BASE_URL + location;
			if (lang != null)
				url = url + "&lang=" + lang;
			
			
			
			
//			
//			con = (HttpURLConnection) ( new URL(url)).openConnection();
//			con.setRequestMethod("GET");
//			con.setDoInput(true);
//			con.setDoOutput(true);
//			con.connect();
//			
//			// Let's read the response
//			StringBuffer buffer = new StringBuffer();
//			is = con.getInputStream();
//			BufferedReader br = new BufferedReader(new InputStreamReader(is));
//			String line = null;
//			while (  (line = br.readLine()) != null )
//				buffer.append(line + "\r\n");
//			
//			is.close();
//			con.disconnect();
//			
//			return buffer.toString();
			String response = postHttpRequest(url ,null);
			return response;
	    }
		catch(Throwable t) {
			t.printStackTrace();
			//response using browser manually
//			return "{\"coord\":{\"lon\":77.06,\"lat\":28.47},\"sys\":{\"message\":0.3108,\"country\":\"India\",\"sunrise\":1403826992,\"sunset\":1403877193},\"weather\":[{\"id\":761,\"main\":\"Dust\",\"description\":\"dust\",\"icon\":\"50d\"}],\"base\":\"cmc stations\",\"main\":{\"temp\":303.44,\"pressure\":1005,\"humidity\":66,\"temp_min\":303.15,\"temp_max\":303.71},\"wind\":{\"speed\":2.1,\"deg\":260},\"rain\":{\"3h\":0},\"clouds\":{\"all\":40},\"dt\":1403833517,\"id\":1270642,\"name\":\"Gurgaon\",\"cod\":200}";
		}
//		finally {
//			try { is.close(); } catch(Throwable t) {}
//			try { con.disconnect(); } catch(Throwable t) {}
//		}

		return null;
				
	}
	
	
	private String postHttpRequest(String serverURL,Map<String, String> params) throws Exception{
		if(httpUrlConnectionClient==null){

			httpUrlConnectionClient=HttpUrlConnectionClient.getInstance();
		}
		String response=httpUrlConnectionClient.post(serverURL, params);
		return response;
	}
	/**
	 * Issue a POST request to the server.
	 *
	 *
	 * @param endpoint POST address.
	 * @param params request parameters.
	 *
	 * @throws Exception propagated from POST.
	 */
	public  String post(String endpoint, Map<String, String> params)
			throws Exception {

		URL url;
		try {
			url = new URL(endpoint.replace(" ", "%20"));
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + endpoint);
		}

		StringBuilder bodyBuilder = new StringBuilder();
		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
		// constructs the POST body using the parameters
		while (iterator.hasNext()) {
			Entry<String, String> param = iterator.next();
			bodyBuilder.append(param.getKey()).append('=')
			.append(param.getValue());
			if (iterator.hasNext()) {
				bodyBuilder.append('&');
			}
		}

		//        String body = bodyBuilder.toString();
		String  body="";
		System.out.println("Posting '" + body + "' to " + url);

		HttpURLConnection conn = null;
		String response;
		try {
			//open connection with URL
			conn = (HttpURLConnection) url.openConnection();
			response=getPostResponse(conn,body);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return response;
	}
	
	/**
	 * Post body to established HttpURLConnection and get Response from there	
	 * @param conn
	 * @param body
	 * @return
	 * @throws Exception
	 */
	private String getPostResponse( HttpURLConnection conn,String body) throws Exception{
		//    	 String response;
		//    	 conn.setDoOutput(true);
		//         conn.setUseCaches(false);
		//         conn.setRequestMethod("POST");
		//         	
		//         conn.setRequestProperty("Content-Type",
		//                 "application/x-www-form-urlencoded;charset=UTF-8");
		//         conn.setRequestProperty("Accept-Encoding", "");
		//         conn.setRequestProperty("Connection", "close");
		//         int status = conn.getResponseCode();
		//         System.out.println("Response status :"+status);
		//         if (status != 200) {
		//           throw new IOException("Post failed with error code " + status);
		//         }else{            	        	
		//         	response = readDataFromStream(conn.getInputStream());
		//         	System.out.println("response is "+response);
		//         	 
		//         }
		//         return response;
		try{
			StringBuffer buffer = new  StringBuffer();
			conn.connect();
			InputStreamReader in = new InputStreamReader((InputStream) conn.getContent());
			BufferedReader buff = new BufferedReader(in);

			String line;
			do {
				line = buff.readLine();
				if(line!=null){
					System.out.println(line);
					buffer.append(line);
				}
			} while (line != null);

			return buffer.toString();     
		} catch(Exception e){
			Log.i("response","exception heandled"+e.getMessage());
			e.printStackTrace();
			return "";
			
			}	
		}



	public String getForecastWeatherData(String location, String lang, String sForecastDayNum) {
		HttpURLConnection con = null ;
		//InputStream is = null;
		int forecastDayNum = Integer.parseInt(sForecastDayNum);
				
		try {
				
			// Forecast
			String url = BASE_FORECAST_URL + location;
			if (lang != null)
				url = url + "&lang=" + lang;
			
			url = url + "&cnt=" + forecastDayNum;
			con = (HttpURLConnection) ( new URL(url)).openConnection();
//			con.setRequestMethod("GET");
//			con.setDoInput(true);
//			con.setDoOutput(true);
//			con.connect();
//			
//			// Let's read the response
//			StringBuffer buffer1 = new StringBuffer();
//			is = con.getInputStream();
//			BufferedReader br1 = new BufferedReader(new InputStreamReader(is));
//			String line1 = null;
//			while (  (line1 = br1.readLine()) != null )
//				buffer1.append(line1 + "\r\n");
//			
//			is.close();
//			con.disconnect();
//			
//			System.out.println("Buffer ["+buffer1.toString()+"]");
			String bufferstring=	getPostResponse(con,url);
			return bufferstring;
			// buffer1.toString();
	    }
		catch(Throwable t) {
			t.printStackTrace();
		}
		finally {
			//try { is.close(); } catch(Throwable t) {}
			try { con.disconnect(); } catch(Throwable t) {}
		}

		return null;
				
	}
	
	public byte[] getImage(String code) {
		HttpURLConnection con = null ;
		InputStream is = null;
		try {
			con = (HttpURLConnection) ( new URL(IMG_URL + code)).openConnection();
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.connect();
			
			// Let's read the response
			is = con.getInputStream();
			byte[] buffer = new byte[1024];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			while ( is.read(buffer) != -1)
				baos.write(buffer);
			
			return baos.toByteArray();
	    }
		catch(Throwable t) {
			t.printStackTrace();
		}
		finally {
			try { is.close(); } catch(Throwable t) {}
			try { con.disconnect(); } catch(Throwable t) {}
		}
		
		return null;
		
	}
}
