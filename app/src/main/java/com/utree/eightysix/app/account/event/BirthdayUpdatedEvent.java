/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account.event;

import java.util.Calendar;

/**
 */
public class BirthdayUpdatedEvent {

  private Calendar mCalendar;
  private String mConstellation;

  public BirthdayUpdatedEvent(Calendar calendar, String constellation) {

    mCalendar = calendar;
    mConstellation = constellation;
  }

  public Calendar getCalendar() {
    return mCalendar;
  }

  public String getConstellation() {
    return mConstellation;
  }
}
