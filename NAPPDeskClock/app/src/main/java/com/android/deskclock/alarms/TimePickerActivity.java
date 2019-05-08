package com.android.deskclock.alarms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.ImageView;

import com.android.deskclock.AlarmClockFragment;
import com.android.deskclock.BaseActivity;
import com.android.deskclock.R;
import com.android.deskclock.provider.Alarm;

import java.util.Calendar;

public class TimePickerActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "TimePickerActivity";
    private static final String ARG_HOUR = TAG + "_hour";
    private static final String ARG_MINUTE = TAG + "_minute";
    private  int[] mTimeFormIntent;

    private MyTimePicker timePicker;
    private ImageView mAddAlarmIv;
    private ImageView mCancelEditAlarmIv;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_timepick);
        timePicker = findViewById(R.id.timerpicker);
        mAddAlarmIv = findViewById(R.id.addimagview);
        mCancelEditAlarmIv = findViewById(R.id.backimagview);

        final Calendar now = Calendar.getInstance();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null){
            mTimeFormIntent = bundle.getIntArray(TAG);
        }
        final int hour;
        final int minute;
        if(mTimeFormIntent!=null && mTimeFormIntent.length==2){
            hour = mTimeFormIntent[0];
            minute = mTimeFormIntent[1];
        }
        else{
            hour = savedInstanceState!=null?savedInstanceState.getInt(ARG_HOUR, now.get(Calendar.HOUR_OF_DAY))
                    :now.get(Calendar.HOUR_OF_DAY);
            minute = savedInstanceState!=null?savedInstanceState.getInt(ARG_MINUTE, now.get(Calendar.MINUTE))
                    :now.get(Calendar.MINUTE);
        }
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);
        timePicker.setIs24HourView(DateFormat.is24HourFormat(this));

        mAddAlarmIv.setOnClickListener(this);
        mCancelEditAlarmIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.addimagview:
                AlarmClockFragment.onTimeSet(timePicker.getCurrentHour(),timePicker.getCurrentMinute());
                finish();
                break;
            case R.id.backimagview:
                finish();
            break;
            default:break;
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_HOUR,timePicker.getCurrentHour());
        outState.putInt(ARG_MINUTE,timePicker.getCurrentMinute());
    }

    public static Intent createTimePickActivityIntent(Context context,Alarm alarm) {
        Bundle data = new Bundle();
        int[] time = {alarm.hour,alarm.minutes};
        if (time!=null && time.length==2){
            data.putIntArray(TAG,time);
        }
        return new Intent(context, TimePickerActivity.class).putExtras(data);
    }
}
