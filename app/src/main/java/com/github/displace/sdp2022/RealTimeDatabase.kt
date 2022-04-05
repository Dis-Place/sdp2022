package com.github.displace.sdp2022

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

/**
 * Implementation of the database
 */
class RealTimeDatabase : Database {

     private lateinit var db: FirebaseDatabase
     private var debug : String = ""

    private fun firebaseSetValue(reference: String, key: String, obj: Any): Task<Void> {
        return getRefAndChild(reference, key).setValue(obj)
    }

    private fun getRefAndChild(reference: String, key: String): DatabaseReference {
        return db.getReference(reference).child(key)
    }

    override fun instantiate(url: String, debug : Boolean): Database {
        //get the instance of the database
        if(debug){
            this.debug = "debug/"
        }
        db = FirebaseDatabase.getInstance(url)

        //Will cache offline data and update the database when online
//        db.setPersistenceEnabled(true)
        return this
    }

    override fun update(reference: String, key: String, obj: Any): Any {
        firebaseSetValue(debug+reference, key, obj)
        return obj
    }

    override fun insert(reference: String, key: String, obj: Any): Any {
        return update(reference, key, obj)
    }

    override fun delete(reference: String, key: String) {
        getRefAndChild(debug+reference, key).removeValue()
    }

    override fun referenceGet(reference: String, key: String): Task<DataSnapshot> {
        return getRefAndChild(debug+reference, key).get()
    }

    override fun noCacheInstantiate(url: String, debug : Boolean): Database {
        //get the instance of the database
        if(debug){
            this.debug = "debug/"
        }
        db = FirebaseDatabase.getInstance(url)
        return this
    }

    override fun getDbReference(path : String) : DatabaseReference  {
        return if(path == ""){
            db.reference
        }else{
            db.getReference(debug+path)
        }
    }



}