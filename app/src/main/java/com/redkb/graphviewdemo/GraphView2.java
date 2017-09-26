package com.redkb.graphviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class GraphView2 extends View {

    private static final int STROKE_WIDTH_DP = 4;
    private final Paint mPaintLine = new Paint();
    private final Path mPath = new Path();

    public GraphView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaintLine.setColor(Color.argb(255, 33, 189, 222));
        mPaintLine.setStrokeWidth((int) Utility.dpToPx(STROKE_WIDTH_DP, getResources()));
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setAntiAlias(true);
        mPaintLine.setStrokeJoin(Paint.Join.ROUND);
    }

    public void setData(float[] data) {
        mPath.reset();
        float max = Utility.findMax(data);
        float min = Utility.findMin(data);
        mPath.rMoveTo(getXFromIndex(0, data.length), getYFromValue(data[0], max, min));
        for (int i = 1; i < data.length; i++) {
            mPath.lineTo(getXFromIndex(i, data.length), getYFromValue(data[i], max, min));
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawAxes(canvas);
        drawLineGraph(canvas);
        super.onDraw(canvas);
    }

    private void drawLineGraph(Canvas canvas) {
        canvas.drawPath(mPath, mPaintLine);
    }

    private void drawAxes(Canvas canvas) {
        canvas.drawLine(0, 0, 0, canvas.getHeight(), mPaintLine);
        canvas.drawLine(0, canvas.getHeight(), canvas.getWidth(), canvas.getHeight(), mPaintLine);
    }

    private float getYFromValue(float value, float max, float min) {
        return (max - value) * (getHeight()) / (max - min);
    }

    private float getXFromIndex(int index, int max) {
        return ((float) index * ((getWidth()) / ((float) max - 1.0f)));
    }
}
