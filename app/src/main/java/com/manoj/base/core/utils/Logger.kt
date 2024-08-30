package com.manoj.base.core.utils

import android.util.Log

object Logger {
    private var TAG = "BASE"
    var isDebug = true
    fun setTAG(tag: String) {
        TAG = tag
    }

    fun d(msg: String?) {
        if (isDebug) Log.d(TAG, msg!!)
    }

    fun d(tag: String?, msg: String?) {
        if (isDebug) Log.d(tag, msg!!)
    }

    fun d(msg: Int) {
        if (isDebug) Log.d(TAG, msg.toString() + "")
    }

    fun d(tag: String?, msg: Int) {
        if (isDebug) Log.d(tag, msg.toString() + "")
    }

    fun e(msg: String?) {
        if (isDebug) Log.e(TAG, msg!!)
    }

    fun e(tag: String?, msg: String?) {
        if (isDebug) Log.e(tag, msg!!)
    }

    fun e(msg: Int) {
        if (isDebug) Log.e(TAG, msg.toString() + "")
    }

    fun e(tag: String?, msg: Int) {
        if (isDebug) Log.e(tag, msg.toString() + "")
    }

    fun i(msg: String?) {
        if (isDebug) Log.i(TAG, msg!!)
    }

    fun i(tag: String?, msg: String?) {
        if (isDebug) Log.i(tag, msg!!)
    }

    fun i(msg: Int) {
        if (isDebug) Log.i(TAG, msg.toString() + "")
    }

    fun i(tag: String?, msg: Int) {
        if (isDebug) Log.i(tag, msg.toString() + "")
    }

    fun v(tag: String?, message: String?) {
        if (isDebug) {
            Log.v(tag, message!!)
        }
    }
}