package com.portabella.app.GuitarActivity;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.audiofx.Equalizer;
import android.os.Build;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * This class represent a cord in the guitar.
 * Created by omerrom on 12/07/16.
 */
public class Cord implements Runnable {
    private Task task;
    private int index; // the index of this cord.
    private short[] sample; // the sample of which we make the soung
    private AudioTrack audioTrack;
    private Equalizer equalizer;
    public static final int DEFAULT_RATE = 44100;
    private static final int MILI_CONVERTOR = 1000;
    private static final int MAX_FREQ = 400 * MILI_CONVERTOR;
    private int bufferAddPerIteration = 0;
    private int numOfIterations = CordManager.NUM_OF_ITERATIONS;
    private short minEQLevel, maxEQLevel, bandNumMaxFreq; // params for the equalizer
    private boolean init;

    private static Context context;
    private static final float[] RATE_ARRAY = new float[13];
    private static final int PRESSURE_CONST = 4000;
    private static final float MIN_PRESSURE = 0.001f;
    private static final int HEADER_SIZE = 44;

    static {
        // creates all the possible Accord's rates of the guitar.
        for (int i = 0; i < RATE_ARRAY.length; i++) {
            RATE_ARRAY[i] = (float) Math.pow(2, i / (float) 12);
        }
    }

    /**
     * Constructor
     * @param index the index of this cord.
     * @param context the calling activity
     * @param wav the resource of the sample
     * @param numOfIterations the number of iterations for a strumming.
     */
    public Cord(int index, Context context, int wav, int numOfIterations) {
        Cord.context = context;
        this.task = new Task();
        this.index = index;
        this.numOfIterations = (int)((double)numOfIterations);
        int minBufferSize = AudioTrack.getMinBufferSize(DEFAULT_RATE,
                AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        this.audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, DEFAULT_RATE,
                AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize,
                AudioTrack.MODE_STREAM);
        setCord(wav);
    }

    /**
     * calculate the volume for the next iteration.
     */
    public float calcVolume(float currVolume, float pressure, boolean withIOIO) {
        if (withIOIO) {
            if (pressure == 0.0) {
                pressure = MIN_PRESSURE;
            }
            currVolume -= ((float)1/40000);//(400 * getPressureLog(pressure)));
//            Log.e("vol: ",Float.toString(currVolume));
        }
        return currVolume;
    }

    private float getPressureLog(float pressure) {
        return (float) Math.log(pressure);
    }

    /**
     * calculate the pitch for the next iteration.
     */
    private static int calcPitch(int frat) {
//        Log.e("pitch: ", "" + (int) (DEFAULT_RATE * RATE_ARRAY[frat]));
        return (int) (DEFAULT_RATE * RATE_ARRAY[frat]);
    }

