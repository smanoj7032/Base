package com.manoj.base.core.common.basedialogs.model

import android.text.Spanned

abstract class DialogMessage<T : CharSequence>(val textAlignment: TextAlignment) {

    abstract fun getText(): T

    companion object {
        fun spanned(text: Spanned, alignment: TextAlignment): SpannedMessage {
            return SpannedMessage(text, alignment)
        }

        fun text(text: String, alignment: TextAlignment): TextMessage {
            return TextMessage(text, alignment)
        }
    }

    class SpannedMessage(
        private val text: Spanned,
        textAlignment: TextAlignment
    ) : DialogMessage<Spanned>(textAlignment) {

        override fun getText(): Spanned {
            return text
        }
    }

    class TextMessage(
        private val text: String,
        textAlignment: TextAlignment
    ) : DialogMessage<String>(textAlignment) {

        override fun getText(): String {
            return text
        }
    }
}
