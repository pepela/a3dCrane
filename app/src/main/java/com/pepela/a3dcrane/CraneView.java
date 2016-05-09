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
    private Paint mShadowLinePaint;
    private Paint mCirclePaint;
    private Paint mShadowCirclePaint;
    private Paint mBorderPaint;
    private Paint mIntervalPaint;

    //circles coordinates
    private int mPositionX = -1;
    private int mPositionY = -1;
    private int mPositionZ = -1;

    //shadow circle coordinates
    private int mShadowPositionX = 10;
    private int mShadowPositionY = 10;
    private int mShadowPositionZ = 10;


    //width and height of our view
    private int mViewWidth;
    private int mViewHeight;

    //dimensions of physical crane
    private int mSizeX;
    private int mSizeY;
    private int mSizeZ;
    private int mSizeBorder;

    //ration between physycal device measurements and pixels
    private static double mRatioX;
    private static double mRatioY;
    private static double mRatioZ;

    //interval size in cm, default is 10cm
    private int mIntervalSize = 20;

    public static final int AXIS_X = 0;
    public static final int AXIS_Y = 1;
    public static final int AXIS_Z = 2;

    private int mBorderWidth;


    private int mTopViewWidth, mTopViewHeight, mFrontViewWidth, mFrontViewHeight;

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

            if (!a.hasValue(R.styleable.CraneView_axisX))
                throw new RuntimeException("Defining axisX is required for CraneView");
            if (!a.hasValue(R.styleable.CraneView_axisY))
                throw new RuntimeException("Defining axisY is required for CraneView");
            if (!a.hasValue(R.styleable.CraneView_axisZ))
                throw new RuntimeException("Defining axisZ is required for CraneView");
            if (!a.hasValue(R.styleable.CraneView_border))
                throw new RuntimeException("Defining border is required for CraneView");


            mSizeX = a.getInt(R.styleable.CraneView_axisX, -1);
            mSizeY = a.getInt(R.styleable.CraneView_axisY, -1);
            mSizeZ = a.getInt(R.styleable.CraneView_axisZ, -1);
            mSizeBorder = a.getInt(R.styleable.CraneView_border, -1);

            mIntervalSize = a.getInt(R.styleable.CraneView_interval, 20);
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

            if (!a.hasValue(R.styleable.CraneView_axisX))
                throw new RuntimeException("Defining axisX is required for CraneView");
            if (!a.hasValue(R.styleable.CraneView_axisY))
                throw new RuntimeException("Defining axisY is required for CraneView");
            if (!a.hasValue(R.styleable.CraneView_axisZ))
                throw new RuntimeException("Defining axisZ is required for CraneView");
            if (!a.hasValue(R.styleable.CraneView_border))
                throw new RuntimeException("Defining border is required for CraneView");

            mSizeX = a.getInt(R.styleable.CraneView_axisX, -1);
            mSizeY = a.getInt(R.styleable.CraneView_axisY, -1);
            mSizeZ = a.getInt(R.styleable.CraneView_axisZ, -1);
            mSizeBorder = a.getInt(R.styleable.CraneView_border, -1);


            mIntervalSize = a.getInt(R.styleable.CraneView_interval, 20);
            init();
        } finally {
            a.recycle();
        }
    }


    public interface OnCranePositionChangeEventListener {
        void onPositionChangeEvent(double x, double y, double z);
    }

    public void setCranePositionChangeEventListener(OnCranePositionChangeEventListener eventListener) {
        mListener = eventListener;
    }


    private void init() {
        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(lineColor);

        mShadowLinePaint = new Paint();
        mShadowLinePaint.setStyle(Paint.Style.STROKE);
        mShadowLinePaint.setColor(lineColor);
        mShadowLinePaint.setAlpha(70);

        mCirclePaint = new Paint();
        mCirclePaint.setColor(circleColor);


        mShadowCirclePaint = new Paint();
        mShadowCirclePaint.setColor(circleColor);
        mShadowCirclePaint.setAlpha(70);

        mBorderPaint = new Paint();
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(borderColor);
        mBorderPaint.setStrokeWidth(10);

        mIntervalPaint = new Paint();
        mIntervalPaint.setStyle(Paint.Style.STROKE);
        mIntervalPaint.setColor(Color.BLACK);
    }

    public void setPosition(@NonNull double cranePositionX, @NonNull double cranePositionY, @NonNull double cranePositionZ) {

        this.mPositionX = mBorderWidth + cmToPixel(cranePositionX, AXIS_X);
        this.mPositionY = mBorderWidth + cmToPixel(cranePositionY, AXIS_Y);
        this.mPositionZ = mBorderWidth + cmToPixel(cranePositionZ, AXIS_Z);

        invalidate();
    }

    public void setShadowPosition(@NonNull double cranePositionX, @NonNull double cranePositionY, @NonNull double cranePositionZ) {

        this.mShadowPositionX = mBorderWidth + cmToPixel(cranePositionX, AXIS_X);
        this.mShadowPositionY = mBorderWidth + cmToPixel(cranePositionY, AXIS_Y);
        this.mShadowPositionZ = mBorderWidth + cmToPixel(cranePositionZ, AXIS_Z);

        invalidate();
    }

    public double getXInCm() {
        return pixelToCm(mPositionX - mBorderWidth, AXIS_X);
    }

    public double getYInCm() {
        return pixelToCm(mPositionY - mBorderWidth, AXIS_Y);
    }

    public double getZInCm() {
        return pixelToCm(mPositionZ - mBorderWidth, AXIS_Z);
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
            width = widthSize;//Math.min(desiredWidth, widthSize);
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
            height = heightSize;//Math.min(desiredHeight, heightSize);
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
        mFrontViewHeight = mTopViewHeight;

        if (mPositionX == -1)
            mPositionX = mTopViewHeight / 2;
        if (mPositionY == -1)
            mPositionY = mTopViewWidth / 2;
        if (mPositionZ == -1)
            mPositionZ = mFrontViewHeight / 2;

        try {
            mRatioX = (double) mTopViewWidth / mSizeX;
            mRatioY = (double) mTopViewHeight / mSizeY;
            mRatioZ = (double) mFrontViewHeight / mSizeZ;
        } catch (Exception e) {
            e.printStackTrace();
        }

        mBorderWidth = cmToPixel(mSizeBorder, AXIS_X);

        //MUST CALL THIS
        setMeasuredDimension(mTopViewWidth + mFrontViewWidth, mTopViewHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mBorderPaint.setStrokeWidth(mBorderWidth);

        //draw borders
        canvas.drawRect(mBorderWidth / 2,
                mBorderWidth / 2,
                mTopViewWidth - mBorderWidth / 2,
                mTopViewHeight - mBorderWidth / 2,
                mBorderPaint);
        canvas.drawLine(mTopViewWidth,
                mBorderWidth / 2,
                mTopViewWidth + mFrontViewWidth,
                mBorderWidth / 2,
                mBorderPaint);
        canvas.drawLine(mTopViewWidth + mFrontViewWidth - mBorderWidth / 2,
                mBorderWidth / 2,
                mTopViewWidth + mFrontViewWidth - mBorderWidth / 2,
                mFrontViewHeight,
                mBorderPaint);


        //draw vertical and horizontal lines
        canvas.drawLine(mPositionX, mBorderWidth, mPositionX, mTopViewHeight, mLinePaint);
        canvas.drawLine(mBorderWidth, mPositionY, mTopViewWidth, mPositionY, mLinePaint);
        canvas.drawLine(mShadowPositionX, mBorderWidth, mShadowPositionX, mTopViewHeight, mLinePaint);
        canvas.drawLine(mBorderWidth, mShadowPositionY, mTopViewWidth, mShadowPositionY, mLinePaint);

        //draw center
        canvas.drawCircle(mPositionX, mPositionY, 20, mCirclePaint);
        canvas.drawCircle(mShadowPositionX, mShadowPositionY, 20, mShadowCirclePaint);

        //draw up and down
        canvas.drawLine(mTopViewWidth + mFrontViewWidth / 2 - mBorderWidth / 2,
                mBorderWidth,
                mTopViewWidth + mFrontViewWidth / 2 - mBorderWidth / 2,
                mTopViewHeight,
                mLinePaint);
        canvas.drawLine(mTopViewWidth + mFrontViewWidth / 2,
                mPositionZ,
                mTopViewWidth + mFrontViewWidth - mBorderWidth / 2,
                mPositionZ,
                mLinePaint);

        canvas.drawLine(mTopViewWidth + mFrontViewWidth / 2,
                mShadowPositionZ,
                mTopViewWidth + mFrontViewWidth - mBorderWidth / 2,
                mShadowPositionZ,
                mLinePaint);

        canvas.drawCircle(mTopViewWidth + mFrontViewWidth / 2 - mBorderWidth / 2, mPositionZ, 20, mCirclePaint);
        canvas.drawCircle(mTopViewWidth + mFrontViewWidth / 2 - mBorderWidth / 2, mShadowPositionZ, 20, mShadowCirclePaint);

        Paint paint = new Paint();

        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        canvas.drawText(String.format("x = %d y = %d z = %d", mPositionX, mPositionY, mPositionZ), 10, 25, paint);

        double actualPositionX = pixelToCm(mPositionX - mBorderWidth, AXIS_X);
        double actualPositionY = pixelToCm(mPositionY - mBorderWidth, AXIS_Y);
        double actualPositionZ = pixelToCm(mPositionZ - mBorderWidth, AXIS_Z);

        canvas.drawText(String.format("x = %f y = %f z = %f", actualPositionX, actualPositionY, actualPositionZ), 10, 45, paint);

        drawIntervals(canvas);
    }

    private void drawIntervals(Canvas canvas) {

        int intervalLengthInPixelsX = (int) cmToPixel(mIntervalSize, AXIS_X);//mIntervalSize * (int) mRatioX;
        int intervalLengthInPixelsY = (int) cmToPixel(mIntervalSize, AXIS_Y);//mIntervalSize * (int) mRatioX;
        int intervalLengthInPixelsZ = (int) cmToPixel(mIntervalSize, AXIS_Z);//mIntervalSize * (int) mRatioZ;

        int intervalCountX = (mSizeX - 2 * mSizeBorder) / mIntervalSize; //mTopViewWidth / intervalLengthInPixelsX;
        int intervalCountY = (mSizeY - 2 * mSizeBorder) / mIntervalSize; //mTopViewHeight / intervalLengthInPixelsX;
        int intervalCountZ = (mSizeZ - 2 * mSizeBorder) / mIntervalSize; //mFrontViewHeight / intervalLengthInPixelsZ;

        for (int i = 0; i <= intervalCountX; i++) {
            canvas.drawLine(mBorderWidth + intervalLengthInPixelsX * i,
                    mTopViewHeight,
                    mBorderWidth + intervalLengthInPixelsX * i,
                    mTopViewHeight - mBorderWidth,
                    mIntervalPaint);
            canvas.drawText(Double.toString(i * mIntervalSize),
                    mBorderWidth + intervalLengthInPixelsX * i + 5,
                    mTopViewHeight - 5,
                    mIntervalPaint);
        }
        canvas.drawLine(mTopViewWidth - mBorderWidth,
                mTopViewHeight,
                mTopViewWidth - mBorderWidth,
                mTopViewHeight - mBorderWidth,
                mIntervalPaint);
        canvas.drawText(Double.toString(mSizeX - 2 * mSizeBorder),
                mTopViewWidth - mBorderWidth,
                mTopViewHeight - 5,
                mIntervalPaint);

        for (int i = 0; i <= intervalCountY; i++) {
            canvas.drawLine(mTopViewWidth - mBorderWidth,
                    mBorderWidth + intervalLengthInPixelsY * i,
                    mTopViewWidth,
                    mBorderWidth + intervalLengthInPixelsY * i,
                    mIntervalPaint);

            canvas.drawText(Double.toString(i * mIntervalSize),
                    mTopViewWidth - mBorderWidth,
                    mBorderWidth + intervalLengthInPixelsY * i - 5,
                    mIntervalPaint);
        }
        canvas.drawLine(mTopViewWidth - mBorderWidth,
                mTopViewHeight - mBorderWidth,
                mTopViewWidth,
                mTopViewHeight - mBorderWidth,
                mIntervalPaint);

        canvas.drawText(Double.toString(mSizeY - 2 * mSizeBorder),
                mTopViewWidth - mBorderWidth,
                mTopViewHeight - mBorderWidth - 5,
                mIntervalPaint);

        for (int i = 0; i <= intervalCountZ; i++) {
            canvas.drawLine(mTopViewWidth + mFrontViewWidth - mBorderWidth,
                    mBorderWidth + intervalLengthInPixelsZ * i,
                    mTopViewWidth + mFrontViewWidth,
                    mBorderWidth + intervalLengthInPixelsZ * i,
                    mIntervalPaint);

            canvas.drawText(Double.toString(i * mIntervalSize),
                    mTopViewWidth + mFrontViewWidth - mBorderWidth,
                    mBorderWidth + intervalLengthInPixelsZ * i,
                    mIntervalPaint);
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
                } else if (isFrontSideView(xTouch, yTouch)) {
                    mPositionZ = yTouch;
                } else {
                    throwEvent = false;
                }

                invalidate();
                handled = true;

                if (mListener != null && throwEvent)
                    mListener.onPositionChangeEvent(pixelToCm(mPositionX - mBorderWidth, AXIS_X),
                            pixelToCm(mPositionY - mBorderWidth, AXIS_Y),
                            pixelToCm(mPositionZ - mBorderWidth, AXIS_Z));

                break;

            case MotionEvent.ACTION_MOVE:
                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);

                if (isTopSideView(xTouch, yTouch)) {
                    mPositionX = xTouch;
                    mPositionY = yTouch;
                } else if (isFrontSideView(xTouch, yTouch)) {
                    mPositionZ = yTouch;
                } else {
                    throwEvent = false;
                }

                invalidate();
                handled = true;

                if (mListener != null && throwEvent)
                    mListener.onPositionChangeEvent(pixelToCm(mPositionX - mBorderWidth, AXIS_X),
                            pixelToCm(mPositionY - mBorderWidth, AXIS_Y),
                            pixelToCm(mPositionZ - mBorderWidth, AXIS_Z));

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

    public static int cmToPixel(double lengthInCm, int axis) {
        switch (axis) {
            case AXIS_X:
                return (int) (lengthInCm * mRatioX);
            case AXIS_Y:
                return (int) (lengthInCm * mRatioY);
            case AXIS_Z:
                return (int) (lengthInCm * mRatioZ);
        }
        return 0;
    }

    private boolean isTopSideView(int x, int y) {
        if (x >= mBorderWidth
                && x <= mTopViewWidth - mBorderWidth
                && y >= mBorderWidth
                && y <= mTopViewHeight - mBorderWidth)
            return true;
        return false;
    }

    private boolean isFrontSideView(int x, int y) {
        if (x >= mTopViewWidth
                && x <= mTopViewWidth + mFrontViewWidth
                && y >= mBorderWidth
                && y <= mFrontViewHeight - mBorderWidth / 2)
            return true;
        return false;
    }

    public static double pixelToCm(int lengthInPixels, int axis) {
        switch (axis) {
            case AXIS_X:
                return lengthInPixels / mRatioX;
            case AXIS_Y:
                return lengthInPixels / mRatioY;
            case AXIS_Z:
                return lengthInPixels / mRatioZ;
        }
        return 0;
    }
}
