package com.github.blecoeur.bootcamp

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

/**
 * Implementation of the database
 */
class RealTimeDatabase : Database {

    private lateinit var db: FirebaseDatabase

    private fun firebaseSetValue(reference: String, key: String, obj: Any): Task<Void> {
        return getRefAndChild(reference, key).setValue(obj)
    }

    private fun getRefAndChild(reference: String, key: String): DatabaseReference{
        return db.getReference(reference).child(key)
    }

    override fun instantiate(url: String): Database {
        //get the instance of the database
        db = FirebaseDatabase.getInstance(url)

        //Will cache offline data and update the database when online
        db.setPersistenceEnabled(true)
        return this
    }

    override fun update(reference: String, key: String, obj: Any): Any {
        firebaseSetValue(reference, key, obj)
        return obj
    }

    override fun insert(reference: String, key: String, obj: Any): Any {
        return update(reference, key, obj)
    }

    override fun delete(reference: String, key: String) {
        getRefAndChild(reference, key).removeValue()
    }

    override fun get(reference: String, key: String): Any? {
        return getRefAndChild(reference, key).get().result.getValue(Any::class.java)
    }


}