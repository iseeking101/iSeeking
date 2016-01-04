package tw.com.kemo.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;

import android.util.Log;
import android.content.Context;

public class MemberDao {
	
	public void getMember() {
		
			StringRequest request = new StringRequest(Request.Method.POST, "https://beaconserverjs.herokuapp.com/getMember", mResponseListenerGet, mErrorListener){
				protected Map<String, String> getParams() throws AuthFailureError {  
		        Map<String, String> map = new HashMap<String, String>();  
		       
		        return map;  
		    }  };
		    NetworkManager.getInstance(this).request(null, request);
		
	}
	private Listener<String> mResponseListenerGet = new Listener<String>() {
		@Override
		public void onResponse(String str) {
			
			try {
				JSONArray ary = new JSONArray(str);
				StringBuilder userName = new StringBuilder();
				StringBuilder userPhone = new StringBuilder();
				StringBuilder userAddress = new StringBuilder();
				for (int i = 0; i < ary.length(); i++) {
					JSONObject json = ary.getJSONObject(i);
					String detail = json.getString("detail");
					userName.append(detail);
					if (i < (ary.length()-1)){
					userName.append(",");
					}
				}
				System.out.println(userName);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
	};
	
	private ErrorListener mErrorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e("Error", error.toString());
		}
	};
}
class NetworkManager {
	
	private static NetworkManager sInstance;
	
	private RequestQueue mQueue;
	
	private NetworkManager(Context context) {
		mQueue = Volley.newRequestQueue(context.getApplicationContext());
	}
	
	public NetworkManager(MemberDao memberDao) {
		// TODO Auto-generated constructor stub
	}

	public static NetworkManager getInstance(MemberDao memberDao) {
		if (sInstance == null) {
			sInstance = new NetworkManager(memberDao);
		}
		return sInstance;
	}
	
	public void request(String tag, Request<?> request) {
		if (tag != null) {
			request.setTag(tag);
		}
		mQueue.add(request);
	}
	
	public void cancelRequest(String tag) {
		if (tag != null) {
			mQueue.cancelAll(tag);
		}
	}
	
	public void stop() {
		mQueue.stop();
	}
	
}
