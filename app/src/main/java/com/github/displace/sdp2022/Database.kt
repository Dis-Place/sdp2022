package com.github.displace.sdp2022

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

interface Database {
    fun instantiate(url: String, debug : Boolean) : Database

    fun update(reference: String, key: String, obj: Any) : Any

    fun insert(reference: String, key: String, obj: Any): Any

    fun delete(reference: String, key: String)

    fun get(reference: String, key: String): Any?
    fun referenceGet(reference: String, key: String): Task<DataSnapshot>
    fun noCacheInstantiate(url: String, debug: Boolean): Database
    fun getDbReference(path: String) : DatabaseReference
}