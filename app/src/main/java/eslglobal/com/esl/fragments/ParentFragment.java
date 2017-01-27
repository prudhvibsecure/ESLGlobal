package eslglobal.com.esl.fragments;

import android.support.v4.app.Fragment;
import android.view.View;

import eslglobal.com.esl.R;

public class ParentFragment extends Fragment {

	public boolean back() {
		return false;
	}

	public String getFragmentName() {
		return getString(R.string.app_name);
	}

	public void onFragmentChildClick(View view) {

	}

	public int getFragmentActionBarColor() {
		return R.color.colorPrimary;
	}

	public int getFragmentStatusBarColor() {
		return R.color.colorPrimaryDark;
	}

}
