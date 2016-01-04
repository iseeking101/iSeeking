package tw.com.kemo.util;


import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import tw.com.kemo.activity.R;

public class nouse_MemberSittingAdpater extends BaseAdapter {
	Context context;
	List<nouser_MemberListViewItem> items;

	/**
	 * Constructor of ListViewAdpater
	 * 
	 * @param context
	 * @param items
	 */
	//建構式建立類別時即傳入context及items
	public nouse_MemberSittingAdpater(Context context, List<nouser_MemberListViewItem> items) {
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
		TextView rowTitle;
		EditText rowInput;
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
			convertView = mInflater.inflate(R.layout.nouse_listview_member, null);
			holder = new ViewHolder();
			holder.rowTitle = (TextView) convertView.findViewById(R.id.rowTitle);
			holder.rowInput = (EditText) convertView.findViewById(R.id.rowInput);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		nouser_MemberListViewItem items = (nouser_MemberListViewItem) getItem(position);
		
		holder.rowTitle.setText(items.getRowTitle());
		holder.rowInput.setText("");
		
		return convertView;
		
	}

}

