package com.portabella.app.GuitarActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.portabella.app.AnimationClass;
import com.portabella.app.Hardware.HelloIOIOService2;
import com.portabella.app.MenuFeatures.ChooseTheme;
import com.portabella.app.MenuFeatures.MenuActivityGif;
import com.portabella.app.R;
import com.portabella.app.Recording.RecordPlayerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * This class is an Activity for playing the guitar.
 * Created by Tomer on 21/08/2016.
 */
public class GuitarActivity extends Activity {

    public static float[] retMeitar = {0f,0f,0f,0f,0f,0f};
    public static int[] retSrigim = {-1,-1,-1,-1,-1,-1};
    public static LinearLayout baseGuitarLayout;
    public static File recordOutput;
    private ActivitySwipeDetector activitySwipeDetector;
    public static ImageView mImageViewRecording;
    public static ImageView mImageViewMenu;

    private IntentFilter mIntentFilter;
    private static String fileName;
    private static final String mBroadcastStringAction = "com.truiton.broadcast.string";
    private static AnimationClass animationDrawableStartRec;
    private static AnimationClass animationDrawableMenu;


    public static final int[] NOTES_LAYOUTS = {R.id.E_LOW, R.id.A, R.id.D, R.id.G, R.id.B, R.id.E_HIGH};
    public static final int[] ROCK_NOTES = {R.raw.e_string_low, R.raw.a_string, R.raw.d_string,
            R.raw.gstring, R.raw.b_string, R.raw.e_string_hi};
    public static final int[] REG_NOTES = {R.raw.estringlow, R.raw.astring, R.raw.dstring,
            R.raw.gstring, R.raw.bstring, R.raw.estringhi};
    public static final int[] BLUES_NOTES = {R.raw.elowstringblues, R.raw.astringblues, R.raw.dstringblues,
            R.raw.gstringblues, R.raw.bstringblues, R.raw.ehighstringblues};
    public static final List<int[]> ALL_NOTES = new ArrayList<int[]>(){{
        add(REG_NOTES);
        add(BLUES_NOTES);
        add(ROCK_NOTES);
    }};


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guitar_layout);
        baseGuitarLayout = (LinearLayout) findViewById(R.id.guitarLayout);

        LinearLayout[] layouts = new LinearLayout[CordManager.NUM_OF_MEITARS];
            for (int i = 0; i < layouts.length; i++) {
            layouts[i] = (LinearLayout) findViewById(NOTES_LAYOUTS[i]);
        }
        //////////////// the gesture  /////////////////
        initTheme();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size);
            CordManager.setWidth(size.x);
        } else {
            CordManager.setWidth(Resources.getSystem().getDisplayMetrics().widthPixels);
        }

        ////////    string layouts detector   ///////////
        activitySwipeDetector = new ActivitySwipeDetector(layouts, this);
        baseGuitarLayout.setOnTouchListener(activitySwipeDetector);


        ///////      rec button     ////////////
        initRecordingButton();


        //////////      menu button     /////////
        initMenuButton();

        //////// start ioio activity //////////
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastStringAction);
        final Intent intent = new Intent(GuitarActivity.this, HelloIOIOService2.class);
        startService(intent);
    }

    /**
     * initialize the theme of this activity.
     */
    private void initTheme() {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int defTheme = SP.getInt(getString(R.string.GuitarActivity_initNotes), 0);
        CordManager.init(this, getApplicationContext(), ALL_NOTES.get(defTheme));
        ChooseTheme.setTheme(getBackground(defTheme), ALL_NOTES.get(defTheme), getResources());
    }

    /**
     * initialize the recording button of this activity.
     */
    private void initRecordingButton() {
        mImageViewRecording = (ImageView) findViewById(R.id.imageview_animated_recording);
        animationDrawableStartRec = new AnimationClass((AnimationDrawable)mImageViewRecording.getBackground());
        mImageViewRecording.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (!CordManager.isRecording()) {
                            animationDrawableStartRec.start();
                            animationDrawableStartRec.checkIfAnimationDone();
                            return true;
                        }
                    }
                    case MotionEvent.ACTION_UP: {
                        if (!CordManager.isRecording()) {
                            if (animationDrawableStartRec.isAnimationFlag()) {
                                animationDrawableStartRec.setAnimationFlag(false);
                                CordManager.startRecording();
                            } else {
                                animationDrawableStartRec.restartAnimation();
                            }
                        } else {
                            animationDrawableStartRec.restartAnimation();
                            CordManager.stopRecording();
                            activitySwipeDetector.clear();
                            openSaveFileDialog();
                        }
                        //TODO: when leave button
                    }
                    return false;
                }
                return false;
            }
        });
    }

    /**
     * initialize the menu button of this activity.
     */
    private void initMenuButton() {
        mImageViewMenu = (ImageView) findViewById(R.id.imageview_animated_menu);
        animationDrawableMenu = new AnimationClass((AnimationDrawable)mImageViewMenu.getBackground());
        mImageViewMenu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN: {
                        animationDrawableMenu.start();
                        animationDrawableMenu.checkIfAnimationDone();
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (animationDrawableMenu.isAnimationFlag()) {
                            animationDrawableMenu.setAnimationFlag(false);
                            Intent intent = new Intent(GuitarActivity.this, MenuActivityGif.class);
                            startActivity(intent);
                        }
                        animationDrawableMenu.restartAnimation();
                    }
                    return false;
                }
                return false;
            }
        });
    }

    /**
     * Opens a dialog when a new recording is done for saving the new song.
     */
    public void openSaveFileDialog() {
        CordManager.pauseAllTasks();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("How would you like to name your new recording?");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    fileName = input.getText().toString();
                    recordOutput = CordManager.saveFile(fileName);
                    openPlaySongDialog(fileName);
                } catch (OutOfMemoryError e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GuitarActivity.this);
                    builder.setTitle("Problem is saving record");
                    builder.setMessage("your record was too long. try to minimize the record duration");
                    builder.setPositiveButton("Get Back", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }});
                    builder.show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CordManager.cancelRecord();
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * Opens a dialog when a new recording is done for playing the new song.
     */
    private void openPlaySongDialog(final String recordName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to play it now?");
        // Set up the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(GuitarActivity.this, RecordPlayerActivity.class);
                intent.putExtra("recordName", recordName);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public static int getBackground(int value) {
        switch (value) {
            case 0:
                return R.drawable.guitarscreenbase;
            case 1:
                return R.drawable.guitarscreenblues;
            case 2:
                return R.drawable.guitarscreenroll;
        }
        return R.drawable.guitarscreenbase;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        int[] lastSarig = {-1,-1,-1,-1,-1,-1};
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(mBroadcastStringAction)) {

                for (int i = 0; i <= 5; i++){
                    String strM = "meitar" + Integer.toString(i);
                    String strS = "srigim" + Integer.toString(i);
                    String strB = "Velbridge" + Integer.toString(i);
                    retMeitar[i] = intent.getFloatExtra(strM, 0);
                    retSrigim[i] = intent.getIntExtra(strS, -1);
                    float retVelBridge = intent.getFloatExtra(strB, 0);
                    if (retVelBridge != 0f & lastSarig[i] != retSrigim[i]){
                        CordManager.restartTask(i, 1, retVelBridge *10000, retSrigim[i]);
                    }
                    lastSarig[i] = retSrigim[i];
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
        animationDrawableMenu.selectDrawable(0);
        animationDrawableStartRec.selectDrawable(0);
//        animationDrawableMenu.setVisible(false, false);
//        animationDrawableStartRec.setVisible(false, false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CordManager.pauseAllTasks();
        unregisterReceiver(mReceiver);
    }
}

//        ///////// menu ////////

//        final SlidingMenu menu = new SlidingMenu(this);
//        menu.setMode(SlidingMenu.LEFT);
//        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
//        menu.setFadeEnabled(true);
//        menu.setFadeDegree(0.35f);
//        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
//        menu.setMenu(R.layout.activity_menu2);
//        menu.setBehindWidth(500);
//        menu.setOnOpenListener(new SlidingMenu.OnOpenListener() {
//            @Override
//            public void onOpen() {
//                Button setBut = (Button)findViewById(R.id.settinButton);
//                setBut.setOnClickListener(new Button.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        menu.toggle();
//                    }
//                });
//            }
//        });