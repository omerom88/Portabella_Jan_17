package com.portabella.app;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

/**
 * This activity is being called from the MainActivity at the start of the app. it is running an
 * animation and then calls to the Guitar Activity.
 * Created by omerrom on 18/09/16.
 */
public class LoadingActivity extends Activity
{
    private AnimationDrawable animationDrawableOpen;
    private AnimationDrawable animationDrawableMove;
    private ImageView mImageViewMoving;
    private MediaPlayer mediaPlayer;
    private boolean startedPlayer = false;

    private static final int MOVE_LOOP_NUM = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        mImageViewMoving = (ImageView) findViewById(R.id.imageview_animated_moving);
        mImageViewMoving.setVisibility(View.INVISIBLE);
        final ImageView mImageViewOpening = (ImageView) findViewById(R.id.imageview_animated_opening);
        animationDrawableOpen = (AnimationDrawable)mImageViewOpening.getBackground();
        animationDrawableMove = (AnimationDrawable)mImageViewMoving.getBackground();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.porta_open_2);
    }

    @Override
    protected void onStart() {
        super.onStart();
        animationDrawableOpen.start();
        checkIfAnimationOpenDone(animationDrawableOpen, this);
    }

    private void checkIfAnimationOpenDone(AnimationDrawable anim, final Activity activity){
        final AnimationDrawable a = anim;
        int timeBetweenChecks = 300;
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                if (a.getCurrent() != a.getFrame(0)  && !startedPlayer) {
                    startedPlayer = true;
                    mediaPlayer.start();
                }
                if (a.getCurrent() != a.getFrame(a.getNumberOfFrames() - 1)) {
                    checkIfAnimationOpenDone(a, activity);
                } else {
                    mImageViewMoving.setVisibility(View.VISIBLE);
                    animationDrawableMove.start();
                    checkIfAnimationMoveDone(animationDrawableMove, MOVE_LOOP_NUM, activity);
                }
            }
        }, timeBetweenChecks);
    }

    private void checkIfAnimationMoveDone(AnimationDrawable anim, final int loopNum, final Activity activity){
        final AnimationDrawable a = anim;
        int timeBetweenChecks = 300;
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                if (a.getCurrent() != a.getFrame(a.getNumberOfFrames() - 1)) {
                    checkIfAnimationMoveDone(a, loopNum, activity);
                } else {
                    int tempLoopNum = loopNum;
                    tempLoopNum--;
                    if (tempLoopNum == 0) {
                        activity.setResult(MainActivity.RESULT_OK);
                        activity.finish();
                    } else {
                        a.selectDrawable(0);
                        a.stop();
                        a.start();
                        checkIfAnimationMoveDone(a, tempLoopNum, activity);
                    }
                }
            }
        }, timeBetweenChecks);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}
