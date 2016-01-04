package tw.com.kemo.util;

import java.util.ArrayList;

import android.app.Application;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import tw.com.kemo.activity.MenuActivity;
import tw.com.kemo.activity.UserApplication;

public class GPSHelper implements LocationListener  {
	UserApplication uapp =  (UserApplication)new UserApplication();
	Context c = (UserApplication) new UserApplication().getContext();
	private LocationManager lms;
	private Location location;
	private String bestProvider = "";
	private ArrayList<String> gps = new ArrayList<String>();
	Context context ;
	public GPSHelper(Context context){
		this.context = context;
		System.out.println("進入gpshelper");
		this.context=context;
        lms = (LocationManager) context.getSystemService(context.LOCATION_SERVICE); //取得系統定位服務
        locationServiceInitialGPS();
	}
	public void locationServiceInitialGPS() {
		
        // 做法二,由Criteria物件判斷提供最準確的資訊
        Criteria criteria = new Criteria();  //資訊提供者選取標準
        bestProvider = lms.getBestProvider(criteria, false);    //選擇精準度最高的提供者
        Location location = lms.getLastKnownLocation(bestProvider);
        getLocationGPS(location,context);
    }
	public void locationServiceInitialWifi(Context context) {
		
		System.out.println("gps is null change wifi");
		 lms = (LocationManager) context.getSystemService(context.LOCATION_SERVICE); //取得系統定位服務
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
    
    private void getLocationGPS(Location location,Context context) { //將定位資訊顯示在畫面中
    	
        if(location != null) {
        	System.out.println(String.valueOf( location.getLongitude())+ String.valueOf(location.getLatitude()));
            gps.add(String.valueOf( location.getLongitude()))  ;
            gps.add( String.valueOf(location.getLatitude()));
        }
        else {
        	//Toast.makeText(this, "無法定位座標", Toast.LENGTH_LONG).show();
        	locationServiceInitialWifi(context);        		
        }
       
    }
    private void getLocationWifi(Location location) { //將定位資訊顯示在畫面中
    	
        if(location != null) {
        	System.out.println(String.valueOf( location.getLongitude())+ String.valueOf(location.getLatitude()));
        	gps.add(String.valueOf( location.getLongitude()))  ;
            gps.add( String.valueOf(location.getLatitude()));
        }
        else {
	        Log.d("nogps", "無法定位座標");
	         
        }
       
    }
    public ArrayList<String> getGPS(){
    	return gps;
    }
    public String doubleFormat(String str){
		System.out.println("經緯度 = "+ str );
		if(str.equals("null") ||  str== null){
			return "0.0";
		}
		System.out.println("double = "+str);
		return str;
	}@Override
    public void onLocationChanged(Location location) {  //當地點改變時
        // TODO 自動產生的方法 Stub
		getLocationGPS(location,context);
    }
    @Override
    public void onProviderDisabled(String arg0) {//當GPS或網路定位功能關閉時
        // TODO 自動產生的方法 Stub
        
    }
    @Override
    public void onProviderEnabled(String arg0) { //當GPS或網路定位功能開啟
        // TODO 自動產生的方法 Stub
        
    }
    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) { //定位狀態改變
        // TODO 自動產生的方法 Stub
    }
    
}
