package com.qm.qmclass.base;

import android.content.Context;

import com.qm.qmclass.tencent.TICManager;
import com.qm.qmclass.tencent.utils.SdkUtil;
import com.qm.qmclass.utils.SharedPreferencesUtils;

public class QMSDK {
    private static Context sContext;
    private static TICManager mticManager;
    private QMSDK() {
    }


    public static Context getContext() {
        return sContext;
    }

    public static void setContext(Context sContext) {
        QMSDK.sContext = sContext;
    }

    public static TICManager getTICManager() {
        return mticManager;
    }

    public static void setTicManager(TICManager ticManager) {
        mticManager = ticManager;
    }
}
