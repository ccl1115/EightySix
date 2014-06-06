package com.utree.eightysix.push;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.utree.eightysix.U;

/**
 */
public class PushHelperImpl implements PushHelper {

    public void startWork() {
        PushManager.startWork(U.getContext(), PushConstants.LOGIN_TYPE_API_KEY, U.getConfig("bd.api_key"));
    }

    public void stopWork() {
        PushManager.stopWork(U.getContext());
    }
}
