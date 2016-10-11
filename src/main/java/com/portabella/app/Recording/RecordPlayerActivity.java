package com.portabella.app.Recording;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.portabella.app.GuitarActivity.GuitarActivity;
import com.portabella.app.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 *
 * This class represent the player activity for the recorded song.
 * it is used after the user finished recording a song.
 * Created by Tomer on 02/09/2016.
 */
public class RecordPlayerActivity extends Activity {
    private Button stopButton, pauseButton, playButton;
    private MediaPlayer mediaPlayer;
    private double startTime = 0;
    private double finalTime = 0;
    private Handler myHandler = new Handler();
    private TextView tx1,tx2,tx3;
    private boolean mediaPlayerInit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_player);

        stopButton = (Button) findViewById(R.id.stopButton);
        pauseButton = (Button) findViewById(R.id.pauseButton);
        playButton = (Button)findViewById(R.id.playButton);
        final View themeLayout = findViewById(R.id.themeLayout);

        tx1=(TextView)findViewById(R.id.textView1);
        tx2=(TextView)findViewById(R.id.textView2);
        tx3=(TextView)findViewById(R.id.textView3);
        File song = GuitarActivity.recordOutput;
        tx3.setText(getIntent().getStringExtra("recordName"));

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.reset();
            FileInputStream fis = new FileInputStream(song);
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.prepare();
            mediaPlayerInit = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        stopButton.setEnabled(false);
        pauseButton.setEnabled(false);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themeLayout.setBackground(getResources().getDrawable(R.drawable.popupfull0001));
                if (mediaPlayerInit) {
                    mediaPlayer.start();

//                    Log.e("getDuration: ", mediaPlayer.getDuration() + "");
                    finalTime = mediaPlayer.getDuration();
                    startTime = mediaPlayer.getCurrentPosition();

                    tx2.setText(String.format("/  %d",
                            TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
                    );

                    tx1.setText(String.format("%d",
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
                    );

                    myHandler.postDelayed(UpdateSongTime, 100);
                    stopButton.setEnabled(true);
                    pauseButton.setEnabled(true);
                    playButton.setEnabled(false);
                }
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themeLayout.setBackground(getResources().getDrawable(R.drawable.popupfull0002));
                if (mediaPlayerInit) {
                    mediaPlayer.pause();
                    pauseButton.setEnabled(false);
                    playButton.setEnabled(true);
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themeLayout.setBackground(getResources().getDrawable(R.drawable.popupfull0003));
                if (mediaPlayerInit) {
                    stopButton.setEnabled(false);
                    pauseButton.setEnabled(false);
                    playButton.setEnabled(true);
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0);
                }
            }
        });
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            if (mediaPlayerInit) {
                startTime = mediaPlayer.getCurrentPosition();
                tx1.setText(String.format("%d",
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                        toMinutes((long) startTime)))
                );
                myHandler.postDelayed(this, 100);
            }
        }
    };

    @Override
    public void onBackPressed() {
        mediaPlayerInit = false;
        mediaPlayer.stop();
        mediaPlayer.release();
        super.onBackPressed();  // optional depending on your needs
    }
}
