package com.github.displace.sdp2022.gameVersus

import android.app.Activity
import com.github.displace.sdp2022.RealTimeDatabase

class GameVersusDBHandler(private val db: RealTimeDatabase, private val gameInstanceName: String, private val mainplayerId: String, private val otherPlayersIds: List<String>,val activity: Activity) {

}