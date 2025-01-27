package com.example.pawfect

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.android.gms.tasks.Tasks
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.net.URL

object FirebaseStorageHelper {

    val storageInstance = FirebaseStorage.getInstance("gs://pawfect-match-30a93.firebasestorage.app")

    fun uploadFile(
        folderPath: String,
        fileName: String,
        fileUri: Uri,
        onSuccess: (Uri) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val storageRef = storageInstance.reference.child("$folderPath/$fileName")

        storageRef.putFile(fileUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    onSuccess(downloadUrl)
                }.addOnFailureListener { exception ->
                    onFailure("Failed to get download URL: ${exception.message}")
                }
            }
            .addOnFailureListener { exception ->
                onFailure("Upload failed: ${exception.message}")
            }
    }

    fun getFilesInFolder(
        folderPath: String,
        onSuccess: (List<String>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val folderRef = storageInstance.reference.child(folderPath)

        folderRef.listAll()
            .addOnSuccessListener { listResult ->
                val downloadUrls = mutableListOf<String>()
                val tasks = listResult.items.map { item ->
                    item.downloadUrl.addOnSuccessListener { downloadUrl ->
                        downloadUrls.add(downloadUrl.toString())
                    }
                }

                Tasks.whenAllComplete(tasks).addOnCompleteListener {
                    onSuccess(downloadUrls)
                }
            }
            .addOnFailureListener { exception ->
                onFailure("Error retrieving files: ${exception.message}")
            }
    }

    fun deleteFile(
        filePath: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val fileRef = storageInstance.reference.child(filePath)

        fileRef.delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure("Error deleting file: ${exception.message}")
            }
    }
}