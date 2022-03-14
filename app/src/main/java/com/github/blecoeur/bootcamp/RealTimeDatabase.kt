package com.github.blecoeur.bootcamp

import android.util.Log
import com.google.firebase.database.FirebaseDatabase

class RealTimeDatabase: Database {
    private lateinit var db: FirebaseDatabase
    override fun instantiate(url: String): Database {
        //get the instance of the database
        db = FirebaseDatabase.getInstance(url)

        //Will cache offline data and update the database when online
        db.setPersistenceEnabled(true)
        return this
    }

    override fun update(reference: String, key: String, obj: Any): Any {
        db.getReference(reference).child(key).setValue(obj).addOnSuccessListener {
            Log.i("firebase", "Updated value $key")
        }.addOnFailureListener {
            Log.e("firebase", "Error updating data", it)
        }
        return obj
    }

    override fun insert(reference: String, key: String, obj: Any): Any {
        db.getReference(reference).child(key).setValue(obj).addOnSuccessListener {
            Log.i("firebase", "Inserted value $key")
        }.addOnFailureListener{
            Log.e("firebase", "Error inserting data", it)
        }
        return obj
    }

    override fun delete(reference: String, key: String) {
        db.getReference(reference).child(key).removeValue().addOnSuccessListener {
            Log.i("firebase", "Removed value $key")
        }.addOnFailureListener{
            Log.e("firebase", "Error deleting data", it)
        }
    }

    override fun get(reference: String, key: String): Any? {
        return db.getReference(reference).child(key).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }.result.getValue(Object::class.java)
    }


}