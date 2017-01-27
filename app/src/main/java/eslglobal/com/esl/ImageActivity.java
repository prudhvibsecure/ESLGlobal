package eslglobal.com.esl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import eslglobal.com.esl.common.AppPreferences;

public class ImageActivity extends Activity {

	private ImageView imageactivity = null;

	private InputStream inputStream = null;

	private VideoView vv_bsecure = null;

	private WebView wv_content = null;

	private ProgressDialog progressDialog;

	private MediaController mediaControls;

	private int position = 0;

	private Timer timer = null;

	private TimerTask timerTask = null;

	private int showtime = 0;
	private TextView waterMark;
	private Runnable mUpdateTimeTask = null;
	private Handler mHandler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		setContentView(R.layout.imageactivity);

		//imageactivity = (ImageView) findViewById(R.id.imageactivity);

		waterMark = (TextView) findViewById(R.id.text_watermark);
		//web_watermark = (TextView) findViewById(R.id.t_watermark);
		Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Maximum.ttf");
		waterMark. setTypeface(font);
		waterMark.setRotation(-55);
		//web_watermark.setTypeface(font);

		waterMark.setText(AppPreferences.getInstance(this).getFromStore("email"));
		Intent intent = this.getIntent();
		if (intent != null) {

			progressDialog = new ProgressDialog(ImageActivity.this);
			progressDialog.setMessage("Loading...");
			progressDialog.setCancelable(false);
			progressDialog.show();
			showtime = intent.getIntExtra("showtime", 0);

			String filePath = intent.getStringExtra("filepath");

			String mimeType = intent.getStringExtra("mimeType");

			IntentFilter filter = new IntentFilter();
			filter.addAction("eslglobal.com.esl.close.activity");
			LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter);

			if (mimeType.contains("application/pdf")) {

//				findViewById(R.id.vv_bsecure).setVisibility(View.GONE);
//				imageactivity.setVisibility(View.GONE);

				wv_content = (WebView) findViewById(R.id.wv_content);
				wv_content.setVisibility(View.VISIBLE);

				wv_content.getSettings().setAllowFileAccess(true);
				wv_content.getSettings().setSupportZoom(true);
				wv_content.setVerticalScrollBarEnabled(true);
				wv_content.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
				wv_content.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
				wv_content.getSettings().setLoadWithOverviewMode(true);
				wv_content.getSettings().setUseWideViewPort(true);
				wv_content.getSettings().setJavaScriptEnabled(true);
				wv_content.getSettings().setPluginState(WebSettings.PluginState.ON);

				wv_content.getSettings().setSaveFormData(false);
				wv_content.getSettings().setSavePassword(false);

				wv_content.getSettings().setRenderPriority(RenderPriority.HIGH);
				wv_content.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

				wv_content.setWebViewClient(new MyWebViewClient());
				wv_content.setWebChromeClient(new MyWebChromeClient());

				wv_content.getSettings().setJavaScriptEnabled(true);
				wv_content.getSettings().setLoadWithOverviewMode(true);
				wv_content.getSettings().setUseWideViewPort(true);
				wv_content.loadUrl("http://docs.google.com/gview?embedded=true&url=" + filePath);
				wv_content.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						return true;
					}
				});

				File file = new File(filePath);

				if (file.exists()) {

					try {

						Uri path = Uri.fromFile(file);
						Intent launchIntent = new Intent(Intent.ACTION_VIEW);
						launchIntent.setDataAndType(path, "application/pdf");
						launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

						startActivity(launchIntent);
					} catch (ActivityNotFoundException e) {
						Toast.makeText(this, "No Application Available to View PDF", Toast.LENGTH_SHORT).show();
					}
				}

