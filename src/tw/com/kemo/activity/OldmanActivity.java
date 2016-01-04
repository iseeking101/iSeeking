package tw.com.kemo.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import tw.com.kemo.networkcommunication.volleymgr.NetworkManager;
import tw.com.kemo.util.GPSHelper;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * 我的老人資訊頁 
*/ 
public class OldmanActivity extends Activity {
	
	private TextView beacon_id_text; 
	private Button old_update,old_back,getBeaconId,groupManaber,btn_missing;
	private Switch old_characteristicButton,old_historyButton,old_addrButton;
	private EditText old_nameText,old_characteristicText,old_historyText,old_clothesText,old_addrText;
	private UserApplication uapp;
	private boolean missingCheck = false;
	

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oldman);
        
        beacon_id_text = (TextView)findViewById(R.id.beacon_id_text);
        old_nameText = (EditText)findViewById(R.id.old_nameText);
        old_characteristicText = (EditText)findViewById(R.id.old_characteristicText);
        old_historyText =  (EditText)findViewById(R.id.old_historyText);
        old_clothesText =  (EditText)findViewById(R.id.old_clothesText);
        old_addrText=  (EditText)findViewById(R.id.old_addrText);
        old_update = (Button)findViewById(R.id.old_update);
        getBeaconId=(Button)findViewById(R.id.getBeaconId);
        groupManaber=(Button)findViewById(R.id.groupManaber);
        btn_missing=(Button)findViewById(R.id.btn_missing);
        uapp = (UserApplication) getApplicationContext();
        getOld();
        if(uapp.getMyOldManList().get(uapp.getIndex()).getStatusv().equals("1")){
        	missingCheck = true;
        	btn_missing.setText("取消通報");
        }else{
        	missingCheck = false;
        }
        btn_missing.setOnClickListener(new OnClickListener(){
        
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(missingCheck == false){
					btn_missing.setEnabled(false);
					pushGCM(old_nameText.getText().toString(),beacon_id_text.getText().toString());
					System.out.println("test ");
					
				}else if(missingCheck ==true ){
					btn_missing.setEnabled(false);
					updateStatus();
				}
			}
        	
        });
        old_update.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				old_update.setEnabled(false);
				
				new exeAsync().execute();
			}
        	
        });
        
        getBeaconId.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		uapp.setServiceCode("2");
        		jumpToBeaconActivity(OldmanActivity.this,BeaconActivity.class);
        	}
        });
        groupManaber.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		if(beacon_id_text.getText() == ""){
        			showMessage("請先為您追蹤的對象設置beaconId");
        		}else{
        			jumpToActivity(OldmanActivity.this,GroupManagerActivity.class);
        		}
        	}
        });
        
        
        
    }
	
	private void pushGCM(String oldName,String beaconId){
		 GPSHelper g = null;
		
    	 LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
    	 if(status.isProviderEnabled(LocationManager.GPS_PROVIDER)|| status.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		        {
		                          //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
    		 		g = new GPSHelper(this);
		        }else{
		            showMessage("請開啟定位服務,或使用googlemap 獲取位置");
		            //startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)); //開啟設定頁面
		 }
    	
    	if(g.getGPS().size()>0 || g != null){
    	System.out.println("g:"+g.getGPS().get(0)+",a : "+g.getGPS().get(1));
		final String oldNamev = oldName;
		final String longitude = g.getGPS().get(0);
		final String latitude = g.getGPS().get(1);
		final String beaconIdv = beaconId;
		System.out.println("經緯度"+g.getGPS().get(0));
		System.out.println("經緯度"+g.getGPS().get(1));
		
		StringRequest request = new StringRequest(Request.Method.POST, "https://beacon-series.herokuapp.com/send", mResPush, mErrorListener){
			protected Map<String, String> getParams() throws AuthFailureError {  
	        Map<String, String> map = new HashMap<String, String>();  
	        map.put("user",uapp.getUser());
	        map.put("oldName", oldNamev);
	        map.put("beaconId", beaconIdv);
	        map.put("longitude", longitude);
	        map.put("latitude", latitude);
	        return map;  
	    }  };
		NetworkManager.getInstance(this).request(null, request);
    	}else{
    		 showMessage("請開啟定位服務,或使用googlemap 獲取位置");
    	}
	}
	private Listener<String> mResPush = new Listener<String>() {
		@Override
		public void onResponse(String str) {
			if(str.trim().equals("ok")){
				showMessage("已通知附近民眾幫忙");
				uapp.clearMissingOldList();
				uapp.setMissingOld();
				uapp.clearMyOldMan();
				uapp.setMyOldMan();
				System.out.println("完成通報程序");
				btn_missing.setText("取消通報");
				btn_missing.setEnabled(true);
				missingCheck = true;
				
			}
			Log.d("Response", str);
		}
	};
	private void updateStatus(){
		StringRequest request = new StringRequest(Request.Method.POST, "https://beacon-series.herokuapp.com/updateStatusv", mResStatus, mErrorListener){
			protected Map<String, String> getParams() throws AuthFailureError {  
	        Map<String, String> map = new HashMap<String, String>();
	        map.put("user",uapp.getUser());
	        map.put("beaconId",beacon_id_text.getText().toString());
	        return map;  
	    }  };
		NetworkManager.getInstance(this).request(null, request);
	}
	private void updateOldPost(String beacon_id_text,String old_nameText, String old_characteristicText, String old_historyText, String old_clothesText,String old_addrText){
		final String n, ch, h, cl, a,bid;
		
		bid = beacon_id_text;
		n = old_nameText;
		ch = old_characteristicText;
		h = old_historyText;
		cl = old_clothesText;
		a= old_addrText;
		StringRequest request = new StringRequest(Request.Method.POST, "https://beacon-series.herokuapp.com/updateOld", mResponseListener, mErrorListener){
			protected Map<String, String> getParams() throws AuthFailureError {  
	        Map<String, String> map = new HashMap<String, String>();
	        map.put("user",uapp.getUser());
	        map.put("beaconId",bid);
	        map.put("oldName", n);  
	        map.put("oldCharacteristic", ch);  
	        map.put("oldhistory", h);
	        map.put("oldclothes", cl);
	        map.put("oldaddr", a);
	        return map;  
	    }  };
		NetworkManager.getInstance(this).request(null, request);
	}
	private Listener<String> mResStatus = new Listener<String>() {

		@Override
		public void onResponse(String str) {
			if(str.trim().equals("ok")){
				showMessage("已找到");
				missingCheck =false ;
				btn_missing.setText("走失通報");
				btn_missing.setEnabled(true);
				uapp.clearMyOldMan();
			    System.out.println("清除myoldmanlist");
				uapp.setMyOldMan();
				 System.out.println("更新oldmanlist");
				uapp.clearMissingOldList();
				uapp.setMissingOld();
			    System.out.println("清除並更新missingold");
				
			}
			Log.d("Response", str);
		}
	};
	private Listener<String> mResponseListener = new Listener<String>() {

		@Override
		public void onResponse(String str) {
			if(str.trim().equals("ok")){
				showMessage("修改成功");
			    uapp.clearMyOldMan();
			    System.out.println("清除myoldmanlist");
				uapp.setMyOldMan();
				 System.out.println("更新oldmanlist");
				uapp.clearMissingOldList();
				uapp.setMissingOld();
				 System.out.println("清除並更新missingold");
				//uapp.getAllOldDetail();
				 old_update.setEnabled(true);
			}
			Log.d("Response", str);
		}
	};
		private ErrorListener mErrorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e("Error", error.toString());
			btn_missing.setEnabled(true);
			
		}
	};
	
	private void showMessage(String msg){
		Toast.makeText(OldmanActivity.this, msg, Toast.LENGTH_LONG).show();
	}
	
	private class exeAsync extends AsyncTask<Void, Integer, String>
	{
	
	    protected void onPreExecute (){
	    
	    }

	    protected String doInBackground(Void...arg0) {
	    	updateOldPost(beacon_id_text.getText().toString(),old_nameText.getText().toString(),old_characteristicText.getText().toString(),old_historyText.getText().toString(),old_clothesText.getText().toString(),old_addrText.getText().toString());
	    	
	        return null;
	    }
	    protected void onProgressUpdate(Integer... progress) {
            // Set progress percentage
           
        }

	    protected void onPostExecute(String result) {
	 
	    }
	  
	}
	
	private void getOld(){
		int index = uapp.getIndex();
		beacon_id_text.setText(uapp.getMyOldManList().get(index).getBeaconId());
		old_nameText.setText(uapp.getMyOldManList().get(index).getOldName());
		old_characteristicText.setText(uapp.getMyOldManList().get(index).getOldCharacteristic());
		old_historyText.setText(uapp.getMyOldManList().get(index).getOldhistory());
		old_clothesText.setText(uapp.getMyOldManList().get(index).getOldClothes());
		old_addrText.setText(uapp.getMyOldManList().get(index).getOldAddr());
	}
	protected void onDestroy() {
		super.onDestroy();
		
		

	}
