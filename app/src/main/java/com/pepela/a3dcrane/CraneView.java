package com.pepela.a3dcrane;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by giorg_000 on 08.04.2016.
 */
public class CraneView extends View {

    OnCranePositionChangeEventListener mListener;

    //color resources
    int circleColor;
    int lineColor;
    int borderColor;

    //paints for lines and circle
    private Paint mLinePaint;
    private Paint mCirclePaint;
    private Paint mBorderPaint;

    //circles coordinates
    private int mPositionX;
    private int mPositionY;
    private int mPositionZ;


    //width and height of our view
    private int mViewWidth;
    private int mViewHeight;

    //dimensions of physical crane
    private int mSizeX;
    private int mSizeY;
    private int mSizeZ;

    private int mTopViewWidth, mTopViewHeight, mFrontViewWidth, mFronViewHeight;

    public CraneView(Context context) {
        super(context);
        init();
    }

    public CraneView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CraneView,
                0, 0
        );

        try {
            circleColor = a.getColor(R.styleable.CraneView_circleColor, 0xff000000);
            lineColor = a.getColor(R.styleable.CraneView_lineColor, 0xff000000);
            borderColor = a.getColor(R.styleable.CraneView_borderColor, 0xff000000);

            mSizeX = a.getIndex(R.styleable.CraneView_axisX);
            mSizeY = a.getIndex(R.styleable.CraneView_axisY);
            mSizeZ = a.getIndex(R.styleable.CraneView_axisZ);

            init();
        } finally {
            a.recycle();
        }
    }

    public CraneView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CraneView,
                0, 0
        );

        try {
            circleColor = a.getColor(R.styleable.CraneView_circleColor, 0xff000000);
            lineColor = a.getColor(R.styleable.CraneView_lineColor, 0xff000000);
            borderColor = a.getColor(R.styleable.CraneView_borderColor, 0xff000000);

            mSizeX = a.getIndex(R.styleable.CraneView_axisX);
            mSizeY = a.getIndex(R.styleable.CraneView_axisY);
            mSizeZ = a.getIndex(R.styleable.CraneView_axisZ);

            init();
        } finally {
            a.recycle();
        }
    }


    public interface OnCranePositionChangeEventListener {
        void onPositionChangeEvent(int x, int y, int z);
    }

    public void setCranePositionChangeEventListener(OnCranePositionChangeEventListener eventListener) {
        mListener = eventListener;
    }


    private void init() {
        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(lineColor);

        mCirclePaint = new Paint();
        mCirclePaint.setColor(circleColor);

        mBorderPaint = new Paint();
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(Color.BLACK);

    }

    public void setPosition(@NonNull int cranePositionX, @NonNull int cranePositionY) {
        this.mPositionX = cranePositionX;
        this.mPositionY = cranePositionY;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int desiredWidth = 100;
        int desiredHeight = 100;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        mViewWidth = width;
        mViewHeight = height;

        mFrontViewWidth = width / 5;

        mTopViewHeight = width > height ? height : width;
        if (height > width) {
            mTopViewHeight -= mFrontViewWidth;
        }

        mTopViewWidth = mTopViewHeight;
        mFronViewHeight = mTopViewHeight;

        mPositionX = mTopViewHeight / 2;
        mPositionY = mTopViewWidth / 2;
        mPositionZ = mFronViewHeight / 2;

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //draw border
        canvas.drawRect(0, 0, mTopViewWidth, mTopViewHeight, mBorderPaint);

        //draw vertical and horizontal lines
        canvas.drawLine(mPositionX, 0, mPositionX, mTopViewHeight, mLinePaint);
        canvas.drawLine(0, mPositionY, mTopViewWidth, mPositionY, mLinePaint);

        //draw center
        canvas.drawCircle(mPositionX, mPositionY, 20, mCirclePaint);

        //draw up and down
        canvas.drawRect(mTopViewWidth, 0, mTopViewWidth + mFrontViewWidth, mTopViewHeight, mBorderPaint);
        canvas.drawLine(mTopViewWidth + mFrontViewWidth / 2, 0, mTopViewWidth + mFrontViewWidth / 2, mTopViewHeight, mLinePaint);
        canvas.drawLine(mTopViewWidth + mFrontViewWidth / 2, mPositionZ, mTopViewWidth, mPositionZ, mLinePaint);
        canvas.drawCircle(mTopViewWidth + mFrontViewWidth / 2, mPositionZ, 20, mCirclePaint);

        Paint paint = new Paint();

        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        canvas.drawText(String.format("x = %d y = %d z = %d", mPositionX, mPositionY, mPositionZ), 10, 25, paint);

        drawIntervals(canvas, 10, 50);
    }

    private void drawIntervals(Canvas canvas, int height, int intervalLength) {
        int horizontalCount = mTopViewWidth / intervalLength;
        int verticalCount = mTopViewHeight / intervalLength;
        for (int i = 0; i <= horizontalCount; i++)
            canvas.drawLine(intervalLength * i, mTopViewHeight, intervalLength * i, mTopViewHeight - height, mBorderPaint);
        for (int i = 0; i <= verticalCount; i++) {
            canvas.drawLine(mTopViewWidth, intervalLength * i, mTopViewWidth - height, intervalLength * i, mBorderPaint);
            canvas.drawLine(mTopViewWidth, intervalLength * i, mTopViewWidth + height, intervalLength * i, mBorderPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean handled = false;
        boolean throwEvent = true;

        int xTouch;
        int yTouch;

        // get touch event coordinates and make transparent circle from it
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);


                if (isTopSideView(xTouch, yTouch)) {
                    mPositionX = xTouch;
                    mPositionY = yTouch;
                } else if (isFronSideView(xTouch, yTouch)) {
                    mPositionZ = yTouch;
                } else {
                    throwEvent = false;
                }

                invalidate();
                handled = true;

                if (mListener != null && throwEvent)
                    mListener.onPositionChangeEvent(mPositionX, mPositionY, mPositionZ);

                break;

            case MotionEvent.ACTION_MOVE:
                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);

                if (isTopSideView(xTouch, yTouch)) {
                    mPositionX = xTouch;
                    mPositionY = yTouch;
                } else if (isFronSideView(xTouch, yTouch)) {
                    mPositionZ = yTouch;
                } else {
                    throwEvent = false;
                }

                invalidate();
                handled = true;

                if (mListener != null && throwEvent)
                    mListener.onPositionChangeEvent(mPositionX, mPositionY, mPositionZ);

                break;

            case MotionEvent.ACTION_CANCEL:
                handled = true;
                break;

            default:
                // do nothing
                break;
        }

        return super.onTouchEvent(event) || handled;
    }

    private boolean isTopSideView(int x, int y) {
        if (x >= 0 && x <= mTopViewWidth && y >= 0 && y <= mTopViewHeight)
            return true;
        return false;
    }

    private boolean isFronSideView(int x, int y) {
        if (x >= mTopViewWidth && x <= mTopViewWidth + mFrontViewWidth && y >= 0 && y <= mFronViewHeight)
            return true;
        return false;
    }
}
