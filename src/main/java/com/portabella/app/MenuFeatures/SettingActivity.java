package com.portabella.app.MenuFeatures;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.portabella.app.GuitarActivity.GuitarActivity;
import com.portabella.app.R;

/**
 * This class represent the Setting Activity of the App. every setting option that has been
 * chosen by the user is saved in the SharedPreferences Object.
 * Created by omerrom on 25/09/16.
 */
public class SettingActivity extends Activity {

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final Button bpm_70 = (Button) findViewById(R.id.bpm70);
        final Button bpm_90 = (Button) findViewById(R.id.bpm90);
        final Button bpm_120 = (Button) findViewById(R.id.bpm120);

        final Button beats_4 = (Button) findViewById(R.id.beats4);
        final Button beats_8 = (Button) findViewById(R.id.beats8);

        final Button roll = (Button) findViewById(R.id.setRoll);
        final Button blues = (Button) findViewById(R.id.setBlues);
        final Button calm = (Button) findViewById(R.id.setCalm);

        final Button clear = (Button) findViewById(R.id.cleanScreen);


        ///////// bpm  //////////

        bpm_70.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSettingsInt(getString(R.string.MetronomActivity_bpm), 70);
                bpm_70.setBackgroundColor(Color.parseColor("#edb3a6"));
                bpm_90.setBackgroundColor(Color.parseColor("#7ca0bf"));
                bpm_120.setBackgroundColor(Color.parseColor("#7ca0bf"));

            }
        });

        bpm_90.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSettingsInt(getString(R.string.MetronomActivity_bpm), 90);
                bpm_90.setBackgroundColor(Color.parseColor("#edb3a6"));
                bpm_70.setBackgroundColor(Color.parseColor("#7ca0bf"));
                bpm_120.setBackgroundColor(Color.parseColor("#7ca0bf"));
            }
        });

        bpm_120.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSettingsInt(getString(R.string.MetronomActivity_bpm), 120);
                bpm_120.setBackgroundColor(Color.parseColor("#edb3a6"));
                bpm_70.setBackgroundColor(Color.parseColor("#7ca0bf"));
                bpm_90.setBackgroundColor(Color.parseColor("#7ca0bf"));
            }
        });

        ///////// beats  //////////

        beats_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSettingsInt(getString(R.string.MetronomActivity_beats), 4);
                beats_4.setBackgroundColor(Color.parseColor("#edb3a6"));
                beats_8.setBackgroundColor(Color.parseColor("#7ca0bf"));
            }
        });

        beats_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSettingsInt(getString(R.string.MetronomActivity_beats), 8);
                beats_8.setBackgroundColor(Color.parseColor("#edb3a6"));
                beats_4.setBackgroundColor(Color.parseColor("#7ca0bf"));
            }
        });


        ///////// themes  //////////

        roll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSettingsInt(getString(R.string.GuitarActivity_initNotes), 2);
                roll.setBackgroundColor(Color.parseColor("#edb3a6"));
                blues.setBackgroundColor(Color.parseColor("#7ca0bf"));
                calm.setBackgroundColor(Color.parseColor("#7ca0bf"));

            }
        });

        blues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSettingsInt(getString(R.string.GuitarActivity_initNotes), 1);
                blues.setBackgroundColor(Color.parseColor("#edb3a6"));
                roll.setBackgroundColor(Color.parseColor("#7ca0bf"));
                calm.setBackgroundColor(Color.parseColor("#7ca0bf"));
            }
        });


        calm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSettingsInt(getString(R.string.GuitarActivity_initNotes), 0);
                calm.setBackgroundColor(Color.parseColor("#edb3a6"));
                roll.setBackgroundColor(Color.parseColor("#7ca0bf"));
                blues.setBackgroundColor(Color.parseColor("#7ca0bf"));
            }
        });

        //////  clean screen    ////

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuitarActivity.mImageViewRecording.setVisibility(View.INVISIBLE);
                GuitarActivity.mImageViewMenu.setVisibility(View.INVISIBLE);
                clear.setBackgroundColor(Color.parseColor("#edb3a6"));
                Toast toast = Toast.makeText(getApplicationContext(), "to bring them back - swipe your finger from side to side",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM,0,0);
                toast.show();
                clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clear.setBackgroundColor(Color.parseColor("#7ca0bf"));
                        GuitarActivity.mImageViewRecording.setVisibility(View.VISIBLE);
                        GuitarActivity.mImageViewMenu.setVisibility(View.VISIBLE);

                    }
                });
            }
        });

    }

    private void changeSettingsInt(String name, int value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(name, value);
        editor.apply();
    }
}