//			} else if (mimeType.contains("image")) {
//				findViewById(R.id.vv_bsecure).setVisibility(View.GONE);
//				loadFromPhone(filePath);
//			} else if (mimeType.contains("video") || mimeType.contains("audio")) {
//
//				imageactivity.setVisibility(View.GONE);
//
//				vv_bsecure = (VideoView) findViewById(R.id.vv_bsecure);
//
//				if (mediaControls == null) {
//					mediaControls = new MediaController(ImageActivity.this);
//				}
//
//				progressDialog = new ProgressDialog(ImageActivity.this);
//				progressDialog.setMessage("Loading...");
//				progressDialog.setCancelable(false);
//				progressDialog.show();
//
//				try {
//					vv_bsecure.setMediaController(mediaControls);
//					vv_bsecure.setVideoURI(Uri.parse(filePath));
//
//				} catch (Exception e) {
//					// Log.e("Error", e.getMessage());
//					e.printStackTrace();
//				}
//
//				vv_bsecure.requestFocus();
//				vv_bsecure.setOnPreparedListener(new OnPreparedListener() {
//
//					public void onPrepared(MediaPlayer mediaPlayer) {
//						progressDialog.dismiss();
//						vv_bsecure.seekTo(position);
//						if (position == 0) {
//							vv_bsecure.start();
//						} else {
//							vv_bsecure.pause();
//						}
//						if (showtime > 0) {
//							mediaControls.setVisibility(View.GONE);
//							int topContainerId = getResources().getIdentifier("mediacontroller_progress", "id",
//									"android");
//							SeekBar seekBarVideo = (SeekBar) mediaControls.findViewById(topContainerId);
//							seekBarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//								@Override
//								public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//									seekBar.setEnabled(false);
//								}
//
//								@Override
//								public void onStartTrackingTouch(SeekBar seekBar) {
//									seekBar.setEnabled(false);
//								}
//
//								@Override
//								public void onStopTrackingTouch(SeekBar seekBar) {
//									seekBar.setEnabled(false);
//								}
//							});
//						}
//					}
//				});
//
			}

			startTimer();

		}

	}

	private void loadFromPhone(String url) {
//		try {
//
//			if (!url.contains("file://")) {
//				url = "file://" + url;
//			}
//
//			Uri contentURI = Uri.parse(url);
//			ContentResolver cr = getContentResolver();
//			inputStream = cr.openInputStream(contentURI);
//			BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inSampleSize = 8;
//			Bitmap thumb = BitmapFactory.decodeStream(inputStream, null, null);
//			imageactivity.setImageBitmap(thumb);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (inputStream != null)
//					inputStream.close();
//				inputStream = null;
//			} catch (Exception e2) {
//				e2.printStackTrace();
//			}
//		}
	}

	@Override
	protected void onDestroy() {
		//imageactivity.setImageBitmap(null);

//		try {
//
//			if (vv_bsecure != null) {
//				if (vv_bsecure.isPlaying()) {
//					vv_bsecure.stopPlayback();
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		try {
			LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (wv_content != null) {
				wv_content.clearHistory();
				wv_content.clearCache(true);
				wv_content.clearFormData();
				wv_content = null;
				progressDialog.dismiss();
			}
		} catch (Exception e) {

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
				ImageActivity.this.finish();
			}
		}
	};

	private class MyWebChromeClient extends WebChromeClient {

		@Override
		public void onProgressChanged(WebView view, int newProgress) {

			/*
			 * if (newProgress >= 1) progressBar.setVisibility(View.VISIBLE);
			 *
			 * if (newProgress >= 95) { progressBar.setVisibility(View.GONE); }
			 */

			super.onProgressChanged(view, newProgress);
		}

	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			view.loadUrl(url);

			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			mUpdateTimeTask = new Runnable() {
				public void run() {
					progressDialog.dismiss();
				}
			};
			mHandler.postDelayed(mUpdateTimeTask, 5000);

			wv_content.loadUrl("javascript:(function() { " +
					"document.getElementsByClassName('ndfHFb-c4YZDc-GSQQnc-LgbsSe ndfHFb-c4YZDc-to915-LgbsSe VIpgJd-TzA9Ye-eEGnhe ndfHFb-c4YZDc-LgbsSe')[0].style.display='none'; })()");

		}

		@Override
		public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
			super.onReceivedError(view, request, error);
			// vodafone.showToast(error.getDescription()+"");
		}
	}

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
							ImageActivity.this.finish();
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
