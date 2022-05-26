package com.github.displace.sdp2022.matchMaking

import com.github.displace.sdp2022.users.PartialUser
import org.osmdroid.util.GeoPoint

/**
 * A lobby : used exclusively to insert the data into the DB since it cannot be retrieved in such form later
 *
 * @param id : id of the new lobby
 * @param max_p : the maximum amount of players in the lobby
 * @param leader : the id of the leader of the lobby
 * @param gp : the gps position of the lobby
 */
class Lobby(id: String, max_p: Long, leader: PartialUser, gp : GeoPoint) {
    val lobbyId = id
    val lobbyMax = max_p
    var lobbyCount = 1
    var lobbyLaunch = false
    var lobbyLeader = leader.uid
    var lobbyPlayers = mutableListOf(leader)    //PARTIAL USER : should be a list of partial users
    var lobbyPosition : GeoPoint = gp
}