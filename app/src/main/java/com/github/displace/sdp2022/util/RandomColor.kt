package com.github.displace.sdp2022.util

import android.graphics.Paint
import kotlin.random.Random

/**
 * generates random Paints
 * @author LeoLgdr
 */
object RandomColor {

    /**
     * Get a random Paint color
     */
    fun next(): Int {
        val paint = Paint()

        // generate random component values
        val random_R = nextComponentRGB()
        val random_G = nextComponentRGB()
        val random_B = nextComponentRGB()

        // set the color
        paint.setARGB(paint.alpha, random_R, random_G, random_B)
        return paint.color
    }

    /**
     * Will return a random RGB value between 0 and 255
     */
    private fun nextComponentRGB(): Int {
        return Random.nextInt(UPPER_BOUND_RGB_COMPONENT_VALUE)
    }

    private const val UPPER_BOUND_RGB_COMPONENT_VALUE = 256
}