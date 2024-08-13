package com.manoj.baseproject.core.utils.picker

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaModel(
    val type: MediaType,
    val itemLabel: String = "",
    val itemIcon: Int = 0,
    val hasBackground: Boolean = true,
    val itemBackgroundColor: Int = 0
) : Parcelable

enum class MediaType {
    TAKE_PICTURE,
    CHOOSE_IMAGE,
    RECORD_VIDEO,
    CHOOSE_VIDEO,
    SELECT_FILES
}