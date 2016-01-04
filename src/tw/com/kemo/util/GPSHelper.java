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
		System.out.println("�i�Jgpshelper");
		this.context=context;
        lms = (LocationManager) context.getSystemService(context.LOCATION_SERVICE); //���o�t�Ωw��A��
        locationServiceInitialGPS();
	}
	public void locationServiceInitialGPS() {
		
        // ���k�G,��Criteria����P�_���ѳ̷ǽT����T
        Criteria criteria = new Criteria();  //��T���Ѫ̿���з�
        bestProvider = lms.getBestProvider(criteria, false);    //��ܺ�ǫ׳̰������Ѫ�
        Location location = lms.getLastKnownLocation(bestProvider);
        getLocationGPS(location,context);
    }
	public void locationServiceInitialWifi(Context context) {
		
		System.out.println("gps is null change wifi");
		 lms = (LocationManager) context.getSystemService(context.LOCATION_SERVICE); //���o�t�Ωw��A��
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
    
    private void getLocationGPS(Location location,Context context) { //�N�w���T��ܦb�e����
    	
        if(location != null) {
        	System.out.println(String.valueOf( location.getLongitude())+ String.valueOf(location.getLatitude()));
            gps.add(String.valueOf( location.getLongitude()))  ;
            gps.add( String.valueOf(location.getLatitude()));
        }
        else {
        	//Toast.makeText(this, "�L�k�w��y��", Toast.LENGTH_LONG).show();
        	locationServiceInitialWifi(context);        		
        }
       
    }
    private void getLocationWifi(Location location) { //�N�w���T��ܦb�e����
    	
        if(location != null) {
        	System.out.println(String.valueOf( location.getLongitude())+ String.valueOf(location.getLatitude()));
        	gps.add(String.valueOf( location.getLongitude()))  ;
            gps.add( String.valueOf(location.getLatitude()));
        }
        else {
	        Log.d("nogps", "�L�k�w��y��");
	         
        }
       
    }
    public ArrayList<String> getGPS(){
    	return gps;
    }
    public String doubleFormat(String str){
		System.out.println("�g�n�� = "+ str );
		if(str.equals("null") ||  str== null){
			return "0.0";
		}
		System.out.println("double = "+str);
		return str;
	}@Override
    public void onLocationChanged(Location location) {  //��a�I���ܮ�
        // TODO �۰ʲ��ͪ���k Stub
		getLocationGPS(location,context);
    }
    @Override
    public void onProviderDisabled(String arg0) {//��GPS�κ����w��\��������
        // TODO �۰ʲ��ͪ���k Stub
        
    }
    @Override
    public void onProviderEnabled(String arg0) { //��GPS�κ����w��\��}��
        // TODO �۰ʲ��ͪ���k Stub
        
    }
    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) { //�w�쪬�A����
        // TODO �۰ʲ��ͪ���k Stub
    }
    
}
