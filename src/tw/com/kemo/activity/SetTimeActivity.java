package tw.com.kemo.activity;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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

public class SetTimeActivity extends Activity {

	private Button btn_update,back;




	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settime);

        btn_update = (Button)findViewById(R.id.btn_update);
        back = (Button)findViewById(R.id.back);
        
        
      
        btn_update.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new exeAsync().execute();
			}
        	
        });
        back.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v){
				jumpToActivity(SetTimeActivity.this,IndexActivity.class);				
			}
		});
        
        
       
        
    }

	private void updateTime(String old_nameText, String old_characteristicText, String old_historyText, String old_clothesText,String old_addrText){
		final String n, ch, h, cl, a;
		
		n = old_nameText;
		ch = old_characteristicText;
		h = old_historyText;
		cl = old_clothesText;
		a= old_addrText;
		StringRequest request = new StringRequest(Request.Method.POST, "https://beacon-series.herokuapp.com/updateOld", mResponseListener, mErrorListener){
			protected Map<String, String> getParams() throws AuthFailureError {  
	        Map<String, String> map = new HashMap<String, String>();
	        Bundle bundle = getIntent().getExtras();
	        map.put("user", bundle.getString("user"));
	        map.put("oldName", n);  
	        map.put("oldCharacteristic", ch);  
	        map.put("oldhistory", h);
	        map.put("oldclothes", cl);
	        map.put("oldaddr", a);
	        return map;  
	    }  };
		NetworkManager.getInstance(this).request(null, request);
	}
	private Listener<String> mResponseListener = new Listener<String>() {

		@Override
		public void onResponse(String str) {
			
			if(str.trim().equals("ok")){showMessage("н╫зяжие\");}
			Log.d("Response", str);
		}
	};

	private ErrorListener mErrorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e("Error", error.toString());
		}
	};
	private void showMessage(String msg){
		Toast.makeText(SetTimeActivity.this, msg, Toast.LENGTH_LONG).show();
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
		
		
		Bundle bundle = new Bundle();
		Bundle bundleUser = getIntent().getExtras();
        
        bundle.putString(
                "user", bundleUser.getString("user"));
        Intent intent = new Intent();
        intent.putExtras(bundle);
		intent.setClass(ct, lt);
		startActivity(intent);
		
	}
	

}
