package com.apcc.utils;

import android.util.Log;

import com.apcc.framework.AppManager;

import java.util.Locale;

public class Logger {

    private static final String TAG = "NGB:";

    private static String getMsg(String appendix) {
        return String.format("%50s %s", getMethod(), appendix);
    }

    public static void d(String appendix) {
        if (AppManager.instance.makeLove())
            Log.d(TAG, getMsg(appendix));
    }

    public static void i(String appendix) {
        if (AppManager.instance.makeLove())
            Log.d(TAG, getMsg(appendix));
    }

    public static void w(String appendix) {
        if (AppManager.instance.makeLove())
            Log.w(TAG, getMsg(appendix));
    }

    public static void e(String appendix) {
        if (AppManager.instance.makeLove())
            Log.e(TAG, getMsg(appendix));
    }

    public static void e(Exception e) {
        if (AppManager.instance.makeLove()) {
            Log.e(TAG, getMsg(e.getMessage()));
            e.printStackTrace();
        }
    }

    private static String getMethod() {
        final StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[5];
        return String.format(Locale.getDefault(), "%s(%s:%d)",
                stackTraceElement.getMethodName(),
                stackTraceElement.getFileName(),
                stackTraceElement.getLineNumber());
    }
}
