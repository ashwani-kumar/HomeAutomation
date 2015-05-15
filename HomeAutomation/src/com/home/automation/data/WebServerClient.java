package com.home.automation.data;

import java.io.IOException;

import org.restlet.Client;
import org.restlet.data.Protocol;
import org.restlet.engine.Engine;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.home.automation.R;

/**
 * Android activity that retrieves the list of email from the GAE backend.
 * 
 */
public class WebServerClient extends Activity {
	TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_mail_client_main);
        textView = (TextView)findViewById(R.id.list_item);
        //ListView emailList = getListView();
        //emailList.setTextFilterEnabled(true);

        // Set-up the Restlet Engine
        Engine.getInstance().getRegisteredClients().clear();
        Engine.getInstance().getRegisteredClients()
                .add(new org.restlet.engine.connector.HttpClientHelper(new Client(Protocol.HTTP)));
//        Engine.getInstance().getRegisteredConverters()
//                .add(new JacksonConverter());

        // Retrieve the list of mails from the remote web API
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
	                    	textView.setText("Server response: "+response);
	                    }
	                });
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
//                ClientResource clientResource = new ClientResource(
//                        "http://reia-ch09.appspot.com/accounts/chunkylover53/mails/");
//                MailsResource mailsResource = clientResource
//                        .wrap(MailsResource.class);
//                MailsRepresentation emails = mailsResource.retrieve();
//                final String[] subjects = new String[emails.getEmails().size()];
//
//                for (int i = 0; i < emails.getEmails().size(); i++) {
//                    System.out.println(emails.getEmails().get(i));
//                    subjects[i] = emails.getEmails().get(i).getSubject();
//                }

                runOnUiThread(new Runnable() {
                    public void run() {
//                        setListAdapter(new ArrayAdapter<String>(
//                                MobileMailClientMainActivity.this,
//                                R.layout.activity_mobile_mail_client_main,
//                                R.id.list_item, subjects));
                    	
                    }
                });

                return null;
            }
        };

        task.execute(null, null, null);
    }

}
