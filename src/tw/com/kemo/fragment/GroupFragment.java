package tw.com.kemo.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import tw.com.kemo.activity.FollowActivity;
import tw.com.kemo.activity.MissingOldManActivity;
import tw.com.kemo.activity.R;
import tw.com.kemo.activity.UserApplication;
import tw.com.kemo.networkcommunication.volleymgr.NetworkManager;
import tw.com.kemo.util.ListViewAdpater;
import tw.com.kemo.util.MissingOldAdpater;
import tw.com.kemo.util.MissingOldItem;
import tw.com.kemo.util.MyFollowAdpater;
import tw.com.kemo.util.MyFollowItem;
import tw.com.kemo.util.MyOldManItem;
import tw.com.kemo.util.OldTraceItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;

/**
 * �ڰl�ܪ��ѤH�C��
 */
// �ڪ��s�ժ��e��
public class GroupFragment extends Fragment {
	private View parentView;
	private ArrayList<MyFollowItem> items;
	private UserApplication uapp;
	private MyFollowAdpater adpater;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ProgressDialog mProgress;
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		parentView = inflater.inflate(R.layout.calendar, container, false);
		uapp = (UserApplication) getActivity().getApplicationContext();

		mSwipeRefreshLayout = (SwipeRefreshLayout) parentView.findViewById(R.id.refresh_layout);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSwipeRefreshLayout.setRefreshing(false);
				mProgress = ProgressDialog.show(getActivity(), null, "��Ƨ�s���A�еy��...", true, false);

				uapp.clearFollowList();
				// ��ƭY��s�ӵe���L��s�� �I�ﶵ�|�����~
				System.out.println("����dao");
				myFollowDAO();
				
			}
		});

		return parentView;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setView();
	}

	public void setView() {
		
		items = uapp.getMyFollow();
		System.out.println("GroupFragment �Ұʫإߵe�� MyFollowList �ƥ�"+items.size());
		ListView listView = (ListView) getActivity().findViewById(R.id.listView);
		adpater = new MyFollowAdpater(getActivity(), items);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
				// �Q�ί��ޭȨ��o�I�������ؤ��e�C
				MyFollowItem myFollowItem = items.get(index);
				uapp.setIndex(index);
//				for(int i =0 ; i<uapp.getMissOldList().size();i++){
//					if(uapp.getMissOldList().get(i).getBeaconId()
//							.equals(myFollowItem.getBeaconId())){
//						uapp.setIndex(i);
//						break;
//					}
					jumpToActivity(getActivity(),FollowActivity.class);
	            	
//				}
				showMessage(myFollowItem.getOldName().toString());
				// ��ܡC
			}
		});
		listView.setAdapter(adpater);
	}

	public void onResume() {
		super.onResume();
		setView();
	}

	private void showMessage(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
	}
	private void myFollowDAO() {
		StringRequest request = new StringRequest(Request.Method.POST,
				"https://beacon-series.herokuapp.com/getMyFollow", mResMyFollow, mErrorListener) {
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("user", uapp.getUser());
				return map;
			}
		};
		NetworkManager.getInstance(getActivity()).request(null, request);
	}
	private Listener<String> mResMyFollow = new Listener<String>() {
		@Override

		public void onResponse(String str) {
			if (str.trim().equals("no detail")) {
				System.out.println("no detail");
				mProgress.dismiss();
				showMessage("�Ӧn�F~�j�a���٦b~");
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
							// statusv =1 ��ܳo�ѤH����
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
						uapp.setMyFollowManForListView(myFollowList);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
				setView();
				mProgress.dismiss();
				showMessage("��Ƥw��s");
			}

		}
	};
	private ErrorListener mErrorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e("Error", error.toString());
			mProgress.dismiss();
			showMessage("��Ƨ�s���ѡA���ˬd�����s�u");
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
	private void jumpToActivity(Context ct,Class<?> lt){
		Intent intent = new Intent();
        intent.setClass(ct, lt);
		startActivity(intent);
	}

}
