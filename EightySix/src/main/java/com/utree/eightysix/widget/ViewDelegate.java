package com.utree.eightysix.widget;

import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * @author Simon
 */
public interface ViewDelegate {
    void measure(int widthMeasureSpec, int heightMeasureSpec);

    void draw(Canvas canvas);

    boolean dispatchTouchEvent(MotionEvent event);

    boolean interceptionTouchEvent(MotionEvent event);

    boolean touchEvent(MotionEvent event);

    void animate(int msg);

    boolean isAnimating();
}
