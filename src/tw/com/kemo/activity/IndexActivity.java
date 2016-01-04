package tw.com.kemo.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import tw.com.kemo.networkcommunication.volleymgr.NetworkManager;
import tw.com.kemo.service.BLEService;
import tw.com.kemo.service.BLEService2;
import tw.com.kemo.util.PollingUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hereapps.ibeacon.IBeacon;
import com.hereapps.ibeacon.IBeaconLibrary;
import com.hereapps.ibeacon.IBeaconListener;


public class IndexActivity extends Activity implements IBeaconListener {
	private static final String TAGGCM = "IndexActivity";
	private ProgressDialog mProgress;
	// this id can be retrieved from Google API Console
	private static final String SENDER_ID = "640374294861";
	//server URL 將gcm獲得的token上傳到server   直接使用heroku提供網址會報錯誤，須加上http//
	private static final String API_SERVER = "http://android-gcm-server.herokuapp.com";
	//======beacon========================================
	private static final int REQUEST_BLUETOOTH_ENABLE = 1;	
	private BluetoothAdapter mBluetoothAdapter  = BluetoothAdapter.getDefaultAdapter();
	private static ArrayList<IBeacon> iBeacons;
	private ArrayAdapter<IBeacon> iBeaconsAdapter;
	private final String TAG="ibeaconsample";
	//====================================================
	private UserApplication uapp;
	
	private Button btn_person,btn_old,btn_oldgroup;
	private ToggleButton btn_scan;
	private boolean isCheck =false; // bindService 內用來讓service停止或開啟
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        btn_oldgroup = (Button)findViewById(R.id.index_oldgroup);
        btn_old = (Button)findViewById(R.id.index_old);
        btn_scan = (ToggleButton)findViewById(R.id.index_scan);
        uapp = (UserApplication) getApplicationContext();
        //=============gcm===========================
        
        String currentRegId = getGcmRegId();
		if (TextUtils.isEmpty(currentRegId)) {
			registration();
			System.out.println("91");
		} 
        //=============check back form notification========================
        Bundle bundle = this.getIntent().getExtras();
        //如果不確認有沒有contain if方法內equals會錯誤
        if(bundle != null && bundle.containsKey("beckFromNotification") ){
        	//比較用equals 用雙等號查不到
        	if(bundle.getString("beckFromNotification").equals("1")){
        		//放入user ，查詢資料庫將參數放入application
        		System.out.println("user = "+bundle.getString("user"));
	        	uapp.setUser(bundle.getString("user"));
	        	uapp.getAllOldDetail();
	        	uapp.setAllMemberDetail();
	        	uapp.getAllGroupMember();
	        	uapp.getMyFollow();
        	}
        }
        //=====================beacon=======================
        if(iBeacons == null)
			iBeacons = new ArrayList<IBeacon>();
        //toggleButton 按鈕 
        btn_scan.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
            
