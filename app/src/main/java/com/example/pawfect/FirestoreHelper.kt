package com.example.pawfect

import android.content.Context
import android.widget.Toast
import android.util.Log
import com.google.firebase.firestore.SetOptions

object FirestoreHelper {

    private fun getFirestoreInstance() = Firebase.firestore

    fun updateFields(
        collectionName: String,
        documentId: String,
        updates: Map<String, Any>,
        context: Context,
        onSuccessMessage: String = "Update successful!",
        onFailureMessage: String = "Failed to update fields",
        onNavigate: (() -> Unit)? = null
    ) {
        val documentRef = getFirestoreInstance().collection(collectionName).document(documentId)

        documentRef.update(updates)
            .addOnSuccessListener {
                Toast.makeText(context, onSuccessMessage, Toast.LENGTH_SHORT).show()
                onNavigate?.invoke()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "$onFailureMessage: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    fun fetchDocumentAsMap(
        collectionName: String,
        documentId: String,
        onSuccess: (Map<String, Any?>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val documentRef = getFirestoreInstance().collection(collectionName).document(documentId)

        documentRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    onSuccess(document.data ?: emptyMap())
                } else {
                    onSuccess(emptyMap())
                }
            }
            .addOnFailureListener { exception ->
                onFailure("Error fetching document: ${exception.message}")
            }
    }

    fun fetchDocument(
        collectionName: String,
        documentId: String,
        onSuccess: (UserFetch?) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val documentRef = getFirestoreInstance().collection(collectionName).document(documentId)

        documentRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(UserFetch::class.java)
                    onSuccess(user?.copy(id = documentId))
                } else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailure("Error fetching document: ${exception.message}")
            }
    }

    fun getMatchIdForUsers(
        user1Id: String,
        user2Id: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {

        getFirestoreInstance().collection("Matches")
            .whereEqualTo("user1", user1Id)
            .whereEqualTo("user2", user2Id)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val matchId = documents.documents[0].id
                    Log.d("Firestore", "Match found with ID: $matchId")
                    onSuccess(matchId)

                } else {
                    getFirestoreInstance().collection("Matches")
                        .whereEqualTo("user1", user2Id)
                        .whereEqualTo("user2", user1Id)
                        .get()
                        .addOnSuccessListener { reversedDocs ->
                            if (!reversedDocs.isEmpty) {
                                val matchId = reversedDocs.documents[0].id
                                Log.d("Firestore", "Match found (reversed) with ID: $matchId")
                                onSuccess(matchId)

                            } else {
                                val errorMessage = "No match found between users: $user1Id and $user2Id."
                                Log.e("Firestore", errorMessage)
                                onFailure(errorMessage)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error fetching reversed match document: ${e.message}")
                            onFailure("Error fetching reversed match document: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching match document: ${e.message}")
                onFailure("Error fetching match document: ${e.message}")
            }
    }

    fun updateOffspringImageUrl(
        matchId: String,
        offspringImageUrl: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val matchDocRef = getFirestoreInstance().collection("Matches").document(matchId)

        matchDocRef.update("offspringImageUrl", offspringImageUrl)
            .addOnSuccessListener {
                Log.d("Firestore", "offspringImageUrl updated successfully for match: $matchId")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error updating offspringImageUrl: ${e.message}")
                onFailure("Error updating offspringImageUrl: ${e.message}")
            }
    }

    fun getOffspringImageUrl(
    matchId: String,
    onSuccess: (String?) -> Unit,
    onFailure: (String) -> Unit
    ) {
        getFirestoreInstance().collection("Matches").document(matchId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val offspringImageUrl = document.getString("offspringImageUrl")
                    onSuccess(offspringImageUrl)
                } else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener { e ->
                onFailure("Error fetching offspringImageUrl: ${e.message}")
            }
    }

    fun storeRoutesInFirestore(matchId: String, newRoutes: List<Map<String, Double>>) {
        val matchDocRef = getFirestoreInstance().collection("Matches").document(matchId)

        matchDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val existingRoutes = document.get("suggestedRoutes") as? List<Map<String, Double>> ?: emptyList()
                val updatedRoutes = existingRoutes + newRoutes

                matchDocRef.update("suggestedRoutes", updatedRoutes)
                    .addOnSuccessListener {
                        Log.d("Firestore", "Routes updated successfully for match: $matchId")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error updating routes: ${e.message}")
                    }
            } else {
                // No existing routes, create new field
                val newRouteData = mapOf("suggestedRoutes" to newRoutes)

                matchDocRef.set(newRouteData, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d("Firestore", "Route successfully added to match: $matchId")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error storing route: ${e.message}")
                    }
            }
        }.addOnFailureListener { e ->
            Log.e("Firestore", "Error fetching match document: ${e.message}")
        }
    }


    fun getUserSuggestedRoutes(
        userId: String,
        onSuccess: (List<Map<String, Double>>) -> Unit,
        onFailure: (String) -> Unit
    ) {

        getFirestoreInstance().collection("Matches")
            .whereEqualTo("user1", userId)
            .get()
            .addOnSuccessListener { user1Matches ->
                getFirestoreInstance().collection("Matches")
                    .whereEqualTo("user2", userId)
                    .get()
                    .addOnSuccessListener { user2Matches ->

                        val allMatches = user1Matches.documents + user2Matches.documents
                        val allRoutes = mutableListOf<Map<String, Double>>()

                        allMatches.forEach { document ->
                            val routes = document.get("suggestedRoutes") as? List<Map<String, Double>> ?: emptyList()
                            allRoutes.addAll(routes)
                        }

                        onSuccess(allRoutes)
                    }
                    .addOnFailureListener { e ->
                        onFailure("Error fetching matches as user2: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                onFailure("Error fetching matches as user1: ${e.message}")
            }
    }


}