package tw.com.kemo.activity;

import java.util.HashMap;
import java.util.Map;




import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;
import tw.com.kemo.networkcommunication.volleymgr.NetworkManager;
import tw.com.kemo.util.MD5;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * 註冊頁面 
 */ 
public class RegisterActivity extends Activity {
	//宣告有有元件連結話面與java
    private EditText input_ac, input_wd, input_email;
	private Button  registerButton;
	private ProgressDialog mProgress;
	@Override
	//啟動app後會跑的第一個方法
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.register);
      
        input_email = (EditText)findViewById(R.id.input_email);
		input_ac = (EditText)findViewById(R.id.input_ac);
        input_wd = (EditText)findViewById(R.id.input_wd);
        
		
		registerButton = (Button)findViewById(R.id.register);
		System.out.println("oncreate in register");
		registerButton.setOnClickListener(new OnClickListener(){
		
			public void onClick(View v){
				if(input_ac.getText().toString().isEmpty()|| input_wd.getText().toString().isEmpty() || input_email.getText().toString().isEmpty()){
					showMessage("please fill all");
				}else{
					System.out.println("press register");
					mProgress = ProgressDialog.show(RegisterActivity.this, null, "登入中...", true, false);
					
					register(input_ac.getText().toString(),input_wd.getText().toString(),input_email.getText().toString());					
				}
			}
		});

       
    }
	private void jumpToActivity(Context ct,Class<?> lt){
		
		Intent intent = new Intent();
		//intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setClass(ct, lt);
		//RegisterActivity.this.setResult(RESULT_OK,intent);
		//RegisterActivity.this.finish();
		
		startActivity(intent);
		finish();
		
	}	
	//register
	private void register(String user,String password, String email){
		System.out.println("get into the register method");
		final String u ,p ,e;
		u = user;
		p = MD5.getMD5(password);
		e = email;
		
		StringRequest request = new StringRequest(Request.Method.POST, "https://beacon-series.herokuapp.com/register", mResponseListenerRis, mErrorListener){
			protected Map<String, String> getParams() throws AuthFailureError {  
		        Map<String, String> map = new HashMap<String, String>();  
		        map.put("user", u);  
		        map.put("password", p);  
		        map.put("email", e);
		        return map;  
		    }
		};
		NetworkManager.getInstance(this).request(null, request);	
	}
	private void showMessage(String msg){
		Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();		
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		
	}	 
	private Listener<String> mResponseListenerRis = new Listener<String>() {

		@Override
		public void onResponse(String str) {
			mProgress.dismiss();
			if(str.trim().equals("OK")){
				showMessage("register success!! 請收認證信");
				mProgress.dismiss();
				finish();
			}
			if(str.trim().equals("exist")){
				showMessage("帳號已存在");
				mProgress.dismiss();
			}
			
			Log.d("Response", str);
			
		}
	};
	
	private ErrorListener mErrorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e("Error", error.toString());
			mProgress.dismiss();
		}
	};

	
	
	
	
}

