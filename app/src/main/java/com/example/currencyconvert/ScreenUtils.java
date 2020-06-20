package com.example.currencyconvert;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class ScreenUtils {


    public static int getScreenWidth (Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return  metrics.widthPixels;
    }

    public static int dpToPx(Context context, Integer value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Float.valueOf(value), context.getResources().getDisplayMetrics());
    }
}
