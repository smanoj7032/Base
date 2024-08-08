package com.manoj.baseproject.core.network.helper

import android.net.Uri
import com.manoj.baseproject.core.utils.picker.ItemType

object SystemVariables {
    var isInternetConnected = false
    var onNetworkChange: (NetworkMonitor.NetworkState) -> Unit = {}
    var onPickerClosed: (ItemType, Uri?, List<Uri>?) -> Unit = { _, _, _ -> }
}