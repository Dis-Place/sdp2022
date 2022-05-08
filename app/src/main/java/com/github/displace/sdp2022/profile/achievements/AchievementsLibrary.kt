package com.github.displace.sdp2022.profile.achievements

import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.users.CompleteUser

object AchievementsLibrary {

    /**
     * These are checked on the user side after a game is completed
     */
    val gamesLib = listOf< (Long) -> Pair<Boolean,String> >(
        { i -> Pair(i >= 1,"Seek and Click : Play a game") },
        { i -> Pair(i >= 5,"Time to move : Play five games") },
        { i -> Pair(i >= 10,"Pinpoint the fun : Play ten games") },
        { i -> Pair(i >= 100,"Dis Place looks familiar : Play a hundred games") }
    )   //DONE
    val victoryLib = listOf< (Long) -> Pair<Boolean,String> >(
        { i ->  Pair(i >= 1,"The taste of victory ... : Win a game") },
        { i ->  Pair(i >= 5,"... is sweet : Win five games") },
        { i ->  Pair(i >= 10,"Champion : Win ten games") },
        { i ->  Pair(i >= 100,"There can only be one : Win a hundred games") }
    )   //DONE

    /**
     * These are checked when the friend list is updated
     */
    val friendLib = listOf < (Long) -> Pair<Boolean,String> >(
        { i ->  Pair(i >= 2,"BFF : Have a friend") },
        { i ->  Pair(i >= 6,"People person : Have five friends") },
        { i -> Pair(i >= 11,"Popular : Have ten friends") },
        { i -> Pair(i >= 16,"Attracting personality : Have fifteen friends") }
            )   //DONE

    /**
     * These are checked when the message list is updated
     */
    val messageLib = listOf < (Long) -> Pair<Boolean,String> >(
        { i ->  Pair(i >= 2,"Communications established : receive one message") },
        { i ->  Pair(i >= 6,"Carrier Pigeon : receive five messages") },
        { i -> Pair(i >= 11,"The Library : receive ten messages") }
            )   //DONE

    /**
     * These are checked when the settings are changed
     */
    val settingsLib = listOf< (Pair<String,String>) -> Pair<Boolean,String> >(
        { s ->  Pair(s.first=="Dark mode" && s.second == " enabled","The Batman : enable dark mode") },
        { s ->  Pair(s.first=="Sound effects" && s.second == " disabled","Peace and calm : disable SFX") },
        { s ->  Pair(s.first=="Music" && s.second == " disabled","The sound of silence : disable music") }
    )   //DONE

    /**
     * These are checked when a game is completed
     */
    val gameDistLib = listOf< (Long) -> Pair<Boolean,String> >(
        { i -> Pair(i <= 10 ,"The chairman : win a game without moving") },
        { i -> Pair(i >= 500 ,"Marathon : win a game while moving") },
        { i -> Pair(i >= 1000 ,"Sonic the Hedgehog : win a game while moving A LOT") }
        )   //TODO : wait for antoine to put distance in

    /**
     * These are checked when creating/joining lobbies
     */
    val mmtLib = listOf< (Boolean) -> Pair<Boolean,String> >(
        { b -> Pair( b ,"Private Club : join/create a private game") },
        { b -> Pair( !b ,"The Wilderness : search for a public game") }
    )   //DONE


    /**
     * Use to check the different types of achievements
     */
    fun <T> achievementCheck( app : MyApplication,  user : CompleteUser , value : T , ls : List<(T) -> Pair<Boolean,String>> ){
        for(cond in ls){
            val res = cond(value)
            if(res.first){
                user.addAchievement(Achievement(res.second,app.getCurrentDate()))
            }
        }
    }


}