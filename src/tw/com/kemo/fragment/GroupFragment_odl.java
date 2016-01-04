package tw.com.kemo.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import tw.com.kemo.activity.R.layout;
import tw.com.kemo.util.ListViewAdpater;
import tw.com.kemo.util.MyFollowItem;
import tw.com.kemo.activity.*;

public class GroupFragment_odl extends Fragment {
	private List<MyFollowItem> rowitem;
	private ArrayList<String> items;
	private UserApplication uapp;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View newsLayout = inflater.inflate(R.layout.group_layout, container,
				false);
		return newsLayout;
	}
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		uapp = (UserApplication)getActivity().getApplicationContext();
		//items = uapp.getMyFollow();
        rowitem = new ArrayList<MyFollowItem>();
        for(int i = 0; i<items.size(); i++){
      //  	rowitem.add(new MyFollowItem(items.get(i),"位置:桃園", "狀態:正常   "));
        }
        
        ListView listView = (ListView) getActivity().findViewById(R.id.listView1);
		ListViewAdpater adpater = new ListViewAdpater(getActivity(),
        		rowitem);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                // 利用索引值取得點擊的項目內容。
            	MyFollowItem items = rowitem.get(index);
      //          showMessage(items.getOldName().toString()+"  "+items.getLocation().toString()+"   "
   //             		+items.getOldStatus().toString());
                // 顯示。  
            }
        });
		listView.setAdapter(adpater);
	}
	private void showMessage(String msg){
		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
	}

}
