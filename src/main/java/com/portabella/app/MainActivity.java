package com.portabella.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.portabella.app.GuitarActivity.GuitarActivity;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends Activity {

    private static final int LOADING_REQUEST_CODE = 123456789;
    private static final int GUITAR_REQUEST_CODE = 7436862;
    public static final String mBroadcastStringAction = "com.truiton.broadcast.string";
    SlidingMenu s;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, LoadingActivity.class);
        startActivityForResult(intent, LOADING_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                Intent intent = new Intent(MainActivity.this, GuitarActivity.class);
                startActivityForResult(intent, GUITAR_REQUEST_CODE);
                break;

            case RESULT_CANCELED:
                finish();
        }
    }
}