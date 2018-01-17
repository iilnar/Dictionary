package com.antat.dictionary;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Ilnar on 28/11/2016.
 */

public abstract class Recorder extends DemoRecorder {
    private static final int RECORDER_BPP = 16;
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.wav";
    private static final int RECORDER_SAMPLERATE = 22050;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private static final String attachmentName = "wav";
    private static final String attachmentFileName = "wav.wav";
    private static final String CRLF = "\r\n";
    private static final String TWO_HYPHENS = "--";
    private static final String BOUNDARY =  "*****";

    private AudioRecord recorder = null;
    private int bufferSize = 0;
    private Thread recordingThread = null;
    private boolean running = false;
    private ByteArrayOutputStream baos;

    public Recorder() {
        bufferSize = AudioRecord.getMinBufferSize(8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferSize);
        int i = recorder.getState();
        if (i == 1)
            recorder.startRecording();
        baos = new ByteArrayOutputStream();
        running = true;

        recordingThread = new Thread(new Runnable() {

            @Override
            public void run() {
                writeAudio();
            }
        }, "AudioRecorder Thread");

        recordingThread.start();
    }

    private void writeAudio() {
        byte data[] = new byte[bufferSize];

        int read = 0;
        while(running){
            read = recorder.read(data, 0, bufferSize);
            if(AudioRecord.ERROR_INVALID_OPERATION != read){
                try {
                    baos.write(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recorder.stop();

        HttpsURLConnection con = null;


        String link = "https://speech.tatar/recognize";

        try {
            URL url = new URL(link);
            con = (HttpsURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Language", "tat");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);

            con.setDoOutput(true);
        } catch (IOException e) {
            if (con != null) {
                con.disconnect();
            }
            return;
        }

        DataOutputStream out = null;

        try {
            out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(TWO_HYPHENS + BOUNDARY + CRLF);
            out.writeBytes("Content-Disposition: form-data; name=\"" +
                    attachmentName + "\";filename=\"" +
                    attachmentFileName + "\"" + CRLF);
            out.writeBytes(CRLF);

            long totalAudioLen = baos.size();
            long totalDataLen = totalAudioLen + 36;
            long longSampleRate = RECORDER_SAMPLERATE;
            int channels = 1;
            long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels / 8;

            writeWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
            out.write(baos.toByteArray());
            out.writeBytes(CRLF);
            out.writeBytes(TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + CRLF);

        } catch (IOException e) {
            return;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        try {
            int responseCode = con.getResponseCode();
            System.out.println(responseCode);

            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String s;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                }
                in.close();
            }
        } catch (IOException e) {
        }

        Looper.prepare();
        Looper.getMainLooper();
        onDone(sb.toString());
    }


    private void writeWaveFileHeader(
            OutputStream out, long totalAudioLen,
            long totalDataLen, long longSampleRate, int channels,
            long byteRate) throws IOException {

        byte[] header = new byte[44];

        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (channels * RECORDER_BPP / 8); // block align
        header[33] = 0;
        header[34] = RECORDER_BPP; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }

    @Override
    public void stop() {
        running = false;
    }
}
