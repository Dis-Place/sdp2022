package com.github.displace.sdp2022.gameComponents

import android.annotation.TargetApi
import android.os.Build
import android.util.Pair

//com.github.displace.sdp2022.model of a player

@TargetApi(Build.VERSION_CODES.ECLAIR)
class Player(val x: Double, val y: Double, val id: Int) : Coordinates {
    val uid = id
    override val pos: Pair<Double, Double> = Pair(x, y)
}