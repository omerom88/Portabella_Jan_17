package com.portabella.app.MenuFeatures;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.portabella.app.R;

/**
 * This class represent the menu activity of the app.
 * Created by omerrom on 17/09/16.
 */
public class MenuActivityGif extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout_gif);

        final View menuView = findViewById(R.id.menu_layout);
        Button metroBut = (Button) findViewById(R.id.stop);
        Button settingBut = (Button) findViewById(R.id.pauseButton);
        Button themeBut = (Button) findViewById(R.id.playButton);

        themeBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuView.setBackground(getResources().getDrawable(R.drawable.menu0004));
                Intent intent = new Intent(MenuActivityGif.this, ChooseTheme.class);
                startActivity(intent);
                finish();
            }
        });


        metroBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuView.setBackground(getResources().getDrawable(R.drawable.menu0002));
                Intent intent = new Intent(MenuActivityGif.this, MetronomeActivity.class);
                startActivity(intent);
                finish();

            }
        });

        settingBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuView.setBackground(getResources().getDrawable(R.drawable.menu0003));
                Intent intent = new Intent(MenuActivityGif.this, SettingActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
