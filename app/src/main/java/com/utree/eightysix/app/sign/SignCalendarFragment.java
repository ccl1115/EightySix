/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.sign;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.response.SignCalendarResponse;
import com.utree.eightysix.response.SignCalendarResponse.SignDate;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.widget.ThemedDialog;

import java.util.Collections;

/**
 */
public class SignCalendarFragment extends BaseFragment {

  private SignCalendarResponse mSignDates;

  public static void start(FragmentManager manager, int factoryId) {
    SignCalendarFragment fragment = new SignCalendarFragment();
    Bundle args = new Bundle();
    args.putInt("factoryId", factoryId);
    fragment.setArguments(args);
    manager.beginTransaction()
        .add(android.R.id.content, fragment)
        .commit();
  }

  @InjectViews({
      R.id.tv_date_1,
      R.id.tv_date_2,
      R.id.tv_date_3,
      R.id.tv_date_4,
      R.id.tv_date_5,
      R.id.tv_date_6,
      R.id.tv_date_7,
      R.id.tv_date_8,
      R.id.tv_date_9,
      R.id.tv_date_10
  })
  public TextView[] mTvDates;

  @InjectView(R.id.tv_info)
  public TextView mTvInfo;

  @InjectView(R.id.fl_parent)
  public FrameLayout mFlParent;

  @InjectView(R.id.iv_left)
  public ImageView mIvLeft;

  @InjectView(R.id.iv_right)
  public ImageView mIvRight;

  @InjectView(R.id.ll_parent)
  public LinearLayout mLlParent;

  private int mFactoryId;

  private int mPage = 1;

  @OnClick(R.id.fl_parent)
  public void onFlParentClicked() {
    getFragmentManager().beginTransaction()
        .remove(this)
        .commit();
  }

  @OnClick(R.id.iv_left)
  public void onIvLeftClicked() {
    final int size = mSignDates.object.size();

    if (mPage == 1 && size > 10) {
      clearSignDates();

      int end = mSignDates.object.size() - 10;
      int index = 10 - end;
      for (SignDate date : mSignDates.object.subList(Math.max(0, end - 10), end)) {
        setSignDate(index, date);
        index++;
      }
      mPage = 2;

      if (size > 20) {
        mIvLeft.setImageResource(R.drawable.ic_arrow_left);
      } else {
        mIvLeft.setImageResource(R.drawable.ic_arrow_left_grey);
      }

      mIvRight.setImageResource(R.drawable.ic_arrow_right);
    } else if (mPage == 2 && size > 20) {
      clearSignDates();

      int end = mSignDates.object.size() - 20;
      int index = 10 - end;
      for (SignDate date : mSignDates.object.subList(Math.max(0, end - 10), end)) {
        setSignDate(index, date);
        index++;
      }

      mPage = 3;

      mIvLeft.setImageResource(R.drawable.ic_arrow_left_grey);
      mIvRight.setImageResource(R.drawable.ic_arrow_right);
    }
  }

  @OnClick(R.id.iv_right)
  public void onIvRightClicked() {

    if (mPage == 2) {
      clearSignDates();
      int index = 0;
      int end = mSignDates.object.size();
      for (SignDate date : mSignDates.object.subList(Math.max(0, end - 10), end)) {
        setSignDate(index, date);
        index ++;
      }

      mPage = 1;

      mIvLeft.setImageResource(R.drawable.ic_arrow_left);

      mIvRight.setImageResource(R.drawable.ic_arrow_right_grey);
    } else if (mPage == 3) {
      clearSignDates();
      int index = 0;
      int end = mSignDates.object.size() - 10;
      for (SignDate date : mSignDates.object.subList(Math.max(0, end - 10), end)) {
        setSignDate(index, date);
        index ++;
      }

      mPage = 2;

      mIvLeft.setImageResource(R.drawable.ic_arrow_left);
      mIvRight.setImageResource(R.drawable.ic_arrow_right);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_sign_calendar, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    ButterKnife.inject(this, view);

    mFactoryId = getArguments().getInt("factoryId");

    requestSignCalendar();

    mFlParent.setFocusableInTouchMode(true);
    mFlParent.setFocusable(true);

    mFlParent.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
          getFragmentManager().beginTransaction()
              .remove(SignCalendarFragment.this)
              .commit();
          return true;
        }

        return false;
      }
    });

    mFlParent.requestFocus();
  }

  private void requestSignCalendar() {
    U.request("userfactory_sign", new OnResponse2<SignCalendarResponse>() {
      @Override
      public void onResponseError(Throwable e) {
        getFragmentManager().beginTransaction()
            .remove(SignCalendarFragment.this)
            .commit();
      }

      @Override
      public void onResponse(final SignCalendarResponse response) {
        if (RESTRequester.responseOk(response) && response.object != null) {
          clearSignDates();
          mLlParent.setVisibility(View.VISIBLE);

          mTvInfo.setText(String.format("每天前10名打卡，获得3倍的蓝星，连续打卡经验翻倍\n你已连续打卡%d天，近一个月漏打卡%d天",
              response.extra.signConsecutiveTimes, response.extra.signMissingTimes));

          mSignDates = response;
          Collections.reverse(mSignDates.object);
          int index = 0;
          for (SignDate date : response.object.subList(Math.max(0, response.object.size() - 10), response.object.size())) {
            setSignDate(index, date);
            index++;
          }

          mPage = 1;

          if (mSignDates.object.size() > 10) {
            mIvLeft.setImageResource(R.drawable.ic_arrow_left);
          } else {
            mIvLeft.setImageResource(R.drawable.ic_arrow_left_grey);
          }
        } else {
          getFragmentManager().beginTransaction()
              .remove(SignCalendarFragment.this)
              .commit();
        }
      }
    }, SignCalendarResponse.class, mFactoryId);
  }

  private void setSignDate(int index, final SignDate date) {
    if (date.signed == 1) {
      mTvDates[index].setTextColor(0xffd4145a);
      mTvDates[index].setText(date.date.split("\\.", 2)[1] + "\n" + "已打卡");
      mTvDates[index].setOnClickListener(null);
    } else {
      mTvDates[index].setTextColor(Color.BLACK);
      mTvDates[index].setBackgroundColor(getResources().getColor(R.color.apptheme_primary_light_color_100));
      mTvDates[index].setText(date.date.split("\\.", 2)[1] + "\n" + "漏打卡");
      mTvDates[index].setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          showSignDialog(date.date, mSignDates.extra.costBluestar);
        }
      });
    }
  }

  private void showSignDialog(final String date, int costBluestar) {
    final ThemedDialog dialog = new ThemedDialog(getActivity());

    dialog.setTitle("确认为" + date + "补打卡");

    TextView view = new TextView(getActivity());
    view.setText(String.format("提醒：补打卡会消耗%d颗蓝星哦", costBluestar));

    int px = U.dp2px(16);
    view.setPadding(px, px, px, px);

    dialog.setContent(view);

    dialog.setPositive(R.string.okay, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        U.request("userfactory_sign_create", new OnResponse2<Response>() {
          @Override
          public void onResponseError(Throwable e) {

          }

          @Override
          public void onResponse(Response response) {
            if (RESTRequester.responseOk(response)) {
              requestSignCalendar();
            }
          }
        }, Response.class, mFactoryId, date);
        dialog.dismiss();
      }
    });

    dialog.setRbNegative(R.string.cancel, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();
      }
    });

    dialog.show();
  }

  private void clearSignDates() {
    for (TextView textView : mTvDates) {
      textView.setText("");
      textView.setBackgroundColor(0x00ffffff);
      textView.setOnClickListener(null);
    }
  }
}