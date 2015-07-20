/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.demo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.utree.eightysix.voice.RecordFile;

import java.io.File;
import java.io.IOException;

/**
 */
public class RecordActivity extends Activity {

  private RecordFile mR;

  @InjectView(R.id.btn_record)
  public Button mBtnRecord;

  @InjectView(R.id.btn_finish)
  public Button mBtnFinish;

  @InjectView(R.id.btn_play)
  public Button mBtnPlay;

  @InjectView(R.id.btn_stop)
  public Button mBtnStop;

  @InjectView(R.id.tv_amplitude)
  public TextView mTvAmplitude;

  @InjectView(R.id.tv_duration)
  public TextView mTvDuration;

  @OnClick(R.id.btn_record)
  public void onBtnRecordClicked() {
    mR.record();
  }

  @OnClick(R.id.btn_finish)
  public void onBtnFinishClicked() {
    mR.finish();
  }

  @OnClick(R.id.btn_play)
  public void onBtnPlayClicked() {
    mR.play();
  }

  @OnClick(R.id.btn_stop)
  public void onBtnStopClicked() {
    mR.stop();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_record);
    ButterKnife.inject(this);

    try {
      mR = new RecordFile(File.createTempFile("record", String.valueOf(System.currentTimeMillis())));
      mR.setCallback(new RecordFile.Callback() {
        @Override
        public void onRecord() {
          mBtnRecord.setEnabled(false);
          mBtnFinish.setEnabled(true);
          mBtnPlay.setEnabled(false);
          mBtnStop.setEnabled(false);
        }

        @Override
        public void onFinish() {
          mBtnRecord.setEnabled(true);
          mBtnFinish.setEnabled(false);
          mBtnPlay.setEnabled(true);
          mBtnStop.setEnabled(false);
        }

        @Override
        public void onPlay() {
          mBtnRecord.setEnabled(false);
          mBtnFinish.setEnabled(false);
          mBtnPlay.setEnabled(false);
          mBtnStop.setEnabled(true);
        }

        @Override
        public void onStop() {
          mBtnRecord.setEnabled(true);
          mBtnFinish.setEnabled(false);
          mBtnPlay.setEnabled(true);
          mBtnStop.setEnabled(false);
        }

        @Override
        public void onAmplitudeUpdate(int amplitude) {
          mTvAmplitude.setText(String.valueOf(amplitude));
        }

        @Override
        public void onDurationUpdate(long duration) {
          mTvDuration.setText(String.valueOf(duration));
        }
      });
    } catch (IOException ignored) {
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mR.recycle();
  }
}