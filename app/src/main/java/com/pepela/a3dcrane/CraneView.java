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

    //paints for lines and circle
    private Paint mLinePaint;
    private Paint mCirclePaint;

    //circles coordinates
    private int positionX;
    private int positionY;

    //width and height of our view
    private int mViewWidth;
    private int mViewHeight;

    public CraneView(Context context) {
        super(context);
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

            init();
        } finally {
            a.recycle();
        }
    }


    public interface OnCranePositionChangeEventListener {
        void onPositionChangeEvent(int x, int y);
    }

    public void setCranePositionChangeEventListener(OnCranePositionChangeEventListener eventListener) {
        mListener = eventListener;
    }


    private void init() {
        mLinePaint = new Paint();
        mLinePaint.setColor(lineColor);

        mCirclePaint = new Paint();
        mCirclePaint.setColor(circleColor);

    }

    public void setPosition(@NonNull int cranePositionX, @NonNull int cranePositionY) {
        this.positionX = cranePositionX;
        this.positionY = cranePositionY;
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

        positionX = width / 2;
        positionY = height / 2;

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawLine(positionX, 0, positionX, mViewHeight, mLinePaint);
        canvas.drawLine(0, positionY, mViewWidth, positionY, mLinePaint);

        canvas.drawCircle(positionX, positionY, 20, mCirclePaint);


        Paint paint = new Paint();


        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        canvas.drawText(String.format("x = %d\ny = %d", positionX, positionY), 10, 25, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean handled = false;

        int xTouch;
        int yTouch;

        // get touch event coordinates and make transparent circle from it
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);

                positionX = xTouch;
                positionY = yTouch;

                invalidate();
                handled = true;

                if (mListener != null)
                    mListener.onPositionChangeEvent(xTouch, yTouch);

                break;

            case MotionEvent.ACTION_MOVE:
                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);

                positionX = xTouch;
                positionY = yTouch;

                invalidate();
                handled = true;

                if (mListener != null)
                    mListener.onPositionChangeEvent(xTouch, yTouch);

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
}
