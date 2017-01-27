package eslglobal.com.esl;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Properties;

import eslglobal.com.esl.callbacks.IItemHandler;
import eslglobal.com.esl.common.AppPreferences;
import eslglobal.com.esl.common.AppSettings;
import eslglobal.com.esl.tasks.HTTPTask;
import eslglobal.com.esl.tasks.HTTPostJson;
import eslglobal.com.esl.utils.Utils;

public class Login extends AppCompatActivity implements IItemHandler{

    private Properties properties = null;

    private int counter = 0;

    LinearLayout loginlinerlayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        loginlinerlayout=(LinearLayout)findViewById(R.id.loginlinerlayout);
        findViewById(R.id.click_forgetpassword).setOnClickListener(onClick);
        findViewById(R.id.login_btn).setOnClickListener(onClick);
        findViewById(R.id.click_signup).setOnClickListener(onClick);
        findViewById(R.id.click_lessons).setOnClickListener(onClick);
        checkUpdate();

    }

    protected void checkUpdate() {
        // TODO Auto-generated method stub
        String url = AppSettings.getInstance(this).getPropertyValue("curr_versn");

        HTTPTask task = new HTTPTask(this, this);
        task.disableProgress();
        task.userRequest("", 2, url);
    }

    public Properties getProperties() {
        return properties;
    }

    public String getPropertyValue(String key) {
        return properties.getProperty(key);
    }

    View.OnClickListener onClick = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);

            switch (view.getId()) {

                case R.id.click_signup:
                    launchActivity(WebRegister.class, 1000);
                    break;

                case R.id.login_btn:
                    loginDefault();
                    break;

                case R.id.click_forgetpassword:
                    launchActivity(ForgetPassword.class, 1001);
                    break;

                case R.id.click_lessons:
                    launchActivity(LessonsAct.class, 1002);
                    break;
                default:
                    break;
            }

        }
    };

    private void loginDefault() {

        try {

            String email = ((EditText) findViewById(R.id.user_email)).getText().toString().trim();

            String password = ((EditText) findViewById(R.id.user_password)).getText().toString().trim();

            if (email.length() == 0) {
                showToast(R.string.peei);
                return;
            }

            if (!emailValidation(email)) {
                showToast(R.string.peavei);
                return;
            }

            if (password.length() == 0) {
                showToast(R.string.pePswd);
                return;
            }

            if (password.length() < 8) {
                showToast(R.string.psmbc);
                return;
            }

           // String strPassword = URLEncoder.encode(password);

            String url = AppSettings.getInstance(this).getPropertyValue("login");
            JSONObject object = new JSONObject();
            object.put("email", email);
            object.put("password", password);
            object.put("regid", AppPreferences.getInstance(Login.this).getFromStore("regID"));
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
                .make(loginlinerlayout, text, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
        //Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private void launchActivity(Class<?> cls, int requestCode) {
        Intent mainIntent = new Intent(Login.this, cls);
        startActivityForResult(mainIntent, requestCode);
    }

    @Override
    public void onFinish(Object results, int requestType) {
        Utils.dismissProgress();

        try {

            switch (requestType) {
                case 1:

                    parseLoginResponse((String) results, requestType);

                    break;
                case 2:
                    JSONObject json;
                    try {
                        json = new JSONObject(results.toString());
                        updatesAvaliable(json);
                    } catch (JSONException e) {
                        //
                        e.printStackTrace();
                    }

                    break;

                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updatesAvaliable(final JSONObject responseObj) {
        // TODO Auto-generated method stub

        PackageInfo pInfo = null;

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String versionName = pInfo.versionName;
        if (responseObj.has("status") && responseObj.optString("status").equalsIgnoreCase("0")) {

            String latestVersion = responseObj.optString("currentversion");

            if (!latestVersion.equals(versionName)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setTitle("New Update Available")
                        .setMessage("There is newer version of this application available, click OK to upgrade now?")
                        .setCancelable(false).setIcon(R.drawable.ic_launcher)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            // if the user agrees to upgrade
                            public void onClick(DialogInterface dialog, int id) {

                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW,
                                            Uri.parse("market://details?id=" + getPackageName())));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                            .parse("http://play.google.com/store/apps/details?id" + getPackageName())));
                                }
                            }
                        }).setNegativeButton("Remind Later", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                builder.create().show();

            }

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
        Snackbar snackbar = Snackbar
                .make(loginlinerlayout, text, Snackbar.LENGTH_LONG);
        //snackbar.show();
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
       // Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private void parseLoginResponse(String response, int requestId) throws Exception {

        if (response != null && response.length() > 0) {

            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.optString("status").equalsIgnoreCase("0")) {
                String email = ((EditText) findViewById(R.id.user_email)).getText().toString().trim();
                AppPreferences.getInstance(Login.this).addToStore("email", email);
                AppPreferences.getInstance(Login.this).addToStore("authid", jsonObject.optString("authid"));
                launchActivity(GeneratePin.class, 1003);
                Login.this.finish();
                return;
            }
            counter++;
            showToast(jsonObject.optString("statusdescription"));
            if (counter > 3) {
                showToast(jsonObject.optString("statusdescription"));
                return;
            }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
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

}
