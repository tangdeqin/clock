package com.android.deskclock.alarms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.deskclock.AlarmClockFragment;
import com.android.deskclock.BaseActivity;
import com.android.deskclock.LabelDialogFragment;
import com.android.deskclock.R;
import com.android.deskclock.Utils;
import com.android.deskclock.data.DataModel;
import com.android.deskclock.data.Weekdays;
import com.android.deskclock.provider.Alarm;
import com.android.deskclock.ringtone.RingtonePickerActivity;
import com.android.deskclock.uidata.UiDataModel;

import java.util.Calendar;
import java.util.List;

public class TimePickerActivity extends BaseActivity implements View.OnClickListener,
              LabelDialogFragment.AlarmLabelDialogHandlerT{
    private static final String TAG = "TimePickerActivity";
    private static final String ARG_HOUR = TAG + "_hour";
    private static final String ARG_MINUTE = TAG + "_minute";

    private static Alarm alarm;
    private int hour;
    private int minute;
    private boolean mVibrateState;
    private String mLabelText;
    private static Uri mRingtoneUri;
    private boolean mRepeatOrNot;

    private MyTimePicker timePicker;
    private SwitchCompat mRepeatOnOff;
    private ImageView mAddAlarmIv;
    private ImageView mCancelEditAlarmIv;
    private LinearLayout mRepeatDays;
    private  CompoundButton[] dayButtons = new CompoundButton[7];
    private static TextView mRingtoneTv;
    private SwitchCompat mVibtareSc;
    private TextView mLabelTv;
    private TextView mDeteleTv;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_timepick);
        timePicker = findViewById(R.id.timerpicker);
        mAddAlarmIv = findViewById(R.id.addimagview);
        mCancelEditAlarmIv = findViewById(R.id.backimagview);
        mRepeatOnOff = findViewById(R.id.repeat_onoff);
        mRepeatDays = findViewById(R.id.repeat_days);
        mRingtoneTv = findViewById(R.id.ringtone_choose);
        mVibtareSc = findViewById(R.id.Vibrate_onoff);
        mLabelTv = findViewById(R.id.edit_label);
        mDeteleTv = findViewById(R.id.deletetv);
        buildEachdayButton();
        final Drawable labelIcon = Utils.getVectorDrawable(this, R.drawable.ic_label);
        mLabelTv.setCompoundDrawablesRelativeWithIntrinsicBounds(labelIcon, null, null, null);

        final Calendar now = Calendar.getInstance();
        Intent intent = getIntent();
        alarm = intent.getParcelableExtra(TAG);
        if(alarm!=null){
            hour = alarm.hour;
            minute = alarm.minutes;
            mRepeatOnOff.setChecked(alarm.deleteAfterUse?false:true);
            mRepeatDays.setVisibility(mRepeatOnOff.isChecked()?View.VISIBLE:View.GONE);
            mRingtoneTv.setText(DataModel.getDataModel().getRingtoneTitle(alarm.alert));
            final boolean silent = Utils.RINGTONE_SILENT.equals(alarm.alert);
            final Drawable icon = Utils.getVectorDrawable(this,
                    silent ? R.drawable.ic_ringtone_silent : R.drawable.ic_ringtone);
            mRingtoneTv.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null);
            final Drawable deleteIcon = Utils.getVectorDrawable(this, R.drawable.ic_delete_small);
            mVibtareSc.setChecked(alarm.vibrate);
            mLabelTv.setText(alarm.label);
            mDeteleTv.setCompoundDrawablesRelativeWithIntrinsicBounds(deleteIcon, null, null, null);

        }
        else{
            alarm = new Alarm();
            hour = savedInstanceState!=null?savedInstanceState.getInt(ARG_HOUR, now.get(Calendar.HOUR_OF_DAY))
                    :now.get(Calendar.HOUR_OF_DAY);
            minute = savedInstanceState!=null?savedInstanceState.getInt(ARG_MINUTE, now.get(Calendar.MINUTE))
                    :now.get(Calendar.MINUTE);
            mRepeatOnOff.setChecked(false);
            mRepeatDays.setVisibility(View.GONE);
            Uri urlSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            mRingtoneTv.setText(DataModel.getDataModel().getRingtoneTitle(urlSound));
            Drawable icon = Utils.getVectorDrawable(this, R.drawable.ic_ringtone);
            mRingtoneTv.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null);
            alarm.alert = urlSound;
            mVibtareSc.setChecked(false);
            mLabelTv.setText("");
            mDeteleTv.setVisibility(View.INVISIBLE);
        }
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);
        timePicker.setIs24HourView(DateFormat.is24HourFormat(this));

        mAddAlarmIv.setOnClickListener(this);
        mCancelEditAlarmIv.setOnClickListener(this);
        mRepeatOnOff.setOnClickListener(this);
        mRepeatDays.setOnClickListener(this);
        mRingtoneTv.setOnClickListener(this);
        mVibtareSc.setOnClickListener(this);
        mLabelTv.setOnClickListener(this);
        mDeteleTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.addimagview:
                setAlarm();
                AlarmClockFragment.onTimeSet(alarm);
                finish();
                break;
            case R.id.backimagview:
                finish();
                break;
            case R.id.repeat_onoff:
                setRepeatOrNot();
                setRepeatDaysVisble();
                break;
            case R.id.repeat_days:
                setRepeatDaysVisble();
                break;
            case R.id.ringtone_choose:
                setRingtone();
                break;
            case R.id.Vibrate_onoff:
                setAlarmVibrationEnabled(mVibtareSc.isChecked());
                break;
            case R.id.edit_label:
                createLabelDilag();
                break;
            case R.id.deletetv:
                deleteAlarm();
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
        return new Intent(context, TimePickerActivity.class).putExtra(TAG,alarm);
    }

    public void createLabelDilag(){
        final LabelDialogFragment fragment =
                LabelDialogFragment.newInstance(alarm, alarm.label, TAG);
        LabelDialogFragment.show(this.getFragmentManager(), fragment);

    }
    public void setAlarmVibrationEnabled(boolean newState) {
        mVibrateState = newState ;
            if (newState) {
                final Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                if (v.hasVibrator()) {
                    v.vibrate(300);
                }
            }
    }
   public void setAlarm(){
       alarm.hour=timePicker.getCurrentHour();
       alarm.minutes = timePicker.getCurrentMinute();
       alarm.vibrate = mVibrateState;
       if (mLabelText!=null && mLabelText.trim().length()!=0){
           alarm.label = mLabelText;
       }else {
           alarm.label = "";
       }

   }
   public void setRepeatOrNot(){
       mRepeatOrNot = mRepeatOnOff.isChecked();
   }
   public void setLabel(Alarm alarm,String label){
       alarm.label = label;
       mLabelTv.setText(label);
       mLabelText = label;
   }
    @Override
    public void onDialogLabelSet(Alarm alarm, String label) {
        setLabel(alarm, label);
    }
    public void setRingtone(){
        final Intent intent =
                RingtonePickerActivity.createAlarmRingtonePickerIntent(this, alarm);
        startActivity(intent);
    }


    public static void setRingtonelUri(Uri uri){
        mRingtoneUri = uri;
        alarm.alert = mRingtoneUri;
        setRingtoneIcon();
        mRingtoneTv.setText(DataModel.getDataModel().getRingtoneTitle(uri));
    }

    public static void setRingtoneIcon(){
        boolean silent = Utils.RINGTONE_SILENT.equals(alarm.alert);
        Drawable icon = Utils.getVectorDrawable(mRingtoneTv.getContext(),
                silent ? R.drawable.ic_ringtone_silent : R.drawable.ic_ringtone);
        mRingtoneTv.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null);
    }
    public void deleteAlarm(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确定删除吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlarmClockFragment.deleteItem(alarm);
                        finish();
                    }
                }).setNegativeButton("放弃", null)
                .create().show();

    }

    public void setRepeatDaysVisble(){
        if(mRepeatOnOff.isChecked()){
            mRepeatDays.setVisibility(View.VISIBLE);
        }else{
            mRepeatDays.setVisibility(View.GONE);
        }
    }

    public void buildEachdayButton(){
        final LayoutInflater inflater = LayoutInflater.from(this);
        final List<Integer> weekdays = DataModel.getDataModel().getWeekdayOrder().getCalendarDays();
        for (int i = 0; i < 7; i++) {
            final View dayButtonFrame = inflater.inflate(R.layout.day_button, mRepeatDays,
                    false /* attachToRoot */);
            final CompoundButton dayButton =
                    (CompoundButton) dayButtonFrame.findViewById(R.id.day_button_box);
            final int weekday = weekdays.get(i);
            dayButton.setText(UiDataModel.getUiDataModel().getShortWeekday(weekday));
            dayButton.setContentDescription(UiDataModel.getUiDataModel().getLongWeekday(weekday));
            mRepeatDays.addView(dayButtonFrame);
            dayButtons[i] = dayButton;
        }
    }
}
