package com.github.blecoeur.bootcamp.news

interface NewsDbConnection {

    fun getNewsList(size : Int) : List<News>

}