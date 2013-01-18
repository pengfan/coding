package com.codingPower.framework.util;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class ActivityUtil {

    public static Class getClass(String classname) {
        Class res = null;
        try {
            res = Class.forName(classname);
        } catch (ClassNotFoundException e) {

        }
        return res;
    }

    /**
     * 构造intent启动activity,key的数组长度必须和value的数组长度
     * 
     * @param key "键"的数组
     * @param value "值"的数组
     * @param className "类名"，无包前缀
     */
    public static void IntentToActivity(Context context, String className, String[] key, String[] value) {
        Class clazz = getClass(className);
        IntentToActivity(context, clazz, key, value);
    }

    /**
     * 构造intent启动activity,key的数组长度必须和value的数组长度
     * 
     * @param key "键"的数组
     * @param value "值"的数组
     * @param className "类名"，无包前缀
     */
    public static void IntentToActivity(Context context, Class clazz, String[] key, String[] value) {
        if (clazz == null)
            return;
        Intent intent = new Intent();
        for (int i = 0; i < key.length; i++) {
            intent.putExtra(key[i], value[i]);
        }
        intent.setClass(context, clazz);
        context.startActivity(intent);
    }

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

}
