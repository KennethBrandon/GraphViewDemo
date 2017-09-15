package com.redkb.graphviewdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.text.NumberFormat;

public class GraphView5 extends View {
    //private static final String TAG = "GraphView5";
    private final Paint mPaintLine = new Paint();
    private final Paint mPaintPointer = new Paint();
    private final Paint mPaintAxes = new Paint();
    private final Paint mPaintText = new Paint();
    private final Path mPath = new Path();
    private final NumberFormat mFormat = NumberFormat.getCurrencyInstance();

    private ValueAnimator mPointerAnimator = ValueAnimator.ofFloat(0, 1);
    private Point mPointerLocation = new Point();
    private float mMaxValue;
    private float mMinValue;
    private int mMaxIndex;
    private boolean mDown = false;
    private float[] mData;
    private float mCurrentValue; //the value of the current down location

    private boolean mShowAxes = true;
    private boolean mShowPointer = true;
    private int pointerAnimationLength = 200; //in ms
    private int mAxesColor;
    private int mLineColor;
    private int mPointerColor;
    private int mLineWidth;
    private int mTextSize;
    private int mPointerRadiusPx;
    private GraphType mGraphType = GraphType.BAR;

    protected enum GraphType {
        LINE,
        BAR;

        public static GraphType fromInteger(int x) {
            switch (x) {
                case 0:
                    return LINE;
                case 1:
                    return BAR;
            }
            return null;
        }
    }

    public GraphView5(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.GraphView5,
                0, 0);

        try {
            mGraphType = GraphType.fromInteger(typedArray.getInteger(R.styleable.GraphView5_graphType, 0));
            mShowAxes = typedArray.getBoolean(R.styleable.GraphView5_showAxes, true);
            mShowPointer = typedArray.getBoolean(R.styleable.GraphView5_showPointer, false);
            pointerAnimationLength = typedArray.getInteger(R.styleable.GraphView5_animationDuration, 200);
            mAxesColor = typedArray.getColor(R.styleable.GraphView5_axesColor, 0x053388);
            mLineColor = typedArray.getColor(R.styleable.GraphView5_lineColor, 0x119944);
            mPointerColor = typedArray.getColor(R.styleable.GraphView5_pointerColor, 0xaa3344);
            mLineWidth = typedArray.getDimensionPixelSize(R.styleable.GraphView5_lineWidth, (int) Utility.dpToPx(2, getResources()));
            mTextSize = typedArray.getDimensionPixelSize(R.styleable.GraphView5_numberTextSize, (int) Utility.dpToPx(20, getResources()));
            mPointerRadiusPx = typedArray.getDimensionPixelSize(R.styleable.GraphView5_pointerRadius, (int) Utility.dpToPx(15, getResources()));
        } finally {
            typedArray.recycle();
        }

        mPaintLine.setColor(mLineColor);
        mPaintLine.setStrokeWidth(mLineWidth);
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setAntiAlias(true);
        mPaintLine.setStrokeJoin(Paint.Join.ROUND);

        mPaintPointer.setColor(mPointerColor);
        mPaintPointer.setStrokeWidth(mLineWidth);
        mPaintPointer.setStyle(Paint.Style.STROKE);
        mPaintPointer.setAntiAlias(true);
        mPaintPointer.setStrokeJoin(Paint.Join.ROUND);

        mPaintAxes.setColor(mAxesColor);
        mPaintAxes.setStrokeWidth(mLineWidth);
        mPaintAxes.setStyle(Paint.Style.STROKE);
        mPaintAxes.setAntiAlias(true);
        mPaintAxes.setStrokeJoin(Paint.Join.ROUND);

        mPaintText.setColor(mPointerColor);
        mPaintText.setStrokeWidth(Utility.dpToPx(1, getResources()));
        mPaintText.setTextSize(mTextSize);
        mPaintText.setAntiAlias(true);

