package com.redkb.graphviewdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.text.NumberFormat;

public class GraphView4 extends View {
    //private static final String TAG = "GraphView4";
    private static final int STROKE_WIDTH_DP = 4;
    private static final int POINTER_RADIUS_DP = 15;
    private static final int POINTER_ANIMATION_LENGTH = 200; //in ms
    private final int mStrokeInPx = (int) Utility.dpToPx(STROKE_WIDTH_DP, getResources());
    private final int mPointerRadiusPx = (int) Utility.dpToPx(POINTER_RADIUS_DP, getResources());
    private final Paint mPaintLine = new Paint();
    private final Paint mPaintPointer = new Paint();
    private final Paint mPaintAxes = new Paint();
    private final Paint mPaintText = new Paint();
    private final Path mPath = new Path();
    private final NumberFormat mFormat = NumberFormat.getCurrencyInstance();
    private Point mPointerLocation = new Point();
    private boolean mDown = false;
    private float mMaxValue;
    private float mMinValue;
    private int mMaxIndex;
    private float[] mData;
    private float mCurrentValue; //the value of the current down location
    private ValueAnimator mPointerAnimator = ValueAnimator.ofFloat(0, 1);

    public GraphView4(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mPaintLine.setColor(Color.argb(255, 33, 234, 152));
        mPaintLine.setStrokeWidth(mStrokeInPx);
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setAntiAlias(true);
        mPaintLine.setStrokeJoin(Paint.Join.ROUND);

        mPaintPointer.setColor(Color.argb(255, 100, 20, 40));
        mPaintPointer.setStrokeWidth(mStrokeInPx);
        mPaintPointer.setStyle(Paint.Style.STROKE);
        mPaintPointer.setAntiAlias(true);
        mPaintPointer.setStrokeJoin(Paint.Join.ROUND);

        mPaintAxes.setColor(Color.argb(255, 10, 120, 100));
        mPaintAxes.setStrokeWidth(mStrokeInPx);
        mPaintAxes.setStyle(Paint.Style.STROKE);
        mPaintAxes.setAntiAlias(true);
        mPaintAxes.setStrokeJoin(Paint.Join.ROUND);

        mPaintText.setColor(Color.argb(255, 100, 20, 40));
        mPaintText.setStrokeWidth(Utility.dpToPx(1, getResources()));
        mPaintText.setTextSize(Utility.dpToPx(20, getResources()));
        mPaintText.setAntiAlias(true);

        mPointerAnimator.setDuration(POINTER_ANIMATION_LENGTH);
        mPointerAnimator.setInterpolator(new DecelerateInterpolator());
        mPointerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                invalidate(); //this causes onDraw to be called to draw the animation
            }
        });
    }

    public void setData(float[] data) {
        mPath.reset();
        mMaxValue = Utility.findMax(data);
        mMinValue = Utility.findMin(data);
        mData = data;
        mMaxIndex = data.length;

        //creating path from data
        mPath.rMoveTo(getXFromIndex(0), getYFromValue(data[0]));
        for (int i = 1; i < data.length; i++) {
            mPath.lineTo(getXFromIndex(i), getYFromValue(data[i]));
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPointerAnimator.start();
                mDown = true;
            case MotionEvent.ACTION_MOVE:
                setPointerLocation(event);
                invalidate();
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mDown = false;
                mPointerAnimator.reverse();
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawAxes(canvas);
        drawLine(canvas);
        if ((mDown || mPointerAnimator.isRunning()) && mData != null) {
            drawPointerCircle(canvas);
            drawPointerX(canvas);
            drawTextValue(canvas);
        }
        super.onDraw(canvas);
    }

    private void drawTextValue(Canvas canvas) {
        if (mPointerAnimator.isRunning()) {
            mPaintText.setAlpha((int) (mPointerAnimator.getAnimatedFraction() * 255));  //fade in
        } else {
            mPaintText.setAlpha(255);
        }
        canvas.drawText(mFormat.format(mCurrentValue), mPointerLocation.x + mPointerRadiusPx * 1.2f, mPointerLocation.y - 2 * mStrokeInPx, mPaintText);
    }

    private void drawPointerX(Canvas canvas) {
        //X Marks the spot
        if (mPointerAnimator.isRunning()) {
            canvas.drawLine(mPointerLocation.x, mPointerLocation.y, mPointerLocation.x, mPointerLocation.y - (mPointerAnimator.getAnimatedFraction() * mPointerLocation.y), mPaintPointer); //vertical line top
            canvas.drawLine(mPointerLocation.x, mPointerLocation.y, mPointerLocation.x, mPointerLocation.y + (mPointerAnimator.getAnimatedFraction() * (canvas.getHeight() - mPointerLocation.y)), mPaintPointer); //vertical line bottom
            canvas.drawLine(mPointerLocation.x - (mPointerAnimator.getAnimatedFraction() * mPointerLocation.x), mPointerLocation.y, mPointerLocation.x, mPointerLocation.y, mPaintPointer); //horizontal line left
            canvas.drawLine(mPointerLocation.x + (mPointerAnimator.getAnimatedFraction() * (canvas.getWidth() - mPointerLocation.x)), mPointerLocation.y, mPointerLocation.x, mPointerLocation.y, mPaintPointer); //horizontal line right

        } else {
            canvas.drawLine(mPointerLocation.x, 0, mPointerLocation.x, canvas.getHeight(), mPaintPointer); //vertical line
            canvas.drawLine(0, mPointerLocation.y, canvas.getWidth(), mPointerLocation.y, mPaintPointer); //horizontal line
        }
    }

    private void drawPointerCircle(Canvas canvas) {
        if (mPointerAnimator.isRunning()) {
            mPaintPointer.setAlpha((int) (mPointerAnimator.getAnimatedFraction() * 255));
        } else {
            mPaintPointer.setAlpha(255);
        }
        canvas.drawCircle(mPointerLocation.x, mPointerLocation.y, mPointerRadiusPx, mPaintPointer);
        mPaintPointer.setAlpha(255); //back to full. This is needed because this paint is shared with drawing the X
    }

    private void drawLine(Canvas canvas) {
        canvas.drawPath(mPath, mPaintLine);
    }

    private void drawAxes(Canvas canvas) {
        canvas.drawLine(0, 0, 0, canvas.getHeight(), mPaintAxes);
        canvas.drawLine(0, canvas.getHeight(), canvas.getWidth(), canvas.getHeight(), mPaintAxes);
    }

    private float getYFromValue(float value) {
        return (mMaxValue - value) * (getHeight()) / (mMaxValue - mMinValue);
    }

    private float getXFromIndex(int i) {
        return ((float) i * ((getWidth()) / ((float) mMaxIndex - 1.0f)));
    }

    private void setPointerLocation(MotionEvent event) {
        if (mData == null) {
            return;
        }
        //Using the width and the x of the event we find the corresponding data index
        int index = (int) (((event.getX()) * (mMaxIndex - 1.0f) / ((float) getWidth())) + 1f);
        if (index < 0) {
            index = 0;
        }
        if (index > mMaxIndex - 1) {
            index = mMaxIndex - 1;
        }
        mCurrentValue = mData[index];
        mPointerLocation.set((int) getXFromIndex(index), (int) getYFromValue(mCurrentValue));
    }
}