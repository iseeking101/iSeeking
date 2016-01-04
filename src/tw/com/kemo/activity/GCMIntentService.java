package tw.com.kemo.activity;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/** 
 * GCMIntentService 處理接收的gcm訊息，並發送推播通知到手機及點選後的轉跳畫面。
*/ 
public class GCMIntentService extends IntentService {

	NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
	private PowerManager mPowerManager;
	UserApplication uapp ;
	public GCMIntentService() {
		super("GCMIntentService");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		uapp = (UserApplication) getApplicationContext();
		mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
	}



	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		//type = gcm
		String type = GoogleCloudMessaging.getInstance(this).getMessageType(intent);
		if (!GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(type)) {
			return;
		}
		
		if (!extras.isEmpty()) {
			System.out.println("data = "+extras.describeContents()+"size = "+extras.size());
			// read extras as sent from server
			System.out.println("data = "+extras.containsKey("data"));
			System.out.println("message = "+extras.containsKey("message"));
			String message = extras.getString("message");
			String user = extras.getString("user");
			String longitude = extras.getString("longitude");
			String latitude = extras.getString("latitude");
			String datev= extras.getString("datetime");
			String beaconId = extras.getString("beaconId");
			String codev =extras.getString("codev")==null || extras.getString("codev")== "" ? 
					"": extras.getString("codev");
			System.out.println(longitude+","+latitude+","+datev+","+beaconId+",codev = "+codev);
			sendNotification("Message: " + message,user,beaconId,codev);
			startPushActivity(message);
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GCMBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String... msg) {
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Intent i = new Intent();
		//因為沒登入所以帳號是空的，由此開啟app，在menu裡讀取資料
		if(uapp.getUser()=="" || uapp.getUser() == null ){
			i = new Intent(this, MenuActivity.class); 
			i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			
		}else{
			//2 = 附近有失蹤人口 /send
			if(msg[3].compareTo("2")==0){
				//有新資料
				System.out.println("~~ in codev 2 ~~");
				uapp.setNewData(1);
				i = new Intent(this, MenuActivity.class); 
				i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			}else{
				//收到尋獲通知 比對beaconId轉跳到相應的失蹤老人畫面 /find
				for(int v = 0 ; v< uapp.getMissOldList().size();v++){
					System.out.println("i="+v+": "+msg[2]+" X "+uapp.getMissOldList().get(v).getBeaconId());
					if(msg[2].equals(uapp.getMissOldList().get(v).getBeaconId())){
						System.out.println("i="+v+": "+msg[2]+" X "+uapp.getMissOldList().get(v).getBeaconId());
							
						uapp.setIndex(v);
					}
				}
				
				
			    i = new Intent(this, MissingOldManActivity.class); 
			    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				
			}
			
		}
		Bundle b = new Bundle();
		b.putString("beckFromNotification","1");
		b.putString("user",msg[1] );
		b.putString("beaconId", msg[2]);
		i.putExtras(b);
		System.out.println("user = "+ msg[1]+ "  message = "+msg[0]);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				i, Intent.FLAG_ACTIVITY_NEW_TASK);
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.old)
				.setContentTitle("iSeeking 通知")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg[0]))
				.setContentText(msg[0])
				.setAutoCancel(true);
		
		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify((int)System.currentTimeMillis(), mBuilder.build());
	}
	
	@SuppressWarnings("deprecation")
	private void startPushActivity(String message) {
		// if the to activity is PushNotification, do not start new activity
		// or screen is opened.
		if (getTopActivityName().equals(PushNotificationActivity.class.getName()) ||
				!mPowerManager.isScreenOn()) {
			Intent intent = new Intent(this, PushNotificationActivity.class);
			intent.putExtra("message", message);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}
	
	@SuppressWarnings("deprecation")
	public String getTopActivityName() {
		String activityName = "";
		try {
			ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningTaskInfo> forGroundActivity = activityManager
					.getRunningTasks(1);
			RunningTaskInfo currentActivity;
			currentActivity = forGroundActivity.get(0);
			activityName = currentActivity.topActivity.getClassName();
		} catch (Exception e) {
		}
		// activityName = package + LoginActivity
		return activityName;
	}

}
