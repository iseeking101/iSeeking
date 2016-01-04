package tw.com.kemo.service;

import java.util.ArrayList;

import com.hereapps.ibeacon.IBeacon;
import com.hereapps.ibeacon.IBeaconLibrary;
import com.hereapps.ibeacon.IBeaconListener;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;
import tw.com.kemo.activity.IndexActivity;
import tw.com.kemo.activity.R;
import tw.com.kemo.activity.UserApplication;
import tw.com.kemo.util.MyOldManItem;
/**
 * Polling service
 * @Author Ryan
 * @Create 2013-7-13 銝��10:18:44
 */
public class ScanService extends Service implements IBeaconListener{

	public static final String ACTION = "tw.com.kemo.service.ScanService";
	
	private static final int REQUEST_BLUETOOTH_ENABLE = 1;	
	private int count = 0 ;
	int icon = R.drawable.old;
	
	private static ArrayList<IBeacon> iBeacons;
	private static IBeaconLibrary iBeaconLibrary;
	private Notification NotificationExit,NotificationFound;
	private NotificationManager mManager;
	private UserApplication uapp;
	PendingIntent pendingIntent;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
//		initNotifiManager();
		System.out.println("onCreate");
		uapp = (UserApplication) getApplicationContext();
		
		if(iBeacons == null)
			iBeacons = new ArrayList<IBeacon>();
		iBeaconLibrary = IBeaconLibrary.getInstance();
		iBeaconLibrary.setBluetoothAdapter(this);
		
		iBeaconLibrary.setListener(this);
		pendingIntent = initNotifPending();
		
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		System.out.println("onStart");
		scanBeacons();
	}
//
//	private void initNotifiManager() {
//		mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//		int icon = R.drawable.ic_launcher;
//		mNotification = new Notification();
//		mNotification.icon = icon;
//		mNotification.tickerText = "New Message";
//		mNotification.defaults |= Notification.DEFAULT_SOUND;
//		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
//	}
//
//	private void showNotification() {
//		mNotification.when = System.currentTimeMillis();
//		//Navigator to the new activity when click the notification title
//		Intent i = new Intent(this, MessageActivity.class);
//		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i,
//				Intent.FLAG_ACTIVITY_NEW_TASK);
//		mNotification.setLatestEventInfo(this,
//				getResources().getString(R.string.app_name), "You have new message!", pendingIntent);
//		mManager.notify(0, mNotification);
//	}

//	/**
//	 * Polling thread
//	 * @Author Ryan
//	 * @Create 2013-7-13 銝��10:18:34
//	 */
//	int count = 0;
//	class PollingThread extends Thread {
//		@Override
//		public void run() {
//			System.out.println("Polling...");
//			count ++;
//			if (count % 5 == 0) {
//				showNotification();
//				System.out.println("New message!");
//			}
//		}
//	}
	private void scanBeacons(){
		Log.i(IBeaconLibrary.LOG_TAG,"Scanning");

			if(iBeaconLibrary.isScanning())
				iBeaconLibrary.stopScan();
			iBeaconLibrary.reset();
			iBeaconLibrary.startScan();		
			
	}
	@Override
	public void beaconFound(IBeacon ibeacon) {
		iBeacons.add(ibeacon);
		Log.d("alarmBeacon","Found beacon :"+ ibeacon);
		System.out.println("Found beacon :"+ibeacon);
		
	}
	
	@Override
	public void beaconEnter(IBeacon arg0) {
		// TODO Auto-generated method stub
		System.out.println("Enter beacon:"+arg0);
			iBeacons = new ArrayList<IBeacon>();
			iBeacons.add(arg0);
		
		
		String _beaconId = "";
		ArrayList<MyOldManItem> myOldMan = uapp.getMyOldManList();
		
		//判斷找到的beacon是不是自己的
		System.out.println("beacon size"+iBeacons.size());
		for(int i = 0 ; i < iBeacons.size();i++){
			
			_beaconId = iBeacons.get(i).getUuidHexStringDashed()+iBeacons.get(i).getMajor()+iBeacons.get(i).getMinor();
			System.out.println("搜尋到beaconid = "+_beaconId);
			
			for(int j = 0; j < myOldMan.size();j++){
				System.out.println("比對次數"+j);
				System.out.println("我的老人beacon ="+uapp.getMyOldManList().get(j).getBeaconId());
				
				if(_beaconId.equals(uapp.getMyOldManList().get(j).getBeaconId())){
					System.out.println("比對符合");
					
					showNotificationFound(uapp.getMyOldManList().get(j).getOldName());
				}
			}
			
		}
		System.out.println("beacon is back~");
	
		
	}
	
	@Override
	public void beaconExit(IBeacon arg0) {
		// TODO Auto-generated method stub
		System.out.println("Exit beacon:"+arg0);
		if(iBeacons == null){
			System.out.println("beacons = null");
			iBeacons = new ArrayList<IBeacon>();
			iBeacons.add(arg0);
		}
		if(iBeacons.size() ==0){
			System.out.println("size="+iBeacons.size());
			iBeacons.add(arg0);
		}
		String _beaconId = "";
		ArrayList<MyOldManItem> myOldMan = uapp.getMyOldManList();
		
		
			count++;
			//判斷找到的beacon是不是自己的
			for(int i = 0 ; i < iBeacons.size();i++){
				_beaconId = iBeacons.get(i).getUuidHexStringDashed()+iBeacons.get(i).getMajor()+iBeacons.get(i).getMinor();
				System.out.println("搜尋到beaconid = "+_beaconId);
				
				for(int j = 0; j < myOldMan.size();j++){
					System.out.println("比對次數"+j);
					System.out.println("我的老人beacon ="+uapp.getMyOldManList().get(j).getBeaconId());
					if(_beaconId.equals(uapp.getMyOldManList().get(j).getBeaconId())){
						System.out.println("比對符合");
						showNotificationExit(uapp.getMyOldManList().get(j).getOldName());
					}
				}
				
			}
			System.out.println("beacon is exit");
		
	
	}
	
	@Override
	public void operationError(int status) {
		Log.i(IBeaconLibrary.LOG_TAG, "Bluetooth error: " + status);	
		
	}
	
	@Override
	public void scanState(int state) {
		// TODO Auto-generated method stub
		switch(state){
		case IBeaconLibrary.SCAN_STARTED:
			//Log.i("felix", "state= SCAN_STARTED");	
			break;
		case IBeaconLibrary.SCAN_END_SUCCESS:
			//Log.i("felix", "state= SCAN_END_SUCCESS");
			break;
		case IBeaconLibrary.SCAN_END_EMPTY:
			//Log.i("felix", "state= SCAN_END_EMPTY");	
			break;
		
		}
	}
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

	
	@Override
	public void onDestroy() {
		super.onDestroy();
		iBeaconLibrary.stopScan();
		System.out.println("Service:onDestroy");
	}

}
