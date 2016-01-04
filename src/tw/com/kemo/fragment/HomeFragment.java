package tw.com.kemo.fragment;

import android.app.Application;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;
import tw.com.kemo.activity.MenuActivity;
import tw.com.kemo.activity.MissingOldManActivity;
import tw.com.kemo.activity.OldmanActivity;
import tw.com.kemo.activity.R;
import tw.com.kemo.activity.UserApplication;
import tw.com.kemo.networkcommunication.volleymgr.NetworkManager;
import tw.com.kemo.util.ListViewAdpater;
import tw.com.kemo.util.MissingOldAdpater;
import tw.com.kemo.util.MissingOldItem;
import tw.com.kemo.util.MyFollowItem;
import tw.com.kemo.util.OldTraceItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.special.ResideMenu.ResideMenu;

/**
 * 失蹤老人列表
 */
public class HomeFragment extends Fragment {
	private UserApplication	uapp;
    private View parentView;
    private ResideMenu resideMenu;
    private ArrayList<MissingOldItem> items;
    private MissingOldAdpater adpater;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressDialog mProgress;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.home, container, false);
        uapp = (UserApplication)getActivity().getApplicationContext();
        mSwipeRefreshLayout = (SwipeRefreshLayout) parentView.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                mProgress = ProgressDialog.show(getActivity(), null, "資料更新中，請稍後...", true, false);
                
                uapp.clearMissingOldList();
                //資料若更新而畫面無更新時 點選項會報錯誤
                adpater.notifyDataSetChanged();
                missingOldDAO();
                
            }
        });
        
        return parentView;
    }
    
    //add gesture operation's ignored views
    private void setUpViews() {
        MenuActivity parentActivity = (MenuActivity) getActivity();
        resideMenu = parentActivity.getResideMenu();
        
//        parentView.findViewById(R.id.btn_open_menu).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
//            }
//        });
//       
        //add gesture operation's ignored views
        //FrameLayout ignored_view = (FrameLayout) parentView.findViewById(R.id.ignored_view);
        //resideMenu.addIgnoredView(ignored_view);
    }
    public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		setView();
		System.out.println("home on activity created");
		
	}
    //建立畫面
    public void setView(){
    	items = uapp.getMissOldList();
    	
    	ListView listView = (ListView) getActivity().findViewById(R.id.listView1);
		adpater = new MissingOldAdpater(getActivity(),items);
		
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                // 利用索引值取得點擊的項目內容。
            	
            	MissingOldItem missingOldItem = items.get(index);
            	uapp.setIndex(index);
                jumpToActivity(getActivity(),MissingOldManActivity.class);
            	
                showMessage(missingOldItem.getOldName());
            	// 顯示。  
            }
        });
        System.out.println("畫面建立");
		
		listView.setAdapter(adpater);
	
		
    }
    //判斷資料是否已改變 ，改變則自動重新建立adapter
    private String checkList() {
		// TODO Auto-generated method stub
		if (items == uapp.getMissOldList()){
			return "1";
		}else{
			return "0";
		}
	}

	public void onStart(){
    	super.onStart();
    	System.out.println("onString home");
    	
    }
    public void onResume(){
    	super.onResume();
    	System.out.println("check:"+checkList());
    	if (checkList()=="0"){
    		setView();
    	}
    	System.out.println("home onResume home");

    }
    private void showMessage(String msg){
		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
	}
    private void jumpToActivity(Context ct,Class<?> lt){
		Intent intent = new Intent();
        intent.setClass(ct, lt);
		startActivity(intent);
	}
    private void missingOldDAO() {
		StringRequest request = new StringRequest(Request.Method.POST,
				"https://beacon-series.herokuapp.com/getMissingOld", mResMissingOld, mErrorListener) {
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				return map;
			}
		};
		NetworkManager.getInstance(getActivity()).request(null, request);
	}
    private Listener<String> mResMissingOld = new Listener<String>() {
		@Override

		public void onResponse(String str) {
			if (str.trim().equals("no detail")) {
				System.out.println("no detail");
				mProgress.dismiss();
				showMessage("太好了~大家都還在~");
			} else {

				try {
					JSONArray ary = new JSONArray(str);
					System.out.println("missingold  = " + ary.length() + "個");
					ArrayList<MissingOldItem> missingOldList = new ArrayList<MissingOldItem>();
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
				setView();
				mProgress.dismiss();
				showMessage("資料已更新");
			}

		}
	};
	private ErrorListener mErrorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e("Error", error.toString());
			mProgress.dismiss();
			showMessage("資料更新失敗，請檢查網路連線");
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
