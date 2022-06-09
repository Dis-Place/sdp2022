package com.github.displace.sdp2022.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

/**
 * util for current date & time retrieval
 */
object DateTimeUtil {
    private const val TIME_PATTERN  = "dd-MM-yyyy HH:mm:ss"
    private const val DATE_PATTERN  = "dd-MM-yyyy HH:mm:ss"

    /**
     * @return current date & time formatted according to pattern dd-MM-yyyy HH:mm:ss
     */
    fun currentTime() : String {
        return SimpleDateFormat(TIME_PATTERN, Locale.getDefault()).format(Calendar.getInstance().time)
    }

    /**
     * @return current date formatted according to pattern dd-MM-yyyy
     */
    @SuppressLint("SimpleDateFormat")
    fun currentDate(): String {
        return SimpleDateFormat(DATE_PATTERN).format(Date())
    }


}