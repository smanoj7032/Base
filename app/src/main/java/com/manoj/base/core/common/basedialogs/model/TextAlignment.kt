package com.manoj.base.core.common.basedialogs.model

import android.view.View

enum class TextAlignment(val alignment: Int) {
    START(View.TEXT_ALIGNMENT_TEXT_START),
    END(View.TEXT_ALIGNMENT_TEXT_END),
    CENTER(View.TEXT_ALIGNMENT_CENTER)
}
