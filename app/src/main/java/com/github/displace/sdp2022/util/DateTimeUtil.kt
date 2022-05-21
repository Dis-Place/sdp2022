package com.github.displace.sdp2022.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * util for current date & time retrieval
 * @author LeoLgdr
 */
object DateTimeUtil {
    private const val PATTERN = "dd-MM-yyyy HH:mm:ss"

    /**
     * @return current date & time formatted according to pattern dd-MM-yyyy HH:mm:ss
     */
    fun currentTime(): String {
        return SimpleDateFormat(PATTERN, Locale.getDefault()).format(Calendar.getInstance().time)
    }
}