package com.example.pawfect

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore

class Firebase : Application() {
    companion object {
        lateinit var firestore: FirebaseFirestore
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()
    }
}