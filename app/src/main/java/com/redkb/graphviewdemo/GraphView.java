package com.redkb.graphviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class GraphView extends View {

    private static final int STROKE_WIDTH_DP = 4;
    private final Paint mPaintLine = new Paint();

    public GraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaintLine.setColor(Color.BLUE);
        mPaintLine.setStrokeWidth((int) dpToPx(STROKE_WIDTH_DP));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawAxes(canvas);
        super.onDraw(canvas);
    }

    private void drawAxes(Canvas canvas) {
        canvas.drawLine(0, 0, 0, canvas.getHeight(), mPaintLine);
        canvas.drawLine(0, canvas.getHeight(), canvas.getWidth(), canvas.getHeight(), mPaintLine);
    }

    private float dpToPx(final float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }
}
