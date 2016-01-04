package tw.com.kemo.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import tw.com.kemo.networkcommunication.volleymgr.NetworkManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hereapps.ibeacon.IBeacon;

import tw.com.kemo.util.ListViewAdpater;

import tw.com.kemo.activity.R;

import tw.com.kemo.util.MyFollowItem;

public class OldGroupActivity extends Activity {

	private Button btn_update,back;
	private ArrayList<String> items;
	private List<MyFollowItem> rowitem;
	private UserApplication uapp;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.textlistview);
        
        uapp = (UserApplication)getApplicationContext();
        //items = uapp.getMyFollow();
        System.out.println("items.size = "+items.size());
        rowitem = new ArrayList<MyFollowItem>();
        
        for(int i = 0; i<items.size(); i++){
        //	rowitem.add(new MyFollowItem(items.get(i),"位置:桃園", "狀態:正常   "));
        }
        
        ListView listView = (ListView) findViewById(R.id.listView1);
		ListViewAdpater adpater = new ListViewAdpater(OldGroupActivity.this,
        		rowitem);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                // 利用索引值取得點擊的項目內容。
            	MyFollowItem items = rowitem.get(index);
//                showMessage(items.getOldName().toString()+"  "+items.getLocation().toString()+"   "
//                		+items.getOldStatus().toString());
                // 顯示。  
            }
        });
		listView.setAdapter(adpater);
        //傳入目前context 用this.
        
        
      
        }
	public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {   
            ConfirmExit();//按返回鍵，則執行退出確認
            return true;   
        }   
        return super.onKeyDown(keyCode, event);   
    }
	
    public void ConfirmExit(){//退出確認
    	jumpToActivity(OldGroupActivity.this,IndexActivity.class);	
    }
    private void getGroupMember(){
    	
        
		StringRequest request = new StringRequest(Request.Method.POST, "https://beacon-series.herokuapp.com/groupService", mResponseListenerGet, mErrorListener){
			protected Map<String, String> getParams() throws AuthFailureError {  
	        Map<String, String> map = new HashMap<String, String>();  
	        map.put("user", uapp.getUser());
	        map.put("status", "3");
	        return map;  
	    }  };
		NetworkManager.getInstance(this).request(null, request);
		
	}
	private Listener<String> mResponseListenerGet = new Listener<String>() {
		@Override
		
		public void onResponse(String str) {
			if(str.trim().equals("no detail")){
				System.out.println("no detail");
			}else{
				
			
			try {
				rowitem = new ArrayList<MyFollowItem>();
 				JSONArray ary = new JSONArray(str);
				System.out.println("ary.length = "+ary.length());
				for (int i = 0; i < ary.length(); i++) {
					JSONObject json = ary.getJSONObject(i);
					if(!json.getString("old_detail").isEmpty()){
						String old_detail = json.getString("old_detail");
						
						JSONObject detailJson = new JSONObject(old_detail);
						
//						rowitem.add(new MyFollowItem(detailJson.getString("oldName"),"位置:桃園", "狀態:正常   "));
//						System.out.println(rowitem.get(i).getOldName());
						
					}else{
						
						System.out.println("old_detail is emptys");
					}
					
				}
				ListView listView = (ListView) findViewById(R.id.listView1);
				ListViewAdpater adpater = new ListViewAdpater(OldGroupActivity.this,
		        		rowitem);
		        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		            @Override
		            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
		                // 利用索引值取得點擊的項目內容。
		            	MyFollowItem items = rowitem.get(index);
//		                showMessage(items.getOldName().toString()+"  "+items.getLocation().toString()+"   "
//		                		+items.getOldStatus().toString());
		                // 顯示。  
		            }
		        });
				listView.setAdapter(adpater);
				
				
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
	private void showMessage(String msg){
		Toast.makeText(OldGroupActivity.this, msg, Toast.LENGTH_LONG).show();
	}
	
	private class exeAsync extends AsyncTask<Void, Integer, String>
	{
	
	    protected void onPreExecute (){
	    
	    }

	    protected String doInBackground(Void...arg0) {
	    	 return null;
	    }
	    protected void onProgressUpdate(Integer... progress) {
            // Set progress percentage
           
        }

	    protected void onPostExecute(String result) {
	 
	    }
	  
	}

	protected void onDestroy() {
		super.onDestroy();
		
	
	}
	private void jumpToActivity(Context ct,Class<?> lt){
		
		
		Intent intent = new Intent();
        intent.setClass(ct, lt);
		startActivity(intent);
		
	}
	

}
