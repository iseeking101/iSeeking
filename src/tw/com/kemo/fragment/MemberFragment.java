package tw.com.kemo.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.special.ResideMenu.ResideMenu;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import tw.com.kemo.activity.MemberActivity;
import tw.com.kemo.activity.MenuActivity;
import tw.com.kemo.activity.R;
import tw.com.kemo.activity.UserApplication;

import tw.com.kemo.networkcommunication.volleymgr.NetworkManager;
import tw.com.kemo.util.ListViewAdpater;
import tw.com.kemo.util.nouser_MemberListViewItem;
import tw.com.kemo.util.nouse_MemberSittingAdpater;
import tw.com.kemo.util.MissingOldAdpater;
import tw.com.kemo.util.MissingOldItem;
import tw.com.kemo.util.MyFollowItem;
import tw.com.kemo.util.MyOldManAdpater;
import tw.com.kemo.util.MyOldManItem;

/**
 * 個人資料修改畫面
 */
public class MemberFragment extends Fragment implements LocationListener{
	
	//
	private TextView member_ID_text,beacon_id_text,member_gps_text,member_gps; 
	private Button btn_member_update,backButton,getBeaconId,btn_get_gps;
	private Switch member_phone_switch;
	private EditText input_member_Name, input_member_phone,old_rewardtext,input_member_addr,input_member_reward;
	private boolean getService = false;     //是否已開啟定位服務
	private LocationManager lms;
	private Location location;
	private String bestProvider = "";
    private UserApplication uapp;
    private View parentView;
    String longitude ="null";  //取得經度
    String latitude ="null";
    NetworkManager nm;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	parentView = inflater.inflate(R.layout.member_sitting, container, false);
    	nm= NetworkManager.getInstance(getActivity());
        
    	uapp = (UserApplication)getActivity().getApplicationContext();
    	setUpViews(parentView);
    	return parentView;
         
    }
    private void setUpViews(View parentView) {
         
        member_ID_text = (TextView)parentView.findViewById(R.id.member_ID_text);
        btn_member_update = (Button)parentView.findViewById(R.id.btn_member_update);
        btn_get_gps =(Button)parentView.findViewById(R.id.btn_get_gps);
        member_phone_switch = (Switch)parentView.findViewById(R.id.member_phone_switch);
        input_member_Name = (EditText)parentView.findViewById(R.id.input_member_Name);
        input_member_phone =(EditText)parentView.findViewById(R.id.input_member_phone);
        input_member_addr = (EditText)parentView.findViewById(R.id.input_member_addr);
        input_member_reward = (EditText)parentView.findViewById(R.id.input_member_reward);
        beacon_id_text = (TextView)parentView.findViewById(R.id.beacon_id_text);
        member_gps_text =(TextView)parentView.findViewById(R.id.member_gps_text);
        getBeaconId = (Button)parentView.findViewById(R.id.getBeaconId);
        member_gps = (TextView)parentView.findViewById(R.id.member_gps);
        member_gps_text.setVisibility(member_gps_text.GONE);
        btn_get_gps.setVisibility(btn_get_gps.GONE);
        member_gps.setVisibility(member_gps.GONE);
        getMember();
        btn_member_update.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updateMemberPost(input_member_Name.getText().toString(),input_member_phone.getText().toString(),input_member_addr.getText().toString(),input_member_reward.getText().toString(),member_gps_text.getText().toString());
			}
        	
        });
      
//        btn_get_gps.setOnClickListener(new OnClickListener(){
//        	MenuActivity parentActivity = (MenuActivity) getActivity();
//			public void onClick(View v){
//				//取得系統定位服務
//		        LocationManager status = (LocationManager) (parentActivity.getSystemService(Context.LOCATION_SERVICE));
//		        
//		        if(status.isProviderEnabled(LocationManager.GPS_PROVIDER)|| status.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
//		        {
//		                          //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
//		                          locationServiceInitialGPS();
//		        }else{
//		            showMessage("請開啟定位服務");
//		            getService = true; //確認開啟定位服務
//		            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)); //開啟設定頁面
//		       }			
//			}
//		});
//        parentView.findViewById(R.id.btn_open_menu).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
//            }
//        });
//       
        //add gesture operation's ignored views
        //FrameLayout ignored_view = (FrameLayout) parentView.findViewById(R.id.ignored_view);
        //resideMenu.addIgnoredView(ignored_view);
    }
   
    private void getMember(){

		input_member_Name.setText(uapp.getUserName());
        input_member_phone.setText(uapp.getUserPhone());
        input_member_addr.setText(uapp.getUserAddress());
        input_member_reward.setText(uapp.getReward());
        member_gps_text.setText(uapp.getLocation());
	}
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
	    System.out.println("165getLoaclClassame  = "+ getActivity().getLocalClassName().toString());
		nm.request(null, request);
	}
	private Listener<String> mResponseListener = new Listener<String>() {

		@Override
		public void onResponse(String str) {
			
			if(str.trim().equals("ok")){
				showMessage("儲存成功");
				uapp.setAllMemberDetail();
				
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
		MenuActivity parentActivity = (MenuActivity) getActivity();
        lms = (LocationManager) uapp.getContext().getSystemService(uapp.getContext().LOCATION_SERVICE); //取得系統定位服務
        
        // 做法二,由Criteria物件判斷提供最準確的資訊
        Criteria criteria = new Criteria();  //資訊提供者選取標準
        bestProvider = lms.getBestProvider(criteria, false);    //選擇精準度最高的提供者
        Location location = lms.getLastKnownLocation(bestProvider);
        getLocationGPS(location);
    }
	private void locationServiceInitialWifi() {
		MenuActivity parentActivity = (MenuActivity) getActivity();
		System.out.println("gps is null change wifi");
        lms = (LocationManager) parentActivity.getSystemService(Context.LOCATION_SERVICE); //取得系統定位服務
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
	         Toast.makeText(getActivity(), "無法定位座標", Toast.LENGTH_LONG).show();
	         
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
        Toast.makeText(getActivity(), "請開啟gps或3G網路", Toast.LENGTH_LONG).show();
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
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if(getService) {
             lms.requestLocationUpdates(bestProvider, 1000, 1, this);
             //服務提供者、更新頻率60000毫秒=1分鐘、最短距離、地點改變時呼叫物件
        }
    }
    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if(getService) {
             lms.removeUpdates(this);   //離開頁面時停止更新
        }
    }
	@Override

	public void onDestroy() {
		super.onDestroy();
		
	}
	public void onStart(){
		super.onStart();
	}
    private void showMessage(String msg){
		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
	}
    

}
