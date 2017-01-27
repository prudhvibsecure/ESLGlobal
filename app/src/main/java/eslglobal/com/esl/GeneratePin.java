package eslglobal.com.esl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import eslglobal.com.esl.callbacks.IItemHandler;
import eslglobal.com.esl.common.AppPreferences;
import eslglobal.com.esl.common.AppSettings;
import eslglobal.com.esl.tasks.HTTPostJson;
import eslglobal.com.esl.utils.Utils;

public class GeneratePin extends FragmentActivity implements IItemHandler {

	private EditText ed_pin = null;
	LinearLayout pinlayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.add_pin_new);

		pinlayout=(LinearLayout)findViewById(R.id.pinlayout);
		ed_pin = (EditText) findViewById(R.id.ed_pin);

		ed_pin.requestFocus();
		ed_pin.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				keyboard.showSoftInput(ed_pin, 0);
			}
		}, 50);

		ed_pin.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Integer textlength1 = ed_pin.getText().length();
				if (textlength1 >= 4) {
					makeAddPinRequest();
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
		});

	}

	private void makeAddPinRequest() {

		try {

			String pin = ed_pin.getText().toString().trim();

			if (pin.length() == 0) {

				ed_pin.requestFocus();
				showToast(R.string.pepin);
				return;
			}

			String url = AppSettings.getInstance(this).getPropertyValue("addPin");
			JSONObject object = new JSONObject();
			object.put("email", AppPreferences.getInstance(this).getFromStore("email"));
			object.put("pin", pin);
			HTTPostJson post = new HTTPostJson(this, this, object.toString(), 1);
			post.setContentType("application/json");
			post.execute(url, "");
			Utils.showProgress(getString(R.string.pwait), this);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void launchActivity(Class<?> cls, int requestCode) {
		GeneratePin.this.finish();
		Intent mainIntent = new Intent(GeneratePin.this, cls);
		// startActivity(mainIntent);
		startActivityForResult(mainIntent, requestCode);
	}

	public void addToStore(String key, String value) {
		AppPreferences.getInstance(GeneratePin.this).addToStore(key, value);
	}

	public void showToast(int text) {
		Snackbar snackbar = Snackbar
				.make(pinlayout, text, Snackbar.LENGTH_LONG);
		View sbView = snackbar.getView();
		TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
		textView.setTextColor(Color.WHITE);
		snackbar.show();
//		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	public String getFromStore(String key) {
		return AppPreferences.getInstance(GeneratePin.this).getFromStore(key);
	}

	@Override
	public void onFinish(Object results, int requestType) {
		Utils.dismissProgress();

		try {

			switch (requestType) {

			case 1:
				parseGeneratePinResponse((String) results, requestType);
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

	private void showToast(String text) {
		Snackbar snackbar = Snackbar
				.make(pinlayout, text, Snackbar.LENGTH_LONG);
		View sbView = snackbar.getView();
		TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
		textView.setTextColor(Color.WHITE);
		snackbar.show();
		//Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1002) {
			switch (resultCode) {

			case RESULT_OK:
				break;

			case RESULT_CANCELED:
				break;

			default:
				break;
			}
		}
	}

	private void parseGeneratePinResponse(String response, int requestId) throws Exception {

		if (response != null && response.length() > 0) {

			JSONObject jsonObject = new JSONObject(response);

			if (jsonObject.optString("status").equalsIgnoreCase("0")) {
				String pin = ed_pin.getText().toString().trim();
				addToStore("authpin", pin);
				launchActivity(Bsecure.class, 1002);
				GeneratePin.this.finish();

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
