package com.utree.eightysix.utils;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

/**
* @author simon
*/
public class IdQueue {
  private Queue<Integer> mQueue = new LinkedList<Integer>();
  private int mBase;
  private int mSize;
  private Integer mLast;

  public IdQueue(int base, int size) {
    mBase = base;
    mSize = size;
  }

  public int get() {
    if (mQueue.size() < mSize) {
      if (mQueue.isEmpty()) {
        mQueue.offer(mBase);
        mLast = mBase;
      } else {
        mLast = mLast + 1;
        mQueue.offer(mLast);
      }
    } else {
      mLast = mQueue.poll();
      mQueue.offer(mLast);
    }

    return mLast;
  }
}
