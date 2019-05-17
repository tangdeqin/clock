package com.android.deskclock.alarms;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.deskclock.R;


public class DeleteAndBackDialogFragment  extends DialogFragment implements View.OnClickListener{

    private static final String TAG = "DeleteAndBackrDialogFragment";
    private static Bundle mBundle;
    private TextView mBackAndDeleteOkTv;
    private TextView mBackAndDeleteKoTv;
    private TextView mBackAndDeleteTitleTv;

    public static DeleteAndBackDialogFragment newInstance(String tag) {
        final Bundle args = new Bundle();
        args.putString(TAG, tag);
        mBundle = args;
        final DeleteAndBackDialogFragment frag = new DeleteAndBackDialogFragment();
        frag.setArguments(args);
        return frag;
    }

    public static void show(FragmentManager manager, DeleteAndBackDialogFragment fragment) {
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
    }
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view=inflater.inflate(R.layout.alarm_edit_delete_back,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBackAndDeleteOkTv = view.findViewById(R.id.delete_back_ok);
        mBackAndDeleteKoTv = view.findViewById(R.id.delete_back_ko);
        mBackAndDeleteTitleTv = view.findViewById(R.id.delete_back_tile);
        if (mBundle.getString(TAG).equals("back")){
            mBackAndDeleteTitleTv.setText(getResources().getString(R.string.alarm_edit_back_title));
        }else {
            mBackAndDeleteTitleTv.setText(getResources().getString(R.string.alarm_edit_delete_title));
        }
        mBackAndDeleteKoTv.setOnClickListener(this);
        mBackAndDeleteOkTv.setOnClickListener(this);
    }

    public void setDialogSize() {
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
        setDialogSize();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.delete_back_ko:
                setKoButton();
                dismiss();
                break;
            case R.id.delete_back_ok:
                setOkButton();
                dismiss();
                break;
            default:break;
        }
    }

    private void setOkButton() {
        if(mBundle.getString(TAG).equals("back")){
            ((AlarmBackDialogHandler)getActivity()).onSetStoreOk();
        }else {
            ((AlarmDeleteDialogHandler)getActivity()).onSetDeleteOk();
        }
    }
    private void setKoButton() {
        if(mBundle.getString(TAG).equals("back")){
            ((AlarmBackDialogHandler)getActivity()).onSetStoreKo();
        }else {
            ((AlarmDeleteDialogHandler)getActivity()).onSetDeleteKo();
        }
    }


    public interface AlarmBackDialogHandler {
         void onSetStoreOk();
         void onSetStoreKo();
    }

    public interface AlarmDeleteDialogHandler {
        void onSetDeleteOk();
        void onSetDeleteKo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
