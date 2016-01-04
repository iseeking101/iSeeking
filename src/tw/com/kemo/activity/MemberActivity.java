 package tw.com.kemo.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import tw.com.kemo.fragment.SettingFragment;
import tw.com.kemo.fragment.SettingsFragment;
import tw.com.kemo.networkcommunication.volleymgr.NetworkManager;
import tw.com.kemo.util.MD5;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hereapps.ibeacon.IBeacon;
import com.hereapps.ibeacon.IBeaconListener;
import com.special.ResideMenu.ResideMenu;
import com.hereapps.ibeacon.IBeaconLibrary;


public class MemberActivity extends FragmentActivity  implements LocationListener {
	private TextView member_ID_text,beacon_id_text,member_gps_text; 
	private Button btn_member_update,backButton,getBeaconId,btn_get_gps;
	private Switch member_phone_switch;
	private EditText input_member_Name,input_member_phone,old_rewardtext,input_member_addr,input_member_reward;
	private boolean getService = false;     //是否已開啟定位服務
	private LocationManager lms;
	private Location location;
	private String bestProvider = "";
    private UserApplication uapp;
    private ResideMenu resideMenu;
    String longitude ="null";  //取得經度
    String latitude ="null";
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member);
        member_ID_text = (TextView)findViewById(R.id.member_ID_text);
        btn_member_update = (Button)findViewById(R.id.btn_member_update);
        btn_get_gps =(Button)findViewById(R.id.btn_get_gps);
        member_phone_switch = (Switch)findViewById(R.id.member_phone_switch);
        input_member_Name = (EditText)findViewById(R.id.input_member_Name);
        input_member_phone =(EditText)findViewById(R.id.input_member_phone);
        input_member_addr = (EditText)findViewById(R.id.input_member_addr);
        input_member_reward = (EditText)findViewById(R.id.input_member_reward);
        beacon_id_text = (TextView)findViewById(R.id.beacon_id_text);
        member_gps_text =(TextView)findViewById(R.id.member_gps_text);
        getBeaconId = (Button)findViewById(R.id.getBeaconId);
        
//        if(iBeacons == null){
//        	System.out.println("get ibeacons");
//        	int position = 0;
//			iBeacons = new ArrayList<IBeacon>();
//			IBeacon beacon = iBeacons.get(position);
//			beacon_id_text.setText(beacon.getMinor());
//        }
        uapp = (UserApplication)getApplicationContext();
        getMember();
        btn_member_update.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				btn_member_update.setEnabled(false);
				new exeAsync().execute();
			}
        	
        });
        
