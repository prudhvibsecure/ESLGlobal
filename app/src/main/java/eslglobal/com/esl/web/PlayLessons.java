package eslglobal.com.esl.web;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import eslglobal.com.esl.ImageActivity;
import eslglobal.com.esl.R;
import eslglobal.com.esl.common.AppPreferences;

/**
 * Created by w7u on 10/3/2016.
 */

public class PlayLessons extends AppCompatActivity {

    private VideoView ll_bsecure = null;
    private ProgressDialog progressDialog;

    private MediaController mediaControls;

    private int position = 0;

    private TextView waterMark;
    @Override
    protected void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lessionview);

        waterMark = (TextView) findViewById(R.id.text_watermark);
        waterMark.setVisibility(View.GONE);

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Maximum.ttf");
        waterMark.setTypeface(font);

        waterMark.setText(AppPreferences.getInstance(this).getFromStore("email"));
        Intent intent = this.getIntent();
        if (intent != null) {
            String filePath = intent.getStringExtra("filepath");

            ll_bsecure = (VideoView) findViewById(R.id.lession_paly);

            if (mediaControls == null) {
                mediaControls = new MediaController(PlayLessons.this);
            }

            progressDialog = new ProgressDialog(PlayLessons.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            try {
                ll_bsecure.setMediaController(mediaControls);
                ll_bsecure.setVideoURI(Uri.parse(filePath));

            } catch (Exception e) {
                // Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            ll_bsecure.requestFocus();
            ll_bsecure.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                public void onPrepared(MediaPlayer mediaPlayer) {
                    progressDialog.dismiss();
                    ll_bsecure.seekTo(position);
                    if (position == 0) {
                        ll_bsecure.start();
                    } else {
                        ll_bsecure.pause();
                    }
                        int topContainerId = getResources().getIdentifier("mediacontroller_progress", "id",
                                "android");
                        SeekBar seekBarVideo = (SeekBar) mediaControls.findViewById(topContainerId);
                        seekBarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                seekBar.setEnabled(true);
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                               // seekBar.setEnabled(false);
                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                               // seekBar.setEnabled(false);
                            }
                        });
                    }

            });
        }
    }
}
