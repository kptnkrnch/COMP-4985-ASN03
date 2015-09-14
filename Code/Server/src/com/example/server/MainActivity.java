package com.example.server;

/*---------------------------------------------------------------------------------------
--	SOURCE FILE:		MainActivity.java- This file contains various functions to create 
--						and start a server on an android phone that receives location data 
--						and displays onto the screen.
--
--	PROGRAM:			Server
--
--	FUNCTIONS:
--  					protected void onCreate(Bundle savedInstanceState)
--  					public boolean onCreateOptionsMenu(Menu menu)
--  					public void startServer()
--  					public void run()
--  					public ReadingServer(Socket c) 			
--
--	DATE:				March 4, 2014
--
--	REVISIONS:			
--
--	DESIGNERS:			Ian Davidson
--
--	PROGRAMMERS:		Ian Davidson and Josh Campbell
--
--	NOTES:
--	This program will wait accept incoming connections and then try to read data from
--  them. If it reads in data, it will pull the necessary data out and then print it
--  to the screen.
---------------------------------------------------------------------------------------*/

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Scanner;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	public static TextView output;
	private ServerListener ls;
	
	/*---------------------------------------------------------------------------------------
	-- FUNCTION:	CreateServerListener
	--
	-- DATE:		March 4, 2014
	--
	-- REVISIONS:	--
	--
	-- DESIGNER:	Josh Campbell
	--
	-- PROGRAMMER:	Josh Campbell
	--
	-- INTERFACE:	public void CreateServerListener()
	--
	-- PARAMETERS:	--
	--
	-- RETURNS:		--
	--
	-- NOTES:
	-- This function creates a new ServerListener object and starts the object as a thread.
	---------------------------------------------------------------------------------------*/
	public void CreateServerListener() {
		ls = new ServerListener();
		ls.start();
	}
	
	/*---------------------------------------------------------------------------------------
	-- FUNCTION:	CreateServerListener
	--
	-- DATE:		March 4, 2014
	--
	-- REVISIONS:	--
	--
	-- DESIGNER:	Josh Campbell
	--
	-- PROGRAMMER:	Josh Campbell
	--
	-- INTERFACE:	public void CreateServerListener(int port)
	--
	-- PARAMETERS:	port - the custom port for the server listener
	--
	-- RETURNS:		--
	--
	-- NOTES:
	-- This function creates a new ServerListener object and starts the object as a thread.
	-- Sets a custom port for the server listener.
	---------------------------------------------------------------------------------------*/
	public void CreateServerListener(int port) {
		ls = new ServerListener(port);
		ls.start();
	}
	
	/*---------------------------------------------------------------------------------------
	-- FUNCTION:	StopServerListener
	--
	-- DATE:		March 4, 2014
	--
	-- REVISIONS:	--
	--
	-- DESIGNER:	Josh Campbell
	--
	-- PROGRAMMER:	Josh Campbell
	--
	-- INTERFACE:	public void StopServerListener()
	--
	-- PARAMETERS:	--
	--
	-- RETURNS:		--
	--
	-- NOTES:
	-- This function kills the ServerListener's thread so that it no longer listens or accepts
	-- new connections.
	-----------------------------------------------------------------------------------------*/
	public void StopServerListener() {
		ls.StopServer();
	}
	
	/*--------------------------------------------------------------------------------------
	-- FUNCTION:	onCreate
	--
	-- DATE:		March 2nd, 2014
	--
	-- REVISIONS:	--
	--
	-- DESIGNER:	Ian Davidson
	--
	-- PROGRAMMER:	Ian Davidson
	--
	-- INTERFACE:	protected void onCreate(Bundle savedInstanceState)
	--
	-- PARAMETERS:	savedInstanceState - the previous saved instance state
	--
	-- RETURNS:		--
	--
	-- NOTES:
	-- This function sets up and displays the main window of the application.
	---------------------------------------------------------------------------------------*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button start = (Button) findViewById(R.id.start_b);
		Button stop = (Button) findViewById(R.id.stop_b);
		output = (TextView) findViewById(R.id.receivedData);
		start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText port = (EditText) findViewById(R.id.edit_port);
				String s_port = port.getText().toString();
				if (s_port != null && s_port.length() > 0) {
					Scanner portscan = new Scanner(s_port);
					int iport = portscan.nextInt();
					CreateServerListener(iport);
				} else {
					CreateServerListener();
				}
			}
		});
		
		stop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				StopServerListener();
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
	
	public class ServerListener extends Thread {
		ServerSocket listen;
		private boolean running;
		private int port;
		
		/*---------------------------------------------------------------------------------------
		-- FUNCTION:	StopServer
		--
		-- DATE:		March 4, 2014
		--
		-- REVISIONS:	--
		--
		-- DESIGNER:	Josh Campbell
		--
		-- PROGRAMMER:	Josh Campbell
		--
		-- INTERFACE:	public void StopServer()
		--
		-- PARAMETERS:	--
		--
		-- RETURNS:		--
		--
		-- NOTES:
		-- This function is called when the program wants to stop the server from listening for
		-- connections. It causes the program to exit the listening thread.
		---------------------------------------------------------------------------------------*/
		public void StopServer() {
			running = false;
		}
		
		/*---------------------------------------------------------------------------------------
		-- FUNCTION:	ServerListener
		--
		-- DATE:		March 4, 2014
		--
		-- REVISIONS:	--
		--
		-- DESIGNER:	Josh Campbell
		--
		-- PROGRAMMER:	Josh Campbell
		--
		-- INTERFACE:	public ServerListener()
		--
		-- PARAMETERS:	--
		--
		-- RETURNS:		--
		--
		-- NOTES:
		-- Initializes the listening server variables such as the default port and whether it is
		-- listening or not.
		---------------------------------------------------------------------------------------*/
		public ServerListener() {
			running = false;
			port = 5150;
		}
		
		/*---------------------------------------------------------------------------------------
		-- FUNCTION:	ServerListener
		--
		-- DATE:		March 4, 2014
		--
		-- REVISIONS:	--
		--
		-- DESIGNER:	Josh Campbell
		--
		-- PROGRAMMER:	Josh Campbell
		--
		-- INTERFACE:	public ServerListener(int p)
		--
		-- PARAMETERS:	p - the custom port set by the user
		--
		-- RETURNS:		--
		--
		-- NOTES:
		-- Initializes the listening server variables. Sets if it is listening or not.
		-- Sets the port to the desired custom port "p"
		---------------------------------------------------------------------------------------*/
		public ServerListener(int p) {
			running = false;
			port = p;
		}
		
		/*-------------------------------------------------------------------------------------------------
		-- FUNCTION:	startServer
		--
		-- DATE:		March 2nd, 2014
		--
		-- REVISIONS:	--
		--
		-- DESIGNER:	Ian Davidson
		--
		-- PROGRAMMER:	Ian Davidson
		--
		-- INTERFACE:	public void startServer()
		--
		-- PARAMETERS:	--
		--
		-- RETURNS:		--
		--
		-- NOTES:
		-- This function sets up the server by creating a server socket listening on the "port" variable.
		---------------------------------------------------------------------------------------------------*/
		public void startServer() {
			try {
				listen = new ServerSocket(port);
				listen.setSoTimeout(1000);
			} catch (IOException e) {
			}
		}
		
		
		/*------------------------------------------------------------------------------------------------------
		-- FUNCTION:	run
		--
		-- DATE:		March 2nd, 2014
		--
		-- REVISIONS:	--
		--
		-- DESIGNER:	Ian Davidson
		--
		-- PROGRAMMER:	Ian Davidson
		--
		-- INTERFACE:	public void run()
		--
		-- PARAMETERS:	--
		--
		-- RETURNS:		--
		--
		-- NOTES:
		-- This function creates a socket for incoming client info and starts reading the data from that socket.
		-------------------------------------------------------------------------------------------------------*/
		public void run() {
			running = true;
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					MainActivity.output.setText("Status: listening...");
				}
			});
			startServer();
			
			while(running) {
				try {
					if (listen != null) {
						Socket client = listen.accept();
						if (client != null) {
							ReadingServer rs = new ReadingServer(client);
							rs.start();
						}
					}
				} catch (IOException e) {
				}
			}
			listen = null;
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					MainActivity.output.setText("Status: inactive");
				}
			});
		}
	}
	
	public class ReadingServer extends Thread {
		Socket client;
		DataInputStream in;
		
		
		/*---------------------------------------------------------------------------------------
		-- FUNCTION:	ReadingServer
		--
		-- DATE:		March 2nd, 2014
		--
		-- REVISIONS:	--
		--
		-- DESIGNER:	Ian Davidson
		--
		-- PROGRAMMER:	Ian Davidson
		--
		-- INTERFACE:	public ReadingServer(Socket c)
		--
		-- PARAMETERS:	c - the socket that incoming client data will be coming in on.
		--
		-- RETURNS:		--
		--
		-- NOTES:
		-- This function stores data from socket c into an input stream for data processing.
		---------------------------------------------------------------------------------------*/
		public ReadingServer(Socket c) {
			client = c;
			try {
				in = new DataInputStream(client.getInputStream());
			} catch (IOException e) {
			}
		}
		
		
		/*---------------------------------------------------------------------------------------
		-- FUNCTION:	run
		--
		-- DATE:		March 4th, 2014
		--
		-- REVISIONS:	--
		--
		-- DESIGNER:	Ian Davidson
		--
		-- PROGRAMMER:	Ian Davidson and Josh Campbell
		--
		-- INTERFACE:	public void run()
		--
		-- PARAMETERS:	--
		--
		-- RETURNS:		--
		--
		-- NOTES:
		-- This function runs a loop that tries to read in data from the newly established
		-- socket. If data is read, it is formatted/stripped from the packet in order to be
		-- printed to the screen. Prints Timestamp, Latitude, Longitude, Device Name, and the
		-- ip address of the client.
		---------------------------------------------------------------------------------------*/
		public void run() {
			boolean connected = true;
			
			do {
				final String data;
				if (client.isClosed()) {
					connected = false;
				}
				
				try {
					if ((data = in.readUTF()) != null) {
						//System.out.println(data);
						String item[] = new String[5];
						item[0] = "Timestamp: ";
						item[1] = "Latitude: ";
						item[2] = "Longitude: ";
						item[3] = "Device Name: ";
						item[4] = "Client IP: ";
						final String format;
						String temp = "Received Data:\n\n";
						Scanner scan = new Scanner(data);
						for (int i = 0; i < 5; i++) {
							if (scan.hasNext()) {
								if (i == 0) {
									String tempTime = scan.next();
									Scanner timeScan = new Scanner(tempTime);
									long ltime = timeScan.nextLong();
									Timestamp time = new Timestamp(ltime);
									temp += item[i] + time.toString() + "\n";
								} else {
									temp += item[i] + scan.next() + "\n";
								}
							} else {
								break;
							}
						}
						format = temp;
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								MainActivity.output.setText(format);
							}
						});
						client.close();
						connected = false;
					}
				} catch (IOException e) {
				}
				
			} while(connected);
		}
	}

}
