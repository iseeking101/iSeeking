package tw.com.kemo.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hereapps.ibeacon.IBeacon;
import com.hereapps.ibeacon.IBeaconListener;
import com.hereapps.ibeacon.IBeaconLibrary;
import tw.com.kemo.service.*;
//import tw.com.kemo.networkcommunication.volleymgr.NetworkManager;



// This activity implements IBeaconListener to receive events about iBeacon discovery
public class BeaconActivity_old extends ListActivity implements IBeaconListener{
	
	private static final int REQUEST_BLUETOOTH_ENABLE = 1;	

	private static ArrayList<IBeacon> iBeacons;
	private ArrayAdapter<IBeacon> iBeaconsAdapter;
	private static IBeaconLibrary iBeaconLibrary;
	
	
	private final String TAG="ibeaconsample"; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
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
                
                Bundle argument = new Bundle();
                argument.putString("returnBeaconId", beacon.getUuidHexStringDashed()+beacon.getMajor()+beacon.getMinor());
                Intent intent = getIntent();
                intent.putExtras(argument);
                setResult(Activity.RESULT_OK, intent);
                updateOldPost( beacon.getUuidHexStringDashed()+beacon.getMajor()+beacon.getMinor());
                
                
            }
        });
		getSelectedItemId ();
		setListAdapter(iBeaconsAdapter);
		
	
		iBeaconLibrary = IBeaconLibrary.getInstance();
		iBeaconLibrary.setListener(this);
		

	}
	private void showMessage(String msg){
		Toast.makeText(BeaconActivity_old.this, msg, Toast.LENGTH_LONG).show();
	}
	
	@Override
	protected void onStop() {
		iBeaconLibrary.stopScan();
		super.onStop();
	}
	protected void onDestroy(){
		
		super.onDestroy();
	}
	
	private void scanBeacons(){
		Log.i(IBeaconLibrary.LOG_TAG,"Scanning");

		if(!IBeaconLibrary.setBluetoothAdapter(this)){
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH_ENABLE );
		}else{
			if(iBeaconLibrary.isScanning())
				iBeaconLibrary.stopScan();
			iBeaconLibrary.reset();
			iBeaconLibrary.startScan();		
		}		
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_BLUETOOTH_ENABLE){
			if(resultCode == Activity.RESULT_OK){
				scanBeacons();
			}
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.track_uncle, menu);
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
			scanBeacons();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}	
	
	private void updateOldPost(String beacon_id_text){
		final String bid;
		
		bid = beacon_id_text;
		
		
		StringRequest request = new StringRequest(Request.Method.POST, "https://beacon-series.herokuapp.com/updateBeaconId", mResponseListener, mErrorListener){
			protected Map<String, String> getParams() throws AuthFailureError {  
	        Map<String, String> map = new HashMap<String, String>();
	        Bundle bundle = getIntent().getExtras();
	        map.put("user", bundle.getString("user"));
	        map.put("beaconId",bid);
	        map.put("oldName", bundle.getString("oldName"));  
	        map.put("oldCharacteristic", bundle.getString("oldCharacteristic"));  
	        map.put("oldhistory", bundle.getString("oldhistory"));
	        map.put("oldclothes", bundle.getString("oldclothes"));
	        map.put("oldaddr", bundle.getString("oldaddr"));
	       
	        return map;  
	    }  };
		NetworkManager.getInstance(this).request(null, request);
	}
	private Listener<String> mResponseListener = new Listener<String>() {

		@Override
		public void onResponse(String str) {
			
			if(str.trim().equals("ok")){showMessage("修改成功");BeaconActivity_old.this.finish();}
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
	// The following methods implement the IBeaconListener interface
	
	@Override
	public void beaconFound(IBeacon ibeacon) {
		iBeacons.add(ibeacon);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				iBeaconsAdapter.notifyDataSetChanged();
				System.out.println("something changed");
			}
		});
	}
	
	@Override
	public void beaconEnter(IBeacon ibeacon) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void beaconExit(IBeacon ibeacon) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void operationError(int status) {
		Log.i(IBeaconLibrary.LOG_TAG, "Bluetooth error: " + status);	
		
	}
	
	@Override
	public void scanState(int state) {
		// TODO Auto-generated method stub
		
	}
	//不知原因 import 的 class finish()及回上頁後不能用 ，所以直接拉到這邊來
	   static class NetworkManager {
			
			private static NetworkManager sInstance;
			
			private RequestQueue mQueue;
			
			private NetworkManager(Context context) {
				mQueue = Volley.newRequestQueue(context.getApplicationContext());
			}
			
			public static  NetworkManager getInstance(Context context) {
				if (sInstance == null) {
					sInstance = new NetworkManager(context);
				}
				return sInstance;
			}
			
			public void request(String tag, Request<?> request) {
				if (tag != null) {
					request.setTag(tag);
				}
				mQueue.add(request);
			}
			
			public void cancelRequest(String tag) {
				if (tag != null) {
					mQueue.cancelAll(tag);
				}
			}
			
			public void stop() {
				mQueue.stop();
			}
			
			
		}
}	

