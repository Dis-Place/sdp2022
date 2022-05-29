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
    private val lobbyId = id
    private val lobbyMax = max_p
    private val lobbyCount = 1L
    private val lobbyLaunch = false
    private val lobbyLeader = leader.uid
    private val lobbyPlayers = listOf(leader)
    private val lobbyPosition : GeoPoint = gp


    fun toMap() : Map<String,*>{
        val gpMap  = HashMap<String,Any>()
        gpMap["latitude"] = lobbyPosition.latitude
        gpMap["longitude"] = lobbyPosition.longitude

        val map = HashMap<String,Any>()
        map["lobbyId"] = lobbyId
        map["lobbyMax"] = lobbyMax
        map["lobbyCount"] = lobbyCount
        map["lobbyLaunch"] = lobbyLaunch
        map["lobbyLeader"] = lobbyLeader
        map["lobbyPlayers"] = lobbyPlayers.map{pu -> pu.toMap()}
        map["lobbyPosition"] = gpMap.toMap()
        return map.toMap()
    }



}