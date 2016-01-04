package tw.com.kemo.fragment;

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
import com.special.ResideMenu.ResideMenu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import tw.com.kemo.activity.AddOldmanActivity;
import tw.com.kemo.activity.MenuActivity;
import tw.com.kemo.activity.OldmanActivity;
import tw.com.kemo.activity.R;
import tw.com.kemo.activity.UserApplication;
import tw.com.kemo.networkcommunication.volleymgr.NetworkManager;
import tw.com.kemo.util.ListViewAdpater;
import tw.com.kemo.util.MissingOldAdpater;
import tw.com.kemo.util.MissingOldItem;
import tw.com.kemo.util.MyFollowItem;
import tw.com.kemo.util.MyOldManAdpater;
import tw.com.kemo.util.MyOldManItem;

/**
 * 我的老人列表
 */
public class MyOldManFragment extends Fragment {
	
	private ArrayList<MyOldManItem> items;
	private UserApplication uapp;
	private ImageView btn_add;
	private View parentView;
	FragmentManager fragmentManager;
	private MyOldManAdpater adpater;
    private ResideMenu resideMenu;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressDialog mProgress;
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	parentView = inflater.inflate(R.layout.myoldman, container, false);
    	fragmentManager = getFragmentManager();
    	uapp = (UserApplication)getActivity().getApplicationContext();
    	 
    	mSwipeRefreshLayout = (SwipeRefreshLayout) parentView.findViewById(R.id.refresh_layout);
    	   mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
               @Override
               public void onRefresh() {
                   mSwipeRefreshLayout.setRefreshing(false);
                   mProgress = ProgressDialog.show(getActivity(), null, "資料更新中，請稍後...", true, false);
           		
                   uapp.clearMyOldMan();
                   
                   //資料若更新而畫面無更新時 點選項會報錯誤
                   adpater.notifyDataSetChanged();
                   getMyOldDAO();
                   
               }
           });
           
           return parentView;
       }
	//判斷資料是否已改變 ，改變則自動重新建立adapter
    private String checkList() {
		if (items == uapp.getMyOldManList()){
			return "1";
		}else{
			return "0";
		}
	}
    public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		setView();
		System.out.println("profile on activity created");
		
    	
	}
    public void setView(){
		items = uapp.getMyOldManList();
		ListView listView = (ListView) getActivity().findViewById(R.id.listView);
	    adpater = new MyOldManAdpater(getActivity(),
        		items);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                // 利用索引值取得點擊的項目內容。
            	MyOldManItem missingOldItem = items.get(index);
            	if(items.size()<index){
            		uapp.setIndex(0);
            	}else{
            		uapp.setIndex(index);
                    
            	}
            	jumpToActivity(getActivity(),OldmanActivity.class);
            	showMessage(missingOldItem.getOldName());
                // 顯示。  
            }
        });
		listView.setAdapter(adpater);

    	btn_add = (ImageView)parentView.findViewById(R.id.btn_add);
    	ButtonListener b =new ButtonListener();
    	btn_add.setOnClickListener(b);
    	btn_add.setOnTouchListener(b);
    }
    //進入畫面重新載入資料
    public void onStart() {
        super.onStart();
        
    }
    public void onResume(){
    	super.onResume();
    	if(checkList()=="0"){
    		setView();
    	}
    	System.out.println("profile on resume");
    }
    
    private void showMessage(String msg){
		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
	}
    
    class ButtonListener implements OnClickListener, OnTouchListener{

		public void onClick(View v) {
			if(v.getId() == R.id.btn_add){
				Log.d("test", "cansal button ---> click");
				jumpToActivity(getActivity(),AddOldmanActivity.class);
				
			}
		}

		public boolean onTouch(View v, MotionEvent event) {
			if(v.getId() == R.id.btn_add){
				if(event.getAction() == MotionEvent.ACTION_UP){
					Log.d("test", "cansal button ---> cancel");
					btn_add.setBackgroundResource(R.drawable.new_unselected);
				} 
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					Log.d("test", "cansal button ---> down");
					
					btn_add.setBackgroundResource(R.drawable.new_selected);
					
				}
			}
			return false;
		}
		
    }
    private void jumpToActivity(Context ct,Class<?> lt){
		Intent intent = new Intent();
        intent.setClass(ct, lt);
		startActivity(intent);
	}

	private void getMyOldDAO() {
		StringRequest request = new StringRequest(Request.Method.POST, "https://beacon-series.herokuapp.com/getOld",
				mResMyOld, mErrorListener) {
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("user", uapp.getUser());
				return map;
			}
		};
		NetworkManager.getInstance(getActivity()).request(null, request);

	}
	private Listener<String> mResMyOld = new Listener<String>() {
		@Override

		public void onResponse(String str) {
			if (str.trim().equals("no detail")) {
				System.out.println("no detail");
				mProgress.dismiss();
				showMessage("目前還沒有新增老人唷!");
			} else {
				try {
					// "beaconId": "15345164-67AB-3E49-F9D6-E290000000082616",
					// "oldName": "鄒亞文",
					// "oldCharacteristic": "美女",
					// "oldhistory": "神經病",
					// "oldclothes": "\u2026qcgc\n",
					// "oldaddr": "\u2026",// "groupMember": [
					ArrayList<MyOldManItem> myOldManList = new ArrayList<MyOldManItem>();
					
					JSONArray ary = new JSONArray(str);
					for (int i = 0; i < ary.length(); i++) {
						JSONObject json = ary.getJSONObject(i);
						JSONArray aryGroup = json.getJSONArray("groupMember");
						ArrayList<String> groupMemberv = new ArrayList<String>();
						for (int k = 0; k < aryGroup.length(); k++) {
							groupMemberv.add(aryGroup.get(k).toString());
						}
						myOldManList.add(new MyOldManItem(stringFormat(json.get("beaconId").toString()),
								stringFormat(json.get("oldName").toString(), "未知"), R.drawable.nobody,
								stringFormat(json.get("oldCharacteristic").toString()),
								stringFormat(json.get("oldhistory").toString()),
								stringFormat(json.get("oldclothes").toString()),
								stringFormat(json.get("oldaddr").toString()), groupMemberv,
								stringFormat(json.get("statusv").toString())));
					}
					System.out.println(myOldManList.size());
					uapp.setMyOldManForListView(myOldManList);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				System.out.println("畫面更新");
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
