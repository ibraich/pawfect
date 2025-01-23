package com.example.pawfect

import android.util.Log
import com.google.firebase.firestore.FieldValue

fun addToHaveSeen(currentUserId: String, seenUserId: String) {
    val firestore = Firebase.firestore

    firestore.collection("HaveSeen")
        .document(currentUserId)
        .update("seen", FieldValue.arrayUnion(seenUserId))
        .addOnSuccessListener {
            Log.d("HaveSeen", "Added $seenUserId to seen list for $currentUserId")
        }
        .addOnFailureListener { exception ->
            // If the document doesn't exist, create it
            firestore.collection("HaveSeen")
                .document(currentUserId)
                .set(hashMapOf("seen" to listOf(seenUserId)))
                .addOnSuccessListener {
                    Log.d("HaveSeen", "Created HaveSeen document and added $seenUserId for $currentUserId")
                }
                .addOnFailureListener { innerException ->
                    Log.e("HaveSeen", "Error creating HaveSeen document", innerException)
                }
        }
}
