package com.manoj.baseproject.core.network.helper

import android.net.Uri
import com.manoj.baseproject.core.utils.picker.MediaType

object SystemVariables {
    /**
     * Holds the current state of internet connectivity.
     *
     * @property isInternetConnected A boolean value that indicates whether the device is currently connected to the internet.
     * Defaults to `false`.
     */
    var isInternetConnected = false

    /**
     * Callback function to be invoked when the network state changes.
     *
     * @property onNetworkChange A lambda function that receives a [NetworkMonitor.NetworkState] parameter.
     * This function will be called whenever there is a change in the network state.
     * Defaults to an empty function.
     */
    var onNetworkChange: (NetworkMonitor.NetworkState) -> Unit = {}

    /**
     * Callback function to be invoked when the media picker is closed.
     *
     * @property onPickerClosed A lambda function with three parameters:
     * - `ItemType`: The type of media item selected.
     * - `Uri?`: The URI of the single selected media item, or `null` if no single item is selected.
     * - `List<Uri>?`: A list of URIs for multiple selected media items, or `null` if no multiple items are selected.
     * Defaults to an empty function.
     */
    var onPickerClosed: (MediaType, Uri?, List<Uri>?) -> Unit = { _, _, _ -> }

}