private void jumpToBeaconActivity(Context ct,Class<?> lt){
		
		
		Bundle bundle = new Bundle();
		bundle.putString("addCheck","add");
        bundle.putString(
                "user", uapp.getUser());
        bundle.putString(
                "oldName", old_nameText.getText().toString());
        bundle.putString(
                "oldCharacteristic", old_characteristicText.getText().toString());
        bundle.putString(
                "oldhistory", old_historyText.getText().toString());
        bundle.putString(
                "oldclothes", old_clothesText.getText().toString());
        bundle.putString(
                "oldaddr", old_addrText.getText().toString());
        Intent intent = new Intent();
        intent.putExtras(bundle);
		intent.setClass(ct, lt);
		//startActivity(intent);
		startActivityForResult(intent,0);
	}
	private void jumpToActivity(Context ct,Class<?> lt){
		
		Intent intent = new Intent();
        intent.setClass(ct, lt);
        
		startActivity(intent);
		//startActivityForResult(intent,0);
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		switch(resultCode){
		case RESULT_OK:
			Bundle bundle = data.getExtras();
			beacon_id_text.setText(bundle.getString("returnBeaconId"));
			break;
		default:
			break;
		
		}
	}
//	public void onResume(){
//		super.onResume();
//	}
	public String stringFormat(String str){
		String strv=str;
		if(strv.equals("null") || strv==null){
			strv="";
		}
		return strv;
	}

	

}
