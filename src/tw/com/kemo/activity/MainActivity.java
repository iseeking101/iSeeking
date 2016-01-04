package tw.com.kemo.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import tw.com.kemo.fragment.ContactsFragment;
import tw.com.kemo.fragment.MessageFragment;
import tw.com.kemo.fragment.GroupFragment_odl;
import tw.com.kemo.fragment.SettingFragment;
import tw.com.kemo.util.ListViewAdpater;
import tw.com.kemo.util.MyFollowItem;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;


public class MainActivity extends Activity implements OnClickListener {
	private MessageFragment messageFragment;
	private ContactsFragment contactsFragment;
	private GroupFragment_odl newsFragment;
	private SettingFragment settingFragment;
	private View messageLayout;
	private View contactsLayout;
	private View newsLayout;
	private View settingLayout;
	private ImageView messageImage;
	private ImageView contactsImage;
	private ImageView newsImage;
	private ImageView settingImage;
	private TextView messageText;
	private TextView contactsText;
	private TextView newsText;
	private TextView settingText;
	private FragmentManager fragmentManager;
	//group_layout part
	
	private UserApplication uapp;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//沒有畫面最上方標題列 FEATURE_NO_TITLE
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.index_main);
		//使用application
		uapp = (UserApplication)getApplicationContext();
		//=============check back form notification========================
        Bundle bundle = this.getIntent().getExtras();
        //如果不確認有沒有contain if方法內equals會錯誤
        if(bundle != null && bundle.containsKey("beckFromNotification") ){
        	//比較用equals 用雙等號查不到
        	if(bundle.getString("beckFromNotification").equals("1")){
        		//放入user ，查詢資料庫將參數放入application
        		System.out.println("user = "+bundle.getString("user"));
	        	uapp.setUser(bundle.getString("user"));
	        	uapp.getAllOldDetail();
	        	uapp.setAllMemberDetail();
	        	uapp.getAllGroupMember();
	        	//uapp.getMyFollowDAO();
        	}
        }
		//=========group
        
        //===============
		initViews();
		fragmentManager = getFragmentManager();

		setTabSelection(0);
	}
	
	private void initViews() {
		messageLayout = findViewById(R.id.message_layout);
		contactsLayout = findViewById(R.id.contacts_layout);
		newsLayout = findViewById(R.id.news_layout);
		settingLayout = findViewById(R.id.setting_layout);
		messageImage = (ImageView) findViewById(R.id.message_image);
		contactsImage = (ImageView) findViewById(R.id.contacts_image);
		newsImage = (ImageView) findViewById(R.id.news_image);
		settingImage = (ImageView) findViewById(R.id.setting_image);
		messageText = (TextView) findViewById(R.id.message_text);
		contactsText = (TextView) findViewById(R.id.contacts_text);
		newsText = (TextView) findViewById(R.id.news_text);
		settingText = (TextView) findViewById(R.id.setting_text);
		messageLayout.setOnClickListener(this);
		contactsLayout.setOnClickListener(this);
		newsLayout.setOnClickListener(this);
		settingLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.message_layout:
			setTabSelection(0);
			break;
		case R.id.contacts_layout:
			setTabSelection(1);
			break;
		case R.id.news_layout:
			setTabSelection(2);
			break;
		case R.id.setting_layout:
			setTabSelection(3);
			break;
		default:
			break;
		}
	}

	private void setTabSelection(int index) {
		clearSelection();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		hideFragments(transaction);
		switch (index) {
		case 0:
			messageImage.setImageResource(R.drawable.message_selected);
			messageText.setTextColor(Color.WHITE);
			if (messageFragment == null) {
				messageFragment = new MessageFragment();
				transaction.add(R.id.content, messageFragment);
			} else {
				transaction.show(messageFragment);
			}
			break;
		case 1:
			contactsImage.setImageResource(R.drawable.contacts_selected);
			contactsText.setTextColor(Color.WHITE);
			if (contactsFragment == null) {
				contactsFragment = new ContactsFragment();
				transaction.add(R.id.content, contactsFragment);
			} else {
				transaction.show(contactsFragment);
			}
			break;
		case 2:
			newsImage.setImageResource(R.drawable.news_selected);
			newsText.setTextColor(Color.WHITE);
			if (newsFragment == null) {
				newsFragment = new GroupFragment_odl();
				transaction.add(R.id.content, newsFragment);
			} else {
				transaction.show(newsFragment);
			}
			break;
		case 3:
		default:
			settingImage.setImageResource(R.drawable.setting_selected);
			settingText.setTextColor(Color.WHITE);
			if (settingFragment == null) {
				settingFragment = new SettingFragment();
				transaction.add(R.id.content, settingFragment);
			} else {
				transaction.show(settingFragment);
			}
			break;
		}
		transaction.commit();
	}
	private void clearSelection() {
		messageImage.setImageResource(R.drawable.message_unselected);
		messageText.setTextColor(Color.parseColor("#82858b"));
		contactsImage.setImageResource(R.drawable.contacts_unselected);
		contactsText.setTextColor(Color.parseColor("#82858b"));
		newsImage.setImageResource(R.drawable.news_unselected);
		newsText.setTextColor(Color.parseColor("#82858b"));
		settingImage.setImageResource(R.drawable.setting_unselected);
		settingText.setTextColor(Color.parseColor("#82858b"));
	}
	private void hideFragments(FragmentTransaction transaction) {
		if (messageFragment != null) {
			transaction.hide(messageFragment);
		}
		if (contactsFragment != null) {
			transaction.hide(contactsFragment);
		}
		if (newsFragment != null) {
			transaction.hide(newsFragment);
		}
		if (settingFragment != null) {
			transaction.hide(settingFragment);
		}
	}
	private void showMessage(String msg){
		Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
	}
}
