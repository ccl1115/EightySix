/*
 * Copyright (c) 2013. All rights reserved by bb.simon.yu@gmail.com
 */

package com.utree.eightysix.widget;

/**
 */
interface ViewGroupDelegate extends ViewDelegate {
    void layout(boolean changed, int l, int t, int r, int b);
}
