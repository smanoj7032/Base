package com.manoj.baseproject.core.common.basedialogs.model

import com.manoj.baseproject.core.common.basedialogs.AbstractDialog

data class DialogButton(
    val title: String,
    val icon: Int,
    val onClickListener: AbstractDialog.OnClickListener
)
