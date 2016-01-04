package tw.com.kemo.activity;

import android.app.Activity;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.Date; 

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);//啟用 我在哪裏 的按鈕
		
		MarkerOptions options = new MarkerOptions();
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(24.989926,121.545414), 16)); //指定地圖中心
		
		Date date = new Date(); //取得現在時間
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = sdf.format(date);
		System.out.println(dateString);
		
		options.position(new LatLng(24.988485,121.544532));
		options.title(dateString);
		map.addMarker(options);
	}
}
