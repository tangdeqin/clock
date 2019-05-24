package com.android.deskclock.ringtone;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.deskclock.R;

import com.android.deskclock.ItemAdapter;

/**
 * Created by user on 19-5-21.
 */

public class HeaderLineViewHolder extends ItemAdapter.ItemViewHolder<HeaderLineHolder> {

    static final int VIEW_TYPE_ITEM_HEADER_LINE = R.layout.ringtone_item_header_line;

    private final View mItemHeaderLine;

    private HeaderLineViewHolder(View itemView) {
        super(itemView);
        mItemHeaderLine = (View) itemView.findViewById(R.id.ringtone_item_header_line);
    }

    @Override
    protected void onBindItemView(HeaderLineHolder itemHolder) {

    }

    public static class Factory implements ItemAdapter.ItemViewHolder.Factory {

        private final LayoutInflater mInflater;

        Factory(LayoutInflater inflater) {
            mInflater = inflater;
        }

        @Override
        public ItemAdapter.ItemViewHolder<?> createViewHolder(ViewGroup parent, int viewType) {
            return new HeaderLineViewHolder(mInflater.inflate(viewType, parent, false));
        }
    }
}