package com.portabella.app.Recording.AudioFormat;

import com.portabella.app.GuitarActivity.PlayingGuitarBuffer;

import java.io.File;

/**
 * This class represent an audio Format that is used for the recording.
 * Created by Tomer on 15/09/2016.
 */
public abstract class AudioFormat {

    protected File file;

    public abstract void writeFile(PlayingGuitarBuffer buffer);

    /**
     * Calculate how much short there should be in the file for the given time.
     * used mostly to compute silent sound.
     */
    public abstract int calcShortsPerTime(int timeInMillis, short[] wavByteArray);

    public void deleteTempFile() {
        file.delete();
    }

    public File getFile() {
        return file;
    }

    /**
     * writes the given data to the file.
     */
    public abstract void writeDataToFile(byte[] outputArray);

    /**
     * Used after the file is written in the disk.
     */
    public abstract void reWriteHeaders();
}
