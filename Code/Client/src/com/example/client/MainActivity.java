package com.example.client;

/*---------------------------------------------------------------------------------------
--	SOURCE FILE:		MainActivity.java
--
--	PROGRAM:			Client
--
--	FUNCTIONS:			
--
--	DATE:				March 4, 2014
--
--	REVISIONS:			
--
--	DESIGNERS:			Josh Campbell
--
--	PROGRAMMERS:		Josh Campbell and Ian Davidson
--
--	NOTES:
--	This program will take in the ip address of the server android device. It will then
--  use the location services to find its latitude and longitude. After finding this
--  info, it will send it to the server via a TCP connection.
---------------------------------------------------------------------------------------*/

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import com.example.testproj_01.R;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	public final static String CONNECTIONINFO = "CONNECTIONINFO";
	Socket s;
	DataOutputStream out;
	String ip_addr;
	int port;
	ClientConnection connection;
	
	/*--------------------------------------------------------------------------------------
	-- FUNCTION:	onCreate
	--
	-- DATE:		March 3, 2014
	--
	-- REVISIONS:	--
	--
	-- DESIGNER:	Josh Campbell
	--
	-- PROGRAMMER:	Josh Campbell
	--
	-- INTERFACE:	protected void onCreate(Bundle savedInstanceState)
	--
	-- PARAMETERS:	savedInstanceState - the previous saved instance state
	--
	-- RETURNS:		--
	--
	-- NOTES:
	-- This function sets up and displays the main window of the application.
	-- Handles the onclick events for the "Connect and Send" button.
	---------------------------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        final Button connect = (Button) findViewById(R.id.connect);
        final TextView tview = (TextView) findViewById(R.id.textView1);
        final EditText ip = (EditText) findViewById(R.id.edit_ip);
        final EditText port = (EditText) findViewById(R.id.edit_port);
        final LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        final String devicename = (android.os.Build.MANUFACTURER + "-" + android.os.Build.MODEL).replace(' ', '-');
        
        final String deviceip = getWifiAddress();
        
        connect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
			    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
				
				Connect(lm, devicename, deviceip);
			}
		});
    }

    //unused
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    /*--------------------------------------------------------------------------------------
	-- FUNCTION:	onCreate
	--
	-- DATE:		March 3, 2014
	--
	-- REVISIONS:	--
	--
	-- DESIGNER:	Josh Campbell
	--
	-- PROGRAMMER:	Josh Campbell
	--
	-- INTERFACE:	protected void onCreate(Bundle savedInstanceState)
	--
	-- PARAMETERS:	LocationManager lm - The location manager, used for getting location data
	--				String devicename - The name of the android device
	--				String deviceip - The IP address for the android device
	--
	-- RETURNS:		--
	--
	-- NOTES:
	-- This function handles creating a new LocationListenerRecord which handles getting
	-- location updates and saving of the location data.
	-- Extracts the connectivity information from the text fields and passes it to the
	-- LocationListenerRecord for connecting.
	---------------------------------------------------------------------------------------*/
    public void Connect(LocationManager lm, String devicename, String deviceip) {
    	EditText ip = (EditText) findViewById(R.id.edit_ip);
        EditText port = (EditText) findViewById(R.id.edit_port);
    	String s_ip = ip.getText().toString();
		String s_port = port.getText().toString();
		
		if (s_ip != null && s_ip.length() > 0 && s_port != null && s_port.length() > 0) {
			Scanner portscan = new Scanner(s_port);
			int iport = portscan.nextInt();
			portscan.close();
			
			final LocationListenerRecord ll = new LocationListenerRecord(lm, devicename, deviceip, s_ip, iport);
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ll);

		} else if (s_ip != null && s_ip.length() > 0) {
			int iport = 5150;
			final LocationListenerRecord ll = new LocationListenerRecord(lm, devicename, deviceip, s_ip, iport);
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ll);
		}
    }
    /*--------------------------------------------------------------------------------------
	-- CLASS:		ClientConnection
	--
	-- DATE:		March 3, 2014
	--
	-- REVISIONS:	--
	--
	-- DESIGNER:	Josh Campbell
	--
	-- PROGRAMMER:	Josh Campbell and Ian Davidson
	--
	-- NOTES:
	-- This class is a thread that handles connecting to the server and sending the location
	-- data.
	---------------------------------------------------------------------------------------*/
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
		
		/*--------------------------------------------------------------------------------------
		-- FUNCTION:	ClientConnection
		--
		-- DATE:		March 3, 2014
		--
		-- REVISIONS:	--
		--
		-- DESIGNER:	Josh Campbell
		--
		-- PROGRAMMER:	Josh Campbell
		--
		-- INTERFACE:	public ClientConnection(String s_ip, int p)
		--
		-- PARAMETERS:	s_ip - the ip address that is being connected to in String form.
		--				p - the port that is going to be used for creating the socket.
		--
		-- RETURNS:		--
		--
		-- NOTES:
		-- This constructor creates a new ClientConnection object. Saves s_ip and p in local
		-- variables for connecting later.
		---------------------------------------------------------------------------------------*/
		public ClientConnection(String s_ip, int p) {
			ip_addr = s_ip;
			port = p;
			message = null;
			s = null;
			out = null;
		}
		
		/*--------------------------------------------------------------------------------------
		-- FUNCTION:	connectToServer
		--
		-- DATE:		March 3, 2014
		--
		-- REVISIONS:	--
		--
		-- DESIGNER:	Josh Campbell
		--
		-- PROGRAMMER:	Josh Campbell
		--
		-- INTERFACE:	public void connectToServer()
		--
		-- PARAMETERS:	--
		--
		-- RETURNS:		--
		--
		-- NOTES:
		-- This function handles creating the new socket as well as the DataOutputStream object.
		---------------------------------------------------------------------------------------*/
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
	    
		/*--------------------------------------------------------------------------------------
		-- FUNCTION:	sendData
		--
		-- DATE:		March 3, 2014
		--
		-- REVISIONS:	--
		--
		-- DESIGNER:	Josh Campbell
		--
		-- PROGRAMMER:	Josh Campbell
		--
		-- INTERFACE:	public boolean sendData()
		--
		-- PARAMETERS:	data - the packet that the application wants to send
		--
		-- RETURNS:		boolean - true if it was sent successfully, false if not.
		--
		-- NOTES:
		-- This function handles sending the "data" packet using the object's socket.
		---------------------------------------------------------------------------------------*/
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
	    
	    /*--------------------------------------------------------------------------------------
		-- FUNCTION:	isConnected
		--
		-- DATE:		March 3, 2014
		--
		-- REVISIONS:	--
		--
		-- DESIGNER:	Josh Campbell
		--
		-- PROGRAMMER:	Josh Campbell
		--
		-- INTERFACE:	public boolean isConnected()
		--
		-- PARAMETERS:	--
		--
		-- RETURNS:		boolean - true if the connection is still active, false if it is not.
		--
		-- NOTES:
		-- This function checks if the socket is still connected or not.
		---------------------------------------------------------------------------------------*/
	    public boolean isConnected() {
			return !s.isClosed();
	    }
	    
	    /*--------------------------------------------------------------------------------------
		-- FUNCTION:	run
		--
		-- DATE:		March 3, 2014
		--
		-- REVISIONS:	--
		--
		-- DESIGNER:	Josh Campbell
		--
		-- PROGRAMMER:	Josh Campbell
		--
		-- INTERFACE:	public void run()
		--
		-- PARAMETERS:	--
		--
		-- RETURNS:		--
		--
		-- NOTES:
		-- This function attempts to connect to the server if there is no connection.
		-- Once connected, the program will create the location packet and then send it to the
		-- server.
		-- Once sent, the connection will be automatically closed.
		---------------------------------------------------------------------------------------*/
	    public void run() {
	    	if (s == null) {
	    		connectToServer();
	    	}
	    	
	    	message = timestamp + " " + latitude + " " + longitude + " " + devicename + " " + deviceip;
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
    	
    	/*--------------------------------------------------------------------------------------
    	-- FUNCTION:	LocationListenerRecord
    	--
    	-- DATE:		March 4, 2014
    	--
    	-- REVISIONS:	--
    	--
    	-- DESIGNER:	Josh Campbell
    	--
    	-- PROGRAMMER:	Josh Campbell
    	--
    	-- INTERFACE:	public LocationListenerRecord(LocationManager manager, String dn, 
		--												String dip, String ip, int p)
    	--
    	-- PARAMETERS:	manager - the location manager object, used for stopping location updates
    	--				dn - the device name
    	--				dip - the device's ip address
    	--				ip - the ip that is going to be connected to
    	--				p - the port that is going to be connected to
    	--
    	-- RETURNS:		--
    	--
    	-- NOTES:
    	-- This constructor handles initializing all data needed for creating a new connection
    	-- after retrieving the location data.
    	---------------------------------------------------------------------------------------*/
    	public LocationListenerRecord(LocationManager manager, String dn, String dip, String ip, int p) {
    		lm = manager;
    		latitude = -1;
    		longitude = -1;
    		timestamp = -1;
    		devicename = dn;
    		deviceip = dip;
    		s_ip = ip;
    		port = p;
    	}
    	
    	
    	/*--------------------------------------------------------------------------------------
    	-- FUNCTION:	onLocationChanged
    	--
    	-- DATE:		March 4, 2014
    	--
    	-- REVISIONS:	--
    	--
    	-- DESIGNER:	Josh Campbell
    	--
    	-- PROGRAMMER:	Josh Campbell
    	--
    	-- INTERFACE:	public void onLocationChanged(Location location)
    	--
    	-- PARAMETERS:	location - container for the received location data
    	--
    	-- RETURNS:		--
    	--
    	-- NOTES:
    	-- This function handles retrieving the location data, creating the client connection
    	-- thread and starting it, and then notifying the used that the location data was sent.
    	---------------------------------------------------------------------------------------*/
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
    		
    		Context context = getApplicationContext();
    		CharSequence text = "Location data sent.";
    		int duration = Toast.LENGTH_SHORT;

    		Toast toast = Toast.makeText(context, text, duration);
    		toast.show();
	    }
	    
	    //unused
	    @Override
	    public void onProviderDisabled(String provider) {
	    }
	    
	    //unused
	    @Override
	    public void onProviderEnabled(String provider) {
	    }
	    
	    //unused
	    @Override
	    public void onStatusChanged(String provider, int status, Bundle extras) {
	    }
    }
    
    /*---------------------------------------------------------------------------------------
	-- FUNCTION:	getWifiAddress
	--
	-- DATE:		March 4, 2014
	--
	-- REVISIONS:	--
	--
	-- DESIGNER:	Ian Davidson
	--
	-- PROGRAMMER:	Ian Davidson
	--
	-- INTERFACE:	public String getWifiAddress()
	--
	-- PARAMETERS:	--
	--
	-- RETURNS:		String - ip address
	--
	-- NOTES:
	-- Gets the wifi ip address for the device and returns it as a string.
	---------------------------------------------------------------------------------------*/
    public String getWifiAddress() {
		WifiManager wifiManager = (WifiManager) getSystemService (WIFI_SERVICE); 
		WifiInfo wifiInfo = wifiManager.getConnectionInfo(); 
		int ipAddress = wifiInfo.getIpAddress(); 
		String ip = intToIp(ipAddress); 
		return ip;
	}
	
    /*---------------------------------------------------------------------------------------
	-- FUNCTION:	intToIp
	--
	-- DATE:		March 4, 2014
	--
	-- REVISIONS:	--
	--
	-- DESIGNER:	Ian Davidson
	--
	-- PROGRAMMER:	Ian Davidson
	--
	-- INTERFACE:	public String intToIp(int ip)
	--
	-- PARAMETERS:	ip - the ip of the device in the form of a 32 bit integer
	--
	-- RETURNS:		String - the formatted ip address in string form
	--
	-- NOTES:
	-- This function converts the integer ip address into a string by extracting each byte
	-- from the integer ip address.
	---------------------------------------------------------------------------------------*/
	private String intToIp(int ip) {
		return String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff),
				(ip >> 16 & 0xff), (ip >> 24 & 0xff));
	}
    
}
