package tw.com.kemo.service;



import java.util.ArrayList;

import com.hereapps.ibeacon.IBeacon;
import com.hereapps.ibeacon.IBeaconLibrary;
import com.hereapps.ibeacon.IBeaconListener;

import tw.com.kemo.activity.IndexActivity;
import tw.com.kemo.activity.R;
import tw.com.kemo.activity.UserApplication;
import tw.com.kemo.util.MyOldManItem;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class BLEService2 extends Service  implements IBeaconListener{

	private IBeaconLibrary iBeaconLibrary;
	private static ArrayList<IBeacon> iBeacons;
	//private static int 
	int icon = R.drawable.old;
	
	private IBeaconListener activityListener; 
	
	private MyBinder myBinder = new MyBinder();
	Handler handler=new Handler();
	Runnable runnableStartScan = new Runnable(){ public void run() {
		myOldManExit = uapp.getMyOldManList();
		myOldManEnter = uapp.getMyOldManList();
		
		iBeaconLibrary.startScan();	
	}};
	ArrayList<MyOldManItem> myOldManExit;
	ArrayList<MyOldManItem> myOldManEnter;
	
	public static final String ACTION = "tw.com.kemo.service.BLEService2";
	private int count = 0 ;
	private Notification NotificationExit,NotificationFound;
	private NotificationManager mManager;
	private UserApplication uapp;
	PendingIntent pendingIntent;
	@Override
	public void onCreate() {
		//application 要先， 因為notifimanager裡面有用到參數
		if(iBeacons == null)
			iBeacons = new ArrayList<IBeacon>();
		uapp = (UserApplication) getApplicationContext();
		myOldManExit = uapp.getMyOldManList();
		myOldManEnter = uapp.getMyOldManList();
		
		pendingIntent = initNotifPending();
		System.out.println("service onCreate");
		
	}
	@Override
	public void onStart(Intent intent, int startId) {
		//Log.e("felix", "onStart:");
		// handler.postDelayed(showTime, 1000);
		super.onStart(intent, startId);
		System.out.println("service onStrat");
	}

	@Override
	public void onDestroy() {
		//Log.e("felix", "onDestroy:");
		// handler.removeCallbacks(showTime);
		super.onDestroy();
		
		System.out.println("service onDestroy");
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		//Log.e("felix", "onBind:");
		iBeaconLibrary = IBeaconLibrary.getInstance();
		iBeaconLibrary.setBluetoothAdapter(this);
		//timeout for last seen beacon;
		IBeaconLibrary.SCANNING_TIMEOUT=5000;
		iBeaconLibrary.setListener(this);
		
		return myBinder;
	}
	
	
	
	public class MyBinder extends Binder {
		
		public void setListener(IBeaconListener listener){
			activityListener = listener;
		}
		public void setScanPeriod(int period){
			IBeaconLibrary.SCANNING_TIMEOUT=period;
		}
		public void startScan(){
			scanBeacons();
			System.out.println("from mybinder to scanbeacons");
		}
		public void stopScan(){
			stopScanCheck();
		}
		
	}
	//====================================
	private void scanBeacons(){
		//Log.i("felix","Scanning");
		//Log.i("felix","iBeaconLibrary.isScanning()="+iBeaconLibrary.isScanning());
		try{
		System.out.println("ibeaconlibrary == null");
		if(iBeaconLibrary.isScanning()){
			iBeaconLibrary.stopScan();
			System.out.println("beacon isScanning reset to scanStart");
			iBeaconLibrary.reset();
			iBeaconLibrary.startScan();	
		}else{
			System.out.println("scanBeacon startScan");
			iBeaconLibrary.startScan();	
	
		}
		}catch(Exception e){
			e.getMessage();
		}
				
	}
	private void stopScanCheck(){
		if(iBeaconLibrary.isScanning()){
			iBeaconLibrary.stopScan();
			
		}else{
				
		}
	}
	
	//=====================================
	@Override
	public void beaconEnter(IBeacon arg0) {
		//Log.i("felix","beaconEnter");
		if(activityListener!=null)activityListener.beaconEnter(arg0);
		
		count ++;
			iBeacons.add(arg0);
			System.out.println("beacon is back~");
			
		String _beaconId = "";
		
		//判斷找到的beacon是不是自己的
		System.out.println("beacon size"+iBeacons.size());
		for(int i = 0 ; i < iBeacons.size();i++){
			
			_beaconId = iBeacons.get(i).getUuidHexStringDashed()+iBeacons.get(i).getMajor()+iBeacons.get(i).getMinor();
			for(int j = 0; j < myOldManEnter.size();j++){
				
				if(_beaconId.equals(myOldManEnter.get(j).getBeaconId())){
					System.out.println("比對符合");
					System.out.println(_beaconId+" = "+myOldManEnter.get(j).getBeaconId());
					showNotificationFound(myOldManEnter.get(j).getOldName());
					myOldManEnter.remove(j);
				}else{
					System.out.println(_beaconId+" X "+myOldManEnter.get(j).getBeaconId());
					
				}
			}
			
		}
		iBeacons.clear();
		
		
	}
	@Override
	public void beaconExit(IBeacon arg0) {
		//Log.i("felix","beaconExit");
		System.out.println("beacon is exit");
		activityListener.beaconExit(arg0);
			iBeacons.add(arg0);
			
		String _beaconId = "";
		
		
			count++;
			//判斷找到的beacon是不是自己的
			for(int i = 0 ; i < iBeacons.size();i++){
				_beaconId = iBeacons.get(i).getUuidHexStringDashed()+iBeacons.get(i).getMajor()+iBeacons.get(i).getMinor();
				for(int j = 0; j < myOldManExit.size();j++){
					if(_beaconId.equals(myOldManExit.get(j).getBeaconId())){
						System.out.println("比對符合");
						System.out.println(_beaconId+" = "+myOldManExit.get(j).getBeaconId());
						
						myOldManExit.remove(j);
						showNotificationExit(myOldManExit.get(j).getOldName());
					}else{
						System.out.println(_beaconId+" X "+uapp.getMyOldManList().get(j).getBeaconId());
						
					}
				}
				
			}
		
		
		iBeacons.clear();
		
		
		
	}
	@Override
	public void beaconFound(IBeacon arg0) {
		//Log.i("felix","beaconFound");
		
		if(activityListener!=null)activityListener.beaconFound(arg0);
		
		
		
	}
	@Override
	public void operationError(int arg0) {
		//Log.i("felix","operationError");
		if(activityListener!=null)activityListener.operationError(arg0);
		
	}
	@Override
	public void scanState(int state) {
		//Log.i("felix","scanState");
		//if(activityListener!=null)activityListener.scanState(arg0);
		switch(state){
		case IBeaconLibrary.SCAN_STARTED:
			//Log.i("felix", "state= SCAN_STARTED");	
			break;
		case IBeaconLibrary.SCAN_END_SUCCESS:
			//Log.i("felix", "state= SCAN_END_SUCCESS");
			//here you can scan again.
//			new Handler().postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					System.out.println("SCAN_END_SUCCESS startScan");
//					iBeaconLibrary.startScan();	
//				}				
//			},5000);//wait 1000ms
			handler.postDelayed(runnableStartScan, 5000);
			break;
		case IBeaconLibrary.SCAN_END_EMPTY:
			//Log.i("felix", "state= SCAN_END_EMPTY");	
			//here you can scan again.
//			new Handler().postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					System.out.println("SCAN_END_EMPTY startScan");
//					iBeaconLibrary.startScan();	
//				}				
//			},5000);//wait 1000ms
			handler.postDelayed(runnableStartScan, 5000);
			break;
		
		}
		
	}
	//=============================================
	//點通知時預定要開啟的activity
	private PendingIntent initNotifPending(){
		mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		int icon = R.drawable.old;
		
		Intent i = new Intent(this, IndexActivity.class);
		Bundle b = new Bundle();
		b.putString("user", uapp.getUser());
		b.putString("beckFromNotification","1");
		i.putExtras(b);
		//原本用 參數4用0 不能傳bundle 改用 Intent.Flag_ACTIVITY_NEW_TASK後可以傳
		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, i,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		return pendingIntent;
	}
	private PendingIntent initNotifiManager() {
		mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		Intent i = new Intent(this, IndexActivity.class);
		Bundle b = new Bundle();
		b.putString("user", uapp.getUser());
		b.putString("beckFromNotification","1");
		i.putExtras(b);
		//原本用 參數4用0 不能傳bundle 改用 Intent.Flag_ACTIVITY_NEW_TASK後可以傳
		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, i,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		return pendingIntent;
		
	}
	//離開範圍時彈出通知
	private void showNotificationExit(String oldName) {
		//mNotification.when = System.currentTimeMillis();
		//Navigator to the new activity when click the notification title
		
		NotificationExit = new Notification.Builder(getApplicationContext())
				.setWhen(System.currentTimeMillis())
				.setDefaults(Notification.DEFAULT_SOUND)
				.setTicker("New Message")
				.setSmallIcon(icon)
				.setContentTitle("iSeeking 通知:")
				.setContentIntent(pendingIntent)
				.setContentText(oldName +" 超出範圍 請注意").build();
		NotificationExit.flags = Notification.FLAG_AUTO_CANCEL;	
		
		mManager.notify(0, NotificationExit);
	}
	private void showNotificationFound(String oldName) {
		//mNotification.when = System.currentTimeMillis();
		//Navigator to the new activity when click the notification title
		
		NotificationFound = new Notification.Builder(getApplicationContext())
				.setWhen(System.currentTimeMillis())
				.setDefaults(Notification.DEFAULT_SOUND)
				.setTicker("New Message")
				.setSmallIcon(icon)
				.setContentTitle("iSeeking 通知:")
				.setContentIntent(pendingIntent)
				.setContentText(oldName +" 回到範圍").build();
		NotificationFound.flags = Notification.FLAG_AUTO_CANCEL;	
		mManager.notify(1, NotificationFound);
	}
}
