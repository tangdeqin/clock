package com.android.deskclock.ringtone;

import android.net.Uri;
import android.support.annotation.StringRes;

import com.android.deskclock.ItemAdapter;

import static android.support.v7.widget.RecyclerView.NO_ID;

/**
 * Created by user on 19-5-21.
 */

public class HeaderLineHolder  extends ItemAdapter.ItemHolder<Uri> {
    HeaderLineHolder() {
        super(null, NO_ID);

    }

    @Override
    public int getItemViewType() {
        return HeaderLineViewHolder.VIEW_TYPE_ITEM_HEADER_LINE;
    }
}