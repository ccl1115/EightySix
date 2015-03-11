/*
 * Copyright (c) 2015. All rights reserved by utree.cn
 */

package com.utree.eightysix.app.account.event;

import java.util.Calendar;

/**
 */
public class BirthdayUpdatedEvent {

  private Calendar mCalendar;

  public BirthdayUpdatedEvent(Calendar calendar) {

    mCalendar = calendar;
  }

  public Calendar getCalendar() {
    return mCalendar;
  }
}
