package tw.com.kemo.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.special.ResideMenu.ResideMenu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import tw.com.kemo.activity.BeaconActivity;
import tw.com.kemo.activity.MemberActivity;
import tw.com.kemo.activity.MenuActivity;
import tw.com.kemo.activity.OldmanActivity;
import tw.com.kemo.activity.R;
import tw.com.kemo.activity.UserApplication;

import tw.com.kemo.networkcommunication.volleymgr.NetworkManager;
import tw.com.kemo.util.ListViewAdpater;
import tw.com.kemo.util.nouser_MemberListViewItem;
import tw.com.kemo.util.nouse_MemberSittingAdpater;
import tw.com.kemo.util.MissingOldAdpater;
import tw.com.kemo.util.MissingOldItem;
import tw.com.kemo.util.MyFollowItem;
import tw.com.kemo.util.MyOldManAdpater;
import tw.com.kemo.util.MyOldManItem;

/**
 * 新增老人畫面
 */

public class AddOldManFragment extends Fragment {
	

	private TextView beacon_id_text; 
	private Button old_update,old_back,getBeaconId,groupManaber,btn_add;
	private Switch old_characteristicButton,old_historyButton,old_addrButton;
	private EditText old_nameText,old_characteristicText,old_historyText,old_clothesText,old_addrText;
	private UserApplication uapp;
	private View parentView ;
    NetworkManager nm;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	parentView = inflater.inflate(R.layout.add_oldman, container, false);
    	nm= NetworkManager.getInstance(getActivity());
        
    	uapp = (UserApplication)getActivity().getApplicationContext();
    	setUpViews(parentView);
    	return parentView;
         
    }
    private void setUpViews(View parentView) {
         
    	 
        beacon_id_text = (TextView)parentView.findViewById(R.id.beacon_id_text);
        old_nameText = (EditText)parentView.findViewById(R.id.old_nameText);
        old_characteristicText = (EditText)parentView.findViewById(R.id.old_characteristicText);
        old_historyText =  (EditText)parentView.findViewById(R.id.old_historyText);
        old_clothesText =  (EditText)parentView.findViewById(R.id.old_clothesText);
        old_addrText=  (EditText)parentView.findViewById(R.id.old_addrText);
        old_update = (Button)parentView.findViewById(R.id.old_update);
        getBeaconId=(Button)parentView.findViewById(R.id.getBeaconId);
        groupManaber=(Button)parentView.findViewById(R.id.groupManaber);
        btn_add= (Button)parentView.findViewById(R.id.btn_add);
        getBeaconId.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		jumpToBeaconActivity(getActivity(),BeaconActivity.class);
        	}
        });
        btn_add.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		if(beacon_id_text.getText().toString().equals("")){
        			showMessage("請先取得beacon");
        		}else{
        			btn_add.setEnabled(false);
        			addOld(beacon_id_text.getText().toString(),old_nameText.getText().toString(),old_characteristicText.getText().toString(),old_historyText.getText().toString(),old_clothesText.getText().toString(),old_addrText.getText().toString());
        		}
        	}
        });
        
    }
   
	private void getOld(){

		beacon_id_text.setText(uapp.getBeaconId());
		old_nameText.setText(uapp.getOldName());
		old_characteristicText.setText(uapp.getOldCharacteristic());
		old_historyText.setText(uapp.getOldHistory());
		old_clothesText.setText(uapp.getOldClothes());
		old_addrText.setText(uapp.getOldAddr());
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
	    nm.request(null, request);
	}

	private Listener<String> mResponseListener = new Listener<String>() {

		@Override
		public void onResponse(String str) {
			if(str.trim().equals("ok")){showMessage("新增成功");
				uapp.setMyOldMan();
				btn_add.setEnabled(true);
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
		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
	}

private void jumpToBeaconActivity(Context ct,Class<?> lt){
		
		
		Bundle bundle = new Bundle();
		
        bundle.putString(
                "user", uapp.getUser());
        bundle.putString(
                "addCheck", "add");
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
		startActivityForResult(intent,111);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		switch(resultCode){
		//fragment 要用Activity.RESULT_OK
		case 111:
			System.out.println();
			Bundle bundle = data.getExtras();
			System.out.println("get beacon id = "+bundle.getString("returnBeaconId"));
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
