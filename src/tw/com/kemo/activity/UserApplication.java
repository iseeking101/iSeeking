package tw.com.kemo.activity;

import java.io.EOFException;
import java.io.IOException;
import java.util.*;
import tw.com.kemo.networkcommunication.volleymgr.*;
import tw.com.kemo.util.GPSHelper;
import tw.com.kemo.util.ListViewAdpater;
import tw.com.kemo.util.MissingOldAdpater;
import tw.com.kemo.util.MissingOldItem;
import tw.com.kemo.util.MyFollowItem;
import tw.com.kemo.util.MyOldManItem;
import tw.com.kemo.util.OldTraceItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import tw.com.kemo.activity.R;

/**
 *  ����ѼơB�@�Τ�k
*/ 
public class UserApplication extends Application {

	private String user;
	private String userName;
	private String userPhone;
	private String userAddress;
	private String reward;
	private Double longitude;// �g��
	private Double latitude; // �n��
	private String location;
	private String beaconId;
	private String oldCharacteristic;
	private String oldhistory;
	private String oldClothes;
	private String oldAddr;
	private String groupMember;
	private String oldName;
	private int index;
	private String serviceCode ;
	private int newData =0;
	ArrayList<MyFollowItem> myFollowList = new ArrayList<MyFollowItem>();
	private ArrayList<MissingOldItem> missingOldList = new ArrayList<MissingOldItem>();
	private ArrayList<MyOldManItem> myOldManList = new ArrayList<MyOldManItem>();
	Context c = this;

	public Context getContext() {
		return c;
	}
	public void setNewData(int newData ){
		this.newData = newData;
	}
	public int getNewData(){
		return newData;
	}
	public void setServiceCode(String serviceCode){
		this.serviceCode = serviceCode;
	}
	public String getServiceCode(){
		return serviceCode;
	}
	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getGroupMember() {
		return groupMember;
	}

	public void setGroupMember(String groupMember) {
		this.groupMember = groupMember;
	}

	public String getOldAddr() {
		return oldAddr;
	}

	public void setOldAddr(String oldAddr) {
		this.oldAddr = oldAddr;
	}

	public String getOldClothes() {
		return oldClothes;
	}

	public void setOldClothes(String oldClothes) {
		this.oldClothes = oldClothes;
	}

	public String getOldHistory() {
		return oldhistory;
	}

	public void setOldHistory(String oldhistory) {
		this.oldhistory = oldhistory;
	}

	public String getOldCharacteristic() {
		return oldCharacteristic;
	}

	public void setOldCharacteristic(String oldCharacteristic) {
		this.oldCharacteristic = oldCharacteristic;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public void setUserName(String username) {
		this.userName = username;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}

	public String getUserAddress() {
		return userAddress;
	}

	public void setBeaconId(String beaconId) {
		this.beaconId = beaconId;
	}

	public String getBeaconId() {
		return beaconId;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public String getOldName() {
		return oldName;
	}

	public void getAllOldDetail() {
		oldDetailDao();
	}

	public void setAllMemberDetail() {
		memberDetailDao();
	}

	// ���o�ۤv�s�ժ��խ� �����
	public void getAllGroupMember() {
		groupMemberDao();
	}

	// �N�l�ܪ��ѤH��Ʃ�JmyFollowList�}�C��
	public void setMyFollow() {
		myFollowDAO();
	}

	// �^�ǰl�ܦѤH��ƪ��}�C
	public ArrayList<MyFollowItem> getMyFollow() {
		System.out.println("myFollowList ��  "+myFollowList.size()+" �H");
		return myFollowList;
	}

	// Ū����Ʈw���ܦѤH��ƥ[�J���ܦѤH�}�C working
	public void setMissingOld() {
		missingOldDAO();
		System.out.println("���omissingoldlist");
	}

	public void clearMissingOldList() {
		missingOldList.clear();
		System.out.println("�M�� missingoldlist");
	}
	public void clearFollowList() {
		myFollowList.clear();
		System.out.println("�M�� missingoldlist");
	}

	// ���o�ثe���ܪ��ѤH��ư}�C working
	public ArrayList<MissingOldItem> getMissOldList() {
		return missingOldList;
	}

	public void clearMyOldMan() {
		myOldManList.clear();
	}

	// �N�Ҧ��ѤH��Ʀs�JmyOldManList�}�C�� working
	public void setMyOldMan() {
		getMyOldDAO();
	}

	public ArrayList<MyOldManItem> getMyOldManList() {
		return myOldManList;
	}
	public void setMissingOldForListView(ArrayList<MissingOldItem> missingOldList){
		this.missingOldList = missingOldList;
	}
	public void setMyOldManForListView(ArrayList<MyOldManItem> myOldManList){
		this.myOldManList = myOldManList;
	}
	public void setMyFollowManForListView(ArrayList<MyFollowItem>  myFollowList){
		this.myFollowList = myFollowList;
	}

	// �s�W�ѤH working
	public void addOldMan(MyOldManItem item) {
		addOldManDAO(item);
	}

	public void pushGCM(String oldName, String beaconId) {
		pushGCMDAO(oldName, beaconId);
	}

	private void addOldManDAO(MyOldManItem item) {
		final String beaconIdv, oldNamev, oldCharacteristicv, oldhistoryv, oldclothesv, oldaddrv;
		beaconIdv = item.getBeaconId();
		oldNamev = item.getOldName();
		oldCharacteristicv = item.getOldCharacteristic();
		oldhistoryv = item.getOldhistory();
		oldclothesv = item.getOldClothes();
		oldaddrv = item.getOldAddr();
		StringRequest request = new StringRequest(Request.Method.POST, "https://beacon-series.herokuapp.com/getOld",
				mResAddOld, mErrorListener) {
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("user", user);
				map.put("beaconId", beaconIdv);
				map.put("oldName", oldNamev);
				map.put("oldCharacteristic", oldCharacteristicv);
				map.put("oldhistory", oldhistoryv);
				map.put("oldaddr", oldaddrv);
				map.put("oldclothes", oldclothesv);
				return map;
			}
		};
		NetworkManager.getInstance(c).request(null, request);
	}

	private void getMyOldDAO() {
		StringRequest request = new StringRequest(Request.Method.POST, "https://beacon-series.herokuapp.com/getOld",
				mResMyOld, mErrorListener) {
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("user", user);
				return map;
			}
		};
		NetworkManager.getInstance(c).request(null, request);

	}

