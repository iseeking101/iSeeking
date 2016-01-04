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
 * �ӤH��ƭק�e��
 */
public class MemberFragment extends Fragment implements LocationListener{
	
	//
	private TextView member_ID_text,beacon_id_text,member_gps_text,member_gps; 
	private Button btn_member_update,backButton,getBeaconId,btn_get_gps;
	private Switch member_phone_switch;
	private EditText input_member_Name, input_member_phone,old_rewardtext,input_member_addr,input_member_reward;
	private boolean getService = false;     //�O�_�w�}�ҩw��A��
	private LocationManager lms;
	private Location location;
	private String bestProvider = "";
    private UserApplication uapp;
    private View parentView;
    String longitude ="null";  //���o�g��
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
//				//���o�t�Ωw��A��
//		        LocationManager status = (LocationManager) (parentActivity.getSystemService(Context.LOCATION_SERVICE));
//		        
//		        if(status.isProviderEnabled(LocationManager.GPS_PROVIDER)|| status.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
//		        {
//		                          //�p�GGPS�κ����w��}�ҡA�I�slocationServiceInitial()��s��m
//		                          locationServiceInitialGPS();
//		        }else{
//		            showMessage("�ж}�ҩw��A��");
//		            getService = true; //�T�{�}�ҩw��A��
//		            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)); //�}�ҳ]�w����
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
				showMessage("�x�s���\");
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
        lms = (LocationManager) uapp.getContext().getSystemService(uapp.getContext().LOCATION_SERVICE); //���o�t�Ωw��A��
        
        // ���k�G,��Criteria����P�_���ѳ̷ǽT����T
        Criteria criteria = new Criteria();  //��T���Ѫ̿���з�
        bestProvider = lms.getBestProvider(criteria, false);    //��ܺ�ǫ׳̰������Ѫ�
        Location location = lms.getLastKnownLocation(bestProvider);
        getLocationGPS(location);
    }
	private void locationServiceInitialWifi() {
		MenuActivity parentActivity = (MenuActivity) getActivity();
		System.out.println("gps is null change wifi");
        lms = (LocationManager) parentActivity.getSystemService(Context.LOCATION_SERVICE); //���o�t�Ωw��A��
        /*//���k�@,�ѵ{���P�_��GPS_provider
        if ( lms.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ) {
            location = lms.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);  //�ϥ�wifi�w��y��
        }
        else if (lms.isProviderEnabled(LocationManager.GPS_PROVIDER))
        { 
        	location = lms.getLastKnownLocation(LocationManager.GPS_PROVIDER); //�ϥ�gps�w��y��
        }*/
        Location location = lms.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);  //�ϥ�wifi�w��y��
        getLocationWifi(location);
    }
    
    private void getLocationGPS(Location location) { //�N�w���T��ܦb�e����
    	
        if(location != null) {
             longitude =String.valueOf( location.getLongitude());   //���o�g��
             latitude = String.valueOf(location.getLatitude());     //���o�n��
             String ll = longitude+","+latitude;
             member_gps_text.setText(ll);     
             System.out.println(ll);
        }
        else {
        	//Toast.makeText(this, "�L�k�w��y��", Toast.LENGTH_LONG).show();
        	locationServiceInitialWifi();        		
        }
    }
    private void getLocationWifi(Location location) { //�N�w���T��ܦb�e����
    	
        if(location != null) {
        	longitude =String.valueOf( location.getLongitude());   //���o�g��
            latitude = String.valueOf(location.getLatitude());     //���o�n��
            String ll = longitude+","+latitude;
             member_gps_text.setText(ll);  
             System.out.println(ll);
        }
        else {
	         Toast.makeText(getActivity(), "�L�k�w��y��", Toast.LENGTH_LONG).show();
	         
        }
    }
	@Override
    public void onLocationChanged(Location location) {  //��a�I���ܮ�
        // TODO �۰ʲ��ͪ���k Stub
		getLocationGPS(location);
    }
    @Override
    public void onProviderDisabled(String arg0) {//��GPS�κ����w��\��������
        // TODO �۰ʲ��ͪ���k Stub
        Toast.makeText(getActivity(), "�ж}��gps��3G����", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onProviderEnabled(String arg0) { //��GPS�κ����w��\��}��
        // TODO �۰ʲ��ͪ���k Stub
    }
    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) { //�w�쪬�A����
        // TODO �۰ʲ��ͪ���k Stub
    }
    
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if(getService) {
             lms.requestLocationUpdates(bestProvider, 1000, 1, this);
             //�A�ȴ��Ѫ̡B��s�W�v60000�@��=1�����B�̵u�Z���B�a�I���ܮɩI�s����
        }
    }
    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if(getService) {
             lms.removeUpdates(this);   //���}�����ɰ����s
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
