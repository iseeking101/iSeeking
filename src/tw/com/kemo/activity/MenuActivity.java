package tw.com.kemo.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import tw.com.kemo.fragment.GroupFragment;
import tw.com.kemo.fragment.HomeFragment;
import tw.com.kemo.fragment.MemberFragment;
import tw.com.kemo.fragment.MyOldManFragment;
import tw.com.kemo.fragment.SettingsFragment;
import tw.com.kemo.networkcommunication.volleymgr.NetworkManager;
import tw.com.kemo.service.BLEService;
import tw.com.kemo.service.BLEService2;
import tw.com.kemo.service.BLEService_Missing;
import tw.com.kemo.service.ScanService;
import tw.com.kemo.util.GPSHelper;
import tw.com.kemo.util.MissingOldAdpater;
import tw.com.kemo.util.MissingOldItem;
import tw.com.kemo.util.PollingUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hereapps.ibeacon.IBeacon;
import com.hereapps.ibeacon.IBeaconLibrary;
import com.hereapps.ibeacon.IBeaconListener;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;


/** 
 * MenuActivity 主畫面
*/ 
public class MenuActivity extends FragmentActivity implements IBeaconListener, View.OnClickListener {
	private ProgressDialog mProgress;
	private static final String SENDER_ID = "640374294861";

	ResideMenu resideMenu;
	private MenuActivity mContext;
	private ResideMenuItem itemHome;
	private ResideMenuItem itemProfile;
	private ResideMenuItem itemCalendar;
	private ResideMenuItem itemMember;
	private ResideMenuItem scan;
	private ResideMenuItem scans;
	
	String longitude = "null"; // 取得經度
	String latitude = "null";
	private String bestProvider = "";

	private LocationManager lms;
	private Location location;
	private boolean getService = false; // 是否已開啟定位服務

	private TextView app_name;
	private UserApplication uapp;
	private ArrayList<MissingOldItem> items;
	// =======================service part of scan beacon
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private static ArrayList<IBeacon> iBeacons;
	private ArrayAdapter<IBeacon> iBeaconsAdapter;
	private final String TAG = "ibeaconsample";
	private static final int REQUEST_BLUETOOTH_ENABLE = 1;
	// =========================================================
	private int scanOn = 0;
	private String findBeaconId ="";

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		app_name = (TextView) findViewById(R.id.app_name);
		// 使用application
				uapp = (UserApplication) getApplicationContext();
				
		// 註冊gcm
		checkUserEquals();
		checkCurrentRegId();
		//System.out.println("gcm = "+getGcmRegId()+"user ="+getSaveUser());
		// =============check back form notification========================
		Bundle bundle = this.getIntent().getExtras();
		// (如果不確認有沒有contain if方法內equals會錯誤)，
		if (bundle != null && bundle.containsKey("beckFromNotification")) {
			
			// 比較用equals 用雙等號查不到
			if (bundle.getString("beckFromNotification").equals("1")) {
					
					String sTip = uapp.getNewData()==1 ? "更新失蹤名單ing...":"登入中...";
					//System.out.println(sTip);
					//進度條
					mProgress = ProgressDialog.show(MenuActivity.this, null, sTip, true, false);
					uapp.setNewData(0);
					
					findBeaconId= bundle.getString("beaconId");
					
					String userv ="";
					userv = getSaveUser();
					
					//System.out.println("userv = "+userv);
					//
					new loginTask().execute(userv);
					bundle.remove("beckFromNotification");
				// 放入user ，查詢資料庫將參數放入application
//				System.out.println("beckFromNotification");
//				System.out.println("user = " + bundle.getString("user"));
//				uapp.setUser(bundle.getString("user"));
//				uapp.setAllMemberDetail();
//				uapp.setMissingOld();
//				uapp.setMyOldMan();
//				uapp.setMyFollow();
				
				//傳值到server後會立即進入app造成資料還未載入完成，暫停三秒後再登入
			
			
			}
		}else{
			// 將beacon 加入 iBeacons 陣列 若沒有則會有nullpointException
			if (iBeacons == null)
				iBeacons = new ArrayList<IBeacon>();
	
			mContext = this;
			
			//建立畫面
			setUpMenu();
	
			if (savedInstanceState == null)
				changeFragment(new HomeFragment());
	        //開掃描beacon service
			Intent bindIntent = new Intent(MenuActivity.this, BLEService_Missing.class);
			bindService(bindIntent, missing_connection, BIND_AUTO_CREATE);
		}
	
	}
	private class loginTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			Log.d(TAG, "Loinging");
				uapp.clearFollowList();
				uapp.clearMissingOldList();
				uapp.clearMyOldMan();
				
				uapp.setUser(params[0]);
				uapp.setMyFollow();
			    uapp.setAllMemberDetail();
			    uapp.setMissingOld();
			    uapp.setMyOldMan();
			//傳值到server後會立即進入app造成資料還未載入完成，暫停三秒後再登入
			try {
				Thread.sleep(6000);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			
			if (mProgress != null) {
				if (iBeacons == null)
					iBeacons = new ArrayList<IBeacon>();
		
				mContext = MenuActivity.this;
		
				setUpMenu();
				changeFragment(new HomeFragment());
				
				
				Intent bindIntent = new Intent(MenuActivity.this, BLEService_Missing.class);
				bindService(bindIntent, missing_connection, BIND_AUTO_CREATE);
				mProgress.dismiss();
				
				jumpToActivity(MenuActivity.this,MissingOldManActivity.class);
				
			}
		}

	}
	private void setUpMenu() {

		// attach to current activity;
		resideMenu = new ResideMenu(this);
		resideMenu.setUse3D(true);
		resideMenu.setBackground(R.drawable.menu_background);
		resideMenu.attachToActivity(this);
		resideMenu.setMenuListener(menuListener);
		// valid scale factor is between 0.0f and 1.0f. leftmenu'width is
		// 150dip.
		resideMenu.setScaleValue(0.8f);

		// create menu items;
		itemHome = new ResideMenuItem(this, R.drawable.icon_home, "走失老人");
		itemProfile = new ResideMenuItem(this, R.drawable.icon_profile, "我的老人");
		itemCalendar = new ResideMenuItem(this, R.drawable.icon_group, "我的群組");
		itemMember = new ResideMenuItem(this, R.drawable.addddd, "個人資料");
//		scan = new ResideMenuItem(this, R.drawable.icon_settings, "掃描我的beacon");
//		scans = new ResideMenuItem(this, R.drawable.icon_settings, "stop");

		// init home
		app_name.setText("失蹤老人");

		itemHome.setOnClickListener(this);
		itemProfile.setOnClickListener(this);
		itemCalendar.setOnClickListener(this);
		itemMember.setOnClickListener(this);
//		scan.setOnClickListener(this);
//		scans.setOnClickListener(this);
		
		
		resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemProfile, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemCalendar, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemMember, ResideMenu.DIRECTION_LEFT);
