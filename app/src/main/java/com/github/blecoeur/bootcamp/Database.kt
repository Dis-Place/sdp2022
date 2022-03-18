package com.github.blecoeur.bootcamp

interface Database {
    fun instantiate(url: String) : Database

    fun update(reference: String, key: String, obj: Any) : Any

    fun insert(reference: String, key: String, obj: Any): Any

    fun delete(reference: String, key: String)

    fun get(reference: String, key: String): Any?
}