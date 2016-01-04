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
 * ���ܦѤH�T����
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
				mProgress = ProgressDialog.show(MissingOldManActivity.this, null, "��Ƨ�s���A�еy��...", true, false);
	        	//���M����ƥH�K�y���h������	
				uapp.clearMissingOldList();
				//�d�߸�Ʈw�ç�s�e��
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
		System.out.println("�e����s");
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
						myOld.getLocation().get(position).getLongitude()), 16)); // ���w�a�Ϥ���

		    }
		    @Override
		    public void onNothingSelected(AdapterView<?> arg0) {
		    
		    }
		});

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);// �ҥ� �ڦb���� �����s
		MarkerOptions options = new MarkerOptions();
		// �r�ꪺ�@����নDate SimpleDateFormat ���ഫ������榡
		Long millis = Long.parseLong(myOld.getMissingMillis());
		Date date = new Date(millis); // ���o�{�b�ɶ�
		
		String dateString = sdf.format(date);
		System.out.println(dateString + "," + myOld.getMissingMillis());
		System.out.println("my longitude = " + myOld.getLongitude() + "  my latitude = " + myOld.getLatitude());
		options.position(new LatLng(myOld.getLatitude(), myOld.getLongitude()));
		options.title(dateString);

		if (!myOld.getLatitude().equals(0.0)) {
			//�_�l�I
			map.addMarker(options);
			if(myOld.getLocation().size()>0){
				System.out.println("���l�ܦa�I");
				System.out.println("�Q�o�{��m�ƥ�"+myOld.getLocation().size());
				for (int i = 0; i < myOld.getLocation().size(); i++) {
					System.out.println("�l�ܦa�I"+i+","+myOld.getLocation().get(i).getLatitude()+","+
							myOld.getLocation().get(i).getLongitude());
					map.addMarker(new MarkerOptions()
							.title(sdf.format(new Date(Long.parseLong(myOld.getLocation().get(i).getMillis()))))
							.position(new LatLng(myOld.getLocation().get(i).getLatitude(),
									myOld.getLocation().get(i).getLongitude())));
					if(i == myOld.getLocation().size()-1){
						map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myOld.getLocation().get(i).getLatitude(),
								myOld.getLocation().get(i).getLongitude()), 16)); // ���w�a�Ϥ���
						
					}
				}
			}else{
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myOld.getLatitude(), myOld.getLongitude()), 16)); // ���w�a�Ϥ���

			}

		} else {
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(24.9872925, 121.5477383), 16)); // ���w�a�Ϥ���

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
						System.out.println("missingold  = " + ary.length() + "��");
						// �Ĥ@�h ������ document
						for (int i = 0; i < ary.length(); i++) {
							// json = document������i��documemt
							JSONObject json = ary.getJSONObject(i);
							JSONArray old_detail = json.getJSONArray("old_detail");
							// �ĤG�h ��i��document����1�ӦѤH���
							for (int j = 0; j < old_detail.length(); j++) {
								System.out.println("�ˬd : ��" + i + "�H�� ��" + j + "�ӦѤH");
								JSONObject detailJson = old_detail.getJSONObject(j);
								// statusv =1 ��ܳo�ѤH����
								if (detailJson.getString("statusv").equals("1")) {
									System.out.println("�o�{: ��" + i + "�H�� ��" + j + "�ӦѤH���ܤF");
									ArrayList<OldTraceItem> oldTraceListv = new ArrayList<OldTraceItem>();
									// ���ogroupMember�}�C�̪��Ҧ��H
									ArrayList<String> groupMemberv = new ArrayList<String>();
									JSONArray aryGroup = detailJson.getJSONArray("groupMember");
									for (int k = 0; k < aryGroup.length(); k++) {
										groupMemberv.add(aryGroup.get(k).toString());
									}
									try {
										
										// �P�_location
										// ���S���s�b�A�Y���h�إߤ@�ճq����쪺��m(OldTraceItem)
										if (detailJson.has("location")) {
											System.out.println("��"+i+"�ӤH�� ��" + j + "�ӦѤH��location : " + detailJson.has("location"));
											
											JSONArray trace = detailJson.getJSONArray("location");
											System.out.println("location��"+trace.length()+"�ӳQ�o�{��m");
											
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
										// �P�_ reportLocation ���S���s�b�A�Y���d�إߤ@�զѤH���ܦ�m
										if (detailJson.has("reportLocation")) {
											JSONObject report = detailJson.getJSONObject("reportLocation");
											lng = report.getDouble("longitude");
											lan = report.getDouble("latitude");
											mills = report.getString("datetime");
										}
										missingOldList.add(new MissingOldItem(
												stringFormat(detailJson.getString("beaconId")),
												stringFormat(detailJson.getString("oldName"), "����"), R.drawable.nobody,
												stringFormat(detailJson.getString("oldCharacteristic")),
												stringFormat(detailJson.getString("oldhistory")),
												stringFormat(detailJson.getString("oldclothes")),
												stringFormat(detailJson.getString("oldaddr")), groupMemberv, lng, lan,
												mills, oldTraceListv));

									} catch (Exception e) {
										System.out.println(e.getMessage());
									}
									System.out.println("��" + i + "�H�� ��" + j + "�ӦѤH�w�n�J");
								}

							}
							System.out.println("��suapp��missingoldlist");
							uapp.setMissingOldForListView(missingOldList);
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					/**�p�G missingoldlist �����ѤH�ƶq��֫h index ���ܷ|�y����ƹ藍�W�����~
					 * ���F�קK�I��s�e����p�G��list�ƶq�W���ܰʡA�]�w�@�ӫ��w���ܪ�id �b�I�o�ӦѤH�ɶǤJ�e���A
					 * ���j��異�ܦѤHbeaconid��ثebeaconid�O�_�ۦP �ۦP�h�ϥΦ�index�ǤJ��ƨç�s�e��
					*/
					for(int i = 0 ; i<missingOldList.size();i++){
						if(missingOldList.get(i).getBeaconId().equals(bid)){
							setMapView(missingOldList.get(i));
							mProgress.dismiss();
							showMessage("��Ƥw��s");
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
				showMessage("��Ƨ�s���ѡA���ˬd�����s�u");
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
