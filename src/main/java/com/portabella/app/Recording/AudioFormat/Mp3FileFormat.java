package com.portabella.app.Recording.AudioFormat;

import com.portabella.app.GuitarActivity.PlayingGuitarBuffer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * This class is an MP3 Audio format for the recorder. for this version it is not implemented in the app.
 * Created by Tomer on 14/09/2016.
 */
public class Mp3FileFormat extends AudioFormat {
    /** AAAAAAAA AAABBCCD EEEEFFGH IIJJKLMM
     *
     * A Frame synchronizer
     * All bits are set to 1. It is used for finding the beginning of frame.
     * But these values can occur many times in binary file so you should test next values from header
     * for validity (eg. bitrate bits arent 1111, sampling rate frequency isnt 11 etc.). But you can
     * never be 100% sure if you find a header.
     * Next method is to find the first header and then go
     * through all frames - almost exact, but time consuming.
     * Be careful with the first frame! It doesn't have to start at the first Byte in file.
     * Either TAG v2 can be included or file can contains of some crap at the beginning.
     * Anyway - to find a header is a little problem.

     * B	  	MPEG version ID
     * 00	  	MPEG Version 2.5 (not an official standard)
     * 01	  	reserved
     * 10	  	MPEG Version 2
     * 11	  	MPEG Version 1
     *
     * In most MP3 files these value should be 11.

     * C	  	Layer
     * 00	  	reserved
     * 01	  	Layer III
     * 10	  	Layer II
     * 11	  	Layer I
     * In most MP3 files these value should be 01 (because MP3 = MPEG 1 Layer 3).
     *
     * D	  	CRC Protection
     * 0	  	Protected by CRC
     * 1	  	Not protected
     * Frames may have some form of check sum - CRC check. CRC is 16 bit long and, if exists,
     * it follows frame header. And then comes audio data.
     * But almost all MP3 files doesnt contain CRC.

     * E	  	Bitrate index
     * 0000	  	free
     * 0001	  	32
     * 0010	  	40
     * 0011	  	48
     * 0100	  	56
     * 0101	  	64
     * 0110	  	80
     * 0111	  	96
     * 1000	  	112
     * 1001	  	128
     * 1010	  	160
     * 1011	  	192
     * 1100	  	224
     * 1101	  	256
     * 1110	  	320
     * 1111	  	bad
     * All values are in kbps.

     * F	  	Samplig rate frequency index
     * 00	  	44100
     * 01	  	48000
     * 10	  	32000
     * 11	  	reserved
     * All values are in Hz. In most MP3 files these value should be 00.
     * Note: Sample frequency 44100 means that one second of audio information is hacked
     * to 44100 pieces. And each 1/44100 sec. is audio value taken and encoded into digital form.

     * G	  	Padding
     * 0	  	Frame is not padded
     * 1	  	Frame is padded
     * Padding is used to fit the bitrates exactly. If you use frames with
     * length 417 Bytes (for 128kbps) it doesnt give exact data flow 128kbps. So you can set Padding
     * and add one extra Byte at the end of some frames to create exact 128kbps.
     * Fuck it.

     * H	  	Private bit
     * It can be freely used for specific needs of an application, eg. it can execute some
     * application specific events.
     * No special meaning, forget it.

     * I	  	Channel
     * 00	  	Stereo	  	Similar to Dual mono, 2 channels, but bitrate can be different for each one
     *                    and is coded dynamically. Eg. if channel 1 is silent, the second one will
     *                    get higher bitrate.
     * 01	  	Joint Stereo	Mostly used in MP3. One channel is common (mid) and is used mainly
                            for common and lower tones. The second is (side) channel for encoding
                            differences between normal channels.
     * Note: Stereo effect is listenable properly only for higher tones because for lower ones is
     * length of sound wave so long that you are not able to distinguish phase move.
     * 10	  	Dual	  	Also known as Dual mono; 2 separate channels.
     * 11	  	Mono (single channel	  	Normal mono.

     * J	  	Mode extension (only if Joint Stereo is set)
     * Intensity Stereo	  	MS Stereo
     * 00		off		off
     * 01		on		off
     * 10		off		on
     * 11		on		on
     * Tells which mode for JointStereo is used.

     * K	  	Copyright
     * 0	  	Audio is not copyrighted
     * 1	  	Audio is copyrighted
     * No special use.

     * L	  	Original
     * 0	  	Copy of original media
     * 1	  	Original media
     * No special use.

     * M	  	Emphasis
     * 00	  	None
     * 01	  	50/15
     * 10	  	reserved
     * 11	  	CCIT J.17
     * Tells if there are emphasised frequencies above cca. 3.2 kHz.
     *
     * from: http://www.multiweb.cz/twoinches/mp3inside.htm.
    */

    public static final int A = 1;
    public static final int B = -1;
    public static final int C = -1;
    public static final int D = -1;
    public static final int E = -1;
    public static final int F = -1;
    public static final int G = -1;
    public static final int H = -1;
    public static final int I = -1;
    public static final int J = -1;
    public static final int K = -1;
    public static final int L = -1;
    public static final int M = -1;

    public static final byte FIRST_BYTE1 = -1;
    public static final byte SECOND_BYTE1 = -5;
    public static final byte THIRD_BYTE1 = -112;
    public static final byte FORTH_BYTE1 = 4;

    public static final byte FIRST_BYTE = 73;
    public static final byte SECOND_BYTE = 68;
    public static final byte THIRD_BYTE = 51;
    public static final byte FORTH_BYTE = 3;

    public static final byte[] DEFAULT_HEADER = {FIRST_BYTE, SECOND_BYTE, THIRD_BYTE, FORTH_BYTE};

    @Override
    public void writeFile(PlayingGuitarBuffer buffer) {
        File file = new File(".mp3");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, true);
            fos.write(DEFAULT_HEADER);
            for (PlayingGuitarBuffer.PlayingSegment seg : buffer.readFromBuffer()) {
                byte[] bytes = new byte[seg.getShortArray().length * 2];
                ByteBuffer byteBuf = ByteBuffer.allocate(2 * seg.getShortArray().length);
                int i = 0;
                while (seg.getShortArray().length > i) {
                    byteBuf.putShort(seg.getShortArray()[i]);
                    i++;
                }
                fos.write(byteBuf.array());
            }
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int calcShortsPerTime(int timeInMillis, short[] wavByteArray) {
        return 0;
    }

    @Override
    public void writeDataToFile(byte[] outputArray) {

    }

    @Override
    public void reWriteHeaders() {

    }
}
