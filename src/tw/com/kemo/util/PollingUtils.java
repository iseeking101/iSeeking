package tw.com.kemo.util;

import com.hereapps.ibeacon.IBeaconListener;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import tw.com.kemo.activity.BeaconActivity;
import tw.com.kemo.service.*;
/**
 * Polling Tools
 * @Author Ryan
 * @Create 2013-7-13 上�??10:14:43
 */
public class PollingUtils {

	/**
	 * @param context
	 * @param seconds
	 * @param cls
	 * @param action
	 */ 
	
	public static void startPollingService(Context context, int seconds, Class<?> cls,String action) {
		AlarmManager manager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, cls);
		intent.setAction(action);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		long triggerAtTime = SystemClock.elapsedRealtime();
		manager.setRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtTime,
				seconds * 1000, pendingIntent);
		

		
	}

	/**
	 * 
	 * @param context
	 * @param cls
	 * @param action
	 */
	public static void stopPollingService(Context context, Class<?> cls,String action) {
		AlarmManager manager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, cls);
		intent.setAction(action);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		manager.cancel(pendingIntent);
		System.out.println("cancel???");
	}
	
}
