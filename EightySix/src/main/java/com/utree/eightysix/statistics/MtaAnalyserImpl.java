package com.utree.eightysix.statistics;

import android.content.Context;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatService;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.C;
import com.utree.eightysix.U;
import com.utree.eightysix.app.BaseApplication;
import java.util.Properties;

/**
 * Analyser implementation by Tencent mta
 *
 * <a href="http://mta.qq.com">mta homepage</a>
 */
public class MtaAnalyserImpl implements Analyser {

    public MtaAnalyserImpl() {
        //StatConfig.setDebugEnable(BuildConfig.DEBUG);
        StatConfig.setInstallChannel(U.getConfig("app.channel"));
    }

    @Override
    public void onResume(Context context) {
        StatService.onResume(context);
    }

    @Override
    public void onPause(Context context) {
        StatService.onPause(context);
    }

    @Override
    public void reportError(Context context, String string) {
        StatService.reportError(context, string);
    }

    @Override
    public void reportException(Context context, Throwable t) {
        StatService.reportException(context, t);
    }

    @Override
    public <ID, VALUES> void trackEvent(Context context, ID id, VALUES... values) {
        if (id instanceof String && values instanceof String[]) {
            StatService.trackCustomEvent(context, (String) id, (String[]) values);
        }
    }

    @Override
    public <ID, VALUES> void trackBeginEvent(Context context, ID id, VALUES... values) {
        if (id instanceof String && values instanceof String[]) {
            StatService.trackCustomBeginEvent(context, (String) id, (String[]) values);
        }
    }

    @Override
    public <ID, VALUES> void trackEndEvent(Context context, ID id, VALUES... values) {
        if (id instanceof String && values instanceof String[]) {
            StatService.trackCustomEndEvent(context, (String) id, (String[]) values);
        }
    }

    @Override
    public <ID, KV> void trackKVEvent(Context context, ID id, KV properties) {
        if (id instanceof String && properties instanceof Properties) {
            StatService.trackCustomKVEvent(context, (String) id, (Properties) properties);
        }
    }

    @Override
    public <ID, KV> void trackBeginKVEvent(Context context, ID id, KV properties) {
        if (id instanceof String && properties instanceof Properties) {
            StatService.trackCustomBeginKVEvent(context, (String) id, (Properties) properties);
        }
    }

    @Override
    public <ID, KV> void trackEndKVEvent(Context context, ID id, KV properties) {
        if (id instanceof String && properties instanceof Properties) {
            StatService.trackCustomEndKVEvent(context, (String) id, (Properties) properties);
        }
    }
}
