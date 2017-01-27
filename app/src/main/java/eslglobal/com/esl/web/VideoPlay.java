package eslglobal.com.esl.web;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

import eslglobal.com.esl.R;
import eslglobal.com.esl.common.AppPreferences;

public class VideoPlay extends AppCompatActivity {

	private VideoView vv_bsecure = null;

	private int position = 0;

	private Timer timer = null;

	private TimerTask timerTask = null;

	private int showtime = 0;

	private TextView waterMark, web_watermark;
	private ProgressDialog progressDialog;

	private MediaController mediaControls;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		setContentView(R.layout.lessionview);

		waterMark = (TextView) findViewById(R.id.text_watermark);

		Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Maximum.ttf");
		waterMark.setTypeface(font);

		waterMark.setText(AppPreferences.getInstance(this).getFromStore("email"));

		Intent intent = this.getIntent();
		if (intent != null) {

			showtime = intent.getIntExtra("showtime", 0);

			String filePath = intent.getStringExtra("filepath");

			String mimeType = intent.getStringExtra("mimeType");

			IntentFilter filter = new IntentFilter();
			filter.addAction("eslglobal.com.esl.close.activity");
			LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter);

			if (mimeType.contains("video") || mimeType.contains("audio")) {
				vv_bsecure = (VideoView) findViewById(R.id.lession_paly);

				if (mediaControls == null) {
					mediaControls = new MediaController(VideoPlay.this);
				}

				progressDialog = new ProgressDialog(VideoPlay.this);
				progressDialog.setMessage("Loading...");
				progressDialog.setCancelable(false);
				progressDialog.show();

				try {
					vv_bsecure.setMediaController(mediaControls);
					vv_bsecure.setVideoURI(Uri.parse(filePath));

				} catch (Exception e) {
					// Log.e("Error", e.getMessage());
					e.printStackTrace();
				}

				vv_bsecure.requestFocus();
				vv_bsecure.setOnPreparedListener(new OnPreparedListener() {

					public void onPrepared(MediaPlayer mediaPlayer) {
						progressDialog.dismiss();
						vv_bsecure.seekTo(position);
						if (position == 0) {
							vv_bsecure.start();
						} else {
							vv_bsecure.pause();
						}
						if (showtime > 0) {
							mediaControls.setVisibility(View.GONE);
							int topContainerId = getResources().getIdentifier("mediacontroller_progress", "id",
									"android");
							SeekBar seekBarVideo = (SeekBar) mediaControls.findViewById(topContainerId);
							seekBarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
								@Override
								public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
									seekBar.setEnabled(false);
								}

								@Override
								public void onStartTrackingTouch(SeekBar seekBar) {
									seekBar.setEnabled(false);
								}

								@Override
								public void onStopTrackingTouch(SeekBar seekBar) {
									seekBar.setEnabled(false);
								}
							});
						}
					}
				});

			}
			startTimer();

		}

	}

	@Override
	protected void onDestroy() {
		waterMark.setText(null);
		try {

			if (vv_bsecure != null) {
				if (vv_bsecure.isPlaying()) {
					vv_bsecure.stopPlayback();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

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

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equalsIgnoreCase("eslglobal.com.esl.close.activity")) {
				VideoPlay.this.finish();
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
							VideoPlay.this.finish();
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

	private void showToast(int value) {
		Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
	}
}
