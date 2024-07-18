package com.manoj.baseproject.utils.picker;

import android.net.Uri;

import java.util.List;

public interface OnPickerCloseListener {
    void onPickerClosed(ItemType type, Uri uri, List<Uri> uris);
}
 