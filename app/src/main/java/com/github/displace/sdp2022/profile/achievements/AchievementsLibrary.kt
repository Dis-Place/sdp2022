package com.github.displace.sdp2022.profile.achievements

object AchievementsLibrary {

    /**
     * These are checked on the user side after a game is completed
     */
    val gamesLib = listOf< (Int) -> Pair<Boolean,String> >(
        { i -> Pair(i >= 1,"Seek and Click : Play a game") },
        { i -> Pair(i >= 5,"Time to move : Play five games") },
        { i -> Pair(i >= 10,"Pinpoint the fun : Play ten games") },
        { i -> Pair(i >= 100,"Dis Place looks familiar : Play a hundred games") }
    )
    val victoryLib = listOf< (Int) -> Pair<Boolean,String> >(
        { i ->  Pair(i >= 1,"The taste of victory ... : Win a game") },
        { i ->  Pair(i >= 5,"... is sweet : Win five games") },
        { i ->  Pair(i >= 10,"Champion : Win ten games") },
        { i ->  Pair(i >= 100,"There can only be one : Win a hundred games") }
    )

    /**
     * These are checked when the friend list is updated
     */
    val friendLib = listOf < (Int) -> Pair<Boolean,String> >(
        { i ->  Pair(i >= 2,"BFF : Have a friend") },
        { i ->  Pair(i >= 6,"People person : Have five friends") },
        { i -> Pair(i >= 11,"Popular : Have ten friends") },
        { i -> Pair(i >= 16,"Attracting personality : Have fifteen friends") }
            )

    /**
     * These are checked when the message list is updated
     */
    val messageLib = listOf < (Int) -> Pair<Boolean,String> >(
        { i ->  Pair(i >= 2,"Communications established : send/receive one message") },
        { i ->  Pair(i >= 6,"Carrier Pigeon : send/receive five messages") },
        { i -> Pair(i >= 11,"The Library : send/receive ten messages") }
            )

    /**
     * These are checked when the settings are changed
     */
    val settingsLib = listOf< (Boolean) -> Pair<Boolean,String> >(
        { b ->  Pair(b,"The Batman : activate dark mode") },
        { b ->  Pair(b,"Peace and calm : deactivate SFX") },
        { b ->  Pair(b,"The sound of silence : deactivate music") }
    )

    /**
     * These are checked when a game is completed
     */
    val gameDistLib = listOf< (Pair<Int,Boolean>) -> Pair<Boolean,String> >(
        { i -> Pair(i.first <= 10 ,"The chairman : win a game without moving") },
        { i -> Pair(i.first >= 500 ,"Marathon : win a game while moving") },
        { i -> Pair(i.first >= 1000 ,"Sonic the Hedgehog : win a game while moving A LOT") }
        )

    /**
     * These are checked when creating/joining lobbies
     */
    val mmtLib = listOf< (Boolean) -> Pair<Boolean,String> >(
        { b -> Pair( b ,"Private Club : join/create a private game") },
        { b -> Pair( !b ,"The Wilderness : search a public game") }
    )


}