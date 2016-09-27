package com.portabella.app.GuitarActivity;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.LinearLayout;

import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

/**
 * This class represent the multitouch listener of the Guitar's activity.
 * Created by Tomer on 13/08/2016.
 */
public class ActivitySwipeDetector implements View.OnTouchListener {

    // there are 6 meitars on the guitar. every layout is devide to 2, where the meitar is in
    // the middle of every layout, and a strumming is preformed when the user make a gusture from
    // the one side of the layout to the second one (top to bottom or n=bottom to top).
    private static LinearLayout[] mietarsLayouts = new LinearLayout[CordManager.NUM_OF_MEITARS];
    private SortedSet<MultiPointerTouch> multiPointerTouch;
    float downXmenu, downYmenu, upXmenu, upYmenu;
//    private Context context;

    /**
     * This inner class represent a Pointer on the screen. each pointer has a unique id and contains
     * a list of layout that the user moved his finger on.
     */
    private class MultiPointerTouch implements Comparable<MultiPointerTouch> {
        private int id;
        private LinkedList<PointerTouch> pointerList;
        private VelocityTracker velocityTracker = null;
//        private boolean isStrumming = false;

        public MultiPointerTouch(int id) {
            this.id = id;
            pointerList = new LinkedList<PointerTouch>();
        }

        public int getId() {
            return id;
        }

        public void setVelocityTracker(VelocityTracker velocityTracker) {
            this.velocityTracker = velocityTracker;
        }

        public void clearVelocityTracker() {
            velocityTracker.clear();
        }

        /**
         * add a new Strumming to the pointer, and start the strumming action (if not started yet).
         * if if LayoutId == -1, than the pointer makes a new strumming.
         * @param layout The layout that the event took place.
         * @param pressure the pressure of the pointer on the screen.
         * @param x The x coordinate of the pointer.
         */
        public void addStrumming(int layout, float pressure, float x) {
            if (pointerList.size() == 0) {
                if (velocityTracker == null) {
                    // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                    setVelocityTracker(VelocityTracker.obtain());
                } else {
                    // Reset the velocity tracker back to its initial state.
                    clearVelocityTracker();
                }
                pointerList.addLast(new PointerTouch(layout, velocityTracker.getYVelocity(), pressure, x));
            } else {
                if (pointerList.getLast().getLayoutId() != layout) {
                    velocityTracker.computeCurrentVelocity(100);
                    pointerList.addLast(new PointerTouch(layout, velocityTracker.getYVelocity(), pressure, x));
//                    long startTime = System.currentTimeMillis();
                    run();
                }
            }
        }

        /**
         * runs the strumming action from the pointerList (plays all the meitars that is between
         * the first two mietarsLayouts in the pointerList). runs until there is no more
         * than 1 layout in the list.
         */
        public void run() {
            LinkedList<PointerTouch> list = this.getPointerList();
            while (list.size() > 1) {
                PointerTouch second = list.get(1);
                int firstLayoutId = list.getFirst().getLayoutId();
                int secondLayoutId = second.getLayoutId();
                if (firstLayoutId != -1) {
                    if (secondLayoutId == -1) {
                        // new strumming.
                        this.removeStrumming();
                    } else if (firstLayoutId < secondLayoutId &&(((firstLayoutId + secondLayoutId) % 4 == 1)
                            || (firstLayoutId + 1 < secondLayoutId))) {
                        // Down swipe.
                        int start = getMeitarBorder(firstLayoutId);
                        int end = getMeitarBorder(secondLayoutId);
                        onUpSwipe(second.getVelocity(), second.getPressure(), second.getX(), start, end);
                    } else if ((firstLayoutId + secondLayoutId) % 4 == 1 || firstLayoutId > secondLayoutId + 1) {
                        // up swipe.
                        int start = getMeitarBorder(firstLayoutId);
                        int end = getMeitarBorder(secondLayoutId);
                        onDownSwipe(second.getVelocity(), second.getPressure(), second.getX(), start, end);
                    }
                }
                this.removeStrumming();
            }
        }

        /**
         * returns the proper meitar according to the given layoutId.
         */
        private int getMeitarBorder(int layoutId) {
            int meitar = layoutId / 2;
            return layoutId % 2 == 1 ? ++meitar : meitar;
        }

        public void removeStrumming() {
            if (pointerList.size() > 1) {
                pointerList.removeFirst();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof MultiPointerTouch)) {
                return false;
            }
            // id must be the same for two MultiPointerTouch to be equal
            MultiPointerTouch mpt = (MultiPointerTouch) o;
            return this.id == mpt.getId();
        }

        @Override
        public int compareTo(MultiPointerTouch o) {
            if (id > o.getId())
                return 1;
            else if (id < o.getId())
                return -1;
            return 0;
        }

        public void addMovement(MotionEvent event) {
            if (velocityTracker == null) {
                // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                setVelocityTracker(VelocityTracker.obtain());
            }
            velocityTracker.addMovement(event);
        }

