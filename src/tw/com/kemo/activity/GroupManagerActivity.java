package tw.com.kemo.activity;

import java.util.HashMap;
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

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import tw.com.kemo.networkcommunication.volleymgr.NetworkManager;



/** 
 * GroupManagerActivity 新增用戶讓其可以得知此老人的訊息
*/ 
public class GroupManagerActivity extends Activity {

	private Button addGroupMember,back;
	private EditText editGroupMember;
	private TextView txtGroupMember;
	private UserApplication uapp;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grouphost);
        
        editGroupMember = (EditText)findViewById(R.id.editGroupMember);
		addGroupMember = (Button)findViewById(R.id.addGroupMember);
		txtGroupMember = (TextView)findViewById(R.id.txtGroupMember);
		uapp = (UserApplication) getApplicationContext();
		
		getGroupMember();
		addGroupMember.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v){
				if(editGroupMember.getText().toString().isEmpty()){
					showMessage("please fill all");
				}else{
					addGroupMember.setEnabled(false);
					new TestAsync().execute();
					
					
				}
			}
		});
		
		
        
    }
	private class TestAsync extends AsyncTask<Void, Integer, String>
	{
	
	    protected void onPreExecute (){
	    
	    }

	    protected String doInBackground(Void...arg0) {

	    	addGroupMember(editGroupMember.getText().toString());
	        return null;
	    }
	    protected void onProgressUpdate(Integer... progress) {
            // Set progress percentage
           
        }

	    protected void onPostExecute(String result) {
	    	
	    }
	  
	}
	private void getGroupMember(){
		String gm= "";
		for(int i =0;i<uapp.getMyOldManList().get(uapp.getIndex()).getGroupMember().size();i++){
			gm += uapp.getMyOldManList().get(uapp.getIndex()).getGroupMember().get(i);
			if( i != uapp.getMyOldManList().get(uapp.getIndex()).getGroupMember().size()-1){
				gm += ",";
			}
		}
		txtGroupMember.setText(gm);
	}
	private void getRefreshView(){
		txtGroupMember.setText(uapp.getGroupMember()+","+editGroupMember.getText().toString());
	}
	
	private void addGroupMember(String editGroupMember){
		final String e;
		
		e = editGroupMember;
		
		StringRequest request = new StringRequest(Request.Method.POST, "https://beacon-series.herokuapp.com/groupService", mResponseListener, mErrorListener){
			protected Map<String, String> getParams() throws AuthFailureError {  
	        Map<String, String> map = new HashMap<String, String>();
	        map.put("user", uapp.getUser());
	        map.put("statusv", "1");
	        map.put("groupMember",e);
	        map.put("beaconId", uapp.getMyOldManList().get(uapp.getIndex()).getBeaconId());
	        
	        return map;  
	    }  };
		NetworkManager.getInstance(this).request(null, request);
	}
	private void showMessage(String msg){
		Toast.makeText(GroupManagerActivity.this, msg, Toast.LENGTH_LONG).show();
	}
	
	private Listener<String> mResponseListener = new Listener<String>() {

		@Override
		public void onResponse(String str) {
			
			if(str.trim().equals("ok")){
				showMessage("新增成功"); 
				uapp.getMyOldManList().get(uapp.getIndex()).getGroupMember().add(editGroupMember.getText().toString());
				addGroupMember.setEnabled(true);
				 getGroupMember();
				//txtGroupMember.setText(txtGroupMember.getText().toString()+editGroupMember.getText().toString());
				editGroupMember.setText("");
			}
			if(str.trim().equals("exist")){showMessage("成員已存在");addGroupMember.setEnabled(true);editGroupMember.setText("");}
			if(str.trim().equals("no user")){showMessage("no user");addGroupMember.setEnabled(true);editGroupMember.setText("");}
			Log.d("Response", str);
			
		}
	};
	private ErrorListener mErrorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e("Error", error.toString());addGroupMember.setEnabled(true);
		}
	};
	//切換activity
	private void jumpToActivity(Context ct,Class<?> lt){
		
		
		Intent intent = new Intent();
        intent.setClass(ct, lt);
		startActivity(intent);
	}
}
