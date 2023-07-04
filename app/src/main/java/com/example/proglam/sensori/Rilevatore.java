package com.example.proglam.sensori;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

public class Rilevatore {
    private MediaRecorder mRecorder = null;
    private String file;
    private static final String TAG = "DbRilevatore";

    public Rilevatore(String file) {
        this.file = file;
        Log.d(TAG, "Rilevatore: " + file);
    }

    public void start() throws IOException {
        if (mRecorder != null) {
            // Il recorder è già avviato, quindi non fare nulla
            return;
        }

        // Inizializza il MediaRecorder con le impostazioni desiderate
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(file);
        mRecorder.prepare();

        // Avvia la registrazione audio
        mRecorder.start();

        Log.d(TAG, "start: successfully started!");
    }

    public void stop() {
        if (mRecorder != null) {
            try {
                // Interrompi la registrazione audio
                mRecorder.stop();
            } catch (RuntimeException e) {
                // Gestisci l'eccezione in modo appropriato
                Log.e(TAG, "stop: Failed to stop the MediaRecorder", e);
            }

            // Rilascia le risorse del MediaRecorder
            mRecorder.release();
            mRecorder = null;
        }
    }

    public double getAmplitude() {
        if (mRecorder != null) {
            try {
                // Ottieni l'ampiezza massima registrata dal MediaRecorder
                return 20 * Math.log10(mRecorder.getMaxAmplitude());
            } catch (RuntimeException e) {
                // Gestisci l'eccezione in modo appropriato
                Log.e(TAG, "getAmplitude: Failed to get the maximum amplitude", e);
            }
        }
        return 0;
    }
}

