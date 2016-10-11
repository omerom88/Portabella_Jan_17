package com.portabella.app.Recording.AudioFormat;

import com.portabella.app.GuitarActivity.Cord;
import com.portabella.app.GuitarActivity.PlayingGuitarBuffer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

/**
 * This class is an WMV Audio format for the recorder.
 * Created by Tomer on 15/09/2016.
 */
public class WavFileFormat extends AudioFormat {

    private RandomAccessFile outFile;

    private long chunkSize;

    private static final int HEADER_SIZE = 44;
    private static final int INT_SIZE_IN_BYTES = 4;
    private static final int SHORT_SIZE_IN_BYTES = 2;
    private static final int BIT_SIZE = 8;
    private static final int DEFAULT_SAMPLE_RATE = 44100;
    private static final int DEFAULT_CHANNELS = 1;
    private static final String RIFF = "RIFF";
    private static final String WAVE = "WAVE";
    private static final String FMT = "fmt ";
    private static final String DATA = "data";
    private static final String WAV = ".wav";
    private static final byte[] BLOCK_ALIGN = shortToByteArray((short)2);
    private static final byte[] BIT_PER_SAMPLE = shortToByteArray((short)16);

    // write out the wav file
    public WavFileFormat(String fileName, String path, short[] sample) {
        try {
            file = createNewFile(path + "/" + fileName + WAV);
            outFile = new RandomAccessFile(file, "rw");
            outFile.write(new byte[HEADER_SIZE]);
            writeHeaders(sample);
            outFile.close();
            chunkSize = HEADER_SIZE;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    private File createNewFile(String fileName) {
        File outputFile = null;
        try {
            outputFile = new File(fileName);
            if (outputFile.exists() && !outputFile.isDirectory()) {
                outputFile.delete();
            }
            outputFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputFile;
    }

    // ===========================
    // CONVERT BYTES TO JAVA TYPES
    // ===========================
    public static byte[] intToByteArray(int num) {
        return new byte[] {
                (byte) (num & 0xFF),
                (byte) ((num >> 8) & 0xFF),
                (byte) ((num >> 16) & 0xFF),
                (byte) ((num >> 24) & 0xFF)
        };
}
    // convert a short to a byte array
    public static byte[] shortToByteArray(short num) {
        return new byte[] {
                (byte)(num & 0xff),
                (byte)((num >> 8) & 0xff)
        };
    }

    private void writeHeaders(short[] sample) {
        // write the wav file per the wav file format
        try {
//            Log.e("reWriteHeaders", "in reWriteHeaders");
            outFile = new RandomAccessFile(file, "rw");
            outFile.writeBytes(RIFF);                 // 00 - RIFF
            outFile.seek(8);
            outFile.writeBytes(WAVE);                 // 08 - WAVE
            outFile.writeBytes(FMT);                 // 12 - fmt
            outFile.write(intToByteArray(16), 0, INT_SIZE_IN_BYTES); // 16 - size of this chunk
            outFile.write(shortToByteArray(sample[10]), 0, SHORT_SIZE_IN_BYTES);        // 20 - what is the audio format? 1 for PCM = Pulse Code Modulation
            outFile.write(shortToByteArray(sample[11]), 0, SHORT_SIZE_IN_BYTES);  // 22 - mono or stereo? 1 or 2?  (or 5 or ???)
            outFile.write(intToByteArray(DEFAULT_SAMPLE_RATE), 0, INT_SIZE_IN_BYTES);        // 24 - samples per second (numbers per second)
            outFile.write(intToByteArray((DEFAULT_SAMPLE_RATE * BIT_SIZE * 2) / 8), 0, INT_SIZE_IN_BYTES);      // 28 - bytes per second
            outFile.write(shortToByteArray(sample[16]), 0, SHORT_SIZE_IN_BYTES);    // 32 - # of bytes in one sample, for all channels
            outFile.write(shortToByteArray(sample[17]), 0, SHORT_SIZE_IN_BYTES); // 34 - how many bits in a sample(number)?  usually 16 or 24
            outFile.writeBytes(DATA);                 // 36 - data
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void reWriteHeaders() {
        // write the wav file per the wav file format
        try {
            outFile = new RandomAccessFile(file, "rw");
            outFile.seek(4);
            outFile.write(intToByteArray((int) chunkSize - RIFF.length() - INT_SIZE_IN_BYTES), 0, INT_SIZE_IN_BYTES);     // 04 - how big is the rest of this file?
            outFile.seek(40);
            outFile.write(intToByteArray((int) chunkSize - HEADER_SIZE), 0, INT_SIZE_IN_BYTES);      // 40 - how big is this data chunk
            outFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int calcShortsPerTime(int timeInMillis, short[] wavByteArray) {
        short channels = wavByteArray[11];
        short bitsPerSample = wavByteArray[17];
//        Log.e("calcShortsPerTime: ", "" + ((channels * bitsPerSample * (Cord.DEFAULT_RATE / (BIT_SIZE * SHORT_SIZE_IN_BYTES)) * timeInMillis)  / 1000));
        return (channels * bitsPerSample * (Cord.DEFAULT_RATE / (BIT_SIZE * SHORT_SIZE_IN_BYTES)) * timeInMillis)  / 1000;
    }

    @Override
    public void writeFile(PlayingGuitarBuffer buffer) {
        List<PlayingGuitarBuffer.PlayingSegment> segList = buffer.readFromBuffer();
        for (PlayingGuitarBuffer.PlayingSegment seg : segList) {
            byte[] bytes = shortArrayToBytesArray(seg.getShortArray(), seg.getVolume());
            writeDataToFile(bytes);
        }
        reWriteHeaders();
    }

    public void writeDataToFile(byte[] bytes) {
        try {
            outFile = new RandomAccessFile(file, "rw");
            outFile.seek(file.length());
            outFile.write(bytes);
            chunkSize += bytes.length;
            outFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static byte[] shortArrayToBytesArray(short[] array, float volume) {
        byte[] bytes = new byte[array.length * 2];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(array);
        for (int i = 0; i < bytes.length; i += 2) {
            short audioSample = (short) (((bytes[i + 1] & 0xff) << 8) | (bytes[i] & 0xff));
            audioSample = (short) (audioSample * volume);
            bytes[i] = (byte) audioSample;
            bytes[i+1] = (byte) (audioSample >> 8);
        }
        return bytes;
    }
}