        mPointerAnimator.setDuration(pointerAnimationLength);
        mPointerAnimator.setInterpolator(new DecelerateInterpolator());
        mPointerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                invalidate(); //this causes onDraw to be called to draw the animation
            }
        });
        if (isInEditMode()) {
            setData(new float[]{5.0f, 22.2f, 12.3f, 16.4f, 15.5f, 19.4f, 14.3f, 11.3f, 7.3f});
            mDown = true;
            mCurrentValue = 12;
            mPointerLocation = new Point(200, 200);
        }
    }

    public void setData(float[] data) {
        mMaxValue = Utility.findMax(data);
        mMinValue = Utility.findMin(data);
        mData = data;
        mMaxIndex = data.length;
        mPath.reset();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.i(TAG, "Event: " + event.getAction() + " (" + event.getX() + ", " + event.getY() + ")");
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
        if (mShowAxes) {
            drawAxes(canvas);
        }
        switch (mGraphType) {
            case LINE:
                drawLine(canvas);
                break;
            case BAR:
                drawBar(canvas);
                break;
        }
        if ((mDown || mPointerAnimator.isRunning()) && mData != null && mShowPointer) {
            drawPointerCircle(canvas);
            drawPointerX(canvas);
            drawTextValue(canvas);
        }
        super.onDraw(canvas);
    }

    private void drawBar(Canvas canvas) {
        if (mData == null) return;
        for (int i = 0; i < mData.length; i++) {
            int x = (int) getXFromIndex(i);
            canvas.drawLine(x, getYFromValue(mData[i]), x, getHeight(), mPaintLine);
        }
    }

    private void drawTextValue(Canvas canvas) {
        if (mPointerAnimator.isRunning()) {
            mPaintText.setAlpha((int) (mPointerAnimator.getAnimatedFraction() * 255));  //fade in
        } else {
            mPaintText.setAlpha(255);
        }
        canvas.drawText(mFormat.format(mCurrentValue), mPointerLocation.x + mPointerRadiusPx * 1.2f, mPointerLocation.y - 2 * mLineWidth, mPaintText);
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
        if (mPath.isEmpty()) {
            buildPath(); //
        }
        canvas.drawPath(mPath, mPaintLine);
    }

    private void buildPath() {
        //creating path from data
        if (mData == null) {
            return;
        }
        mPath.reset();
        mPath.rMoveTo(getXFromIndex(0), getYFromValue(mData[0]));
        for (int i = 1; i < mData.length; i++) {
            mPath.lineTo(getXFromIndex(i), getYFromValue(mData[i]));
        }
    }

    private void drawAxes(Canvas canvas) {
        canvas.drawLine(0, 0, 0, canvas.getHeight(), mPaintAxes);
        canvas.drawLine(0, canvas.getHeight(), canvas.getWidth(), canvas.getHeight(), mPaintAxes);
    }

    private float getYFromValue(float value) {
        if (mGraphType == GraphType.LINE) {
            return (mMaxValue - value) * (getHeight()) / (mMaxValue - mMinValue);//if line the smallest point is at the bottom of the graph
        } else {
            return (mMaxValue - value) * (getHeight()) / (mMaxValue - mMinValue * .9f);//if bar make the smallest point above the bottom of the graph so there is a bar
        }
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

    public boolean ismShowAxes() {
        return mShowAxes;
    }

    public void setmShowAxes(boolean mShowAxes) {
        this.mShowAxes = mShowAxes;
    }

    public boolean ismShowPointer() {
        return mShowPointer;
    }

    public void setmShowPointer(boolean mShowPointer) {
        this.mShowPointer = mShowPointer;
    }

    public int getPointerAnimationLength() {
        return pointerAnimationLength;
    }

    public void setPointerAnimationLength(int pointerAnimationLength) {
        this.pointerAnimationLength = pointerAnimationLength;
    }

    public int getmAxesColor() {
        return mAxesColor;
    }

    public void setmAxesColor(int mAxesColor) {
        this.mAxesColor = mAxesColor;
    }

    public int getmLineColor() {
        return mLineColor;
    }

    public void setmLineColor(int mLineColor) {
        this.mLineColor = mLineColor;
    }

    public int getmPointerColor() {
        return mPointerColor;
    }

    public void setmPointerColor(int mPointerColor) {
        this.mPointerColor = mPointerColor;
    }

    public int getmLineWidth() {
        return mLineWidth;
    }

    public void setmLineWidth(int mLineWidth) {
        this.mLineWidth = mLineWidth;
    }

    public int getmTextSize() {
        return mTextSize;
    }

    public void setmTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
    }

    public int getmPointerRadiusPx() {
        return mPointerRadiusPx;
    }

    public void setmPointerRadiusPx(int mPointerRadiusPx) {
        this.mPointerRadiusPx = mPointerRadiusPx;
    }

    public GraphType getmGraphType() {
        return mGraphType;
    }

    public void setmGraphType(GraphType mGraphType) {
        this.mGraphType = mGraphType;
    }
}