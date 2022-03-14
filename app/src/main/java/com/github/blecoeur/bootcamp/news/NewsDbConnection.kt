package com.github.blecoeur.bootcamp.news

import com.github.blecoeur.bootcamp.profile.achievements.Achievement

interface NewsDbConnection {

    fun getNewsList(size : Int) : List<News>

}