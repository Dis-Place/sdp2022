package com.github.blecoeur.bootcamp.profile.achievements

public class DBAchAdapter() {

    private val dummyAchList : List<Achievement>  = listOf( Achievement("ach1","today")  ,
        Achievement("ach1","today") ,
        Achievement("ach1","today") )

    fun getAchList(size : Int) : List<Achievement>{
        return dummyAchList.take(size)
    }

}