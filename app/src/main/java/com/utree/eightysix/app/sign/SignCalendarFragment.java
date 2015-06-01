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
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseFragment;
import com.utree.eightysix.response.SignCalendarResponse;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;
import com.utree.eightysix.widget.ThemedDialog;

import java.util.List;

/**
 */
public class SignCalendarFragment extends BaseFragment {

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
    if (mPage < 3) {
      mPage += 1;
    }

    if (mPage == 3) {
      mIvLeft.setImageResource(R.drawable.ic_arrow_left_grey);
      mIvRight.setImageResource(R.drawable.ic_arrow_right);
    } else {
      mIvLeft.setImageResource(R.drawable.ic_arrow_left);
      mIvRight.setImageResource(R.drawable.ic_arrow_right);
    }

    requestSignCalendar();
  }

  @OnClick(R.id.iv_right)
  public void onIvRightClicked() {
    if (mPage > 1) {
      mPage -= 1;
    }

    if (mPage == 1) {
      mIvLeft.setImageResource(R.drawable.ic_arrow_left);
      mIvRight.setImageResource(R.drawable.ic_arrow_right_grey);
    } else {
      mIvLeft.setImageResource(R.drawable.ic_arrow_left);
      mIvRight.setImageResource(R.drawable.ic_arrow_right);
    }

    requestSignCalendar();
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

      }

      @Override
      public void onResponse(final SignCalendarResponse response) {

        mTvInfo.setText(String.format("你已连续打卡%d天，近一个月漏打卡%d天",
            response.extra.signConsecutiveTimes, response.extra.signMissingTimes));

        List<SignCalendarResponse.SignDate> object = response.object;
        int index = 0;
        for (int i = object.size() - 1; i >= 0; i--) {
          final SignCalendarResponse.SignDate date = object.get(i);
          mTvDates[index].setText(date.date.split("\\.", 2)[1] + "\n" + (date.signed == 1 ? "已打卡" : "未打卡"));
          if (date.signed == 1) {
            mTvDates[index].setBackgroundColor(Color.WHITE);
            mTvDates[index].setOnClickListener(null);
          } else {
            mTvDates[index].setBackgroundColor(getResources().getColor(R.color.apptheme_primary_light_color_100));
            if (!(mPage == 1 && i == 0)) {
              mTvDates[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  showSignDialog(date.date, response.extra.costBluestar);
                }
              });
            }
          }
          index++;
        }
      }
    }, SignCalendarResponse.class, mFactoryId, mPage);
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
}