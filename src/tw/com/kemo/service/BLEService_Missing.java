package tw.com.kemo.service;



import java.util.ArrayList;

import com.hereapps.ibeacon.IBeacon;
import com.hereapps.ibeacon.IBeaconLibrary;
import com.hereapps.ibeacon.IBeaconListener;

import tw.com.kemo.activity.IndexActivity;
import tw.com.kemo.activity.R;
import tw.com.kemo.activity.UserApplication;
import tw.com.kemo.util.MissingOldItem;
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

public class BLEService_Missing extends Service  implements IBeaconListener{

	private IBeaconLibrary iBeaconLibrary;
	private static ArrayList<IBeacon> iBeacons;
	//private static int 
	int icon = R.drawable.old;
	String _beaconId = "";
	int countRun =0;
	int countFind = 0;
	private IBeaconListener activityListener; 
	ArrayList<MissingOldItem> _missingOld;
	private MyBinder myBinder = new MyBinder();
	Handler handler=new Handler();
	Runnable runnableStartScan = new Runnable(){ public void run() {
		System.out.println("runnable ���� "+(++countRun));
		_missingOld = uapp.getMissOldList();
		iBeaconLibrary.startScan();	
	}};
	public static final String ACTION = "tw.com.kemo.service.BLEService_Missing";
	private int count = 0 ;
	private Notification NotificationExit,NotificationFound;
	private NotificationManager mManager;
	private UserApplication uapp;
	PendingIntent pendingIntent;
	
