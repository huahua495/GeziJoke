package com.example.libcommon;

import android.util.DisplayMetrics;

public class PixUtils {

    public static int dp2px(int dpValue) {
        DisplayMetrics displayMetrics = AppGlobals.getsApplication().getResources().getDisplayMetrics();
        return (int) (displayMetrics.density * dpValue + 0.5f);
    }


    public static int getScreenWidth() {
        DisplayMetrics displayMetrics = AppGlobals.getsApplication().getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight() {
        DisplayMetrics displayMetrics = AppGlobals.getsApplication().getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }
}
