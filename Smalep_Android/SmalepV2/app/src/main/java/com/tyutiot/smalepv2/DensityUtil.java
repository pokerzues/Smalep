package com.tyutiot.smalepv2;

import android.content.Context;

/**
 * This is a backup class without using in the application.
 */
public class DensityUtil {

    /**
     * Dp to Px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * Px to Dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