//		resideMenu.addMenuItem(scan, ResideMenu.DIRECTION_LEFT);
//		resideMenu.addMenuItem(scans, ResideMenu.DIRECTION_LEFT);

		// You can disable a direction by setting ->
		// resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

		findViewById(R.id.title_bar_left_menu).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
			}
		});
		findViewById(R.id.title_bar_right_menu).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
			}
		});
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return resideMenu.dispatchTouchEvent(ev);
	}

	@Override
	public void onClick(View view) {

		if (view == itemHome) {
			app_name.setText("走失老人");
			changeFragment(new HomeFragment());
		} else if (view == itemProfile) {
			app_name.setText("我的老人");
			changeFragment(new MyOldManFragment());
		} else if (view == itemCalendar) {
			app_name.setText("我的群組");
			changeFragment(new GroupFragment());
		} else if (view == itemMember) {
			app_name.setText("個人資料");
			changeFragment(new MemberFragment());
		} 
		else if (view == scan) {// 點這按鈕 啟動service
			switch (scanOn) {
			case 0:
				showMessage("開始偵測追蹤對象");
				if (mBluetoothAdapter.isEnabled()) // 判斷目前bluetooth狀態 開啟會回傳true
				{
//							Intent bindIntent = new Intent(MenuActivity.this, BLEService2.class);
//							bindService(bindIntent, connection, BIND_ADJUST_WITH_ACTIVITY | BIND_AUTO_CREATE);
							jumpToActivity(this,BeaconActivityAround.class);

					
				} else {
					Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH_ENABLE);
				}
				scanOn = 1;
				break;
			case 1:
				showMessage("關閉偵測追蹤對象");

				scanOn = 0;
				break;

			}

		}else if (view == scans) {
			switch (scanOn) {
			case 0:
				showMessage("開始偵測追蹤對象");
				
					PollingUtils.startPollingService(this, 10, ScanService.class, ScanService.ACTION);
					
					scanOn = 1;
				break;
			case 1:
				showMessage("關閉偵測追蹤對象");
				PollingUtils.stopPollingService(this, ScanService.class, ScanService.ACTION);
				
				scanOn = 0;
				break;

			}

		
		} 
		resideMenu.closeMenu();
	}

	public void onResume() {
		super.onResume();
		updateGPS();
	}

	// 在登入時自動update 使用者位置 在onResume==========================
	public void updateGPS() {
		// 取得系統定位服務
		LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));

		if (status.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			// 如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
			locationServiceInitialGPS();
		} else {
			showMessage("請開啟定位服務");
			getService = true; // 確認開啟定位服務
			startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
					.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); // 開啟設定頁面
		}

	}

	private void updateMemberPost() {
		StringRequest request = new StringRequest(Request.Method.POST,
				"https://beacon-series.herokuapp.com/setMemberLocation", mResMemberLocation, mErrorListener) {
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("user", uapp.getUser());
				map.put("longitude", longitude);
				map.put("latitude", latitude);
				return map;
			}
		};
		NetworkManager.getInstance(this).request(null, request);
	}

	private Listener<String> mResMemberLocation = new Listener<String>() {

		@Override
		public void onResponse(String str) {

			if (str.trim().equals("ok")) {
				Log.d("setMemberLocation", "更新成功");
			}
			Log.d("Response", str);
		}
	};
	private ErrorListener mErrorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e("Error", error.toString());
		}
	};

	private void locationServiceInitialGPS() {
		lms = (LocationManager) uapp.getContext().getSystemService(this.LOCATION_SERVICE); // 取得系統定位服務

		// 做法二,由Criteria物件判斷提供最準確的資訊
		Criteria criteria = new Criteria(); // 資訊提供者選取標準
		bestProvider = lms.getBestProvider(criteria, false); // 選擇精準度最高的提供者
		Location location = lms.getLastKnownLocation(bestProvider);
		getLocationGPS(location);
	}

	private void locationServiceInitialWifi() {
		System.out.println("gps is null change wifi");
		lms = (LocationManager) getSystemService(Context.LOCATION_SERVICE); // 取得系統定位服務
		/*
		 * //做法一,由程式判斷用GPS_provider if (
		 * lms.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ) { location
		 * = lms.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		 * //使用wifi定位座標 } else if
		 * (lms.isProviderEnabled(LocationManager.GPS_PROVIDER)) { location =
		 * lms.getLastKnownLocation(LocationManager.GPS_PROVIDER); //使用gps定位座標 }
		 */
		Location location = lms.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); // 使用wifi定位座標
		getLocationWifi(location);
	}

	private void getLocationGPS(Location location) { // 將定位資訊顯示在畫面中

		if (location != null) {
			longitude = String.valueOf(location.getLongitude()); // 取得經度
			latitude = String.valueOf(location.getLatitude()); // 取得緯度
			String ll = longitude + "," + latitude;
			System.out.println(ll);
			updateMemberPost();
		} else {
			// Toast.makeText(this, "無法定位座標", Toast.LENGTH_LONG).show();
			locationServiceInitialWifi();
		}
	}

	private void getLocationWifi(Location location) { // 將定位資訊顯示在畫面中

		if (location != null) {
			longitude = String.valueOf(location.getLongitude()); // 取得經度
			latitude = String.valueOf(location.getLatitude()); // 取得緯度
			String ll = longitude + "," + latitude;
			System.out.println(ll);
			updateMemberPost();
		} else {
			Log.d("setMemberLocation", "無法更新座標");
		}
	}

	// =====================================================================
	private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
		@Override
		public void openMenu() {
			// Toast.makeText(mContext, "Menu is opened!",
			// Toast.LENGTH_SHORT).show();
		}

		@Override
		public void closeMenu() {
			// Toast.makeText(mContext, "Menu is closed!",
			// Toast.LENGTH_SHORT).show();
		}
	};

	private void changeFragment(Fragment targetFragment) {
		resideMenu.clearIgnoredViewList();

		getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, targetFragment, "fragment")
				.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
	}

	// What good method is to access resideMenu嚗?
	public ResideMenu getResideMenu() {
		return resideMenu;
	}

	public TextView getApp_name() {
		return app_name;
	}

	private void showMessage(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	// 暫時不能用back關閉activity
	public boolean onKeyDown(int keyCode, KeyEvent event) {// 捕捉返回鍵
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			ConfirmExit();// 按返回鍵，則執行退出確認
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void ConfirmExit() {// 退出確認
		resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
	}

	// ==============beacon service tracing missingold============
	private BLEService_Missing.MyBinder missing_serviceBinder;
	private ServiceConnection missing_connection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(TAG, "Service disconnected");
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "Service connected");
			Log.e("felix", "onServiceConnected:");
			missing_serviceBinder = (BLEService_Missing.MyBinder) service;
			missing_serviceBinder.setListener(MenuActivity.this);
			missing_serviceBinder.startScan();

		}
	};
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
			serviceBinder.setListener(MenuActivity.this);
			serviceBinder.startScan();

		}
	};

	// ====================beacon service===================
	@Override
	public void beaconFound(IBeacon ibeacon) {

		if (iBeacons == null) {
			iBeacons.add(ibeacon);
		}

		Log.i(TAG, "beaconFound: " + ibeacon);

	}

	@Override
	public void beaconEnter(IBeacon ibeacon) {

		// Log.i(TAG, "beaconEnter: " + ibeacon.toString());

	}

	@Override
	public void beaconExit(IBeacon ibeacon) {
		// Log.i(TAG, "beaconExit: " + ibeacon.toString());

	}

	@Override
	public void operationError(int status) {
		Log.i(TAG, "Bluetooth error: " + status);

	}

	@Override
	public void scanState(int state) {
		switch (state) {
		case IBeaconLibrary.SCAN_STARTED:
			Log.i(TAG, "state= SCAN_STARTED");
			break;
		case IBeaconLibrary.SCAN_END_SUCCESS:
			Log.i(TAG, "state= SCAN_END_SUCCESS");
			// here you can scan again.
			break;
		case IBeaconLibrary.SCAN_END_EMPTY:
			Log.i(TAG, "state= SCAN_END_EMPTY");
			// here you can scan again.
			break;

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_BLUETOOTH_ENABLE) {
			if (resultCode == Activity.RESULT_OK) {
				Thread thread = new Thread() {
					@Override
					public void run() {
						Intent bindIntent = new Intent(MenuActivity.this, BLEService2.class);
						bindService(bindIntent, connection, BIND_ADJUST_WITH_ACTIVITY | BIND_AUTO_CREATE);

					}
				};

				thread.start();
			}
		}
	}

	private void jumpToActivity(Context ct, Class<?> lt) {

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.setClass(ct, lt);
		for(int i = 0 ; i< uapp.getMissOldList().size();i++){
			if(findBeaconId.equals(uapp.getMissOldList().get(i).getBeaconId())){
				uapp.setIndex(i);
			}
		}
		startActivity(intent);
		// startActivityForResult(intent,0);
	}

	// ======gcm================
	public void checkCurrentRegId() {
		String currentRegId = getGcmRegId();
		if (TextUtils.isEmpty(currentRegId)) {
			registration();
			//System.out.println("91");
		}
	}

	private String getGcmRegId() {
		return PreferenceManager.getDefaultSharedPreferences(this).getString("registration_id", null);
	}
	//取得目前儲存的帳號
	private String getSaveUser(){
		return PreferenceManager.getDefaultSharedPreferences(this).getString("user", null);
	}

	private void registration() {
		mProgress = ProgressDialog.show(this, null, "Please wait", true, false);
		GCMRegistrationTask task = new GCMRegistrationTask();
		task.execute();
	}
	
	private void saveGcmRegId(String result) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.putString("registration_id", result);
		editor.commit();
	}
	//檢查目前user 並比對更變或儲存
	private void checkUserEquals(){
		String defaultUser = getSaveUser();
		System.out.println("default = "+defaultUser +",uapp.getUser = "+ uapp.getUser());
		if (TextUtils.isEmpty(defaultUser)) {
			saveUser(uapp.getUser());
			System.out.println("已儲存:"+uapp.getUser());
		}else if( uapp.getUser() != null && uapp.getUser() != "null" && !uapp.getUser().equals(defaultUser)){
			removeUser();
			saveUser(uapp.getUser());
			System.out.println("已更變:"+uapp.getUser());
		}
		
	}
	//儲存user
	private void saveUser(String user){
		Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.putString("user", user);
		editor.commit();
	}
	//刪除user
	private void removeUser(){
		Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.remove("user");
		editor.commit();
	}
	
	class GCMRegistrationTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			Log.d("GCM", "Registering");
			GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
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
				Toast.makeText(getApplicationContext(), "registered with GCM", Toast.LENGTH_LONG).show();
				saveGcmRegId(result);
				sendRegIdToServer(result);
			}
			if (mProgress != null) {
				mProgress.dismiss();
			}
		}

		private void sendRegIdToServer(String result) {
			sendRegIdToServerDAO(result);
		}

	}

	private void sendRegIdToServerDAO(String result) {
		try {
			JSONObject json = new JSONObject();
			json.put("user", uapp.getUser());
			json.put("type", "android");
			json.put("token", result);
			JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
					"http://android-gcm-server.herokuapp.com/subscribe", json, mCompleteListener, mErrorListenerGCM);
			String contentType = request.getBodyContentType();
			Log.d("GCM", "Send To Server " + contentType);
			NetworkManager.getInstance(this).request(null, request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private Listener<JSONObject> mCompleteListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject json) {
			Log.d("GCM", "onResponse " + json);

		}
	};

	private ErrorListener mErrorListenerGCM = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError err) {
			Log.e("GCM", "onError " + err);
		}
	};
	public void onStop(){
		super.onStop();
		//PollingUtils.stopPollingService(this, ScanService.class, ScanService.ACTION);
		
	}
	public void onDestory(){
//		PollingUtils.stopPollingService(this, ScanService.class, ScanService.ACTION);
		
		super.onDestroy();
		
	}
	

}
