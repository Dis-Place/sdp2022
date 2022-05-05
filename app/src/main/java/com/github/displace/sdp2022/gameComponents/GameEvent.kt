package com.github.displace.sdp2022.gameComponents

//definition of the possible event during a gameversus

sealed class GameEvent {
    data class OnUpdate(val PlayerId: String, val goal: Coordinates) :
        GameEvent()

    data class OnPointSelected(val PlayerId: String, val test: Coordinates) :
        GameEvent() //Try of one of the player

    data class OnStart(val Goal: Coordinates, val PlayerId: String, val gid: String, val other: String, val nbPlayer: Long) :
        GameEvent()

    // Goal represent the coordinate that one of the player set for the other, Photo is the photo of the location and player id make it possible to know which player send the goal
    data class OnSurrend(val PlayerId: String) :
        GameEvent() //If one of the player want to end the game without having any of the player found their goal
}