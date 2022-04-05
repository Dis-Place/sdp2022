package com.github.displace.sdp2022.gameComponents

import android.annotation.TargetApi
import android.os.Build
import android.util.Pair

//com.github.displace.sdp2022.model of a point
@TargetApi(Build.VERSION_CODES.ECLAIR)
class Point(x: Double, y: Double) : Coordinates {
    override val pos: Pair<Double, Double> = Pair(x, y)
}