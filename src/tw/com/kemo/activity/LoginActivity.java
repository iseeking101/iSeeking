package tw.com.kemo.activity;

import java.io.IOException;
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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import tw.com.kemo.networkcommunication.volleymgr.NetworkManager;
import tw.com.kemo.util.MD5;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GoogleCloudMessaging;


/** 
 *燈入頁面，登入成功後先取得必要資料再轉跳到主頁面(MenuActivity)
*/ 
public class LoginActivity extends Activity {

	private EditText input_ac, input_wd;
	private Button loginButton, registerButton;
	private ProgressDialog mProgress;
	private ImageView img_login;
	private UserApplication uapp;
	private final String TAG="Login";
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
        
        input_ac = (EditText)findViewById(R.id.input_ac);
		input_wd = (EditText)findViewById(R.id.input_wd);

		registerButton = (Button)findViewById(R.id.toRegister);
		loginButton = (Button)findViewById(R.id.login);
		
		img_login =(ImageView)findViewById(R.id.img_login);
        uapp = (UserApplication)getApplicationContext();
		
        //登入按鈕
		loginButton.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v){
				if(input_ac.getText().toString().isEmpty() || input_wd.getText().toString().isEmpty()){
					showMessage("please fill all");
				}else{
					loginButton.setEnabled(false);
					//狀態列
					mProgress = ProgressDialog.show(LoginActivity.this, null, "登入中...", true, false);
					
					//System.out.println("press login");
					//背景執行
					new LoginAsync().execute();
					
					
				}
			}
		});
		
		registerButton.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v){	
				jumpToActivity(LoginActivity.this,RegisterActivity.class);
			}
		});
        
    }
	private class LoginAsync extends AsyncTask<Void, Integer, String>
	{
	
	    protected void onPreExecute (){
	    
	    }

	    protected String doInBackground(Void...arg0) {

	    	serverPost(input_ac.getText().toString(),input_wd.getText().toString());
	        return null;
	    }
	    protected void onProgressUpdate(Integer... progress) {
            // Set progress percentage
           
        }

	    protected void onPostExecute(String result) {
	 
	    }
	  
	}
	
	//method post
	private void serverPost(String...post){
		final String u,p;
		u = post[0];
		//密碼加密
		p = MD5.getMD5(post[1]);
		StringRequest request = new StringRequest(Request.Method.POST, "https://beacon-series.herokuapp.com/login", mResponseListener, mErrorListener){
			protected Map<String, String> getParams() throws AuthFailureError {  
	        Map<String, String> map = new HashMap<String, String>();  
	        map.put("user", u);  
	        map.put("password", p);  
	        return map;  
	    }};
	    
		NetworkManager.getInstance(this).request(null, request);
	}
	
	//訊息快顯
	private void showMessage(String msg){
		Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
	}
	
	private Listener<String> mResponseListener = new Listener<String>() {
		
		@Override
		//回傳訊息種類
		public void onResponse(String str) {
			
			switch(Integer.parseInt(str.trim())){
				case 0:
					showMessage("worng account or password");
					loginButton.setEnabled(true);
					mProgress.dismiss();
					break;
				case 1:
				    doLogin();
				    loginButton.setEnabled(true);
				    break;
				case 2:
					showMessage("check comfirm email");
					loginButton.setEnabled(true);
					mProgress.dismiss();
					break;	
			}
			
			Log.d("Response", str);
			/*解析json
			try {
				JSONArray ary = new JSONArray(str);
				StringBuilder users = new StringBuilder();
				StringBuilder passwords = new StringBuilder();
				for (int i = 0; i < ary.length(); i++) {
					JSONObject json = ary.getJSONObject(i);
					String user = json.getString("user");
					users.append(user);
					if (i < (ary.length()-1)){
					users.append(",");
					}
					String password = json.getString("password");
					passwords.append(password);
					if (i < ary.length()-1 ){
						passwords.append(",");
					}
					
				}
				
				
				System.out.println("users:"+users.toString());
				System.out.println("passwords:"+passwords.toString());

				
			} catch (JSONException e) {
				e.printStackTrace();
			}*/
		}
	};
	private ErrorListener mErrorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e("Error", error.toString());
			loginButton.setEnabled(true);
			mProgress.dismiss();
			showMessage("登入逾時，請再試一次");
		}
	};
	//切換activity
	private void jumpToActivity(Context ct,Class<?> lt){
		
		Intent intent = new Intent();
		intent.setClass(ct, lt);
		//startActivityForResult(intent,0);
		startActivity(intent);
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		switch(resultCode){
		case RESULT_OK:
			System.out.println("RESULT_OK");
			break;
		default:
			break;
		
		}
	}
	private void jumpToIndex(Context ct,Class<?> lt){
		Intent intent = new Intent();
		intent.setClass(ct, lt);
		startActivity(intent);
		
	}
	protected void onDestroy() {
		super.onDestroy();
		

	}	
	public void doLogin() {
		DataTask task = new DataTask();
		task.execute();
	}
	public void onResume(){
		super.onResume();
	}
	private class DataTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			Log.d(TAG, "Loinging");
				
				uapp.setUser(input_ac.getText().toString().trim());
				uapp.setMyFollow();
			    uapp.setAllMemberDetail();
			    uapp.setMissingOld();
			    uapp.setMyOldMan();
			    
			   
			    
			//傳值到server後會立即進入app造成資料還未載入完成，暫停三秒後再登入
			try {
				Thread.sleep(8000);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			
			if (mProgress != null) {
				mProgress.dismiss();
				jumpToIndex(LoginActivity.this,MenuActivity.class);
				finish();
			}
		}

	}
	
	
	
	
}
