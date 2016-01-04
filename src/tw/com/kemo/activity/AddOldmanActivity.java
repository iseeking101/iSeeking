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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import tw.com.kemo.fragment.MyOldManFragment;
import tw.com.kemo.networkcommunication.volleymgr.NetworkManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.special.ResideMenu.ResideMenu;
/** 
* AddOldmanActivity 新增追蹤老人. 
*/ 
public class AddOldmanActivity extends Activity {
	FragmentManager fragmentManager;
	private TextView beacon_id_text; 
	private Button old_update,old_back,getBeaconId,groupManaber,btn_add;
	private Switch old_characteristicButton,old_historyButton,old_addrButton;
	private EditText old_nameText,old_characteristicText,old_historyText,old_clothesText,old_addrText;
	private UserApplication uapp;
	private ProgressDialog mProgress;
	private ProgressDialog mProgress2;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_oldman);
        
        beacon_id_text = (TextView)findViewById(R.id.beacon_id_text);
        old_nameText = (EditText)findViewById(R.id.old_nameText);
        old_characteristicText = (EditText)findViewById(R.id.old_characteristicText);
        old_historyText =  (EditText)findViewById(R.id.old_historyText);
        old_clothesText =  (EditText)findViewById(R.id.old_clothesText);
        old_addrText=  (EditText)findViewById(R.id.old_addrText);
        old_update = (Button)findViewById(R.id.old_update);
        getBeaconId=(Button)findViewById(R.id.getBeaconId);
        groupManaber=(Button)findViewById(R.id.groupManaber);
        btn_add= (Button)findViewById(R.id.btn_add);
        uapp = (UserApplication) getApplicationContext();
        
        btn_add.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		if(beacon_id_text.getText().toString().equals("")){
        			showMessage("請先取得beacon");
        		}else{
        			doAdd();
        		}
        	}
        });
        
        getBeaconId.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		jumpToBeaconActivity(AddOldmanActivity.this,BeaconActivity.class);
        	}
        });
        
    }

	private void addOld(String beacon_id_text,String old_nameText, String old_characteristicText, String old_historyText, String old_clothesText,String old_addrText){
		final String n, ch, h, cl, a,bid;
		
		bid = beacon_id_text;
		n = old_nameText;
		ch = old_characteristicText;
		h = old_historyText;
		cl = old_clothesText;
		a= old_addrText;
		StringRequest request = new StringRequest(Request.Method.POST, "https://beacon-series.herokuapp.com/addOld", mResponseListener, mErrorListener){
			protected Map<String, String> getParams() throws AuthFailureError {  
	        Map<String, String> map = new HashMap<String, String>();
	        map.put("user",uapp.getUser());
	        map.put("beaconId",bid);
	        map.put("oldName", n);  
	        map.put("oldCharacteristic", ch);  
	        map.put("oldhistory", h);
	        map.put("oldclothes", cl);
	        map.put("oldaddr", a);
	        return map;  
	    }  };
		NetworkManager.getInstance(this).request(null, request);
	}
	public void doUpdate() {
		mProgress2 = ProgressDialog.show(this, null, "更新中...", true, false);
		updateTask task = new updateTask();
		task.execute();
	}
	public void doAdd() {
		mProgress = ProgressDialog.show(this, null, "新增中...", true, false);
		addTask task = new addTask();
		task.execute();
	}
	private class addTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			addOld(beacon_id_text.getText().toString(),old_nameText.getText().toString(),old_characteristicText.getText().toString(),old_historyText.getText().toString(),old_clothesText.getText().toString(),old_addrText.getText().toString());
    		
			    
			//傳值到server後會立即進入app造成資料還未載入完成，暫停三秒後再登入
			try {
				Thread.sleep(3000);
				
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
				
			}
		}

	}
	private class updateTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			
				 
			    
			//傳值到server後會立即進入app造成資料還未載入完成，暫停三秒後再登入
			try {
				Thread.sleep(2000);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (mProgress2 != null) {
				mProgress2.dismiss();
				AddOldmanActivity.this.finish();
			}
		}

	}
	private Listener<String> mResponseListener = new Listener<String>() {

		@Override
		public void onResponse(String str) {
			if(str.trim().equals("ok") || str.trim().equals("OK")){
			showMessage("新增成功");
			uapp.clearMyOldMan();
			uapp.setMyOldMan();
			uapp.setMissingOld();
			}
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
		Toast.makeText(AddOldmanActivity.this, msg, Toast.LENGTH_LONG).show();
	}

	
	private void getOld(){

		beacon_id_text.setText(uapp.getBeaconId());
		old_nameText.setText(uapp.getOldName());
		old_characteristicText.setText(uapp.getOldCharacteristic());
		old_historyText.setText(uapp.getOldHistory());
		old_clothesText.setText(uapp.getOldClothes());
		old_addrText.setText(uapp.getOldAddr());
	}
	protected void onDestroy() {
		super.onDestroy();
	}
private void jumpToBeaconActivity(Context ct,Class<?> lt){
		
		
		Bundle bundle = new Bundle();
		bundle.putString(
                "addCheck", "add");
        bundle.putString(
                "user", uapp.getUser());
        bundle.putString(
                "oldName", old_nameText.getText().toString());
        bundle.putString(
                "oldCharacteristic", old_characteristicText.getText().toString());
        bundle.putString(
                "oldhistory", old_historyText.getText().toString());
        bundle.putString(
                "oldclothes", old_clothesText.getText().toString());
        bundle.putString(
                "oldaddr", old_addrText.getText().toString());
        Intent intent = new Intent();
        intent.putExtras(bundle);
		intent.setClass(ct, lt);
		//startActivity(intent);
		startActivityForResult(intent,0);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		switch(resultCode){
		case RESULT_OK:
			Bundle bundle = data.getExtras();
			beacon_id_text.setText(bundle.getString("returnBeaconId"));
			break;
		default:
			break;
		
		}
	}
//	public void onResume(){
//		super.onResume();
//	}
	public String stringFormat(String str){
		String strv=str;
		if(strv.equals("null") || strv==null){
			strv="";
		}
		return strv;
	}
	
	

}
