package com.portabella.app.MenuFeatures;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.portabella.app.R;

/**
 * This class is the Metronome Activity of the the App.
 * Created by omerrom on 24/09/16.
 */


public class MetronomeActivity extends Activity {

    public static double bpm = 0;
    public static int beats = 0;
    private boolean playPressed = false;
    private View metroLayout;

    Metronome metronome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metronom);

        Button bmp_70 = (Button)findViewById(R.id.bpm1);
        Button bmp_90 = (Button)findViewById(R.id.bpm2);
        Button bmp_120 = (Button)findViewById(R.id.bpm3);
        final Button beats_4 = (Button)findViewById(R.id.beats1);
        final Button beats_8 = (Button)findViewById(R.id.beats2);
        Button stop = (Button)findViewById(R.id.stop);
        Button play = (Button)findViewById(R.id.play);
        metroLayout = findViewById(R.id.metroLayout);

        metronome = new Metronome();

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        bpm = SP.getInt(getString(R.string.MetronomActivity_bpm), 0);
        beats = SP.getInt(getString(R.string.MetronomActivity_beats), 0);

        bmp_70.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bpm = 70;
                metroLayout.setBackground(getResources().getDrawable(R.drawable.metronom0002));

                beats_4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        beats = 4;
                        metroLayout.setBackground(getResources().getDrawable(R.drawable.metronom0003));
                    }
                });

                beats_8.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        beats = 8;
                        metroLayout.setBackground(getResources().getDrawable(R.drawable.metronom0004));
                    }
                });
            }
        });

        bmp_90.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bpm = 90;
                metroLayout.setBackground(getResources().getDrawable(R.drawable.metronom0005));

                beats_4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        beats = 4;
                        metroLayout.setBackground(getResources().getDrawable(R.drawable.metronom0006));
                    }
                });

                beats_8.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        beats = 8;
                        metroLayout.setBackground(getResources().getDrawable(R.drawable.metronom0007));
                    }
                });

            }
        });

        bmp_120.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bpm = 120;
                metroLayout.setBackground(getResources().getDrawable(R.drawable.metronom0008));

                beats_4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        beats = 4;
                        metroLayout.setBackground(getResources().getDrawable(R.drawable.metronom0009));
                    }
                });

                beats_8.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        beats = 8;
                        metroLayout.setBackground(getResources().getDrawable(R.drawable.metronom00010));
                    }
                });
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playPressed) {
                    playPressed = false;
                    metronome.stop();
                }

            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bpm != 0 && beats != 0) {
                    metronome.stop();
                    playPressed = true;
                    playPublic(bpm,beats);
                }
            }
        });
    }

    public void playPublic(final double bpm, final int beats) {
        new Thread(new Runnable() {
            public void run() {
                metronome.setVars(bpm, beats);
                metronome.play();
            }
        }).start();
    }


    @Override
    protected void onStop() {
        setResult(Activity.RESULT_OK);
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        setResult(Activity.RESULT_OK);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // When the user hits the back button set the resultCode
        // to Activity.RESULT_CANCELED to indicate a failure
        metronome.stop();
        super.onBackPressed();
    }
}
