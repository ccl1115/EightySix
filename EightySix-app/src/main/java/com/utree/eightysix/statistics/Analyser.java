package com.utree.eightysix.statistics;

import android.content.Context;

/**
 * Interface for analysing application usage
 */
public interface Analyser {

    void onResume(Context context);

    void onPause(Context context);

    void reportError(Context context, String string);

    void reportException(Context context, Throwable t);

    <ID, VALUE> void trackEvent(Context context, ID id, VALUE... values);

    <ID, VALUE> void trackBeginEvent(Context context, ID id, VALUE... values);

    <ID, VALUE> void trackEndEvent(Context context, ID id, VALUE... values);

    <ID, KV> void trackKVEvent(Context context, ID id, KV properties);

    <ID, KV> void trackBeginKVEvent(Context context, ID id, KV properties);

    <ID, KV> void trackEndKVEvent(Context context, ID id, KV properties);

  <STAT> void reportHttpRequest(Context context, STAT stat);
}
