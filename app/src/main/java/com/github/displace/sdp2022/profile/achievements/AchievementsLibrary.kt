package com.github.displace.sdp2022.profile.achievements

import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.users.CompleteUser

object AchievementsLibrary {

    /**
     * These are checked on the user side after a game is completed
     */
    val gamesLib = listOf< (Long) -> Triple<Boolean,String,String> >(
        { i -> Triple(i >= 1,"Seek and Click" , "Play a game") },
        { i -> Triple(i >= 5,"Time to move" , "Play five games") },
        { i -> Triple(i >= 10,"Pinpoint the fun" , "Play ten games") },
        { i -> Triple(i >= 100,"Dis Place looks familiar" , "Play a hundred games") }
    )   //DONE
    val victoryLib = listOf< (Long) -> Triple<Boolean,String,String> >(
        { i ->  Triple(i >= 1,"The taste of victory ..." , "Win a game") },
        { i ->  Triple(i >= 5,"... is sweet" , "Win five games") },
        { i ->  Triple(i >= 10,"Champion" , "Win ten games") },
        { i ->  Triple(i >= 100,"There can only be one" , "Win a hundred games") }
    )   //DONE

    /**
     * These are checked when the friend list is updated
     */
    val friendLib = listOf < (Long) -> Triple<Boolean,String,String> >(
        { i ->  Triple(i >= 2,"BFF" , "Have a friend") },
        { i ->  Triple(i >= 6,"People person" , "Have five friends") },
        { i -> Triple(i >= 11,"Popular" , "Have ten friends") },
        { i -> Triple(i >= 16,"Attracting personality" , "Have fifteen friends") }
            )   //DONE

    /**
     * These are checked when the message list is updated
     */
    val messageLib = listOf < (Long) -> Triple<Boolean,String,String> >(
        { i ->  Triple(i >= 2,"Communications established" , "receive one message") },
        { i ->  Triple(i >= 6,"Carrier Pigeon" , "receive five messages") },
        { i -> Triple(i >= 11,"The Library" , "receive ten messages") }
            )   //DONE

    /**
     * These are checked when a game is completed
     */
    val gameDistLib = listOf< (Long) -> Triple<Boolean,String,String> >(
        { i -> Triple(i <= 10 ,"The chairman" , "win a game without moving") },
        { i -> Triple(i >= 500 ,"Marathon" , "win a game while moving") },
        { i -> Triple(i >= 1000 ,"Sonic the Hedgehog","win a game while moving A LOT") }
        )   //TODO : wait for antoine to put distance in

    /**
     * These are checked when creating/joining lobbies
     */
    val mmtLib = listOf< (Boolean) -> Triple<Boolean,String,String> >(
        { b -> Triple( b ,"Private Club","join/create a private game") },
        { b -> Triple( !b ,"The Wilderness","search for a public game") }
    )   //DONE

    val accountSettingsLib = listOf< (Int) -> Triple<Boolean,String, String> >(
        { i -> Triple(i==0 , "You're looking stunning", "change your profile picture") },
        { i -> Triple(i==1 , "A new life","change your username") }
        )

    val newsLib = listOf< (Boolean) -> Triple<Boolean,String,String>> { b ->
        Triple(
            b,
            "Up to date",
            "check the news"
        )
    }

    /**
     * Use to check the different types of achievements
     */
    fun <T> achievementCheck( app : MyApplication,  user : CompleteUser , value : T , ls : List<(T) -> Triple<Boolean,String,String>> ){
        for(cond in ls){
            val res = cond(value)
            if(res.first){
                user.addAchievement(Achievement(res.second,res.third,app.getCurrentDate()))
            }
        }
    }


}