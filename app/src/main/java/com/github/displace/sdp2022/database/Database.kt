package com.github.displace.sdp2022.database

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

/**
 * Database
 *
 * A basic interface that describe a database
 *
 * @constructor Create Database object
 */
interface Database {
    fun instantiate(url: String, debug: Boolean): Database

    fun update(reference: String, key: String, obj: Any): Any

    fun insert(reference: String, key: String, obj: Any): Any

    fun delete(reference: String, key: String)

    fun referenceGet(reference: String, key: String): Task<DataSnapshot>

    fun noCacheInstantiate(url: String, debug: Boolean): Database

    fun getDbReference(path: String): DatabaseReference
}