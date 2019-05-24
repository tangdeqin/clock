package com.android.deskclock.alarms;

import android.util.AttributeSet;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.util.Calendar;
import java.util.TimeZone;

import com.android.deskclock.R;

public class MyAnalogClock extends View{
    Bitmap mBmpDial;
    Bitmap mBmpHour;
    Bitmap mBmpMinute;
    Bitmap mBmpSecond;
    BitmapDrawable bmdHour;
    BitmapDrawable bmdMinute;
    BitmapDrawable bmdSecond;
    BitmapDrawable bmdDial;
    Paint mPaint;
    Handler tickHandler;
    int mWidth;
    int mHeigh;
    int mTempWidth;
    int mTempHeigh;
    int centerX;
    int centerY;
    private String sTimeZoneString;
    public MyAnalogClock(Context context,AttributeSet attr)
    {
        this(context,"GMT+8：00");
    }
    public MyAnalogClock(Context context, String sTime_Zone) {
        super(context);
        sTimeZoneString = sTime_Zone;
        mBmpHour = BitmapFactory.decodeResource(getResources(),
                R.drawable.alarm_clock_hourhand);
        bmdHour = new BitmapDrawable(mBmpHour);
        mBmpMinute = BitmapFactory.decodeResource(getResources(),
                R.drawable.alarm_clock_minutehand);
        bmdMinute = new BitmapDrawable(mBmpMinute);
        mBmpSecond = BitmapFactory.decodeResource(getResources(),
                R.drawable.alarm_clock_secondhand);
        bmdSecond = new BitmapDrawable(mBmpSecond);
        mBmpDial = BitmapFactory.decodeResource(getResources(),
                R.drawable.alarm_clockdial_bg);
        bmdDial = new BitmapDrawable(mBmpDial);
        mWidth = mBmpDial.getWidth();
        mHeigh = mBmpDial.getHeight();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        centerX = width / 2;
        centerY = mHeigh/2+30;
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        run();
    }
    public void run() {
        tickHandler = new Handler();
        tickHandler.post(tickRunnable);
    }
    private Runnable tickRunnable = new Runnable() {
        public void run() {
            postInvalidate();
            tickHandler.postDelayed(tickRunnable, 1000);
        }
    };
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Calendar cal = Calendar.getInstance(TimeZone
                .getTimeZone(sTimeZoneString));
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        float minuteRotate = minute * 6.0f;
        float secondRotate = second * 6.0f - 118.5f;
        float hourRotate = (hour % 12) * 30 + 30 * minuteRotate/ 360.0f -210.0f;
        boolean scaled = false;
        if (centerX*2 < mWidth || centerY*2 < mHeigh) {
            scaled = true;
            float scale = Math.min((float) centerX*2 / (float) mWidth,
                    (float) centerX*2 / (float) mHeigh);
            canvas.save();
            canvas.scale(scale, scale, centerX, centerY);
        }
        bmdDial.setBounds(centerX - (mWidth / 2), centerY - (mHeigh / 2),
                centerX + (mWidth / 2), centerY + (mHeigh / 2));
        bmdDial.draw(canvas);
        mTempWidth = bmdHour.getIntrinsicWidth()*2;
        mTempHeigh = bmdHour.getIntrinsicHeight()*2;
        canvas.save();
        canvas.rotate(hourRotate, centerX, centerY);
        bmdHour.setBounds(centerX - (mTempWidth / 2), centerY
                - (mTempHeigh / 2), centerX + (mTempWidth / 2), centerY
                + (mTempHeigh / 2));
        bmdHour.draw(canvas);
        canvas.restore();
        mTempWidth = bmdMinute.getIntrinsicWidth()*2;
        mTempHeigh = bmdMinute.getIntrinsicHeight()*2;
        canvas.save();
        canvas.rotate(minuteRotate, centerX, centerY);
        bmdMinute.setBounds(centerX - (mTempWidth / 2), centerY
                - (mTempHeigh / 2), centerX + (mTempWidth / 2), centerY
                + (mTempHeigh / 2));
        bmdMinute.draw(canvas);
        canvas.restore();
        mTempWidth = bmdSecond.getIntrinsicWidth()*2;
        mTempHeigh = bmdSecond.getIntrinsicHeight()*2;
        canvas.rotate(secondRotate, centerX, centerY);
        bmdSecond.setBounds(centerX - (mTempWidth / 2), centerY
                - (mTempHeigh / 2), centerX + (mTempWidth / 2), centerY
                + (mTempHeigh / 2));
        bmdSecond.draw(canvas);
        if (scaled) {
            canvas.restore();
        }
    }
}
