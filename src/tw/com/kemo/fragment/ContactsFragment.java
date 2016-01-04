package tw.com.kemo.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import tw.com.kemo.activity.R;
import tw.com.kemo.activity.R.layout;

public class ContactsFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contactsLayout = inflater.inflate(R.layout.oldman_layout,
				container, false);
		return contactsLayout;
	}

}
