package com.manoj.baseproject.utils.picker

import android.net.Uri

interface MediaPickerListener {
    fun onMediaPicked(uri: Uri?)
    fun onMultipleMediaPicked(uri: List<Uri>?)
}