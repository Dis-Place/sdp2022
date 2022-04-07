package com.github.displace.sdp2022.util

import android.graphics.Color
import android.graphics.Paint
import kotlin.random.Random

/**
 * generates random Paints
 * @author LeoLgdr
 */
object RandomColor {

    /**
     * get a random Paint
     */
    fun next(): Int {
        val paint = Paint()
        paint.setARGB(paint.alpha,nextComponentRGB(),nextComponentRGB(),nextComponentRGB())
        return paint.color
    }

    private fun nextComponentRGB() : Int {
        return Random.nextInt(UPPER_BOUND_RGB_COMPONENT_VALUE)
    }

    private const val UPPER_BOUND_RGB_COMPONENT_VALUE = 256
}