	private void missingOldDAO() {
		StringRequest request = new StringRequest(Request.Method.POST,
				"https://beacon-series.herokuapp.com/getMissingOld", mResMissingOld, mErrorListener) {
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				return map;
			}
		};
		NetworkManager.getInstance(c).request(null, request);
	}

	private void myFollowDAO() {
		StringRequest request = new StringRequest(Request.Method.POST,
				"https://beacon-series.herokuapp.com/getMyFollow", mResMyFollow, mErrorListener) {
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("user", user);
				map.put("status", "3");
				return map;
			}
		};
		NetworkManager.getInstance(c).request(null, request);
	}

	private void groupMemberDao() {

		StringRequest request = new StringRequest(Request.Method.POST,
				"https://beacon-series.herokuapp.com/groupService", mResGroup, mErrorListener) {
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("user", user);
				map.put("status", "2");
				return map;
			}
		};
		NetworkManager.getInstance(c).request(null, request);
	}

	private Listener<String> mResAddOld = new Listener<String>() {
		@Override
		public void onResponse(String str) {
			if (str.trim().equals("ok")) {
				showMessage("�ק令�\");

			}
			Log.d("Response", str);
		}
	};
	private Listener<String> mResMissingOld = new Listener<String>() {
		@Override

		public void onResponse(String str) {
			if (str.trim().equals("no detail")) {
				System.out.println("no detail");
			} else {

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
						
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}

		}
	};

	private void jumpToActivity(Context ct,Class<?> lt){
		Intent intent = new Intent();
        intent.setClass(ct, lt);
		startActivity(intent);
	}
	private Listener<String> mResMyOld = new Listener<String>() {
		@Override

		public void onResponse(String str) {
			if (str.trim().equals("no detail")) {
				System.out.println("no detail");
			} else {
				try {
					// "beaconId": "15345164-67AB-3E49-F9D6-E290000000082616",
					// "oldName": "�Q�Ȥ�",
					// "oldCharacteristic": "���k",
					// "oldhistory": "���g�f",
					// "oldclothes": "\u2026qcgc\n",
					// "oldaddr": "\u2026",// "groupMember": [
					JSONArray ary = new JSONArray(str);
					for (int i = 0; i < ary.length(); i++) {
						JSONObject json = ary.getJSONObject(i);
						JSONArray aryGroup = json.getJSONArray("groupMember");
						ArrayList<String> groupMemberv = new ArrayList<String>();
						for (int k = 0; k < aryGroup.length(); k++) {
							groupMemberv.add(aryGroup.get(k).toString());
						}
						myOldManList.add(new MyOldManItem(stringFormat(json.get("beaconId").toString()),
								stringFormat(json.get("oldName").toString(), "����"), R.drawable.nobody,
								stringFormat(json.get("oldCharacteristic").toString()),
								stringFormat(json.get("oldhistory").toString()),
								stringFormat(json.get("oldclothes").toString()),
								stringFormat(json.get("oldaddr").toString()), groupMemberv,
								stringFormat(json.get("statusv").toString())));
					}
					System.out.println(myOldManList.size());

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}
	};
	private Listener<String> mResMyFollow = new Listener<String>() {
		@Override

		public void onResponse(String str) {
			if (str.trim().equals("no detail")) {
				System.out.println("no detail");
			} else {

				try {
					JSONArray ary = new JSONArray(str);
					System.out.println("missingold  = " + ary.length() + "��");
					ArrayList<MyFollowItem> myFollowList = new ArrayList<MyFollowItem>();
					// �Ĥ@�h ������ document
					for (int i = 0; i < ary.length(); i++) {
						// json = document������i��documemt
						JSONObject json = ary.getJSONObject(i);
						JSONArray old_detail = json.getJSONArray("old_detail");
						// �ĤG�h ��i��document����1�ӦѤH���
						for (int j = 0; j < old_detail.length(); j++) {
							System.out.println("�ˬd : ��" + i + "�H�� ��" + j + "�ӦѤH");
							JSONObject detailJson = old_detail.getJSONObject(j);
						
								System.out.println("�o�{: ��" + i + "�H�� ��" + j);
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
									myFollowList.add(new MyFollowItem(
											stringFormat(detailJson.getString("beaconId")),
											stringFormat(detailJson.getString("oldName"), "����"), R.drawable.nobody,
											stringFormat(detailJson.getString("oldCharacteristic")),
											stringFormat(detailJson.getString("oldhistory")),
											stringFormat(detailJson.getString("oldclothes")),
											stringFormat(detailJson.getString("oldaddr")), groupMemberv
											,detailJson.getString("statusv"), lng, lan,
											mills, oldTraceListv));

								} catch (Exception e) {
									System.out.println(e.getMessage());
								}
								System.out.println("��" + i + "�H�� ��" + j + "�ӦѤH�w�n�J");
							

						}
						System.out.println("��suapp��missingoldlist");
						setMyFollowManForListView(myFollowList);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}
	};
	private Listener<String> mResGroup = new Listener<String>() {
		@Override
		public void onResponse(String str) {
			if (str.trim().equals("no detail")) {
				System.out.println("no detail");
			} else {

				try {
					JSONArray ary = new JSONArray(str);
					StringBuilder groupMember = new StringBuilder();

					for (int i = 0; i < ary.length(); i++) {
						JSONObject json = ary.getJSONObject(i);
						if (!json.getString("old_detail").isEmpty()) {
							JSONArray Ary_Old_detail = json.getJSONArray("old_detail");
							for (int j = 0; j < Ary_Old_detail.length(); j++) {
								JSONObject old_detail = ary.getJSONObject(j);
								JSONArray aryGroup = old_detail.getJSONArray("groupMember");
								for (int k = 0; k < aryGroup.length(); k++) {
									groupMember.append(aryGroup.get(k).toString());
									if (k < aryGroup.length() - 1) {
										groupMember.append(",");
									}
								}
							}
							// String old_detail = json.getString("old_detail");
							// JSONObject detailJson = new
							// JSONObject(old_detail);
							// JSONArray aryGroup =
							// detailJson.getJSONArray("groupMember");
							// for(int j = 0;j<aryGroup.length();j++){
							// groupMember.append(aryGroup.get(j).toString());
							// if(j<aryGroup.length()-1){
							// groupMember.append(",");
							// }
							// }
							//
							// setGroupMember(""+groupMember);

						}

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}
	};

	private void memberDetailDao() {

		StringRequest request = new StringRequest(Request.Method.POST, "https://beacon-series.herokuapp.com/getMember",
				mResponseListenerGet, mErrorListener) {
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("user", user);
				return map;
			}
		};
		NetworkManager.getInstance(c).request(null, request);
	}

	private Listener<String> mResponseListenerGet = new Listener<String>() {
		@Override
		public void onResponse(String str) {
			if (str.trim().equals("no detail")) {
				System.out.println("no detail");
			} else {

				try {
					JSONArray ary = new JSONArray(str);
					System.out.println("ary.length = " + ary.length());

					for (int i = 0; i < ary.length(); i++) {
						JSONObject json = ary.getJSONObject(i);
						if (!json.getString("detail").isEmpty()) {

							String detail = json.getString("detail");
							System.out.println("detail=" + detail);
							// detail is a object incloud 3 item
							JSONObject detailJson = new JSONObject(detail);
							setUserName(stringFormat(detailJson.getString("userName")));
							setUserPhone(stringFormat(detailJson.getString("userPhone")));
							setUserAddress(stringFormat(detailJson.getString("userAddress")));
							setReward(stringFormat(detailJson.getString("reward")));
							setLocation(stringFormat(detailJson.getString("location")));
							if (!detailJson.getString("location").isEmpty()) {
								System.out.println();
								setLongitude(Double.valueOf(doubleFormat(detailJson.getString("longitude"))));
								setLatitude(Double.valueOf(doubleFormat(detailJson.getString("latitude"))));
							}
						} else {
							System.out.println("no detail");
							setUserName("");
							setUserPhone("");
							setUserAddress("");
							setReward("");
							setLocation("");
						}

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}
	};

	private void oldDetailDao() {
		try {
			StringRequest request = new StringRequest(Request.Method.POST, "https://beacon-series.herokuapp.com/getOld",
					mResponseListener, mErrorListener) {
				protected Map<String, String> getParams() throws AuthFailureError {
					Map<String, String> map = new HashMap<String, String>();
					map.put("user", user);
					return map;
				}
			};
			NetworkManager.getInstance(c).request(null, request);
		} catch (Exception e) {
			if (user == null) {
				System.out.println("�Х�serUser()");
			}
		}
	}

	private Listener<String> mResponseListener = new Listener<String>() {
		@Override
		public void onResponse(String str) {
			if (str.trim().equals("no detail")) {
				System.out.println("no detail");
			} else {

				try {
					JSONArray ary = new JSONArray(str);
					for (int i = 0; i < ary.length(); i++) {
						JSONObject json = ary.getJSONObject(i);
						if (!json.getString("old_detail").isEmpty()) {

							String old_detail = json.getString("old_detail");
							System.out.println("old_detail=" + old_detail);
							// detail is a object incloud 5 item
							JSONObject detailJson = new JSONObject(old_detail);
							setBeaconId(stringFormat(detailJson.getString("beaconId")));
							setOldName(stringFormat(detailJson.getString("oldName")));
							setOldCharacteristic(stringFormat(detailJson.getString("oldCharacteristic")));
							setOldHistory(stringFormat(detailJson.getString("oldhistory")));
							setOldClothes(stringFormat(detailJson.getString("oldclothes")));
							setOldAddr(stringFormat(detailJson.getString("oldaddr")));

						} else {
							setBeaconId("");
							setOldName("");
							setOldCharacteristic("");
							setOldHistory("");
							setOldClothes("");
							setOldAddr("");

						}

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}
	};
	private ErrorListener mErrorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e("Error", error.toString());
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

	public String doubleFormat(String str) {
		System.out.println("�g�n�� = " + str);
		if (str.equals("null") || str == null) {
			return "0.0";
		}
		System.out.println("double = " + str);
		return str;
	}

	private void showMessage(String msg) {
		Toast.makeText(c, msg, Toast.LENGTH_LONG).show();
	}
	// ===========================for gcm s==================

	private void pushGCMDAO(String oldName, String beaconId) {
		GPSHelper g = null;
		LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
		if (status.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			// �p�GGPS�κ����w��}�ҡA�I�slocationServiceInitial()��s��m
			g = new GPSHelper(c);

		} else {
			showMessage("�ж}�ҩw��A��,�Ψϥ�googlemap �����m");
			// startActivity(new
			// Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			// //�}�ҳ]�w����
		}

		if (g.getGPS().size() > 0 || g != null) {
			System.out.println("g:" + g.getGPS().get(0) + ",a : " + g.getGPS().get(1));
			final String oldNamev = oldName;
			final String longitude = g.getGPS().get(0);
			final String latitude = g.getGPS().get(1);
			final String beaconIdv = beaconId;
			Log.d("�g�n��", g.getGPS().get(0));
			Log.d("�g�n��", g.getGPS().get(1));
			StringRequest request = new StringRequest(Request.Method.POST,
					"https://beacon-series.herokuapp.com/findReport", mResPush, mErrorListener) {
				protected Map<String, String> getParams() throws AuthFailureError {
					Map<String, String> map = new HashMap<String, String>();
					map.put("user", user);
					map.put("oldName", oldNamev);
					map.put("beaconId", beaconIdv);
					map.put("longitude", longitude);
					map.put("latitude", latitude);
					return map;
				}
			};
			NetworkManager.getInstance(c).request(null, request);
		} else {
			showMessage("�ж}�ҩw��A��,�Ψϥ�googlemap �����m");
		}
	}

	private Listener<String> mResPush = new Listener<String>() {
		@Override
		public void onResponse(String str) {
			if (str.trim().equals("ok")) {
//				clearMissingOldList();
//				setMissingOld();
				System.out.println("�����^���{��");
			}
			Log.d("Response", str);
		}
	};
	

}