	@Override
	public void onCreate() {
		//application �n���A �]��notifimanager�̭����Ψ�Ѽ�
		if(iBeacons == null)
			iBeacons = new ArrayList<IBeacon>();
		uapp = (UserApplication) getApplicationContext();
	    _missingOld =uapp.getMissOldList();
		
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
		IBeaconLibrary.SCANNING_TIMEOUT=10000;
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
//		count ++;
//		if(iBeacons == null){
//			System.out.println("beacons = null");
//			iBeacons = new ArrayList<IBeacon>();
//			iBeacons.add(arg0);
//		}
//		if(iBeacons.size() ==0){
//			System.out.println("size="+iBeacons.size());
//			iBeacons.add(arg0);
//		}
//		String _beaconId = "";
//		ArrayList<MissingOldItem> missingOld = uapp.getMissOldList();
//		
//		//�P�_��쪺beacon�O���O�ۤv��
//		System.out.println("beacon size"+iBeacons.size());
//		for(int i = 0 ; i < iBeacons.size();i++){
//			
//			_beaconId = iBeacons.get(i).getUuidHexStringDashed()+iBeacons.get(i).getMajor()+iBeacons.get(i).getMinor();
//			System.out.println("�j�M��beaconid = "+_beaconId);
//			
//			for(int j = 0; j < missingOld.size();j++){
//				System.out.println("��隸��"+j);
//				System.out.println("�ڪ��ѤHbeacon ="+missingOld.get(j).getBeaconId());
//				
//				if(_beaconId.equals(missingOld.get(j).getBeaconId())){
//					System.out.println("���ŦX");
//					showNotificationFound(missingOld.get(j).getOldName(),_beaconId);
//				}
//			}
//		}
//		System.out.println("beacon is back~");
		if(activityListener!=null)activityListener.beaconEnter(arg0);
		
	}
	@Override
	public void beaconExit(IBeacon arg0) {
		//Log.i("felix","beaconExit");
//		if(iBeacons == null){
//			System.out.println("beacons = null");
//			iBeacons = new ArrayList<IBeacon>();
//			iBeacons.add(arg0);
//		}
//		if(iBeacons.size() ==0){
//			System.out.println("size="+iBeacons.size());
//			iBeacons.add(arg0);
//		}
//		
//		ArrayList<MissingOldItem> missingOld = uapp.getMissOldList();
//		
//		if(activityListener!=null){
//			count++;
//			//�P�_��쪺beacon�O���O�ۤv��
//			for(int i = 0 ; i < iBeacons.size();i++){
//				_beaconId = iBeacons.get(i).getUuidHexStringDashed()+iBeacons.get(i).getMajor()+iBeacons.get(i).getMinor();
//				System.out.println("�j�M��beaconid = "+_beaconId);
//				
//				for(int j = 0; j < missingOld.size();j++){
//					System.out.println("��隸��"+j);
//					System.out.println("�ڪ��ѤHbeacon ="+missingOld.get(j).getBeaconId());
//					if(_beaconId.equals(missingOld.get(j).getBeaconId())){
//						System.out.println("���ŦX");
////						showNotificationExit(missingOld.get(j).getOldName());
//					}
//				}
//				
//			}
//			System.out.println("beacon is exit");
			activityListener.beaconExit(arg0);
//		}
		
	}
	@Override
	public void beaconFound(IBeacon arg0) {
		//Log.i("felix","beaconFound");
		count ++;

		System.out.println("beacon is find~");
		
		if(activityListener!=null)activityListener.beaconFound(arg0);
		
		System.out.println("beacons.add arg0");
		//�������y�쪺beacon ��J�}�C
		iBeacons.add(arg0);
		String _beaconId = "";
		ArrayList<MissingOldItem> missingOld = new ArrayList<MissingOldItem>(_missingOld);
		//�P�_��쪺beacon�O���O�ۤv��
		/**
		 * ����:service�Ұʱ��y�@���A�|���y�P�D�Ҧ�beacon ���X�ӴN�|Ĳ�o�X��beaconFound��k�A�]����_missingOld�}�C
		 * 	    �N�ѤH��Ʃ�J�䤤�A�Ω���ثe���y�쪺beaconid�O�_�P�Y���ܦѤHid�ۦP�A�Y�ۦP�hpushGCM�A�çR��_missingOld�}�C
		 * ����ѤH����ơA�H���P�����y�ɦA��Ĳ�o�P�@��ѤH�y�����ƶǰe�A�b�������y�����᪺handler runnable�̭����s�إ�_missingOld�}�C
		 * �Ӥ��U�������y�C
		 */
		for(int i = 0 ; i < iBeacons.size();i++){
			
			_beaconId = iBeacons.get(i).getUuidHexStringDashed()+iBeacons.get(i).getMajor()+iBeacons.get(i).getMinor();
			for(int j = 0; j < missingOld.size();j++){
				System.out.println("��"+countRun+"�����y�A"+"����beacon ��"+iBeacons.size()+"�ӡA"+"��"+i+"��beacon�P��"+j+"�ӦѤH���");
				if(_beaconId.equals(missingOld.get(j).getBeaconId())){
					System.out.println("���ŦX"+_beaconId+" = "+missingOld.get(j).getBeaconId());
					showNotificationFound(missingOld.get(j).getOldName(),_beaconId);
					missingOld.remove(j);
				}else{
					System.out.println("���G����:"+_beaconId+" X "+missingOld.get(j).getBeaconId());
					
				}
			}
		}
		iBeacons.clear();
		
		
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
			handler.postDelayed(runnableStartScan, 60000);
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
			handler.postDelayed(runnableStartScan, 60000);
			break;
		
		}
		
	}
	//=============================================
	private PendingIntent initNotifPending(){
		mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		int icon = R.drawable.old;
		
		Intent i = new Intent(this, IndexActivity.class);
		Bundle b = new Bundle();
		b.putString("user", uapp.getUser());
		b.putString("beckFromNotification","1");
		i.putExtras(b);
		//�쥻�� �Ѽ�4��0 �����bundle ��� Intent.Flag_ACTIVITY_NEW_TASK��i�H��
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
		//�쥻�� �Ѽ�4��0 �����bundle ��� Intent.Flag_ACTIVITY_NEW_TASK��i�H��
		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, i,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		return pendingIntent;
		
	}
	private void showNotificationExit(String oldName,String beaconId) {
		//mNotification.when = System.currentTimeMillis();
		//Navigator to the new activity when click the notification title
		if(count <2){
			return ;
		}
		NotificationExit = new Notification.Builder(getApplicationContext())
				.setWhen(System.currentTimeMillis())
				.setDefaults(Notification.DEFAULT_SOUND)
				.setTicker("New Message")
				.setSmallIcon(icon)
				.setContentTitle("iSeeking �q��:")
				.setContentIntent(pendingIntent)
				.setContentText(oldName +" �W�X�d�� �Ъ`�N").build();
		NotificationExit.flags = Notification.FLAG_AUTO_CANCEL;	
		
		mManager.notify(0, NotificationExit);
		
	}
	//���P�򦳥��ܪ̶ǰe�]�t�ثe�y�Ъ��q����server �A��server�q���D�H
	private void showNotificationFound(String oldName,String beaconId) {
		//mNotification.when = System.currentTimeMillis();
		//Navigator to the new activity when click the notification title
//		if(count <2){
//			return ;
//		}
//		NotificationFound = new Notification.Builder(getApplicationContext())
//				.setWhen(System.currentTimeMillis())
//				.setDefaults(Notification.DEFAULT_SOUND)
//				.setTicker("New Message")
//				.setSmallIcon(icon)
//				.setContentTitle("iSeeking �q��:")
//				.setContentIntent(pendingIntent)
//				.setContentText(oldName +" �^��d��").build();
//		NotificationFound.flags = Notification.FLAG_AUTO_CANCEL;	
//		mManager.notify(1, NotificationFound);
		uapp.pushGCM(oldName, beaconId);
	}
}
