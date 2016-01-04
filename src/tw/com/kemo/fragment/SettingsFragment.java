package tw.com.kemo.fragment;

import com.special.ResideMenu.ResideMenu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import tw.com.kemo.activity.MemberActivity;
import tw.com.kemo.activity.MenuActivity;
import tw.com.kemo.activity.R;
import tw.com.kemo.activity.UserApplication;


public class SettingsFragment extends Fragment {
	private UserApplication	uapp;
    private View parentView;
    private ResideMenu resideMenu;
    
    FragmentManager fragmentManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	parentView = inflater.inflate(R.layout.settings, container, false);
    	fragmentManager = getFragmentManager();
    	 final MenuActivity parentActivity = (MenuActivity) getActivity();
        
        parentView.findViewById(R.id.profile_layout).setOnClickListener(new View.OnClickListener() {
       
        	@Override
          public void onClick(View view) {
              //jumpToActivity(getActivity(),MemberActivity.class);
        	  parentActivity.getApp_name().setText("個人資料");
              changeFragment(new MemberFragment());
          }
        });
        return parentView;
    }
    private void jumpToActivity(Context ct,Class<?> lt){
		Intent intent = new Intent();
        intent.setClass(ct, lt);
		startActivity(intent);
	}
    private void changeFragment(Fragment targetFragment){
    	MenuActivity parentActivity = (MenuActivity) getActivity();
        resideMenu = parentActivity.getResideMenu();
    	resideMenu.clearIgnoredViewList();
    	FragmentTransaction transaction = fragmentManager.beginTransaction();
    	transaction.replace(R.id.main_fragment, targetFragment, "fragment");
    	transaction .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    	transaction.commit();
  
    }
}