        public LinkedList<PointerTouch> getPointerList() {
            return pointerList;
        }
    }

    /**
     * Constructor.
     */
    public ActivitySwipeDetector(LinearLayout[] mietarsLayouts, Context context){
        ActivitySwipeDetector.mietarsLayouts = mietarsLayouts;
//        this.context = context;
        this.multiPointerTouch = new TreeSet<MultiPointerTouch>();
    }

    /**
     * Makes a strumming from bottom to top on the start to end meitar's
     */
    private static void onDownSwipe(float velocity, float pressure, float x, int start, int end) {
        for (int i = start - 1; i >= end; i--) {
            playMeitar(i, pressure, velocity, x);
        }
    }

    /**
     * Makes a strumming from top to bottom on the start to end meitar's
     */
    private static void onUpSwipe(float velocity, float pressure, float x, int start, int end) {
        for (int i = start; i < end; i++) {
            playMeitar(i, pressure, velocity, x);
        }
    }

    /**
     * plays the meitar with the given index and with the given params.
     */
    private static void playMeitar(int index, float pressure, float velocity, float x) {
        try {
            CordManager.restartTask(index, pressure, velocity, x);
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * react to a user gusture on the screen.
     * on ACTION_DOWN\ACTION_POINTER_DOWN: start a new strumming for the pointer with the given pointerId.
     * on ACTION_MOVE: update all the location of all the pointers that are on the screen.
     * on ACTION_POINTER_UP: start a new strumming for the pointer with the given pointerId.
     * on ACTION_UP\ACTION_CANCEL: start a new strumming for all pointers.
     */
    public boolean onTouch(View v, MotionEvent event) {
        // get pointer ID
        int pointerId = event.getPointerId(event.getActionIndex());

//        CordManager.pauseUnRunningTasks();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downXmenu = event.getX();
                downYmenu = event.getY();
            case MotionEvent.ACTION_POINTER_DOWN: {
                multiPointerTouch.add(new MultiPointerTouch(pointerId));
                MultiPointerTouch mpt = getPointer(pointerId);
                if (mpt != null && pointerId < event.getPointerCount()) {
                    float downY = event.getY(pointerId);
                    mpt.addStrumming(computeLayout(downY), event.getPressure(pointerId), event.getY(pointerId));
                }
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                for (int i = 0; i < event.getPointerCount(); i++) {
                    MultiPointerTouch mpt = getPointer(i);
                    if (mpt != null) {
                        float downY = event.getY(i);
                        mpt.addMovement(event);
                        mpt.addStrumming(computeLayout(downY), event.getPressure(i), event.getY(i));
                    } else {
                        multiPointerTouch.add(new MultiPointerTouch(i));
                    }
                }
                return true;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                MultiPointerTouch mpt = getPointer(pointerId);
                if (mpt != null) {
                    mpt.addStrumming(-1, 0, 0);
                }
                return true;
            }

            case MotionEvent.ACTION_UP:
                upXmenu = event.getX();
                upYmenu = event.getY();
                if (Math.abs(downXmenu - upXmenu) > 1100 && (downYmenu - upYmenu) < 20 &&
                        GuitarActivity.mImageViewRecording.getVisibility() == View.INVISIBLE) {
                    GuitarActivity.mImageViewRecording.setVisibility(View.VISIBLE);
                    GuitarActivity.mImageViewMenu.setVisibility(View.VISIBLE);
                }


            case MotionEvent.ACTION_CANCEL: {
                for (MultiPointerTouch element : multiPointerTouch) {
                    element.addStrumming(-1, 0 , 0);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * returns the proper MultiPointerTouch with the given id.
     */
    private MultiPointerTouch getPointer(int id) {
        for (MultiPointerTouch element : multiPointerTouch) {
            if (element.getId() == id) {
                return element;
            }
        }
        return null;
    }

    /**
     * returns the proper layout (out of 12 mietarsLayouts) from the given position x.
     */
    private static int computeLayout(float y) {
        int meitar = getMeitarByPosition(y);
        Log.e("meitar: ", "" + meitar);
        int tempMeitar = meitar;
        meitar *= 2;
        if (y > getMiddleOfLayout(mietarsLayouts[tempMeitar])) {
            meitar++;
        }
        return meitar;
    }

    /**
     * returns the proper meitar's number, by the given position.
     */
    private static int getMeitarByPosition(float y) {
        Log.e("y: ", "" + y);
        for (int i = 0; i < mietarsLayouts.length; i++) {
            Log.e("i: ", "" + i);
            Log.e("getBottom: ", "" + mietarsLayouts[i].getBottom());
            Log.e("getTop: ", "" + mietarsLayouts[i].getTop());
            if (y <= mietarsLayouts[i].getBottom() && y >= mietarsLayouts[i].getTop()) {
                return i;
            }
        }
        if (y <= mietarsLayouts[mietarsLayouts.length - 1].getTop()) {
            return 0;
        }
        return mietarsLayouts.length - 1;
    }

    /**
     * returns the position of the middle of the given meitar
     */
    private static float getMiddleOfLayout(LinearLayout meitar) {
        return (meitar.getBottom() + meitar.getTop()) / 2;
    }

    public void clear() {
        multiPointerTouch.clear();
    }
}
