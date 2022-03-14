package com.github.blecoeur.bootcamp

interface Database {
    fun instantiate(url: String) : Database

    fun update(reference: String, key: String, obj: Object) : Object

    fun insert(reference: String, key: String, obj: Object): Object

    fun delete(reference: String, key: String)

    fun get(reference: String, key: String): Object?
}