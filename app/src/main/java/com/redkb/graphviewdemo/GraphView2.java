package com.redkb.graphviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class GraphView2 extends View {

    private static final int STROKE_WIDTH_DP = 4;
    private final Paint mPaintLine = new Paint();
    private final Path mPath = new Path();

    private float[] mData;

    public GraphView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaintLine.setColor(Color.argb(255, 33, 189, 222));
        int strokeInPx = (int) dpToPx(STROKE_WIDTH_DP);
        mPaintLine.setStrokeWidth(strokeInPx);
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setAntiAlias(true);
        mPaintLine.setStrokeJoin(Paint.Join.ROUND);
    }

    public void setData(float[] data) {
        mData = data;
        mPath.reset();
        float max = findMax(data);
        float min = findMin(data);
        mPath.rMoveTo(getXFromIndex(0, data.length), getYFromValue(data[0], max, min));
        for (int i = 1; i < data.length; i++) {
            mPath.lineTo(getXFromIndex(i, data.length), getYFromValue(data[i], max, min));
        }
        invalidate();
    }

    private float findMax(float[] data) {
        float max = Float.MIN_VALUE;
        for (int i = 0; i < data.length; i++) {
            if (data[i] > max) {
                max = data[i];
            }
        }
        return max;
    }

    private float findMin(float[] data) {
        float min = Float.MAX_VALUE;
        for (int i = 0; i < data.length; i++) {
            if (data[i] < min) {
                min = data[i];
            }
        }
        return min;
    }

    private float getYFromValue(float value, float max, float min) {
        return (max - value) * (getHeight()) / (max - min);
    }

    private float getXFromIndex(int i, int max) {
        return ((float) i * ((getWidth()) / ((float) max - 1.0f)));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        drawAxes(canvas);
        drawLine(canvas);
        super.onDraw(canvas);
    }

    private void drawLine(Canvas canvas) {
        canvas.drawPath(mPath, mPaintLine);
    }

    private void drawAxes(Canvas canvas) {
        canvas.drawLine(0, 0, 0, canvas.getHeight(), mPaintLine);
        canvas.drawLine(0, canvas.getHeight(), canvas.getWidth(), canvas.getHeight(), mPaintLine);
    }

    private float dpToPx(final float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }
}
