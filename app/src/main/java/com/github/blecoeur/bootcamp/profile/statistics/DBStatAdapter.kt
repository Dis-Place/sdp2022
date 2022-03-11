package com.github.blecoeur.bootcamp.profile.statistics

import com.github.blecoeur.bootcamp.profile.achievements.Achievement

class DBStatAdapter {

    private val dummyStatList : List<Statistic>  = listOf( Statistic("stat1",0)  ,
        Statistic("stat2",0) ,
        Statistic("stat3",0) )

    fun getStatList(size : Int) : List<Statistic>{
        return dummyStatList.take(size)
    }

}