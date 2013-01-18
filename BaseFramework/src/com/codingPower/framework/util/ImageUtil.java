package com.codingPower.framework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ImageUtil {
    /**
     * 根据指定颜色 生成边缘高亮的背景图
     * @param color 传入的颜色值(最深点颜色)
     * @param width 生成Drawable高度
     * @param height 生成Drawable宽度
     * @return
     */
    public static Drawable genBackColor(int color, int width, int height) {
        int ABS = 60;//渐变范围值
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);//
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        int a = Color.alpha(color), r = Color.red(color), g = Color.green(color), b = Color.blue(color);
        int color1 = Color.argb(a, r, g, b), color2 = Color.argb(a - ABS, r, g, b);
        LinearGradient lg = new LinearGradient(width >> 1, 0, width >> 1, height, color1, color2, Shader.TileMode.MIRROR);
        paint.setShader(lg);
        int alpha = ABS >> 2;
        int color3 = Color.argb(alpha, r, g, b);
        canvas.drawRect(0, 0, width, height, paint);
        //绘制高亮边缘
        LinearGradient lg2 =
                new LinearGradient(width >> 1, -(height >> 3), width >> 1, height >> 2, Color.WHITE, color3, Shader.TileMode.MIRROR);
        paint.setShader(lg2);
        canvas.drawRect(0, 0, width, height >> 2, paint);
        // ColorMatrix colorMatrix=new ColorMatrix();
        return new BitmapDrawable(output);//返回处理过的Drawable
    }

    /**
     * 从磁盘读取图片数据
     * @param path
     * @return
     */
    public static Bitmap getBitmapFromDisk(String path) {
        File file = new File(path);
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                return bitmap;
            } catch (FileNotFoundException e) {
                return null;
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 从磁盘读取图片数据
     * @param path
     * @return
     */
    public static Bitmap getBitmapFromDisk(String path, int width, int height) {
        File file = new File(path);
        if (file.exists()) {
            Bitmap bitmap = decodeSampledBitmapFromResource(file, width, height);
            return bitmap;
        }
        return null;
    }

    public static Bitmap decodeSampledBitmapFromResource(File file, int reqWidth, int reqHeight) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(fis, null, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            fis = new FileInputStream(file);
            return BitmapFactory.decodeStream(fis, new Rect(-1, -1, -1, -1), options);
        } catch (FileNotFoundException e) {
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if ((height > reqHeight && reqHeight > 0) || (width > reqWidth && reqWidth > 0)) {

            //等比缩小
            if (reqWidth <= 0) {
                reqWidth = Math.round((float) width * reqHeight / (float) height);
            } else if (reqHeight <= 0) {
                reqHeight = Math.round((float) height * reqWidth / (float) width);
            }

            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger
            // inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down
            // further.
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }
}
