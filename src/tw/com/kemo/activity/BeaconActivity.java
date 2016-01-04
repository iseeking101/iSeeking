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
public class BeaconActivity extends ListActivity implements IBeaconListener{
	
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
		if(add.getString("addCheck").equals("add")){
			postMethod = "/checkBeaconId";
		}else{
			postMethod = "/updateBeaconId";
			
		}
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
		this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                // 利用索引值取得點擊的項目內容。
                IBeacon beacon = iBeacons.get(index);
                // 顯示。
                _beaconId =beacon.getUuidHexStringDashed()+beacon.getMajor()+beacon.getMinor();
                updateOldPost( beacon.getUuidHexStringDashed()+beacon.getMajor()+beacon.getMinor());
                beaconExit(beacon);
                
            }
        });
		
		setListAdapter(iBeaconsAdapter);
//service is in the main Thread's background so the service will confuse
		Intent bindIntent = new Intent(BeaconActivity.this,BLEService.class);   
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
		
		if(iBeacons.contains(ibeacon)==false){
			iBeacons.add(ibeacon);
		}
		
		Log.i(TAG, "beaconFound: " + ibeacon);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				iBeaconsAdapter.notifyDataSetChanged();
			}
		});
	}
	
	@Override
	public void beaconEnter(IBeacon ibeacon) {
		
		Log.i(TAG, "beaconEnter: " + ibeacon.toString());
		
	}
	
	@Override
	public void beaconExit(IBeacon ibeacon) {
		Log.i(TAG, "beaconExit: " + ibeacon.toString());
		
	}
	
	@Override
	public void operationError(int status) {
		Log.i(TAG, "Bluetooth error: " + status);	
		
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
	private void updateOldPost(String beacon_id_text){
		final String bid;
		
		bid = beacon_id_text;
		StringRequest request = new StringRequest(Request.Method.POST, "https://beacon-series.herokuapp.com"+postMethod, mResponseListener, mErrorListener){
			protected Map<String, String> getParams() throws AuthFailureError {  
	        Map<String, String> map = new HashMap<String, String>();
	        map.put("user", uapp.getUser());
	        map.put("beaconId",bid);
	        _beaconId=bid;
	        return map;  
	    }  };
		NetworkManager.getInstance(this).request(null, request);
	}
	private Listener<String> mResponseListener = new Listener<String>() {

		@Override
		public void onResponse(String str) {
			
			if(str.trim().equals("ok")){
				showMessage("修改成功");
				Bundle argument = new Bundle();
                argument.putString("returnBeaconId", _beaconId);
                Intent intent = getIntent();
                intent.putExtras(argument);
                setResult(Activity.RESULT_OK, intent);
                
				uapp.setBeaconId(_beaconId);
				
				finish();
				}
			if(str.trim().equals("exist")){showMessage("此id已使用，請選擇其他beacon");}
			Log.d("Response", str);
			
		}
	};
	private ErrorListener mErrorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e("Error", error.toString());
		}
	};
	private void showMessage(String msg){
		Toast.makeText(BeaconActivity.this, msg, Toast.LENGTH_LONG).show();
	}
	//==========================
	private BLEService.MyBinder serviceBinder;
	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(TAG, "Service disconnected");
			
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "Service connected");
			Log.e("felix", "onServiceConnected:");
			serviceBinder = (BLEService.MyBinder) service;
			serviceBinder.setListener(BeaconActivity.this);
			serviceBinder.startScan();
			
		}
	};


}
