package com.codingPower.framework.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Environment;

public class FileUtil {

    public static synchronized String read(InputStream is) {
        try {
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            return new String(buffer);
        } catch (IOException e) {
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException e1) {
            }
        }
    }

    /**
     * 删除文件或目录
     * @param path
     */
    public static synchronized void delete(String path) {
        File f = new File(path);
        if (f.isDirectory()) {
            deleteDirectory(f);
        } else {
            f.delete();
        }
    }

    /**
     * 删除目录，必须确保是目录
     * @param directory
     */
    public static synchronized void deleteDirectory(File directory) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            } else if (file.isFile()) {
                file.delete();
            }
        }
        directory.delete();
    }

    public static File getDiskCacheDir(Context context, String uniqueName) {
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !isExternalStorageRemovable()
                        ? getExternalCacheDir(context).getPath() : context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * sd卡是否存在
     * @return
     */
    public static boolean isSDCardMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !isExternalStorageRemovable();
    }

    /**
     * Check if external storage is built-in or removable.
     *
     * @return True if external storage is removable (like an SD card), false
     *         otherwise.
     */
    @TargetApi(9)
    public static boolean isExternalStorageRemovable() {
        if (Utils.hasGingerbread()) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    @TargetApi(8)
    public static File getExternalCacheDir(Context context) {
        if (Utils.hasFroyo()) {
            return context.getExternalCacheDir();
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }
}
