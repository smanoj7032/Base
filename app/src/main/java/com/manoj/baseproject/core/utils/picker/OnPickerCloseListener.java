package com.manoj.baseproject.core.utils.picker;

import android.net.Uri;

import java.util.List;

public interface OnPickerCloseListener {
    void onPickerClosed(ItemType type, Uri uri, List<Uri> uris);
}
 