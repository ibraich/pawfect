package com.example.pawfect

import android.content.Context
import android.widget.Toast

object FirestoreHelper {

    fun updateFields(
        collectionName: String,
        documentId: String,
        updates: Map<String, Any>,
        context: Context,
        onSuccessMessage: String = "Update successful!",
        onFailureMessage: String = "Failed to update fields",
        onNavigate: (() -> Unit)? = null
    ) {
        val db = Firebase.firestore
        val documentRef = db.collection(collectionName).document(documentId)

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
        val db = Firebase.firestore
        val documentRef = db.collection(collectionName).document(documentId)

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


}
