package com.pepela.a3dcrane;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by giorg_000 on 03.05.2016.
 */
public class CraneAngleView extends View {

    private int mViewWidth;
    private int mViewHeight;

    private int mOutsideCircleRadius;
    private int mInsideCircleRadius;
    private int mCraneCircleRadius;

    private int mCranePositionX;
    private int mCranePositionY;

    private int mViewCenterX;
    private int mViewCenterY;

    private Path mPath;

    private Paint mDangerBackgroundPaint;
    private Paint mOkBackgroundPaint;
    private Paint mCranePaint;
    private Paint mLinePaint;

    private int mAngleHorizontal;
    private int mAngleVertical;


    public CraneAngleView(Context context) {
        super(context);
        init();
    }

    public CraneAngleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CraneAngleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CraneAngleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {

        mDangerBackgroundPaint = new Paint();
        mDangerBackgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.danger));

        mOkBackgroundPaint = new Paint();
        mOkBackgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.lightGreen));

        mCranePaint = new Paint();
        mCranePaint.setColor(ContextCompat.getColor(getContext(), R.color.black));

        mLinePaint = new Paint();
        mLinePaint.setColor(ContextCompat.getColor(getContext(), R.color.black));
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setPathEffect(new DashPathEffect(new float[]{5, 10}, 0));


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int desiredWidth = 200;
        int desiredHeight = 200;

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

        height = height > width ? width : height;
        width = width > height ? height : width;

        mCranePositionX = mViewCenterX = width / 2;
        mCranePositionY = mViewCenterY = width / 2;

        mOutsideCircleRadius = width / 2 - 20;
        mInsideCircleRadius = mOutsideCircleRadius / 2;
        mCraneCircleRadius = mInsideCircleRadius / 2;

        mViewWidth = width;
        mViewHeight = height;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(mViewCenterX, mViewCenterY, mOutsideCircleRadius, mDangerBackgroundPaint);
        canvas.drawCircle(mViewCenterX, mViewCenterY, mInsideCircleRadius, mOkBackgroundPaint);
        canvas.drawCircle(mCranePositionX, mCranePositionY, mCraneCircleRadius, mCranePaint);


        mPath = new Path();
        mPath.moveTo(0, mViewHeight / 2);
        mPath.quadTo(mViewWidth / 2, mViewHeight / 2, mViewWidth, mViewHeight / 2);
        canvas.drawPath(mPath, mLinePaint);

        mPath.moveTo(mViewWidth / 2, 0);
        mPath.quadTo(mViewWidth / 2, mViewHeight / 2, mViewWidth / 2, mViewHeight);
        canvas.drawPath(mPath, mLinePaint);


//        canvas.drawLine(mViewWidth / 2, 0, mViewWidth / 2, mViewHeight, mCranePaint);
//        canvas.drawLine(0, mViewHeight / 2, mViewWidth, mViewHeight / 2, mCranePaint);
    }


    public void setCraneAngle(double angleHorizontal, double angleVertical, double height) {
        double aH = Math.cos(angleHorizontal) * height;
        double aV = Math.cos(angleVertical) * height;

//        mCranePositionX += aH;
//        mCranePositionY += aV;
    }
}