//        backButton.setOnClickListener(new OnClickListener(){
//			
//			public void onClick(View v){
//				jumpToActivity(MemberActivity.this,MenuActivity.class);	
//		        
//			}
//		});
        btn_get_gps.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v){
				//取得系統定位服務
		        LocationManager status = (LocationManager) (getSystemService(Context.LOCATION_SERVICE));
		        
		        if(status.isProviderEnabled(LocationManager.GPS_PROVIDER)|| status.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		        {
		                          //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
		                          locationServiceInitialGPS();
		        }else{
		            showMessage("請開啟定位服務");
		            getService = true; //確認開啟定位服務
		            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)); //開啟設定頁面
		       }			
			}
		});
        //android.app.Activity
      
          
         
             
         
             
    }

	public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {   
            ConfirmExit();//按返回鍵，則執行退出確認
            return true;   
        }   
        return super.onKeyDown(keyCode, event);   
    }
	
    public void ConfirmExit(){//退出確認
    	//showMessage("...");
    }


	
	private void showMessage(String msg){
		Toast.makeText(MemberActivity.this, msg, Toast.LENGTH_LONG).show();
	}
	
	private class exeAsync extends AsyncTask<Void, Integer, String>
	{
	
	    protected void onPreExecute (){
	    
	    }

	    protected String doInBackground(Void...arg0) {
	    	updateMemberPost(input_member_Name.getText().toString(),input_member_phone.getText().toString(),input_member_addr.getText().toString(),input_member_reward.getText().toString(),member_gps_text.getText().toString());
	    	
	        return null;
	    }
	    protected void onProgressUpdate(Integer... progress) {
            // Set progress percentage
           
        }

	    protected void onPostExecute(String result) {
	 
	    }
	  
	}
	private void getMember(){

		input_member_Name.setText(uapp.getUserName());
        input_member_phone.setText(uapp.getUserPhone());
        input_member_addr.setText(uapp.getUserAddress());
        input_member_reward.setText(uapp.getReward());
        member_gps_text.setText(uapp.getLocation());
	}
	
	/*
	private void getMember(){
        
		StringRequest request = new StringRequest(Request.Method.POST, "https://beacon-series.herokuapp.com/getMember", mResponseListenerGet, mErrorListener){
			protected Map<String, String> getParams() throws AuthFailureError {  
	        Map<String, String> map = new HashMap<String, String>();  
	        map.put("user", uapp.getUser());
	        return map;  
	    }  };
		NetworkManager.getInstance(this).request(null, request);
	}*/
	private void updateMemberPost(String input_member_Name, String input_member_phone, String input_member_addr, String input_member_reward, String gps){
		final String n, p, a, r, g;
		
		n = input_member_Name;
		p = input_member_phone;
		a = input_member_addr;
		r = input_member_reward;
		g = gps;
		StringRequest request = new StringRequest(Request.Method.POST, "https://beacon-series.herokuapp.com/updateMember", mResponseListener, mErrorListener){
			protected Map<String , String> getParams() throws AuthFailureError {  
	        Map<String, String> map = new HashMap<String, String>();
	        map.put("user", uapp.getUser());
	        map.put("userName", n);  
	        map.put("userPhone", p);  
	        map.put("userAddress", a);
	        map.put("reward", r);
	        map.put("location", g);
	        map.put("longitude", longitude);
	        map.put("latitude", latitude);
	        return map;  
	    }  };
		NetworkManager.getInstance(this).request(null, request);
	}
	private Listener<String> mResponseListener = new Listener<String>() {

		@Override
		public void onResponse(String str) {
			
			if(str.trim().equals("ok")){
				showMessage("修改成功");
				uapp.setAllMemberDetail();
				btn_member_update.setEnabled(true);
				
			}
			Log.d("Response", str);
		}
	};
	/*getMember
	private Listener<String> mResponseListenerGet = new Listener<String>() {
		@Override
		public void onResponse(String str) {
			if(str.trim().equals("no detail")){
				System.out.println("no detail");
			}else{
				
			
			try {
				JSONArray ary = new JSONArray(str);
				System.out.println("ary.length = "+ary.length());
				
				StringBuilder userName = new StringBuilder();
				StringBuilder userPhone = new StringBuilder();
				StringBuilder userAddress = new StringBuilder();
				StringBuilder reward = new StringBuilder();
				StringBuilder gps = new StringBuilder();
				for (int i = 0; i < ary.length(); i++) {
					JSONObject json = ary.getJSONObject(i);
					if(!json.getString("detail").isEmpty()){
					
						String detail = json.getString("detail");
						System.out.println("detail="+detail);
						//detail is a object incloud 3 item
						JSONObject detailJson = new JSONObject(detail);
						String _userName = detailJson.getString("userName");
						userName.append(_userName);
						String _userPhone = detailJson.getString("userPhone");
						userPhone.append(_userPhone);
						String _userAddress = detailJson.getString("userAddress");
						userAddress.append(_userAddress);
						String _reward = detailJson.getString("reward");
						reward.append(_reward);
						String _gps = detailJson.getString("location");
						gps.append(_gps);
						//show on comp
						input_member_Name.setText(userName);
				        input_member_phone.setText(userPhone);
				        input_member_addr.setText(userAddress);
				        input_member_reward.setText(reward);
				        member_gps_text.setText(gps);
					}else{
						input_member_Name.setText("");
				        input_member_phone.setText("");
				        input_member_addr.setText("");
				        input_member_reward.setText("");
				        member_gps_text.setText("");
					}
					
				}
				
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			}
			
		}
	};
	*/
	private ErrorListener mErrorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e("Error", error.toString());
		}
	};
	@Override

	protected void onDestroy() {
		super.onDestroy();
		
	
	}
	
	private void jumpToActivity(Context ct,Class<?> lt){
	
        Intent intent = new Intent();
        intent.setClass(ct, lt);
		//startActivity(intent);
		startActivityForResult(intent,0);
	
	}
	private void locationServiceInitialGPS() {
        lms = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //取得系統定位服務
        
        // 做法二,由Criteria物件判斷提供最準確的資訊
        Criteria criteria = new Criteria();  //資訊提供者選取標準
        bestProvider = lms.getBestProvider(criteria, false);    //選擇精準度最高的提供者
        Location location = lms.getLastKnownLocation(bestProvider);
        getLocationGPS(location);
    }
	private void locationServiceInitialWifi() {
		System.out.println("gps is null change wifi");
        lms = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //取得系統定位服務
        /*//做法一,由程式判斷用GPS_provider
        if ( lms.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ) {
            location = lms.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);  //使用wifi定位座標
        }
        else if (lms.isProviderEnabled(LocationManager.GPS_PROVIDER))
        { 
        	location = lms.getLastKnownLocation(LocationManager.GPS_PROVIDER); //使用gps定位座標
        }*/
        Location location = lms.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);  //使用wifi定位座標
        getLocationWifi(location);
    }
    
    private void getLocationGPS(Location location) { //將定位資訊顯示在畫面中
    	
        if(location != null) {
             longitude =String.valueOf( location.getLongitude());   //取得經度
             latitude = String.valueOf(location.getLatitude());     //取得緯度
             String ll = longitude+","+latitude;
             member_gps_text.setText(ll);         
             System.out.println(ll);
        }
        else {
        	//Toast.makeText(this, "無法定位座標", Toast.LENGTH_LONG).show();
        	locationServiceInitialWifi();        		
        }
    }
    private void getLocationWifi(Location location) { //將定位資訊顯示在畫面中
    	
        if(location != null) {
        	longitude =String.valueOf( location.getLongitude());   //取得經度
            latitude = String.valueOf(location.getLatitude());     //取得緯度
            String ll = longitude+","+latitude;
             member_gps_text.setText(ll);     
             System.out.println(ll);
        }
        else {
	         Toast.makeText(this, "無法定位座標", Toast.LENGTH_LONG).show();
	         
        }
    }
	@Override
    public void onLocationChanged(Location location) {  //當地點改變時
        // TODO 自動產生的方法 Stub
		getLocationGPS(location);
    }
    @Override
    public void onProviderDisabled(String arg0) {//當GPS或網路定位功能關閉時
        // TODO 自動產生的方法 Stub
        Toast.makeText(this, "請開啟gps或3G網路", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onProviderEnabled(String arg0) { //當GPS或網路定位功能開啟
        // TODO 自動產生的方法 Stub
    }
    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) { //定位狀態改變
        // TODO 自動產生的方法 Stub
    }
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if(getService) {
             lms.requestLocationUpdates(bestProvider, 60000, 1, this);
             //服務提供者、更新頻率60000毫秒=1分鐘、最短距離、地點改變時呼叫物件
        }
    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if(getService) {
             lms.removeUpdates(this);   //離開頁面時停止更新
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.track_uncle, menu);
        return true;
    }
  
	
}
