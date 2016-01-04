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
 * MenuActivity �D�e��
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
	
	String longitude = "null"; // ���o�g��
	String latitude = "null";
	private String bestProvider = "";

	private LocationManager lms;
	private Location location;
	private boolean getService = false; // �O�_�w�}�ҩw��A��

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
		// �ϥ�application
				uapp = (UserApplication) getApplicationContext();
				
		// ���Ugcm
		checkUserEquals();
		checkCurrentRegId();
		//System.out.println("gcm = "+getGcmRegId()+"user ="+getSaveUser());
		// =============check back form notification========================
		Bundle bundle = this.getIntent().getExtras();
		// (�p�G���T�{���S��contain if��k��equals�|���~)�A
		if (bundle != null && bundle.containsKey("beckFromNotification")) {
			
			// �����equals ���������d����
			if (bundle.getString("beckFromNotification").equals("1")) {
					
					String sTip = uapp.getNewData()==1 ? "��s���ܦW��ing...":"�n�J��...";
					//System.out.println(sTip);
					//�i�ױ�
					mProgress = ProgressDialog.show(MenuActivity.this, null, sTip, true, false);
					uapp.setNewData(0);
					
					findBeaconId= bundle.getString("beaconId");
					
					String userv ="";
					userv = getSaveUser();
					
					//System.out.println("userv = "+userv);
					//
					new loginTask().execute(userv);
					bundle.remove("beckFromNotification");
				// ��Juser �A�d�߸�Ʈw�N�ѼƩ�Japplication
//				System.out.println("beckFromNotification");
//				System.out.println("user = " + bundle.getString("user"));
//				uapp.setUser(bundle.getString("user"));
//				uapp.setAllMemberDetail();
//				uapp.setMissingOld();
//				uapp.setMyOldMan();
//				uapp.setMyFollow();
				
				//�ǭȨ�server��|�ߧY�i�Japp�y������٥����J�����A�Ȱ��T���A�n�J
			
			
			}
		}else{
			// �Nbeacon �[�J iBeacons �}�C �Y�S���h�|��nullpointException
			if (iBeacons == null)
				iBeacons = new ArrayList<IBeacon>();
	
			mContext = this;
			
			//�إߵe��
			setUpMenu();
	
			if (savedInstanceState == null)
				changeFragment(new HomeFragment());
	        //�}���ybeacon service
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
			//�ǭȨ�server��|�ߧY�i�Japp�y������٥����J�����A�Ȱ��T���A�n�J
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
		itemHome = new ResideMenuItem(this, R.drawable.icon_home, "�����ѤH");
		itemProfile = new ResideMenuItem(this, R.drawable.icon_profile, "�ڪ��ѤH");
		itemCalendar = new ResideMenuItem(this, R.drawable.icon_group, "�ڪ��s��");
		itemMember = new ResideMenuItem(this, R.drawable.addddd, "�ӤH���");
//		scan = new ResideMenuItem(this, R.drawable.icon_settings, "���y�ڪ�beacon");
//		scans = new ResideMenuItem(this, R.drawable.icon_settings, "stop");

		// init home
		app_name.setText("���ܦѤH");

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
			app_name.setText("�����ѤH");
			changeFragment(new HomeFragment());
		} else if (view == itemProfile) {
			app_name.setText("�ڪ��ѤH");
			changeFragment(new MyOldManFragment());
		} else if (view == itemCalendar) {
			app_name.setText("�ڪ��s��");
			changeFragment(new GroupFragment());
		} else if (view == itemMember) {
			app_name.setText("�ӤH���");
			changeFragment(new MemberFragment());
		} 
		else if (view == scan) {// �I�o���s �Ұ�service
			switch (scanOn) {
			case 0:
				showMessage("�}�l�����l�ܹ�H");
				if (mBluetoothAdapter.isEnabled()) // �P�_�ثebluetooth���A �}�ҷ|�^��true
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
				showMessage("���������l�ܹ�H");

				scanOn = 0;
				break;

			}

		}else if (view == scans) {
			switch (scanOn) {
			case 0:
				showMessage("�}�l�����l�ܹ�H");
				
					PollingUtils.startPollingService(this, 10, ScanService.class, ScanService.ACTION);
					
					scanOn = 1;
				break;
			case 1:
				showMessage("���������l�ܹ�H");
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

	// �b�n�J�ɦ۰�update �ϥΪ̦�m �bonResume==========================
	public void updateGPS() {
		// ���o�t�Ωw��A��
		LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));

		if (status.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			// �p�GGPS�κ����w��}�ҡA�I�slocationServiceInitial()��s��m
			locationServiceInitialGPS();
		} else {
			showMessage("�ж}�ҩw��A��");
			getService = true; // �T�{�}�ҩw��A��
			startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
					.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); // �}�ҳ]�w����
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
				Log.d("setMemberLocation", "��s���\");
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
		lms = (LocationManager) uapp.getContext().getSystemService(this.LOCATION_SERVICE); // ���o�t�Ωw��A��

		// ���k�G,��Criteria����P�_���ѳ̷ǽT����T
		Criteria criteria = new Criteria(); // ��T���Ѫ̿���з�
		bestProvider = lms.getBestProvider(criteria, false); // ��ܺ�ǫ׳̰������Ѫ�
		Location location = lms.getLastKnownLocation(bestProvider);
		getLocationGPS(location);
	}

	private void locationServiceInitialWifi() {
		System.out.println("gps is null change wifi");
		lms = (LocationManager) getSystemService(Context.LOCATION_SERVICE); // ���o�t�Ωw��A��
		/*
		 * //���k�@,�ѵ{���P�_��GPS_provider if (
		 * lms.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ) { location
		 * = lms.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		 * //�ϥ�wifi�w��y�� } else if
		 * (lms.isProviderEnabled(LocationManager.GPS_PROVIDER)) { location =
		 * lms.getLastKnownLocation(LocationManager.GPS_PROVIDER); //�ϥ�gps�w��y�� }
		 */
		Location location = lms.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); // �ϥ�wifi�w��y��
		getLocationWifi(location);
	}

	private void getLocationGPS(Location location) { // �N�w���T��ܦb�e����

		if (location != null) {
			longitude = String.valueOf(location.getLongitude()); // ���o�g��
			latitude = String.valueOf(location.getLatitude()); // ���o�n��
			String ll = longitude + "," + latitude;
			System.out.println(ll);
			updateMemberPost();
		} else {
			// Toast.makeText(this, "�L�k�w��y��", Toast.LENGTH_LONG).show();
			locationServiceInitialWifi();
		}
	}

	private void getLocationWifi(Location location) { // �N�w���T��ܦb�e����

		if (location != null) {
			longitude = String.valueOf(location.getLongitude()); // ���o�g��
			latitude = String.valueOf(location.getLatitude()); // ���o�n��
			String ll = longitude + "," + latitude;
			System.out.println(ll);
			updateMemberPost();
		} else {
			Log.d("setMemberLocation", "�L�k��s�y��");
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

	// What good method is to access resideMenu�?
	public ResideMenu getResideMenu() {
		return resideMenu;
	}

	public TextView getApp_name() {
		return app_name;
	}

	private void showMessage(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	// �Ȯɤ����back����activity
	public boolean onKeyDown(int keyCode, KeyEvent event) {// ������^��
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			ConfirmExit();// ����^��A�h����h�X�T�{
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void ConfirmExit() {// �h�X�T�{
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
	//���o�ثe�x�s���b��
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
	//�ˬd�ثeuser �ä����ܩ��x�s
	private void checkUserEquals(){
		String defaultUser = getSaveUser();
		System.out.println("default = "+defaultUser +",uapp.getUser = "+ uapp.getUser());
		if (TextUtils.isEmpty(defaultUser)) {
			saveUser(uapp.getUser());
			System.out.println("�w�x�s:"+uapp.getUser());
		}else if( uapp.getUser() != null && uapp.getUser() != "null" && !uapp.getUser().equals(defaultUser)){
			removeUser();
			saveUser(uapp.getUser());
			System.out.println("�w����:"+uapp.getUser());
		}
		
	}
	//�x�suser
	private void saveUser(String user){
		Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.putString("user", user);
		editor.commit();
	}
	//�R��user
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
