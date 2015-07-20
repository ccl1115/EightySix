/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.voice;

import android.annotation.TargetApi;
import android.media.*;
import android.os.AsyncTask;
import android.os.Build;

import java.io.*;

/**
 */
public class RecordFile implements Recordable, Playable {

  private static final int STATE_IDLE = 0;
  private static final int STATE_RECORDING = 1;
  private static final int STATE_RECORDED = 2;
  private static final int STATE_PLAYING = 3;
  private static final int STATE_STOPPED = 4;

  private File mRecordFile;
  private Callback mCallback;
  private int mState;
  private MediaPlayer mPlayer;
  private AudioRecord mRecord;
  private int mMinBufferSize;
  private RecordWorker mRecordWriter;
  private long mStartTimestamp;

  @TargetApi(Build.VERSION_CODES.ECLAIR)
  public RecordFile(File file) {
    mRecordFile = file;
    mMinBufferSize = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
  }


  @TargetApi(Build.VERSION_CODES.ECLAIR)
  @Override
  public void record() {
    if (mState != STATE_IDLE && mState != STATE_RECORDED && mState != STATE_STOPPED) {
      return;
    }

    if (mRecord == null) {
      mRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
          16000,
          AudioFormat.CHANNEL_IN_MONO,
          AudioFormat.ENCODING_PCM_16BIT,
          mMinBufferSize);
      mRecord.setPositionNotificationPeriod(100);
      mRecord.setRecordPositionUpdateListener(new AudioRecord.OnRecordPositionUpdateListener() {
        @Override
        public void onMarkerReached(AudioRecord recorder) {

        }

        @Override
        public void onPeriodicNotification(AudioRecord recorder) {
          if (mCallback != null) {
            mCallback.onDurationUpdate(System.currentTimeMillis() - mStartTimestamp);
          }
        }
      });
    }

    mRecord.startRecording();
    mStartTimestamp = System.currentTimeMillis();
    mState = STATE_RECORDING;
    mRecordWriter = new RecordWorker();
    mRecordWriter.execute();
    if (mCallback != null) {
      mCallback.onRecord();
    }
  }

  @Override
  public void finish() {
    if (mState == STATE_RECORDING) {
      mRecord.stop();
      mState = STATE_RECORDED;
      mStartTimestamp = 0l;
      if (mRecordWriter != null) {
        mRecordWriter.cancel(false);
        mRecordWriter = null;
      }
      if (mCallback != null) {
        mCallback.onFinish();
      }
    }
  }

  @Override
  public void play() {
    if (mState != STATE_RECORDED && mState != STATE_STOPPED) {
      return;
    }

    if (mPlayer == null) {
      mPlayer = new MediaPlayer();
    }
    try {
      mPlayer.setDataSource(mRecordFile.getPath());
      mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
      mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
          if (mCallback != null) {
            mCallback.onStop();
          }
        }
      });
      mPlayer.prepare();
      mPlayer.start();
      mState = STATE_PLAYING;
      if (mCallback != null) {
        mCallback.onPlay();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  @Override
  public void stop() {
    if (mState == STATE_PLAYING) {
      if (mPlayer != null) {
        mPlayer.stop();
      }
      mState = STATE_STOPPED;
      if (mCallback != null) {
        mCallback.onStop();
      }
    }
  }

  public void recycle() {
    if (mPlayer != null) {
      mPlayer.reset();
      mPlayer.release();
      mPlayer = null;
    }

    if (mRecord != null) {
      mRecord.release();
      mRecord = null;
    }
  }

  public void setCallback(Callback callback) {
    mCallback = callback;
  }

  public interface Callback {
    void onRecord();

    void onFinish();

    void onPlay();

    void onStop();

    void onAmplitudeUpdate(int amplitude);

    void onDurationUpdate(long duration);
  }

  private class RecordWorker extends AsyncTask<Void, Integer, Void> {

    @Override
    protected Void doInBackground(Void... params) {
      byte[] buffer = new byte[mMinBufferSize];
      OutputStream stream = null;
      FileOutputStream out = null;
      try {
        int readSize;
        double amplitude;
        out = new FileOutputStream(mRecordFile);
        stream = new DataOutputStream(out);
        while (mState == STATE_RECORDING) {
          double sum = 0.0;
          readSize = mRecord.read(buffer, 0, buffer.length);
          for (int i = 0; i < readSize; i++) {
            stream.write(buffer[i]);
            sum += buffer[i] * buffer[i];
          }
          if (readSize > 0) {
            amplitude = Math.sqrt(sum / readSize);
            publishProgress((int) amplitude);
          }
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        return null;
      } catch (IOException e) {
        e.printStackTrace();
        return null;
      } finally {
        if (stream != null) {
          try {
            stream.flush();
          } catch (IOException e) {
            e.printStackTrace();
          } finally {
            try {
              stream.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
        if (out != null) {
          try {
            out.flush();
          } catch (IOException e) {
            e.printStackTrace();
          } finally {
            try {
              out.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      }
      return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
      if (mCallback != null) {
        mCallback.onAmplitudeUpdate(values[0]);
      }
    }
  }
}
