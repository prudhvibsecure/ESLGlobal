package eslglobal.com.esl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.net.URLEncoder;

import eslglobal.com.esl.callbacks.IItemHandler;
import eslglobal.com.esl.common.AppPreferences;
import eslglobal.com.esl.common.AppSettings;
import eslglobal.com.esl.tasks.HTTPostJson;
import eslglobal.com.esl.utils.Utils;

public class ChangePassword extends AppCompatActivity implements IItemHandler {
	private EditText et_current = null;
	private EditText et_new = null;
	private EditText et_conform = null;
	LinearLayout passwordlayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.change_password);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.password_change_head);
		toolbar.setNavigationIcon(R.drawable.back);
		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		passwordlayout=(LinearLayout) findViewById(R.id.passwordlayout);
		et_current = (EditText) findViewById(R.id.curr_pass_txt);

		et_new = (EditText) findViewById(R.id.newpass_txt);

		et_conform = (EditText) findViewById(R.id.con_password);

		findViewById(R.id.submit_pass).setOnClickListener(onClick);
		et_new.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					String password = et_new.getText().toString();
					password = password.trim();

					if (password.length() == 0 || password.length() < 8) {
						showToast(R.string.psmbc);
						et_new.setSelection(password.length());
						return;
					}
				}
			}
		});
		et_conform.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {

					String cpassword = et_conform.getText().toString();
					cpassword = cpassword.trim();

					if (cpassword.length() == 0 || cpassword.length() < 8 || cpassword.length() > 16) {
						showToast(R.string.psmbc);
						et_conform.setSelection(cpassword.length());
						return;
					}

					String password = et_new.getText().toString();
					password = password.trim();

					if (!password.equals(cpassword)) {
						showToast(R.string.cpmm);
						return;
					}

				}
			}
		});
		et_conform.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before, int count) {

				int textlength1 = et_new.getText().length();
				int textlength2 = et_conform.getText().length();

				String password = et_new.getText().toString();
				password = password.trim();

				String cpassword = et_conform.getText().toString();
				cpassword = cpassword.trim();

				if (textlength1 == textlength2) {
					if (!password.equals(cpassword)) {
						showToast(R.string.cpmm);
						return;
					}
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:

			ChangePassword.this.finish();

			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View view) {

			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(view.getWindowToken(), 0);

			switch (view.getId()) {

			case R.id.submit_pass:
				addProfileView();
				break;

			default:
				break;
			}

		}
	};

	private void addProfileView() {

		try {

			String curent_pass = ((EditText) findViewById(R.id.curr_pass_txt)).getText().toString().trim();

			if (curent_pass.length() == 0) {
				showToast(R.string.cur_passss);
				((EditText) findViewById(R.id.curr_pass_txt)).requestFocus();
				return;
			}

			String new_pass = ((EditText) findViewById(R.id.newpass_txt)).getText().toString().trim();

			if (new_pass.length() == 0) {
				showToast(R.string.new_passss);
				((EditText) findViewById(R.id.newpass_txt)).requestFocus();
				return;
			}
			if (new_pass.length() < 8 || new_pass.length() > 16) {
				showToast(R.string.psmbc);
				((EditText) findViewById(R.id.newpass_txt)).requestFocus();
				return;
			}

			String conf_pass = ((EditText) findViewById(R.id.con_password)).getText().toString().trim();
			if (conf_pass.length() == 0) {
				showToast(R.string.conform_passss);
				((EditText) findViewById(R.id.con_password)).requestFocus();
				return;
			}
			if (conf_pass.length() < 8 || conf_pass.length() > 16) {
				showToast(R.string.psmbc);
				((EditText) findViewById(R.id.con_password)).requestFocus();
				return;
			}
			if (!new_pass.equals(conf_pass)) {
				showToast(R.string.cpmm);
				((EditText) findViewById(R.id.con_password)).requestFocus();
				return;
			}
			String strPassword = URLEncoder.encode(curent_pass);
			String newPassword = URLEncoder.encode(new_pass);
			String url = AppSettings.getInstance(this).getPropertyValue("change_pass");
			JSONObject object = new JSONObject();
			object.put("email", AppPreferences.getInstance(this).getFromStore("email"));
			object.put("cpassword", strPassword);
			object.put("npassword", newPassword);

			HTTPostJson post = new HTTPostJson(this, this, object.toString(), 1);
			post.setContentType("application/json");
			post.execute(url, "");
			Utils.showProgress(getString(R.string.pwait), this);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void showToast(String text) {
		Snackbar snackbar = Snackbar
				.make(passwordlayout, text, Snackbar.LENGTH_LONG);
		View sbView = snackbar.getView();
		TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
		textView.setTextColor(Color.WHITE);
		snackbar.show();
		//Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
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

	@Override
	public void onError(String errorData, int requestType) {
		Utils.dismissProgress();
		showToast(errorData);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == 4) {
		}

		return super.onKeyDown(keyCode, event);
	}

	public void showToast(int text) {
		//Toast.makeText(this, text, Toast.LENGTH_LONG).show();
		Snackbar snackbar = Snackbar
				.make(passwordlayout, text, Snackbar.LENGTH_LONG);
		View sbView = snackbar.getView();
		TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
		textView.setTextColor(Color.WHITE);
		snackbar.show();
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

				// launchActivity(Login.class);
				ChangePassword.this.finish();
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