            @Override  
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {  
                /**傳一個boolean來判斷 true,false
                 * ToggleButton按鈕用來切換true,false，也就是XML定義時的checked的值*/  
                if(isChecked){//true
                	isCheck = true; // bindService 內用來讓service停止或開啟
                	if (mBluetoothAdapter.isEnabled())//判斷目前bluetooth狀態 開啟會回傳true
    				{
                		Intent bindIntent = new Intent(IndexActivity.this,BLEService2.class);   
    			    	bindService(bindIntent, connection, BIND_AUTO_CREATE);
    				}
    				else{
    					Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    				    startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH_ENABLE );
    				}
                }else{//false
                   isCheck = false;
                   if (mBluetoothAdapter.isEnabled())//判斷目前bluetooth狀態 開啟會回傳true
   				{
   					Intent bindIntent = new Intent(IndexActivity.this,BLEService2.class);   
   			    	bindService(bindIntent, connection, BIND_AUTO_CREATE);
   				}
   				else{
   					Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
   				    startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH_ENABLE );
   				}
                }  
            }  
        });  
        
        btn_oldgroup.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v){	
				jumpToActivity(IndexActivity.this,OldGroupActivity.class);
			}
		});
        btn_old.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v){	
				jumpToActivity(IndexActivity.this,OldmanActivity.class);
			}
		});
       
   
        
    }
	public void onStart(){
		super.onStart();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_BLUETOOTH_ENABLE){
			if(resultCode == Activity.RESULT_OK){
				Intent bindIntent = new Intent(IndexActivity.this,BLEService2.class);    	
		    	bindService(bindIntent, connection, BIND_AUTO_CREATE);
			}
		}
	}	
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
		switch (id) {
	    case R.id.Member:
	    	jumpToActivity(IndexActivity.this,MemberActivity.class);
	        return true;
	    case R.id.Old:
	    	jumpToActivity(IndexActivity.this,OldmanActivity.class);
	    	return true;
	    	
		}
		return super.onOptionsItemSelected(item);
	}

	private void jumpToActivity(Context ct,Class<?> lt){
		Intent intent = new Intent();
        intent.setClass(ct, lt);
		startActivity(intent);
	}
	private void showMessage(String msg){
		Toast.makeText(IndexActivity.this, msg, Toast.LENGTH_LONG).show();
	}
	protected void onDestroy() {
		this.unbindService(connection);
		super.onDestroy();
	}
	//====================beacon===================
	@Override
	public void beaconFound(IBeacon ibeacon) {
		
		if(iBeacons.contains(ibeacon)==false){
			iBeacons.add(ibeacon);
		}
		
		Log.i(TAG, "beaconFound: " + ibeacon);
		
	}
	
	@Override
	public void beaconEnter(IBeacon ibeacon) {
		
		Log.i(TAG, "beaconEnter: " + ibeacon.toString());
		System.out.println("beacon is back!!!");
	}
	
	@Override
	public void beaconExit(IBeacon ibeacon) {
		Log.i(TAG, "beaconExit: " + ibeacon.toString());
		System.out.println("beacon is gone!");
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
	
	//==========================
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
				serviceBinder.setListener(IndexActivity.this);
				//按鈕偵測/停止時觸發
				if(isCheck == true){
					serviceBinder.startScan();
				}else{
					serviceBinder.stopScan();
				}
			}
		};
//===============gcm================
		public void registration() {
			mProgress = ProgressDialog.show(this, null, "Please wait", true, false);
			GCMRegistrationTask task = new GCMRegistrationTask();
			task.execute();
		}
		//改為查詢資料庫 檢查 此帳號有無存在 若無存在 則 註冊gcm
		public String getGcmRegId() {
			return PreferenceManager.getDefaultSharedPreferences(this).getString(
					"registration_id", null);
		}
		//此方法將改為傳 帳號給server 紀錄
		public void saveGcmRegId(String result) {
			Editor editor = PreferenceManager.getDefaultSharedPreferences(this)
					.edit();
			editor.putString("registration_id", result);
			editor.commit();
		}
		/**
		 * This task can help you to s
		 * @author Justin
		 *
		 */
		private class GCMRegistrationTask extends AsyncTask<Void, Void, String> {

			@Override
			protected String doInBackground(Void... params) {
				Log.d(TAGGCM, "Registering");
				GoogleCloudMessaging gcm = GoogleCloudMessaging
						.getInstance(getApplicationContext());
				try {
					return gcm.register(SENDER_ID);
				} catch (IOException e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				if (result != null) {
					Toast.makeText(getApplicationContext(), "registered with GCM",
							Toast.LENGTH_LONG).show();
					saveGcmRegId(result);
					sendRegIdToServer(result);
				}
				if (mProgress != null) {
					mProgress.dismiss();
				}
			}
			
			private void sendRegIdToServer(String result) {
				try {
					JSONObject json = new JSONObject();
					json.put("user", uapp.getUser());
					json.put("type", "android");
					json.put("token", result);
					JsonObjectRequest request = new JsonObjectRequest(
							Request.Method.POST,
							API_SERVER + "/subscribe",
							json, mCompleteListener, mErrorListener);
					String contentType = request.getBodyContentType();
					Log.d(TAGGCM, "Send To Server " + contentType);
					NetworkManager.getInstance(IndexActivity.this).request(null,
							request);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}
		private Listener<JSONObject> mCompleteListener = new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject json) {
				Log.d(TAGGCM, "onResponse " + json);
			}
		};

		public ErrorListener mErrorListener = new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError err) {
				Log.e(TAGGCM, "onError " + err);
			}
		};
}
