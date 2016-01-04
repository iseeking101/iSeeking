package tw.com.kemo.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import tw.com.kemo.fragment.MyOldManFragment;
import tw.com.kemo.networkcommunication.volleymgr.NetworkManager;
import tw.com.kemo.util.MissingOldItem;
import tw.com.kemo.util.OldTraceItem;

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
import com.special.ResideMenu.ResideMenu;

/** 
 * 失蹤老人訊息頁
 */ 
public class MissingOldManActivity extends Activity {
	FragmentManager fragmentManager;
	private TextView beacon_id_text, beacon_id;
	private TextView old_nameText, old_characteristicText, old_historyText, old_clothesText, old_addrText;
	private Button map_update;
	private UserApplication uapp;
	private ProgressDialog mProgress;
	private ProgressDialog mProgress2;
	MissingOldItem myOld;
	private Spinner spinner;      
	private ArrayList<String> spinnerItem;
	private GoogleMap map;
	private String bid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.missing_oldman);
		map_update = (Button) findViewById(R.id.map_update);
		beacon_id_text = (TextView) findViewById(R.id.beacon_id_text);
		old_nameText = (TextView) findViewById(R.id.old_nameText);
		old_characteristicText = (TextView) findViewById(R.id.old_characteristicText);
		old_historyText = (TextView) findViewById(R.id.old_historyText);
		old_clothesText = (TextView) findViewById(R.id.old_clothesText);
		old_addrText = (TextView) findViewById(R.id.old_addrText);
		beacon_id = (TextView) findViewById(R.id.beacon_id);
		uapp = (UserApplication) getApplicationContext();
		spinner = (Spinner) findViewById(R.id.my_spinner);
		beacon_id_text.setVisibility(View.INVISIBLE);
		beacon_id.setVisibility(View.INVISIBLE);
		// =============check back form notification========================
	
		myOld = uapp.getMissOldList().get( uapp.getIndex());
		
	    bid = uapp.getMissOldList().get( uapp.getIndex()).getBeaconId();
		getOld(myOld);
		setMapView(myOld);
				map_update.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try{
				mProgress = ProgressDialog.show(MissingOldManActivity.this, null, "資料更新中，請稍後...", true, false);
	        	//先清除資料以免造成多筆重複	
				uapp.clearMissingOldList();
				//查詢資料庫並更新畫面
				missingOldDAO();
//				setMapView(myOld);
				}catch(Exception e){
					System.out.println(e.getMessage());
					
				}
				
			}
        	
        });
		

	}
	
	
	private void showMessage(String msg) {
		Toast.makeText(MissingOldManActivity.this, msg, Toast.LENGTH_LONG).show();
	}

	private void getOld(MissingOldItem myOld) {

		beacon_id_text.setText(myOld.getBeaconId());
		old_nameText.setText(myOld.getOldName());
		old_characteristicText.setText(myOld.getOldCharacteristic());
		old_historyText.setText(myOld.getOldhistory());
		old_clothesText.setText(myOld.getOldClothes());
		old_addrText.setText(myOld.getOldAddr());
	}

	protected void onDestroy() {
		super.onDestroy();
	}
	
	private void setMapView(final MissingOldItem myOld) {
		System.out.println("");
		System.out.println("畫面更新");
		spinnerItem = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		if(myOld.getLocation().size()>0){
			for (int i = 0; i < myOld.getLocation().size(); i++) {
				spinnerItem.add(sdf.format(new Date(Long.parseLong(myOld.getLocation().get(i).getMillis()))));
			}
		}                                   
		spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerItem));
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){

		    @Override
		    public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
		    	map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myOld.getLocation().get(position).getLatitude(),
						myOld.getLocation().get(position).getLongitude()), 16)); // 指定地圖中心

		    }
		    @Override
		    public void onNothingSelected(AdapterView<?> arg0) {
		    
		    }
		});

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);// 啟用 我在哪裏 的按鈕
		MarkerOptions options = new MarkerOptions();
		// 字串的毫秒數轉成Date SimpleDateFormat 裡轉換成日期格式
		Long millis = Long.parseLong(myOld.getMissingMillis());
		Date date = new Date(millis); // 取得現在時間
		
		String dateString = sdf.format(date);
		System.out.println(dateString + "," + myOld.getMissingMillis());
		System.out.println("my longitude = " + myOld.getLongitude() + "  my latitude = " + myOld.getLatitude());
		options.position(new LatLng(myOld.getLatitude(), myOld.getLongitude()));
		options.title(dateString);

		if (!myOld.getLatitude().equals(0.0)) {
			//起始點
			map.addMarker(options);
			if(myOld.getLocation().size()>0){
				System.out.println("有追蹤地點");
				System.out.println("被發現位置數目"+myOld.getLocation().size());
				for (int i = 0; i < myOld.getLocation().size(); i++) {
					System.out.println("追蹤地點"+i+","+myOld.getLocation().get(i).getLatitude()+","+
							myOld.getLocation().get(i).getLongitude());
					map.addMarker(new MarkerOptions()
							.title(sdf.format(new Date(Long.parseLong(myOld.getLocation().get(i).getMillis()))))
							.position(new LatLng(myOld.getLocation().get(i).getLatitude(),
									myOld.getLocation().get(i).getLongitude())));
					if(i == myOld.getLocation().size()-1){
						map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myOld.getLocation().get(i).getLatitude(),
								myOld.getLocation().get(i).getLongitude()), 16)); // 指定地圖中心
						
					}
				}
			}else{
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myOld.getLatitude(), myOld.getLongitude()), 16)); // 指定地圖中心

			}

		} else {
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(24.9872925, 121.5477383), 16)); // 指定地圖中心

		}
	}

	 private void missingOldDAO() {
			StringRequest request = new StringRequest(Request.Method.POST,
					"https://beacon-series.herokuapp.com/getMissingOld", mResMissingOld, mErrorListener) {
				protected Map<String, String> getParams() throws AuthFailureError {
					Map<String, String> map = new HashMap<String, String>();
					return map;
				}
			};
			NetworkManager.getInstance(this).request(null, request);
		}
	    private Listener<String> mResMissingOld = new Listener<String>() {
			@Override

			public void onResponse(String str) {
				if (str.trim().equals("no detail")) {
					System.out.println("no detail");
					mProgress.dismiss();
					
				} else {
					ArrayList<MissingOldItem> missingOldList = new ArrayList<MissingOldItem>();
					
					try {
						JSONArray ary = new JSONArray(str);
						System.out.println("missingold  = " + ary.length() + "個");
						// 第一層 全部的 document
						for (int i = 0; i < ary.length(); i++) {
							// json = document中的第i個documemt
							JSONObject json = ary.getJSONObject(i);
							JSONArray old_detail = json.getJSONArray("old_detail");
							// 第二層 第i個document的第1個老人資料
							for (int j = 0; j < old_detail.length(); j++) {
								System.out.println("檢查 : 第" + i + "人的 第" + j + "個老人");
								JSONObject detailJson = old_detail.getJSONObject(j);
								// statusv =1 表示這老人失蹤
								if (detailJson.getString("statusv").equals("1")) {
									System.out.println("發現: 第" + i + "人的 第" + j + "個老人失蹤了");
									ArrayList<OldTraceItem> oldTraceListv = new ArrayList<OldTraceItem>();
									// 取得groupMember陣列裡的所有人
									ArrayList<String> groupMemberv = new ArrayList<String>();
									JSONArray aryGroup = detailJson.getJSONArray("groupMember");
									for (int k = 0; k < aryGroup.length(); k++) {
										groupMemberv.add(aryGroup.get(k).toString());
									}
									try {
										
										// 判斷location
										// 有沒有存在，若有則建立一組通報找到的位置(OldTraceItem)
										if (detailJson.has("location")) {
											System.out.println("第"+i+"個人的 第" + j + "個老人的location : " + detailJson.has("location"));
											
											JSONArray trace = detailJson.getJSONArray("location");
											System.out.println("location有"+trace.length()+"個被發現位置");
											
											for (int l = 0; l < trace.length(); l++) {
												
												JSONObject traceEatch = trace.getJSONObject(l);
												oldTraceListv.add(new OldTraceItem(traceEatch.getDouble("longitude"),
														traceEatch.getDouble("latitude"),
														traceEatch.getString("datetime")));
											}
										}

									} catch (Exception e) {
										System.out.println(e.getMessage());
									}
									try {
										Double lng = 0.0;
										Double lan = 0.0;
										String mills = "";
										// 判斷 reportLocation 有沒有存在，若有責建立一組老人失蹤位置
										if (detailJson.has("reportLocation")) {
											JSONObject report = detailJson.getJSONObject("reportLocation");
											lng = report.getDouble("longitude");
											lan = report.getDouble("latitude");
											mills = report.getString("datetime");
										}
										missingOldList.add(new MissingOldItem(
												stringFormat(detailJson.getString("beaconId")),
												stringFormat(detailJson.getString("oldName"), "未知"), R.drawable.nobody,
												stringFormat(detailJson.getString("oldCharacteristic")),
												stringFormat(detailJson.getString("oldhistory")),
												stringFormat(detailJson.getString("oldclothes")),
												stringFormat(detailJson.getString("oldaddr")), groupMemberv, lng, lan,
												mills, oldTraceListv));

									} catch (Exception e) {
										System.out.println(e.getMessage());
									}
									System.out.println("第" + i + "人的 第" + j + "個老人已登入");
								}

							}
							System.out.println("更新uapp的missingoldlist");
							uapp.setMissingOldForListView(missingOldList);
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					/**如果 missingoldlist 走失老人數量減少則 index 改變會造成資料對不上的錯誤
					 * 為了避免點更新畫面後如果有list數量上的變動，設定一個指定不變的id 在點這個老人時傳入畫面，
					 * 巡迴比對失蹤老人beaconid跟目前beaconid是否相同 相同則使用此index傳入資料並更新畫面
					*/
					for(int i = 0 ; i<missingOldList.size();i++){
						if(missingOldList.get(i).getBeaconId().equals(bid)){
							setMapView(missingOldList.get(i));
							mProgress.dismiss();
							showMessage("資料已更新");
							break;
						}
					}
					
					
					
				}

			}
		};
		private ErrorListener mErrorListener = new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e("Error", error.toString());
				showMessage("資料更新失敗，請檢查網路連線");
				mProgress.dismiss();
			}
		};
		

		public String stringFormat(String str) {

			if (str.equals("null") || str == null) {
				return "";
			}
			return str;
		}

		public String stringFormat(String str, String str2) {

			if (str.equals("null") || str == null || str.equals("") || str == "") {
				return str2;
			}

			return str;
		}

}
