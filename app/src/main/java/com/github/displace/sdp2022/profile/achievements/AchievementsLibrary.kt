package com.github.displace.sdp2022.profile.achievements

import com.github.displace.sdp2022.MyApplication
import com.github.displace.sdp2022.users.CompleteUser

object AchievementsLibrary {



    /**
     * These are checked on the user side after a game is completed
     * The value used for the condition is the number of games completed (played)
     */
    val gamesLib = listOf(
        AchievementCheck( { i : Long -> i >= 1 } ,"Seek and Click" , "Play a game" ),
        AchievementCheck( { i : Long -> i >= 5} ,"Time to move" , "Play five games" ),
        AchievementCheck( { i : Long -> i >= 10} ,"Pinpoint the fun" , "Play ten games" ),
        AchievementCheck( { i : Long -> i >= 100},"Dis Place looks familiar" , "Play a hundred games")
    )
    val victoryLib = listOf(

        AchievementCheck( { i : Long -> i >= 1 } ,"The taste of victory ..." , "Win a game" ),
        AchievementCheck( { i : Long -> i >= 5} ,"... is sweet" , "Win five games" ),
        AchievementCheck( { i : Long -> i >= 10} ,"Champion" , "Win ten games" ),
        AchievementCheck( { i : Long -> i >= 100} ,"There can only be one" , "Win a hundred games" )


    )

    /**
     * These are checked when the friend list is updated
     * The value used for the condition is the number of games won
     */
    val friendLib = listOf(

        AchievementCheck( { i : Long -> i >= 2 } ,"BFF" , "Have a friend" ),
        AchievementCheck( { i : Long -> i >= 6} ,"People person" , "Have five friends" ),
        AchievementCheck( { i : Long -> i >= 11} ,"Popular" , "Have ten friends" ),
        AchievementCheck( { i : Long -> i >= 16} ,"Attracting personality" , "Have fifteen friends" )

            )

    /**
     * These are checked when the message list is updated
     * The value used for the condition is the number of messages received
     */
    val messageLib = listOf(

        AchievementCheck( { i : Long -> i >= 2 } ,"Communications established" , "receive one message"),
        AchievementCheck( { i : Long ->i >= 6} ,"Carrier Pigeon" , "receive five messages"),
        AchievementCheck( { i : Long -> i >= 11},"The Library" , "receive ten messages")

    )

    /**
     * These are checked when the settings are changed
     * The value used for the condition is the pair of : name of the setting and if it has been enabled/disabled
     */
    val settingsLib = listOf(

        AchievementCheck( { i : Pair<String,String> -> i.first=="Dark mode" && i.second == " enabled" } ,"The Batman" , "enable dark mode"),
        AchievementCheck( { i : Pair<String,String> -> i.first=="Sound effects" && i.second == " disabled"} ,"Peace and calm" , "disable SFX"),
        AchievementCheck( { i : Pair<String,String> -> i.first=="Music" && i.second == " disabled"} ,"The sound of silence" , "disable music")

    )

    /**
     * These are checked when a game is completed
     * The value used for the condition is the distance moved during the last game
     */
    val gameDistLib = listOf(

        AchievementCheck( { i : Long -> i <= 10 } ,"The chairman","win a game without moving" ),
        AchievementCheck( { i : Long -> i >= 500} ,"Marathon","win a game while moving" ),
        AchievementCheck( { i : Long -> i >= 1000} ,"Sonic the Hedgehog","win a game while moving A LOT" )

        )

    /**
     * These are checked when creating/joining lobbies
     * The value used for the condition is the type of lobby (private or public)
     */
    val mmtLib = listOf(
        AchievementCheck( { i : Boolean -> i } ,"Private Club","join/create a private game" ),
        AchievementCheck( { i : Boolean -> !i } ,"The Wilderness","search for a public game" )
    )

    /**
     * These are checked when modifying the profile
     * The value used for the condition represents what has changed (username or profile picture)
     */
    val accountSettingsLib = listOf(
         AchievementCheck( { i : Int -> i==0 } ,"You're looking stunning","change your profile picture" ),
         AchievementCheck( { i : Int -> i==1 } ,"A new life","change your username" )
        )

    /**
     * These are checked when checking out the news
     * There is no real value to be used for the condition, as such it will always be true
     */
    val newsLib = listOf(
        AchievementCheck( { _: Boolean -> true } ,"Up to date", "check the news")
    )

    /**
     * Adds the completed Achievements from the specific list to the user.
     * A completed achievement is the one that indicates a true value with the value given.
     *
     * @param app : the application context, used to obtain the date
     * @param user : the user to which to add the achievement to
     * @param value : the value used for the condition
     * @param ls : the list of Achievements to check for
     */
    fun <T> achievementCheck( app : MyApplication,  user : CompleteUser , value : T , ls : List<AchievementCheck<T>> ){
        for(check in ls){
            val res = check.cond(value)
            if(res){
                user.addAchievement(Achievement(check.name,check.description,app.getCurrentDate()))
            }
        }
    }


}

/**
 * An Achievement Check is structured in the following way :
 *
 * @param cond : takes a value and determines if the achievement has been completed
 * @param name : the name of the corresponding achievement
 * @param description : the description of the corresponding achievement
 *
 */
data class AchievementCheck<T> ( val cond : (T) -> Boolean , val name : String , val description : String )