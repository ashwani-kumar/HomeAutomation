package com.home.weatherAdapter;



import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import android.util.Log;

/**
 * Use to make  connection with underlying network to Http web server.
 * Responsible to send request to http server and get response from there.
 * @author kaushidx
 *
 */
public class HttpUrlConnectionClient {

	/**
	 * Logger
	 */

	private static  HttpUrlConnectionClient httpUrlConnectClient=null;


	/**
	 * Singleton object accessor
	 * @return Singleton instance
	 */
	public static  HttpUrlConnectionClient getInstance(){

		if(httpUrlConnectClient==null){

			synchronized(HttpUrlConnectionClient.class){

				httpUrlConnectClient=new HttpUrlConnectionClient();
			}

		}

		return httpUrlConnectClient; 
	}

	private String response;

	private HttpUrlConnectionClient(){
		//exist only to stop  instantiation 
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

//		StringBuilder bodyBuilder = new StringBuilder();
//		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
//		// constructs the POST body using the parameters
//		while (iterator.hasNext()) {
//			Entry<String, String> param = iterator.next();
//			bodyBuilder.append(param.getKey()).append('=')
//			.append(param.getValue());
//			if (iterator.hasNext()) {
//				bodyBuilder.append('&');
//			}
//		}

		//        String body = bodyBuilder.toString();
		String  body="";
		System.out.println("Posting '" + body + "' to " + url);

		HttpURLConnection conn = null;
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



	/**
	 * Read Input Stream Data and Convert it to String
	 * @param in
	 * @return
	 */

//	private static String readDataFromStream(InputStream in) {
//		StringBuffer sb= new StringBuffer();
//		if(in!=null){    		
//			InputStreamReader isr=new InputStreamReader(in);
//			BufferedReader br=new BufferedReader(isr, 2048);
//			String line="";
//			try {
//				while((line=br.readLine())!=null){
//					sb.append(line+System.getProperty("line.separator"));
//				}
//				isr.close();
//			} catch (IOException e) {	
//				System.out.println("Exception read data from stream "+e.getMessage());				
//			}   
//
//		}
//
//		return sb.toString();
//	}


	//     if(httpUrlConnectionClient!=null){
	//			
	//			httpUrlConnectionClient.cleanup();
	//		}
	/**
	 * Clean instance 
	 */
	public boolean cleanup(){

		httpUrlConnectClient=null;

		return true;
	}

	/*****Unit Test methods ********/

	public  static void ut_setInstance(HttpUrlConnectionClient httpUrlConnection){

		httpUrlConnectClient=httpUrlConnection;
	}



}
