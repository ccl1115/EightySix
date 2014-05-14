package com.utree.eightysix.statistics;

/**
 */
public interface Statistics {

    void onResume();

    void onPause();

    void reportError(String string);

    void reportException(Throwable t);

    <ID, VALUE> void trackEvent(ID id, VALUE... values);

    <ID, VALUE> void trackBeginEvent(ID id, VALUE... values);

    <ID, VALUE> void trackEndEvent(ID id, VALUE... values);

    <ID, KV> void trackKVEvent(ID id, KV properties);

    <ID, KV> void trackBeginKVEvent(ID id, KV properties);

    <ID, KV> void trackEndKVEvent(ID id, KV properties);
}
