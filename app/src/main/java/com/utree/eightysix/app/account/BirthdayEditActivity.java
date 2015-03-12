/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import butterknife.InjectView;
import com.utree.eightysix.Account;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseActivity;
import com.utree.eightysix.app.Layout;
import com.utree.eightysix.app.TopTitle;
import com.utree.eightysix.app.account.event.BirthdayUpdatedEvent;
import com.utree.eightysix.rest.OnResponse2;
import com.utree.eightysix.rest.RESTRequester;
import com.utree.eightysix.rest.Response;

import java.util.Calendar;

/**
 */
@Layout(R.layout.activity_birthday_edit)
@TopTitle(R.string.birthday_edit)
public class BirthdayEditActivity extends BaseActivity {

  public final Calendar mNowCalendar = Calendar.getInstance();
  private int mAge;
  private String mConstellation;

  public static void start(Context context, Calendar calendar) {
    Intent intent = new Intent(context, BirthdayEditActivity.class);
    intent.putExtra("calendar", calendar);

    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    context.startActivity(intent);
  }

  @InjectView(R.id.dp_birthday)
  public DatePicker mDpBirthday;

  @InjectView(R.id.tv_age)
  public TextView mTvAge;

  @InjectView(R.id.tv_constellation)
  public TextView mTvConstellation;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final Calendar calendar = (Calendar) getIntent().getSerializableExtra("calendar");


    mDpBirthday.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
        new DatePicker.OnDateChangedListener() {
          @Override
          public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            mAge = Utils.computeAge(mNowCalendar, calendar);
            mConstellation = Utils.Constellation.get(calendar);
            mTvAge.setText(String.valueOf(mAge) + "岁");
            mTvConstellation.setText(mConstellation);
          }
        });

    mDpBirthday.setMaxDate(Calendar.getInstance().getTimeInMillis());

    getTopBar().getAbLeft().setDrawable(getDrawable(R.drawable.top_bar_return));
    getTopBar().getAbRight().setText(getString(R.string.submit));
    getTopBar().getAbRight().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Utils.updateProfile(null, null, null, calendar.getTimeInMillis(), mConstellation, null, null, mAge,
            new OnResponse2<Response>() {
              @Override
              public void onResponseError(Throwable e) {

              }

              @Override
              public void onResponse(Response response) {
                if (RESTRequester.responseOk(response)) {
                  U.getBus().post(new BirthdayUpdatedEvent(calendar));
                  finish();
                }
              }
            });
      }
    });
  }

  @Override
  public void onActionLeftClicked() {
    finish();
  }

  @Override
  public void onLogout(Account.LogoutEvent event) {
    finish();
  }

}