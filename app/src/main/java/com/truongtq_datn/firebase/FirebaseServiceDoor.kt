package com.truongtq_datn.firebase

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseServiceDoor {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getDoors(): List<DocumentReference>? {
        return try {
            val querySnapshot = db.collection("doors").get().await()
            querySnapshot.documents.map { it.reference }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getDoorsPosition(): List<String>? {
        return try {
            val querySnapshot = db.collection("doors").get().await()
            querySnapshot.documents.map { it.getString("position") ?: "Unknown Door" }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getDoorID(position: String): String? {
        return try {
            val querySnapshot =
                db.collection("doors").whereEqualTo("position", position).get().await()
            if (!querySnapshot.isEmpty) {
                querySnapshot.documents[0].id
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getDoor(idDoor: String): Map<String, Any>? {
        return try {
            val documentReference = db.collection("doors").document(idDoor)
            val documentSnapshot = documentReference.get().await()
            if (documentSnapshot.exists()) {
                documentSnapshot.data
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}