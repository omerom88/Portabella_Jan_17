package com.portabella.app;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;

/**
 * This class is representing an animation. used in the activities mostly for running the animation
 * and check when it is done.
 * Created by Tomer on 24/09/2016.
 */
public class AnimationClass extends AnimationDrawable {
    private boolean animationFlag = false;
    private boolean animationRunning = false;
    private AnimationDrawable animation;

    public AnimationClass(AnimationDrawable animation) {
        this.animation = animation;
    }

    public boolean isAnimationFlag() {
        return animationFlag;
    }

    public void setAnimationFlag(boolean animationFlag) {
        this.animationFlag = animationFlag;
    }

    public AnimationDrawable getAnimation() {
        return animation;
    }

    public void setAnimation(AnimationDrawable animation) {
        this.animation = animation;
    }

    public boolean isAnimationRunning() {
        return animationRunning;
    }

    public void setAnimationRunning(boolean animationRunning) {
        this.animationRunning = animationRunning;
    }

    @Override
    public void start() {
        animation.start();
        animationRunning = true;
    }

    @Override
    public void stop() {
        animation.stop();
        animationRunning = false;
    }

    @Override
    public boolean selectDrawable(int idx) {
        return animation.selectDrawable(idx);
    }

    /**
     * Checks if the animation is done running. if so, the animationFlag variable will become true.
     */
    public void checkIfAnimationDone(){
        int timeBetweenChecks = 300;
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                if (animation.getCurrent() != animation.getFrame(animation.getNumberOfFrames() - 1)) {
                    if (isAnimationRunning()) {
                        checkIfAnimationDone();
                    }
                } else {
                    animationFlag = true;
                }
            }
        }, timeBetweenChecks);
    }

    public void restartAnimation() {
        animation.selectDrawable(0);
        animation.stop();
    }
}
