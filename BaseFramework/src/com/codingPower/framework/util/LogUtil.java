package com.codingPower.framework.util;

import android.util.Log;

public class LogUtil {
    public static boolean LOG = true;

    private boolean instanceLog = false;
    private String tag = "TAG";

    public static void i(Class clazz, String msg) {
        if (LOG) {
            Log.i(clazz.getSimpleName(), msg);
        }
    }

    public static void w(Class clazz, String msg) {
        if (LOG) {
            Log.w(clazz.getSimpleName(), msg);
        }
    }

    public static void d(Class clazz, String msg) {
        if (LOG) {
            Log.w(clazz.getSimpleName(), msg);
        }
    }

    public static void e(Class clazz, String msg) {
        if (LOG) {
            Log.e(clazz.getSimpleName(), msg);
        }
    }

    private LogUtil(boolean log, String tag) {
        instanceLog = log;
        this.tag = tag;
    }

    public static LogUtil getInstance(boolean log, String tag) {
        return new LogUtil(log, tag);
    }

    public void i(String msg) {
        if (instanceLog) {
            Log.i(tag, msg);
        }
    }

    public void w(String msg) {
        if (instanceLog) {
            Log.w(tag, msg);
        }
    }

    public void d(String msg) {
        if (instanceLog) {
            Log.w(tag, msg);
        }
    }

    public void e(String msg) {
        if (instanceLog) {
            Log.e(tag, msg);
        }
    }
}
