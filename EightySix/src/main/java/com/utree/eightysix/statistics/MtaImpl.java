package com.utree.eightysix.statistics;

import com.tencent.stat.StatService;
import com.utree.eightysix.app.BaseApplication;
import java.util.Properties;

/**
 */
public class MtaImpl implements Statistics {
    @Override
    public void onResume() {
        StatService.onResume(BaseApplication.getContext());
    }

    @Override
    public void onPause() {
        StatService.onPause(BaseApplication.getContext());
    }

    @Override
    public void reportError(String string) {
        StatService.reportError(BaseApplication.getContext(), string);
    }

    @Override
    public void reportException(Throwable t) {
        StatService.reportException(BaseApplication.getContext(), t);
    }

    @Override
    public <ID, VALUES> void trackEvent(ID id, VALUES... values) {
        if (id instanceof String && values instanceof String[]) {
            StatService.trackCustomEvent(BaseApplication.getContext(), (String) id, (String[]) values);
        }
    }

    @Override
    public <ID, VALUES> void trackBeginEvent(ID id, VALUES... values) {
        if (id instanceof String && values instanceof String[]) {
            StatService.trackCustomBeginEvent(BaseApplication.getContext(), (String) id, (String[]) values);
        }
    }

    @Override
    public <ID, VALUES> void trackEndEvent(ID id, VALUES... values) {
        if (id instanceof String && values instanceof String[]) {
            StatService.trackCustomEndEvent(BaseApplication.getContext(), (String) id, (String[]) values);
        }
    }

    @Override
    public <ID, KV> void trackKVEvent(ID id, KV properties) {
        if (id instanceof String && properties instanceof Properties) {
            StatService.trackCustomKVEvent(BaseApplication.getContext(), (String) id, (Properties) properties);
        }
    }

    @Override
    public <ID, KV> void trackBeginKVEvent(ID id, KV properties) {
        if (id instanceof String && properties instanceof Properties) {
            StatService.trackCustomBeginKVEvent(BaseApplication.getContext(), (String) id, (Properties) properties);
        }
    }

    @Override
    public <ID, KV> void trackEndKVEvent(ID id, KV properties) {
        if (id instanceof String && properties instanceof Properties) {
            StatService.trackCustomEndKVEvent(BaseApplication.getContext(), (String) id, (Properties) properties);
        }
    }
}
