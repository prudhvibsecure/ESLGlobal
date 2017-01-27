package eslglobal.com.esl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import eslglobal.com.esl.callbacks.IItemHandler;
import eslglobal.com.esl.common.AppSettings;
import eslglobal.com.esl.tasks.HTTPostJson;
import eslglobal.com.esl.utils.Utils;

public class ForgetPassword extends AppCompatActivity implements IItemHandler {

	RelativeLayout recverpasslayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.passwordrecovery);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.recoverpassword);
		toolbar.setNavigationIcon(R.drawable.back);
		setSupportActionBar(toolbar);

		recverpasslayout= (RelativeLayout) findViewById(R.id.recverpasslayout);
		findViewById(R.id.tv_recover).setOnClickListener(onClick);

	}

	OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View view) {

			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(view.getWindowToken(), 0);

			switch (view.getId()) {

			case R.id.tv_recover:
				makeForgetPwdRequest();
				break;

			default:
				break;
			}

		}
	};
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

			case android.R.id.home:

				ForgetPassword.this.finish();

				break;
			default:
				break;
		}

		return super.onOptionsItemSelected(item);
	}
	private void makeForgetPwdRequest() {

		try {

			String email = ((EditText) findViewById(R.id.account_txt)).getText().toString().trim();

			if (email.length() == 0) {
				showToast(R.string.peei);
				return;
			}

			if (!emailValidation(email)) {
				showToast(R.string.peavei);
				return;
			}

			String url = AppSettings.getInstance(this).getPropertyValue("pwdrecover");
			JSONObject object = new JSONObject();
			object.put("email", email);
			HTTPostJson post = new HTTPostJson(this, this, object.toString(), 1);
			post.setContentType("application/json");
			post.execute(url, "");
			Utils.showProgress(getString(R.string.pwait), this);
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	private void showToast(String text) {
//		Snackbar snackbar = Snackbar
//				.make(recverpasslayout, text, Snackbar.LENGTH_LONG);
//		//snackbar.show();
//		View sbView = snackbar.getView();
//		TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
//		textView.setTextColor(Color.WHITE);
//		snackbar.show();
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onFinish(Object results, int requestType) {
		Utils.dismissProgress();

		try {

			switch (requestType) {

			case 1:
				parseForgetPasswordResponse((String) results, requestType);
				break;

			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onError(String errorData, int requestType) {
		showToast(errorData);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == 4) {
		}

		return super.onKeyDown(keyCode, event);
	}

	public void showToast(int text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
//		Snackbar snackbar = Snackbar
//				.make(recverpasslayout, text, Snackbar.LENGTH_LONG);
//		//snackbar.show();
//		View sbView = snackbar.getView();
//		TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
//		textView.setTextColor(Color.WHITE);
//		snackbar.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	private void parseForgetPasswordResponse(String response, int requestId) throws Exception {

		if (response != null && response.length() > 0) {

			JSONObject jsonObject = new JSONObject(response);

			if (jsonObject.optString("status").equalsIgnoreCase("0")) {
				showToast(jsonObject.optString("statusdescription"));
				ForgetPassword.this.finish();
				return;
			}
			showToast(jsonObject.optString("statusdescription"));

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public boolean emailValidation(String email) {

		if (email == null || email.length() == 0 || email.indexOf("@") == -1 || email.indexOf(" ") != -1) {
			return false;
		}
		int emailLenght = email.length();
		int atPosition = email.indexOf("@");

		String beforeAt = email.substring(0, atPosition);
		String afterAt = email.substring(atPosition + 1, emailLenght);

		if (beforeAt.length() == 0 || afterAt.length() == 0) {
			return false;
		}
		if (email.charAt(atPosition - 1) == '.') {
			return false;
		}
		if (email.charAt(atPosition + 1) == '.') {
			return false;
		}
		if (afterAt.indexOf(".") == -1) {
			return false;
		}
		char dotCh = 0;
		for (int i = 0; i < afterAt.length(); i++) {
			char ch = afterAt.charAt(i);
			if ((ch == 0x2e) && (ch == dotCh)) {
				return false;
			}
			dotCh = ch;
		}
		if (afterAt.indexOf("@") != -1) {
			return false;
		}
		int ind = 0;
		do {
			int newInd = afterAt.indexOf(".", ind + 1);

			if (newInd == ind || newInd == -1) {
				String prefix = afterAt.substring(ind + 1);
				if (prefix.length() > 1 && prefix.length() < 6) {
					break;
				} else {
					return false;
				}
			} else {
				ind = newInd;
			}
		} while (true);
		dotCh = 0;
		for (int i = 0; i < beforeAt.length(); i++) {
			char ch = beforeAt.charAt(i);
			if (!((ch >= 0x30 && ch <= 0x39) || (ch >= 0x41 && ch <= 0x5a) || (ch >= 0x61 && ch <= 0x7a) || (ch == 0x2e)
					|| (ch == 0x2d) || (ch == 0x5f))) {
				return false;
			}
			if ((ch == 0x2e) && (ch == dotCh)) {
				return false;
			}
			dotCh = ch;
		}
		return true;
	}

}
