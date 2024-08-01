package com.manoj.baseproject.core.common.motiontoast

enum class MotionToastStyle {
    SUCCESS, ERROR, WARNING, INFO, DELETE, NO_INTERNET;

    fun getName(): String {
        if (this.name.contains("_")) {
            return this.name.replaceFirst("_", " ")
        }
        return this.name
    }
}