package eslglobal.com.esl;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import eslglobal.com.esl.callbacks.IItemHandler;
import eslglobal.com.esl.common.AppPreferences;
import eslglobal.com.esl.common.AppSettings;
import eslglobal.com.esl.tasks.HTTPostJson;
import eslglobal.com.esl.utils.Utils;

public class ContactUs extends AppCompatActivity implements IItemHandler {
	private EditText et_cname, et_cmobile, et_msg;
	private Bsecure bsecure;
	private ImageView close;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.contactus);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.cont_head);
		toolbar.setNavigationIcon(R.drawable.back);
		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		et_cname = (EditText) findViewById(R.id.cont_uname);

		et_cmobile = (EditText) findViewById(R.id.cont_mobile);

		et_msg = (EditText) findViewById(R.id.cont_message);

		findViewById(R.id.submit_contact).setOnClickListener(onClick);

		et_cname.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					String cname = et_cname.getText().toString();
					cname = cname.trim();

					if (cname.length() == 0) {
						showToast(R.string.peu);
						et_cname.setSelection(cname.length());
						return;
					}

				}
			}
		});
		et_msg.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					String msg = et_msg.getText().toString();
					msg = msg.trim();

					if (msg.length() == 0) {
						showToast(R.string.cn_msg);
						et_cname.setSelection(msg.length());
						return;
					}

				}
			}
		});

	}

	OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View view) {

			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(view.getWindowToken(), 0);

			switch (view.getId()) {

			case R.id.submit_contact:
				addContact();
				break;

			default:
				break;
			}

		}
	};

	private void addContact() {

		try {

			String cont_name = ((EditText) findViewById(R.id.cont_uname)).getText().toString().trim();

			if (cont_name.length() == 0) {
				showToast(R.string.peu);
				((EditText) findViewById(R.id.cont_uname)).requestFocus();
				return;
			}

			String Msg = ((EditText) findViewById(R.id.cont_message)).getText().toString().trim();

			if (Msg.length() == 0) {
				showToast(R.string.cn_msg);
				((EditText) findViewById(R.id.cont_message)).requestFocus();
				return;
			}

			String url = AppSettings.getInstance(this).getPropertyValue("queries");
			JSONObject object = new JSONObject();

			object.put("userName", cont_name);
			object.put("userEmail", getFromStore("email"));
			object.put("userPhone", et_cmobile.getText());
			object.put("userMsg", Msg);

			HTTPostJson post = new HTTPostJson(this, this, object.toString(), 1);
			post.setContentType("application/json");
			post.execute(url, "");
			Utils.showProgress(getString(R.string.pwait), this);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:

			ContactUs.this.finish();

			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onFinish(Object results, int requestType) {
		Utils.dismissProgress();

		try {

			switch (requestType) {
			case 1:
				parseRegistrationResponse((String) results, requestType);
				break;

			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String getFromStore(String key) {
		return AppPreferences.getInstance(ContactUs.this).getFromStore(key);
	}

	@Override
	public void onError(String errorData, int requestType) {
		Utils.dismissProgress();
		showToast(errorData);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// if (keyCode == 4) {
		// exitByBackKey();
		// }
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitByBackKey();

			// moveTaskToBack(false);

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	protected void exitByBackKey() {

		AlertDialog alertbox = new AlertDialog.Builder(this).setMessage("Do you want to exit?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {

						finish();
						// close();

					}
				}).setNegativeButton("No", new DialogInterface.OnClickListener() {

					// do something when the button is clicked
					public void onClick(DialogInterface arg0, int arg1) {
					}
				}).show();

	}

	public void showToast(int text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	private void parseRegistrationResponse(String response, int requestId) throws Exception {

		// Log.e("-=-=-=-=-=-=-=-=-", response + "");

		if (response != null && response.length() > 0) {
			response = response.trim();
			JSONObject jsonObject = new JSONObject(response);

			if (jsonObject.has("status") && jsonObject.optString("status").equalsIgnoreCase("0")) {
				// showToast(R.string.syhrwbpltata);
				showToast(jsonObject.optString("statusdescription"));
				ContactUs.this.finish();
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
