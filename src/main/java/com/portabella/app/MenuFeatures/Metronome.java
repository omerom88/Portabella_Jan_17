package com.portabella.app.MenuFeatures;

/**
 * This class represent a metronome Object that is used by the MetronomeActivity.
 * Created by omerrom on 24/09/16.
 */
public class Metronome {

    private double bpm;
    private int beat;
    //private int noteValue;
    private int silence;

    private double beatSound;
    private double sound;
    private final int tick = 1000; // samples of tick

    private boolean play = true;

    private AudioGenerator audioGenerator = new AudioGenerator(8000);

    public void setVars(double bpm, int beats) {
        this.bpm = bpm;
        this.beat = beats;
        this.beatSound = 523.25; // do
        this.sound = 698.46; // fa
    }

    public void calcSilence() {
        silence = (int) (((60/this.bpm)*8000)-tick);
    }

    public void play() {
        play = true;
        audioGenerator.createPlayer();
        calcSilence();
        double[] tick = audioGenerator.getSineWave(this.tick, 8000, this.beatSound);
        double[] tock = audioGenerator.getSineWave(this.tick, 8000, this.sound);
        double silence = 0;
        double[] sound = new double[8000];
        int t = 0, s = 0, b = 0;
        do {
            for(int i = 0; i < sound.length && play; i++) {
                if(t < this.tick) {
                    if(b == 0)
                        sound[i] = tock[t];
                    else
                        sound[i] = tick[t];
                    t++;
                } else {
                    sound[i] = silence;
                    s++;
                    if(s >= this.silence) {
                        t = 0;
                        s = 0;
                        b++;
                        if(b > (this.beat-1))
                            b = 0;
                    }
                }
            }
            audioGenerator.writeSound(sound);
        } while(play);
    }

    public void stop() {
        play = false;
        audioGenerator.destroyAudioTrack();
    }
}