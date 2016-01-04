package tw.com.kemo.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.hereapps.ibeacon.IBeacon;
import com.hereapps.ibeacon.IBeaconListener;
import com.hereapps.ibeacon.IBeaconLibrary;


import tw.com.kemo.networkcommunication.volleymgr.*;
import tw.com.kemo.service.*;



// This activity implements IBeaconListener to receive events about iBeacon discovery
public class BeaconActivityAround extends ListActivity implements IBeaconListener{
	
	private static final int REQUEST_BLUETOOTH_ENABLE = 1;	

	private static ArrayList<IBeacon> iBeacons;
	private ArrayAdapter<IBeacon> iBeaconsAdapter;
	private UserApplication uapp;
	private  String postMethod="";
	String _beaconId="";
	
	
	private final String TAG="ibeaconsample"; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uapp = (UserApplication) getApplicationContext();
		Bundle add =getIntent().getExtras();
		
		System.out.println(postMethod);
		if(iBeacons == null)
			iBeacons = new ArrayList<IBeacon>();
		iBeaconsAdapter = new ArrayAdapter<IBeacon>(this, android.R.layout.simple_list_item_2, android.R.id.text1, iBeacons){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				
				TextView text1 = (TextView) view.findViewById(android.R.id.text1);
				TextView text2 = (TextView) view.findViewById(android.R.id.text2);
				
				IBeacon beacon = iBeacons.get(position);
				
				text1.setText(beacon.getUuidHexStringDashed());
				text2.setText("Major: " + beacon.getMajor() + " Minor: " + beacon.getMinor() + " Distance: " + beacon.getProximity() + "m.");
				return view;
			}
		};
		
		
		setListAdapter(iBeaconsAdapter);
//service is in the main Thread's background so the service will confuse
		  
			    	  Intent bindIntent = new Intent(BeaconActivityAround.this,BLEService2.class);    	
			    	  bindService(bindIntent, connection, BIND_AUTO_CREATE);


		  
		
	}

	@Override
	protected void onStop() {	
		
		super.onStop();
	}
	protected void onDestroy(){
	    
	    super.onDestroy();
	    this.unbindService(connection);

	}
	
	
	
	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_scan) {
			iBeacons.clear();
			iBeaconsAdapter.notifyDataSetChanged();
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}	
	*/
	
	// The following methods implement the IBeaconListener interface
	
	@Override
	public void beaconFound(IBeacon ibeacon) {
	
	}
	
	@Override
	public void beaconEnter(IBeacon ibeacon) {
		
	}
	
	@Override
	public void beaconExit(IBeacon ibeacon) {
		
	}
	
	@Override
	public void operationError(int status) {
		
	}
	
	@Override
	public void scanState(int state) {
		switch(state){
		case IBeaconLibrary.SCAN_STARTED:
			Log.i(TAG, "state= SCAN_STARTED");	
			break;
		case IBeaconLibrary.SCAN_END_SUCCESS:
			Log.i(TAG, "state= SCAN_END_SUCCESS");
			//here you can scan again.
			break;
		case IBeaconLibrary.SCAN_END_EMPTY:
			Log.i(TAG, "state= SCAN_END_EMPTY");	
			//here you can scan again.
			break;
		
		}
		
	}
		private void showMessage(String msg){
		Toast.makeText(BeaconActivityAround.this, msg, Toast.LENGTH_LONG).show();
	}
	// ==============beacon service============
		private BLEService2.MyBinder serviceBinder;
		private ServiceConnection connection = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
				Log.d(TAG, "Service disconnected");
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.d(TAG, "Service connected");
				Log.e("felix", "onServiceConnected:");
				serviceBinder = (BLEService2.MyBinder) service;
				serviceBinder.setListener(BeaconActivityAround.this);
				serviceBinder.startScan();
				BeaconActivityAround.this.finish();
			}
		};


}
