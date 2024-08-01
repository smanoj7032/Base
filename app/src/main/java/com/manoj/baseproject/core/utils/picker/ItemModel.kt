package com.manoj.baseproject.core.utils.picker

import android.os.Parcelable
import androidx.annotation.LongDef
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemModel(
    val type: ItemType,
    val itemLabel: String = "",
    val itemIcon: Int = 0,
    val hasBackground: Boolean = true,
    val itemBackgroundColor: Int = 0
) : Parcelable

enum class ItemType {
    ITEM_CAMERA,
    ITEM_GALLERY,
    ITEM_VIDEO,
    ITEM_VIDEO_GALLERY,
    ITEM_FILES
}