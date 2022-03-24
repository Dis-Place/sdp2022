package com.github.displace.sdp2022.model

import android.annotation.TargetApi
import android.os.Build
import com.github.displace.sdp2022.gameComponents.Coordinates

//com.github.displace.sdp2022.model of a gameversus

class GameVersus(
    val goal: Coordinates,
    val Photo: Int,
    val nbTry: Int,
    val nbTryMax: Int,
    val threshold: Double
) : Game(goal) {

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    override fun verify(test: Coordinates): Boolean {
        return goal.pos.first + threshold * goal.pos.first > test.pos.first &&
                goal.pos.first - threshold * goal.pos.first < test.pos.first &&
                goal.pos.second + threshold * goal.pos.second > test.pos.second &&
                goal.pos.second - threshold * goal.pos.second < test.pos.second

    }
}