package com.github.displace.sdp2022.news

interface NewsDbConnection {

    fun getNewsList(size: Int): List<News>

}