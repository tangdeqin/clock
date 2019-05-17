/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.deskclock;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.deskclock.data.DataModel;
import com.android.deskclock.data.Timer;
import com.android.deskclock.provider.Alarm;
import com.android.deskclock.R;

/**
 * DialogFragment to edit label.
 */
public class LabelDialogFragment extends DialogFragment implements View.OnClickListener {

    /**
     * The tag that identifies instances of LabelDialogFragment in the fragment manager.
     */
    private static final String TAG = "label_dialog";

    private static final String ARG_LABEL = "arg_label";
    private static final String ARG_ALARM = "arg_alarm";
    private static final String ARG_TIMER_ID = "arg_timer_id";
    private static final String ARG_TAG = "arg_tag";

    private EditText mLabelEditText;
    private Alarm mAlarm;
    private int mTimerId;
    private String mTag;
    private TextView mCancelEditlabelTv;
    private TextView mOkEditlabelTv;


    public static LabelDialogFragment newInstance(Alarm alarm, String label, String tag) {
        final Bundle args = new Bundle();
        args.putString(ARG_LABEL, label);
        args.putParcelable(ARG_ALARM, alarm);
        args.putString(ARG_TAG, tag);

        final LabelDialogFragment frag = new LabelDialogFragment();
        frag.setArguments(args);
        return frag;
    }

    public static LabelDialogFragment newInstance(Timer timer) {
        final Bundle args = new Bundle();
        args.putString(ARG_LABEL, timer.getLabel());
        args.putInt(ARG_TIMER_ID, timer.getId());

        final LabelDialogFragment frag = new LabelDialogFragment();
        frag.setArguments(args);
        return frag;
    }

    /**
     * Replaces any existing LabelDialogFragment with the given {@code fragment}.
     */
    public static void show(FragmentManager manager, LabelDialogFragment fragment) {
        if (manager == null || manager.isDestroyed()) {
            return;
        }

        // Finish any outstanding fragment work.
        manager.executePendingTransactions();

        final FragmentTransaction tx = manager.beginTransaction();

        // Remove existing instance of LabelDialogFragment if necessary.
        final Fragment existing = manager.findFragmentByTag(TAG);
        if (existing != null) {
            tx.remove(existing);
        }
        tx.addToBackStack(null);

        fragment.show(tx, TAG);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // As long as the label box exists, save its state.
        if (mLabelEditText != null) {
            outState.putString(ARG_LABEL, mLabelEditText.getText().toString());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view=inflater.inflate(R.layout.alarm_edit_label,container,false);
        return view;
    }
    public void setEditLabelSize() {
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = width/9*8;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setAttributes(layoutParams);
    }
    @Override
    public void onStart(){
        super.onStart();
        setEditLabelSize();
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                showSoftInputFromWindow(mLabelEditText);
            }
        };
        handler.postDelayed(r, 100);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLabelEditText = view.findViewById(R.id.label_edit);
        mCancelEditlabelTv = view.findViewById(R.id.label_edit_ko);
        mOkEditlabelTv = view.findViewById(R.id.label_edit_ok);
        mCancelEditlabelTv.setOnClickListener(this);
        mOkEditlabelTv.setOnClickListener(this);

        final Bundle args = getArguments() == null ? Bundle.EMPTY : getArguments();
        mAlarm = args.getParcelable(ARG_ALARM);
        mTimerId = args.getInt(ARG_TIMER_ID, -1);
        mTag = args.getString(ARG_TAG);

        String label = args.getString(ARG_LABEL);
        if (savedInstanceState != null) {
            label = savedInstanceState.getString(ARG_LABEL, label);
        }

        mLabelEditText.setOnEditorActionListener(new ImeDoneListener());
        mLabelEditText.addTextChangedListener(new TextChangeListener());
        mLabelEditText.setSingleLine();
        mLabelEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        mLabelEditText.setText(label);
        mLabelEditText.selectAll();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.label_edit_ko:
                dismiss();
                break;
            case R.id.label_edit_ok:
                setLabel();
                dismiss();
                break;
            default:break;
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Stop callbacks from the IME since there is no view to process them.
        mLabelEditText.setOnEditorActionListener(null);
    }

    /**
     * Sets the new label into the timer or alarm.
     */
    private void setLabel() {
        String label = mLabelEditText.getText().toString();
        if (label.trim().isEmpty()) {
            // Don't allow user to input label with only whitespace.
            label = "";
        }

        if (mAlarm != null) {
            if(getActivity().getClass().getName().equals("com.android.deskclock.DeskClock")){
                ((AlarmLabelDialogHandler) getActivity()).onDialogLabelSet(mAlarm, label, mTag);
            }else {
                ((AlarmLabelDialogHandlerT)getActivity()).onDialogLabelSet(mAlarm,label);
            }
        } else if (mTimerId >= 0) {
            final Timer timer = DataModel.getDataModel().getTimer(mTimerId);
            if (timer != null) {
                DataModel.getDataModel().setTimerLabel(timer, label);
            }
        }
    }


    public interface AlarmLabelDialogHandler {
        void onDialogLabelSet(Alarm alarm, String label, String tag);
    }

    public interface AlarmLabelDialogHandlerT {
        void onDialogLabelSet(Alarm alarm, String label);
    }

    /**
     * Alters the UI to indicate when input is valid or invalid.
     */
    private class TextChangeListener implements TextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mLabelEditText.setActivated(!TextUtils.isEmpty(s));
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    /**
     * Handles completing the label edit from the IME keyboard.
     */
    private class ImeDoneListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                setLabel();
                dismissAllowingStateLoss();
                return true;
            }
            return false;
        }
    }

    public void showSoftInputFromWindow(EditText editText){
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, 0);
    }
}