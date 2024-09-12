package com.manoj.base.core.common.basedialogs.model

import com.manoj.base.core.common.basedialogs.AbstractDialog

data class DialogButton(
    val title: String,
    val icon: Int?,
    val onClickListener: AbstractDialog.OnClickListener
)
