package tw.com.kemo.util;


import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import tw.com.kemo.activity.R;

public class MissingOldAdpater extends BaseAdapter {
	Context context;
	List<MissingOldItem> items;

	/**
	 * Constructor of ListViewAdpater
	 * 
	 * @param context
	 * @param items
	 */
	//建構式建立類別時即傳入context及items
	public MissingOldAdpater(Context context, List<MissingOldItem> items) {
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
		ImageView imgOld;
		TextView txtOldName;
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
			convertView = mInflater.inflate(R.layout.listitem_missing, null);
			holder = new ViewHolder();
			holder.txtOldName = (TextView) convertView.findViewById(R.id.oldName);
			holder.imgOld = (ImageView) convertView.findViewById(R.id.imageView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		MissingOldItem items = (MissingOldItem) getItem(position);
		
		holder.txtOldName.setText(items.getOldName());
		holder.imgOld.setImageResource(items.getOldPic());
		
		return convertView;
	}

}

