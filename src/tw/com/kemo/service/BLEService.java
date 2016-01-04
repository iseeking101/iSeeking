package tw.com.kemo.service;



import com.hereapps.ibeacon.IBeacon;
import com.hereapps.ibeacon.IBeaconLibrary;
import com.hereapps.ibeacon.IBeaconListener;


import tw.com.kemo.activity.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class BLEService extends Service implements IBeaconListener{

	
	private IBeaconLibrary iBeaconLibrary;
	
	//private static int 
	Handler handler=new Handler();
	Runnable runnableStartScan = new Runnable(){ public void run() {
		iBeaconLibrary.startScan();	
	}};

	private IBeaconListener activityListener; 
	
	private MyBinder myBinder = new MyBinder();
	
	public static final String ACTION = "tw.com.kemo.service.BLEService";
	
	private Notification mNotification;
	private NotificationManager mManager;
	
	@Override
	public void onCreate() {
		//initNotifiManager();
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
//		handler.removeCallbacks(runnableStartScan);
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
			iBeaconLibrary.stopScan();
			
			
		}
		
		
	}
	//====================================
	private void scanBeacons(){
		//Log.i("felix","Scanning");
		//Log.i("felix","iBeaconLibrary.isScanning()="+iBeaconLibrary.isScanning());
		
		if(iBeaconLibrary.isScanning()){
			iBeaconLibrary.stopScan();
			System.out.println("beacon isScanning reset to scanStart");
			iBeaconLibrary.reset();
			iBeaconLibrary.startScan();	
		}else{
			System.out.println("scanBeacon startScan");
			iBeaconLibrary.startScan();	
	
		}
				
	}
	
	//=====================================
	@Override
	public void beaconEnter(IBeacon arg0) {
		//Log.i("felix","beaconEnter");
		if(activityListener!=null)activityListener.beaconEnter(arg0);
		
	}
	@Override
	public void beaconExit(IBeacon arg0) {
		//Log.i("felix","beaconExit");
		if(activityListener!=null){
			activityListener.beaconExit(arg0);
		}
		
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
	private void initNotifiManager() {
		mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		int icon = R.drawable.old;
		mNotification = new Notification.Builder(getApplicationContext())
				.setWhen(System.currentTimeMillis())
				.setDefaults(Notification.DEFAULT_SOUND)
				.setTicker("New Message")
				.setSmallIcon(icon)
				.setContentTitle("testing")
				.setContentText("tesing").build();
		
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
	}
	private void showNotification() {
		//mNotification.when = System.currentTimeMillis();
		//Navigator to the new activity when click the notification title
//		Intent i = new Intent(this, MessageActivity.class);
//		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i,
//				Intent.FLAG_ACTIVITY_NEW_TASK);
		
//		mNotification.setLatestEventInfo(this,
//				getResources().getString(R.string.app_name), "You have new message!", pendingIntent);
		System.out.println("success");
		mManager.notify(0, mNotification);
	}
}
