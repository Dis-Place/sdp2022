package com.github.displace.sdp2022.news

import android.graphics.Bitmap

/**
 * Represents a piece of News
 *
 * It contains a title, a description , a date and an image.
 */
class News(val title: String, val description: String, val date: String, var image: Bitmap?)