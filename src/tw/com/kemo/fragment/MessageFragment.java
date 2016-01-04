package tw.com.kemo.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import tw.com.kemo.activity.R;
import tw.com.kemo.activity.R.layout;

public class MessageFragment extends Fragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View messageLayout = inflater.inflate(R.layout.missing_layout,
				container, false);
		return messageLayout;
	}

}
