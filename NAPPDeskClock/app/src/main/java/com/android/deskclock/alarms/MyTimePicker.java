package com.android.deskclock.alarms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import com.android.deskclock.R;
import com.android.deskclock.VerticalTextPicker;
import com.android.deskclock.VerticalTextPicker.OnChangedListener;

public class MyTimePicker extends FrameLayout {

	private int mCurrentHour = 0;

	private int mCurrentMinute = 0;

	private final VerticalTextPicker mHourPicker;

	private final VerticalTextPicker mMinutePicker;

	private VerticalTextPicker mAmPmPicker;

	private OnTimeChangedListener mOnTimeChangedListener;

	private boolean mIs24HourView;

	private String str[];

	public interface OnTimeChangedListener {
		void onTimeChanged(MyTimePicker myTimePicker, int hour, int minute);
	}

	public MyTimePicker(Context context) {
		this(context, null);
	}

	public MyTimePicker(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@SuppressLint("NewApi")
	public MyTimePicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setIs24HourView(DateFormat.is24HourFormat(context));
		if (mIs24HourView) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inflater.inflate(R.layout.alarm_24timer_picker, this, true);
			mHourPicker = (VerticalTextPicker) findViewById(R.id.timerpicker_hour);
			mHourPicker.setRange(0, 23);
			mHourPicker.setOnChangeListener(mChangedListener);

			mMinutePicker = (VerticalTextPicker) findViewById(R.id.timerpicker_minute);
			mMinutePicker.setRange(0, 59);
			mMinutePicker.setOnChangeListener(mChangedListener);

		} else {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inflater.inflate(R.layout.alarm_12timer_picker, this, true);
			mHourPicker = (VerticalTextPicker) findViewById(R.id.timerpicker_hour);
			mHourPicker.setRange(1, 12);
			mHourPicker.setOnChangeListener(mChangedListener);

			mMinutePicker = (VerticalTextPicker) findViewById(R.id.timerpicker_minute);
			mMinutePicker.setRange(0, 59);
			mMinutePicker.setOnChangeListener(mChangedListener);
			mAmPmPicker = (VerticalTextPicker) findViewById(R.id.amPmpicker_timer);

			String amStr = "AM";//context.getResources().getString(R.string.time_am);
			String pmStr = "PM";//context.getResources().getString(R.string.time_pm);

			str = new String[]{amStr,pmStr};
			mAmPmPicker.setWrapAround(false);
			mAmPmPicker.setItems(str);
			mAmPmPicker.setOnChangeListener(mChangedListener);

		}
		if (!isEnabled()) {
			setEnabled(false);
		}
	}


	protected void setIs24HourView(boolean is24HourView) {
		mIs24HourView = is24HourView;
	}
    public boolean is24HourView(){
    	return mIs24HourView;
    }
	@Override
	public void onRestoreInstanceState(Parcelable state) {
		super.onRestoreInstanceState(state);
	}

	@Override
	public Parcelable onSaveInstanceState() {
		return super.onSaveInstanceState();
	}

	private OnChangedListener mChangedListener = new OnChangedListener() {
		public void onChanged(VerticalTextPicker spinner, int oldPos,
				int newPos, String[] items) {
		    if (is24HourView()) {
		        mCurrentHour = Integer.parseInt(mHourPicker.getCurrent());
            } else {
                if (Integer.parseInt(mHourPicker.getCurrent()) != 12) {
                    if (mAmPmPicker.getCurrent().equals(str[1])) {
                        mCurrentHour = Integer.parseInt(mHourPicker.getCurrent()) + 12;
                    } else {
                        mCurrentHour = Integer.parseInt(mHourPicker.getCurrent());
                    }
                } else {
                    if (mAmPmPicker.getCurrent().equals(str[1])) {
                        mCurrentHour = Integer.parseInt(mHourPicker.getCurrent());
                    } else {
                        mCurrentHour = Integer.parseInt(mHourPicker.getCurrent()) + 12;
                    }
                }
            }
			mCurrentMinute = Integer.parseInt(mMinutePicker.getCurrent());
			onTimeChanged();
		}
	};


	public Integer getCurrentHour() {
		return mCurrentHour;
	}

	public Integer getCurrentMinute() {
		return mCurrentMinute;
	}

	public void setCurrentHour(Integer currentHour) {
		mCurrentHour = currentHour;

        if (!is24HourView()) {
            if (currentHour == 24 || currentHour == 0) {
                currentHour = 12;
                mAmPmPicker.setCurrent(str[0]);
            } else if (currentHour == 12){
                mAmPmPicker.setCurrent(str[1]);
            } else if (currentHour > 12) {
                currentHour = currentHour - 12;
                mAmPmPicker.setCurrent(str[1]);
            } else {
                mAmPmPicker.setCurrent(str[0]);
            }
        }
		mHourPicker.setCurrent(pad(currentHour));
	}

	public void setCurrentMinute(Integer currentMinute) {
		mCurrentMinute = currentMinute;
		mMinutePicker.setCurrent(pad(currentMinute));
	}



	private void onTimeChanged() {
		if (mOnTimeChangedListener != null) {
			mOnTimeChangedListener.onTimeChanged(this, getCurrentHour(),
					getCurrentMinute());
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		mMinutePicker.setEnabled(enabled);
		mHourPicker.setEnabled(enabled);
		mAmPmPicker.setEnabled(enabled);
	}

	private String pad(int i) {
		return i < 10 ? "0" + i : String.valueOf(i);
	}

}
