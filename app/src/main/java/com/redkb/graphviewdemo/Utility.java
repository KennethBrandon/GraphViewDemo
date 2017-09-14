package com.redkb.graphviewdemo;

import android.content.res.Resources;
import android.util.TypedValue;

final class Utility {

    static float dpToPx(float value, Resources resources) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.getDisplayMetrics());
    }

    static float findMax(float[] data) {
        float max = Float.MIN_VALUE;
        for (float aData : data) {
            if (aData > max) {
                max = aData;
            }
        }
        return max;
    }

    static float findMin(float[] data) {
        float min = Float.MAX_VALUE;
        for (float aData : data) {
            if (aData < min) {
                min = aData;
            }
        }
        return min;
    }
}