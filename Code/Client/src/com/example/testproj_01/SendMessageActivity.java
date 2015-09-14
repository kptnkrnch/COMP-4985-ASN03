package com.example.testproj_01;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SendMessageActivity extends Activity {
	/*
	Socket s;
	DataOutputStream out;
	String ip_addr;
	int port;
	ClientConnection connection;
	
	class ClientConnection extends Thread {
		Socket s;
		DataOutputStream out;
		String ip_addr;
		int port;
		private String message;
		public double latitude;
		public double longitude;
		public long timestamp;
		public String devicename;
		public String deviceip;
		public ClientConnection(String s_ip, int p) {
			ip_addr = s_ip;
			port = p;
			message = null;
			s = null;
			out = null;
		}
		
		public void connectToServer() {
	    	try {
				if (ip_addr != null && port != -1) {
					try {
						s = new Socket(ip_addr, port);
					} catch (NetworkOnMainThreadException e) {
						
					}
					if (s != null) {
						out = new DataOutputStream(s.getOutputStream());
					}
				}
			} catch (UnknownHostException e) {
			} catch (IOException e) {
			}
	    }
	    
	    public boolean sendData(String data) {
			if (out != null) {
				try {
					out.writeUTF(data);
				} catch (IOException e) {
					return false;
				}
			}
			return true;
		}
	    
	    public boolean isConnected() {
			return !s.isClosed();
	    }
	    
	    public void run() {
	    	if (s == null) {
	    		connectToServer();
	    	}
	    	
	    	message = timestamp + " " + latitude + " " + longitude + " " + devicename + " " + deviceip;
	    	message += " PROT_EXIT";
			if (s != null && out != null && message != null) {
				sendData(message);
				message = null;
				try {
					s.close();
				} catch (IOException e) {
				}
			}
	    }
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendclient);
        
        Intent intent = getIntent();
        
        String c_data = intent.getStringExtra(MainActivity.CONNECTIONINFO);
        Scanner scan = new Scanner(c_data);
        final String s_ip = scan.next();
        String s_port = scan.next();
        scan = new Scanner(s_port);
        final int p = scan.nextInt();

        //} else {
        	//p = -1;
        	//ip_addr = null;
        //}
        
        final TextView tview = (TextView) findViewById(R.id.testView);
        //
        
        final EditText message = (EditText) findViewById(R.id.edit_message);
        
        Button send = (Button) findViewById(R.id.senddata);
        final LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        final String devicename = (android.os.Build.MANUFACTURER + "-" + android.os.Build.MODEL).replace(' ', '-');
        
        final LocationListenerRecord ll = new LocationListenerRecord(lm, devicename, s_ip, p);
        InetAddress inet = null;
        InetThread it = new InetThread();
        it.start();
        while(!it.isFound());
        inet = it.getAddress();
        
        final String deviceip = inet.getHostAddress();
        ll.deviceip = deviceip;
        //lm.req
        send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ll);
				//String data = "Latitude: " + ll.latitude + " Longitude: " + ll.longitude;//message.getText().toString();
			}
		});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public class LocationListenerRecord implements LocationListener
    {
    	public double latitude;
    	public double longitude;
    	public long timestamp;
    	public String deviceip;
    	public String devicename;
    	public String s_ip;
    	public int port;
    	LocationManager lm = null;
    	
    	public LocationListenerRecord(LocationManager manager, String dn, String ip, int p) {
    		lm = manager;
    		latitude = -1;
    		longitude = -1;
    		timestamp = -1;
    		devicename = dn;
    		s_ip = ip;
    		port = p;
    	}
    	
	    @Override
	    public void onLocationChanged(Location location) {
	    	latitude = location.getLatitude();
	    	longitude = location.getLongitude();
	    	timestamp = location.getTime();
	    		
    		ClientConnection connection = new ClientConnection(s_ip, port);
			connection.longitude = longitude;
			connection.latitude = latitude;
			connection.timestamp = timestamp;
			connection.devicename = devicename;
			connection.deviceip = deviceip;
			connection.start();
    		
    		lm.removeUpdates(this);
	    }

	    @Override
	    public void onProviderDisabled(String provider) {
	    }
	
	    @Override
	    public void onProviderEnabled(String provider) {
	    }
	
	    @Override
	    public void onStatusChanged(String provider, int status, Bundle extras) {
	    }
    }
    
    public class InetThread extends Thread {
    	boolean retrieved;
    	InetAddress address;
    	
    	public InetThread() {
    		retrieved = false;
    		address = null;
    	}
    	
    	public void run() {
    		try {
				address = InetAddress.getLocalHost();
				retrieved = true;
			} catch (UnknownHostException e) {
			}
    	}
    	
    	public boolean isFound() {
    		return retrieved;
    	}
    	
    	public InetAddress getAddress() {
    		return address;
    	}
    }*/
    
}