    public void initEqualizer(int eqFreq) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            for (short i = 0; i < equalizer.getNumberOfBands(); i++) {
                if (i <= bandNumMaxFreq) {
                    double mult = getBandPrecentage(i + 1, bandNumMaxFreq + 1, eqFreq);
                    equalizer.setBandLevel(i, (short)(minEQLevel + ((maxEQLevel - minEQLevel) * mult)));
                } else {
                    equalizer.setBandLevel(i, maxEQLevel);
                }
            }
        }
    }

    private double getBandPrecentage(int bandNum, int bandNumMaxFreq, int freq) {
        double percentage = Math.pow(((double) (bandNum) / (bandNumMaxFreq)), 2) * (1 - ((double) freq / MAX_FREQ));
        return Math.min(2 * percentage, 1);
    }

    public void stopTrack() {
        if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            setVolume(0);
        }
    }

    /**
     * play the next itration of this cord.
     */
    private void playIteration(int currIndex, int curSarig, float currVolume) {
        int playbackRate = Cord.calcPitch(GuitarActivity.retSrigim[curSarig] + 1);
        audioTrack.setPlaybackRate(playbackRate);
//       check the 10* ___, its something with the equlizer.
        audioTrack.setStereoVolume(currVolume, currVolume);
//                setVolume(audioTrack, currVolume);
        play(currIndex);
        if (CordManager.isRecording()) {
            new RecordCord(sample, currIndex, currIndex + bufferAddPerIteration, playbackRate, currVolume).run();
        }
    }

    private void play(int start) {
        int writeSize =  audioTrack.write(sample, start, bufferAddPerIteration);
    }

    private void play(short[] music, int start, int end) {
        int writeSize =  audioTrack.write(music, start, end);
    }

    private static byte[] convertStreamToByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final int BUFFER_SIZE = 8192;
        byte[] buff = new byte[BUFFER_SIZE];
        int i;
        while ((i = is.read(buff, 0, buff.length)) > 0) {
            baos.write(buff, 0, i);
        }
        return baos.toByteArray();
    }

    public void pauseTask() {
        if (task != null) {
            try {
                task.pauseTask();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    synchronized void resume(float pressure, float velocityY, float xPos) {
        task.setAndStart(pressure, velocityY, xPos);
    }

    /**
     * remove the headers of the byte array of the wav file.
     */
    private static byte[] removeHeaders(byte[] array) {
        byte[] tempBytes = array.clone();
        array = new byte[tempBytes.length - HEADER_SIZE];
        System.arraycopy(tempBytes, HEADER_SIZE, array, 0, tempBytes.length - HEADER_SIZE);
        return array;
    }

    public void setCord(int wav) {
        InputStream in = context.getResources().openRawResource(wav);
        sample = readSampleInShort(in, true);
        this.bufferAddPerIteration = sample.length / numOfIterations;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            this.equalizer = new Equalizer(0, audioTrack.getAudioSessionId());
            this.equalizer.setEnabled(true);
            this.minEQLevel = equalizer.getBandLevelRange()[0];
            this.maxEQLevel = equalizer.getBandLevelRange()[1];
            this.bandNumMaxFreq = (short) Math.max((int) equalizer.getBand(MAX_FREQ), 0);
        }
        init = true;
    }

    public static short[] readSampleInShort(InputStream in, boolean removeHeaders) {
        byte[] array = new byte[0];
        try {
            array = convertStreamToByteArray(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(removeHeaders) {
            removeHeaders(array);
        }
        short[] shortSample = new short[array.length / 2];
        ByteBuffer.wrap(array).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shortSample);
        return shortSample;
    }

    @Override
    public void run() {
        task.start();
    }

    public short[] getSample() {
        return sample;
    }

    /**
     * this class represent a task of a strumming cord. each time the user swipe on this cord,
     * the task will start and play the right sound of this cord,
     * according to all parameters (like pitch, volume etc.).
     */
    public class Task extends Thread {
        private float pressure = 0f;
        private float velocityY;
        private float xPos;
        private volatile boolean running;
        private volatile boolean playing;
        private volatile boolean new_task;
        private float startVolume;
        private int eqFreq;
        public int counter = 0;
        private long lastTimeRunMillis;

        private static final float MIN_VELOCITY = 0;
        private static final int VELOCITY_NORMALIZE_CONSTANT = 10000;
        private static final float MAX_PRESSURE = 1f;
        private static final float MIN_PRESSURE = 0.7f;
        private static final int MAX_BRIDG_PRESSURE = 1000;
        private static final int MIN_BRIDG_PRESSURE = 0;


        public Task() {
            running = true;
            playing = true;
            new_task = true;
        }

        @Override
        public void run() {
            while(running) {
                audioTrack.play();
                synchronized(this) {
                    cordNotPlaying();
                }
                if (playing) {
                    new_task = false;
                    if(CordManager.isRecording()) {
                        lastTimeRunMillis = recordEmptySound(lastTimeRunMillis);
                    }
                    stopTrack();
                    int currIndex = 0;
                    if (setProperties()) {
                        initEqualizer(eqFreq);
                        float currVolume = startVolume;
                        for (int i = 0; i < numOfIterations; i++) {
                            if (!playing || new_task) {
                                lastTimeRunMillis = System.currentTimeMillis();
                                break;
                            }

                            playIteration(currIndex, index, currVolume * 10);
                            currIndex += bufferAddPerIteration;
                            currVolume = calcVolume(currVolume, GuitarActivity.retMeitar[index], true);
                            float presh = GuitarActivity.retMeitar[index];
                            if (!BridgPressure(presh, index)) {
                                lastTimeRunMillis = System.currentTimeMillis();
                                break;
                            }

                        }
                    }
                    if (!new_task) {
                        playing = false;
                    }
                    lastTimeRunMillis = System.currentTimeMillis();
                }
            }
            try {
                wait(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void cordNotPlaying() {
            while(!playing) {
                try {
                    if(CordManager.isRecording()) {
                        lastTimeRunMillis = recordEmptySound(lastTimeRunMillis);
                    }
                    wait(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public Boolean BridgPressure(float presh, int index){
            if (presh == 0.0){
                presh = 1000;
            }
            else if (presh < 0.1){
                presh = 8;
            }
            else{
                if (index == 100)
                {
                    presh = (float)(((MAX_BRIDG_PRESSURE - MIN_BRIDG_PRESSURE) * (presh - 0.01) / (0.4 - 0.01)) + MIN_BRIDG_PRESSURE);
                }
                else {
                    presh = (float) (((MAX_BRIDG_PRESSURE - MIN_BRIDG_PRESSURE) * (presh - 0.01) / (0.6 - 0.01)) + MIN_BRIDG_PRESSURE);
                }
            }

            if (counter > presh){
                counter = 0;
                //currVolume = 0;
                return false;
            }
            counter++;
            return true;
        }

        /**
         * starts a new task of strumming this cord.
         * @param pressure the pressure on the phone in the strumming time (for start volume).
         * @param velocityY the velocity on strumming (for start volume).
         * @param xPos the position of the strumming on the cord.
         */
        public void setAndStart(float pressure, float velocityY, float xPos) {
            this.pressure = pressure;
            this.velocityY = velocityY;
            this.xPos = xPos;
            this.new_task = true;
            resumeThread();
        }

        /**
         * sets the start volume and eq for the next strumming
         * @return true if the velocity is highr than the velocity threshold. otherwise, returns false.
         */
        private boolean setProperties() {
            float normVelocity = Math.abs(velocityY / VELOCITY_NORMALIZE_CONSTANT);
            if (normVelocity > MIN_VELOCITY) {
                float normPressure = MIN_PRESSURE + (MAX_PRESSURE - MIN_PRESSURE) * pressure;
                this.startVolume = normVelocity * normPressure;
                this.eqFreq = calcEqFreq(xPos);
                return true;
            } else {
                return false;
            }
        }

        public void pauseTask() throws InterruptedException {
            stopTrack();
            this.playing = false;
        }

        /**
         * calculating the wanted equalizer freq using the distance of the X axis event from
         * the middle of the screen (reltive distance from the middle).
         * @param currX the X axis of the touch event.
         * @return
         */
        private int calcEqFreq(float currX) {
            return (int) (2 * Cord.MAX_FREQ * Math.abs((CordManager.getWidth() / 2) - currX) / CordManager.getWidth());
        }

        synchronized void resumeThread() {
            playing = true;
            lastTimeRunMillis = System.currentTimeMillis();
            notify();
        }

        /**
         * record an empty sound (in case of not strumming this cord during recording).
         * @param startTime time since last record.
         * @return new time since record.
         */
        private long recordEmptySound(long startTime) {
            long nowTime = System.currentTimeMillis();
            short[] emptySound;
            emptySound = new short[calcShortsPerTime((int) (nowTime - startTime))];
            new RecordCord(emptySound, 0, emptySound.length, DEFAULT_RATE, 0).run();
            return nowTime;
        }

        private int calcShortsPerTime(int timeInMillis) {
            return CordManager.calcShortsPerTime(index, timeInMillis, sample);
        }
    }

    private void setVolume(float volume) {
        audioTrack.setStereoVolume(volume, volume);
    }

    /**
     * This class represent a record task for recording this cord. will be played only if the recorder is on.
     */
    private class RecordCord implements Runnable {
        short[] audioSample;
        int start;
        int end;
        int playbackRate;
        float currVolume;

        public RecordCord(short[] audioSample, int start, int end, int playbackRate, float currVolume) {
            this.audioSample = audioSample;
            this.start = start;
            this.end = end;
            this.playbackRate = playbackRate;
            this.currVolume = currVolume;
        }

        @Override
        public void run() {
            // saves the sounds that has been played to the buffer.
            CordManager.writeToBuffer(index, Arrays.copyOfRange(audioSample, start, end), playbackRate, currVolume);
            // writes from the buffer to the file.
            CordManager.writeToFile(index);
        }
    }
}
