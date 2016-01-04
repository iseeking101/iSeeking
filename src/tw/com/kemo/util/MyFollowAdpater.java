package tw.com.kemo.util;


import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import tw.com.kemo.activity.R;

public class MyFollowAdpater extends BaseAdapter {
	Context context;
	List<MyFollowItem> items;

	/**
	 * Constructor of ListViewAdpater
	 * 
	 * @param context
	 * @param items
	 */
	//建構式建立類別時即傳入context及items
	public MyFollowAdpater(Context context, List<MyFollowItem> items) {
		this.context = context;
		this.items = items;
		
	}

	/**
	 * hold views for costomized listview
	 * 
	 * @author cho-hanwu
	 * 
	 */
	private class ViewHolder {
		TextView txtOldName;
		TextView txtOldStatus;
		TextView txtLocation;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		//context由activity傳入
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.fragment0_listitem, null);
			holder = new ViewHolder();
			holder.txtOldName = (TextView) convertView.findViewById(R.id.oldName);
			holder.txtOldStatus = (TextView) convertView.findViewById(R.id.oldStatus);
			holder.txtLocation = (TextView) convertView.findViewById(R.id.location);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		MyFollowItem items = (MyFollowItem) getItem(position);
		
		holder.txtOldName.setText(items.getOldName());
		holder.txtLocation.setText("桃園");
		if(items.getStatusv().equals("1")){

			holder.txtOldStatus.setText("走失");
		}else{

			holder.txtOldStatus.setText("正常");
		}
//		holder.txtOldStatus.setText(items.getOldStatus());
//		holder.txtLocation.setText(items.getLocation());
		
		return convertView;
	}

}

