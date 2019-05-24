package com.android.deskclock.alarms;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;

public class CheckboxSelf extends CompoundButton {
    public CheckboxSelf(Context context) {
        this(context, null);
    }

    public CheckboxSelf(Context context, AttributeSet attrs) {
        this(context, attrs, com.android.internal.R.attr.checkboxStyle);
    }

    public CheckboxSelf(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CheckboxSelf(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return CheckboxSelf.class.getName();
    }
}
