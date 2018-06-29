package com.example.user.kotlinapp.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseSettings {

    companion object{
        const val USERS_BAR = "/users/"
        const val USERS = "users"
        const val LAUNCHES_BAR = "/launches/"
        const val LAUNCHES = "launches"
    }

    private var mFirebaseAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null

    fun getFirebaseAuthInstance(): FirebaseAuth? {
        if (mFirebaseAuth == null) {
            mFirebaseAuth = FirebaseAuth.getInstance()
        }

        return mFirebaseAuth
    }

    fun getDatabaseInstance(): DatabaseReference {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance().reference
        }

        return mDatabase as DatabaseReference
    }
}
