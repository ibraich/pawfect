package com.example.pawfect

import android.util.Log


fun handleMatch(currentUserId: String, targetUserId: String, onMatchConfirmed: () -> Unit) {
    val firestore = Firebase.firestore

    // Check if the target user already initiated a match with the current user
    firestore.collection("PendingMatches")
        .whereEqualTo("initiator", targetUserId)
        .whereEqualTo("target", currentUserId)
        .get()
        .addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                // Match confirmed
                onMatchConfirmed()
                confirmMatch(currentUserId, targetUserId, documents.documents.first().id)
            } else {
                // No mutual match; create a pending match
                createPendingMatch(currentUserId, targetUserId)
            }
        }
        .addOnFailureListener { exception ->
            Log.e("Match", "Error checking for pending matches", exception)
        }
}

fun confirmMatch(currentUserId: String, targetUserId: String, pendingMatchId: String) {
    val firestore = Firebase.firestore

    val matchData = hashMapOf(
        "user1" to currentUserId,
        "user2" to targetUserId
    )

    // Add to Matches collection
    firestore.collection("Matches")
        .add(matchData)
        .addOnSuccessListener {
            Log.d("Match", "Match confirmed between $currentUserId and $targetUserId")

            // Delete the pending match
            firestore.collection("PendingMatches")
                .document(pendingMatchId)
                .delete()
                .addOnSuccessListener {
                    Log.d("Match", "Pending match deleted")
                }
                .addOnFailureListener { exception ->
                    Log.e("Match", "Error deleting pending match", exception)
                }
        }
        .addOnFailureListener { exception ->
            Log.e("Match", "Error confirming match", exception)
        }
}

fun createPendingMatch(currentUserId: String, targetUserId: String) {
    val firestore = Firebase.firestore

    val pendingMatchData = hashMapOf(
        "initiator" to currentUserId,
        "target" to targetUserId,
        "status" to "pending"
    )

    firestore.collection("PendingMatches")
        .add(pendingMatchData)
        .addOnSuccessListener {
            Log.d("Match", "Pending match created between $currentUserId and $targetUserId")
        }
        .addOnFailureListener { exception ->
            Log.e("Match", "Error creating pending match", exception)
        }
}