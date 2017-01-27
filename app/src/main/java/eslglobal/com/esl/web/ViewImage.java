package eslglobal.com.esl.web;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.Timer;
import java.util.TimerTask;

import eslglobal.com.esl.R;
import eslglobal.com.esl.common.AppPreferences;
import eslglobal.com.esl.imageloader.ImageLoader;

/**
 * Created by w7u on 10/6/2016.
 */

public class ViewImage extends AppCompatActivity{

    private ImageView viewimage = null;
    private Timer timer = null;
    private TimerTask timerTask = null;
    private ImageLoader loader;
    private int showtime = 0;
    private TextView waterMark;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.view_image);

        viewimage = (ImageView) findViewById(R.id.im_showimage);

        waterMark = (TextView) findViewById(R.id.text_watermark);

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Maximum.ttf");
        waterMark.setTypeface(font);

        waterMark.setText(AppPreferences.getInstance(this).getFromStore("email"));
        loader=new ImageLoader(this,false);
        Intent intent = this.getIntent();
        if (intent != null) {

            showtime = intent.getIntExtra("showtime", 0);

            String filePath = intent.getStringExtra("filepath");

            String mimeType = intent.getStringExtra("mimeType");

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            IntentFilter filter = new IntentFilter();
            filter.addAction("eslglobal.com.esl.close.activity");
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter);
            if (mimeType.contains("image")) {
                //loader.DisplayImage(filePath,viewimage);
                Glide.with(this)
                        .load(filePath)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(viewimage);
                progressDialog.dismiss();
                startTimer();
            }

        }
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equalsIgnoreCase("eslglobal.com.esl.close.activity")) {
                ViewImage.this.finish();
            }
        }
    };
    private void startTimer() {

        try {

            if (showtime == -1)
                return;

            if (showtime == 0)
                return;

            final long milliseconds = showtime * 1000;

            timer = new Timer();

            initializeTimerTask();

            timer.schedule(timerTask, milliseconds);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initializeTimerTask() {

        try {

            timerTask = new TimerTask() {
                public void run() {

                    Handler h = new Handler(Looper.getMainLooper());
                    h.post(new Runnable() {
                        public void run() {
                            showToast(R.string.ymnlefv);
                            ViewImage.this.finish();
                        }
                    });
                }
            };

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stoptimertask() {

        try {

            if (timer != null)
                timer.cancel();

            timer = null;

            if (timerTask != null)
                timerTask.cancel();

            timerTask = null;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void onDestroy() {

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            stoptimertask();
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }
    private void showToast(int value) {
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
    }

}
